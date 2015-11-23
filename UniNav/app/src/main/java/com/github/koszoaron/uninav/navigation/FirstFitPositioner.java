package com.github.koszoaron.uninav.navigation;

import java.util.LinkedList;
import java.util.List;

import com.github.koszoaron.uninav.fp.graph.GraphEdge;
import com.github.koszoaron.uninav.pojo.FpConfig;

/**
 * First fit online positioner algorithm.
 * 
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class FirstFitPositioner extends AbsPositioner {
	
	private FpConfig conf;
	private List<GraphEdge> navPathEdges = null;

	/** A list for storing each step along with its direction. */
	private LinkedList<Double> dirHistory = new LinkedList<Double>();
	/** When this value is passed, lookahead is started. */
	private int maxFallBackSteps = 4;
	
	private INavigator navigatorCb = null;
	
	public FirstFitPositioner(INavigator nav, List<GraphEdge> navPathEdges, FpConfig conf) {
		this.navPathEdges = navPathEdges;
		this.conf = conf;
		this.navigatorCb = nav;
	}
	
	/**
	 * Recalculates the position from the entire route data.
	 */
	public void recalcPos() {
		FpConfig x = new FpConfig(conf);
		/* reset the route to the beginning */
		x.lastMatchedStep = -1;
		x.edgePointer = 0;
		x.currentLen = 0.0;
		x.unmatchedSteps = dirHistory.size() - 1;
		
		x = findMatch(x, false);
		x.lastMatchedStep = dirHistory.size() - 1;
		x.matchedSteps = x.lastMatchedStep;
		
		/* update the position on the path */
		conf = x;
	}

	/**
	 * This is called each time a step is detected, and thus information about
	 * the users whereabouts need to be updated.
	 */
	@Override
	public void addStep(double compValue) {
		//dirHistory.add(new Double(compValue));
		dirHistory.add(Double.valueOf(compValue));

		if (conf.edgePointer < navPathEdges.size()) {
			/* we haven't reached a destination
			 * successive matching, incoming values are/have been
			 * roughly (in range of acceptanceWidth) correct
			 * and not more than maxFallBackSteps have been unmatched */
			if (isInRange(compValue, navPathEdges.get(conf.edgePointer).getCompDir(), navigatorCb.getAcceptanceWidth())
					&& ((conf.unmatchedSteps <= maxFallBackSteps))	/* unmatched steps might be errors */
					|| (navigatorCb.getNavPathLengthRemaining()<maxFallBackSteps*(conf.stepSize+1))) { /* near to end */
					
				/* updated the latest matched step index from step history */
				conf.lastMatchedStep = dirHistory.size()-1;
				/* Reset unmatched steps */
				conf.unmatchedSteps = 0;
				/* Add one more matched step to counter */
				conf.matchedSteps++;
				
				/* update how far we have walked on this edge */
				if (navPathEdges.get(conf.edgePointer).isStairs()) {
					/* don't use step length because of stairs */
					if (navPathEdges.get(conf.edgePointer).getSteps() > 0) {
						/* calculate length from number of steps on stairs */
						conf.currentLen += navPathEdges.get(conf.edgePointer).getLength() / navPathEdges.get(conf.edgePointer).getSteps();
					} else if (navPathEdges.get(conf.edgePointer).getSteps() == -1) {
						/* naive length for steps on stairs if steps undefined */
						conf.currentLen += navigatorCb.getNaiveStairsWidth();
					} else {
						/* error in data: edge isStairs is defined but the number of steps are undefined/invalid */
						conf.currentLen += conf.stepSize;
					}
				} else {
					conf.currentLen += conf.stepSize;
				}
								
				if (conf.currentLen >= navPathEdges.get(conf.edgePointer).getLength()) { 
					/* edge length was exceeded so skip to next edge */
					conf.edgePointer++;
					/* reset the amount of walked length to the remainder of step length */
					conf.currentLen = conf.currentLen - navPathEdges.get(conf.edgePointer - 1).getLength();
					/* stop navigating if we have passed the last edge */
					if (conf.edgePointer >= navPathEdges.size()) {
						navigatorCb.setNavigating(false);
					}
				}
			} else {
				/* the step did not match current assumed edge, try lookahead, if we 
				 * calculated with steps being larger in reality
				 * if steps are smaller in reality then try to wait and resize the step size */
				
				/* increase the amount of unmatched steps */
				conf.unmatchedSteps++;
				
				/* do not do lookahead if the direction matches the last edge
				 * and just wait for the user to turn on to this edge */
				if (conf.edgePointer >= 1 && conf.currentLen <= conf.stepSize
						&& isInRange(compValue, navPathEdges.get(conf.edgePointer - 1).getCompDir(), navigatorCb.getAcceptanceWidth())) {
					return;
				}	
				
				/* enough steps were unmatched to start lookahead */
				if (conf.unmatchedSteps > maxFallBackSteps) {
					/* call for new position first find */
					conf = findMatch(conf, true);
				}
			}
		} 
	}
	
	@Override
	public double getProgress() {
		double len = 0.0;
		
		/* sum all traversed edges */
		for (int i = 0; i < conf.edgePointer; i++) {
			len += navPathEdges.get(i).getLength();
		}
		
		/* and how far we have walked on the current edge */
		len += conf.currentLen;
		
		return len;
	}

	/**
	 * Calculate the best/first matching position on path from the given configuration.
	 * 
	 * @param config the current configuration to base calculation on
	 * @param first set to true if first match should be returned
	 * @return new configuration for position
	 */
	private FpConfig findMatch(FpConfig config, boolean first) {
		if (dirHistory == null) {
			return config;
		}
		
		if (dirHistory.size() == 0) {
			return config;
		}
		
		/* move backwards through the step history and look forwards to match it to an edge
		 * this works based on the assumption that at some point we have correct values again
		 * accept only if we have found at least minMetresToMatch on a single edge */
		
		double lastDir = dirHistory.get(dirHistory.size() - 1);  /* the last unmatched value */
		int maxBackLogCount = Integer.MIN_VALUE;
		int newPointer = config.edgePointer;
		double newCurLen = 0.0;
		int minCount = 4; /* minimum 4 steps to match backwards */
		
		/* go through all remaining edges and find the first edge matching the current direction */
		for (int localPointer = config.edgePointer; localPointer < navPathEdges.size(); localPointer++) {
			/* there is an edge matching a direction on the path */
			if (isInRange(navPathEdges.get(localPointer).getCompDir(), lastDir, navigatorCb.getAcceptanceWidth())) {
				UglyObject o = new UglyObject();
				o.count = 0;
				o.historyPointer = dirHistory.size() - 1;
				
				int oldCount = 0;
				int backLogPointer = localPointer;
				double edgeDir = navPathEdges.get(backLogPointer).getCompDir();
				double edgeLength = navPathEdges.get(backLogPointer).getLength();

				/* sum up the path length */
				while (backLogPointer > config.edgePointer && findMatchingSteps(o, edgeDir, config, edgeLength, config.stepSize)) {
					oldCount = o.count;
					backLogPointer--;
					
					if (backLogPointer < 0) {
						break;
					}
					
					edgeDir = navPathEdges.get(backLogPointer).getCompDir();
					/* remember last count, on last loop o.count is set to zero */
				}

				if (oldCount >= minCount && oldCount > maxBackLogCount) {
					maxBackLogCount = oldCount;
					newPointer = localPointer;
					newCurLen = amountInSameDirection(dirHistory.size() - 1,navPathEdges.get(localPointer), config);
					if (first) {
						break;
					}
				}
			}
		}
		
		if (maxBackLogCount != Integer.MIN_VALUE) {
			if(newPointer == config.edgePointer){
				/* jump along the same edge */
				config.currentLen += newCurLen;
				
				if (config.currentLen > navPathEdges.get(config.edgePointer).getLength()) {
					/* do not exceed the edge */
					config.currentLen = navPathEdges.get(config.edgePointer).getLength();
				}
			} else {					
				/* jump along a different edge */
				config.edgePointer = newPointer;
				config.currentLen = newCurLen;
				
				if (config.currentLen > navPathEdges.get(config.edgePointer).getLength()) {
					/* do not exceed the edge */
					config.currentLen = navPathEdges.get(config.edgePointer).getLength();
				}
			}
			
			config.unmatchedSteps = 0;
			config.lastMatchedStep = dirHistory.size() - 1;
		}

		return config;
	}
	
	private boolean findMatchingSteps(UglyObject o, double edgeDir, FpConfig config, double edgeLength, double stepLength) {
		int oldCount = o.count;

		while (o.historyPointer >= 0
				&& o.historyPointer > config.lastMatchedStep 
				&& isInRange(dirHistory.get(o.historyPointer), edgeDir, navigatorCb.getAcceptanceWidth())) {
			
			o.count++;
			o.historyPointer--;
			
			double lengthToAdd = 0.0;
			
			if (navPathEdges.get(config.edgePointer).isStairs()) {
				/* don't use step length because of stairs */
				if (navPathEdges.get(config.edgePointer).getSteps() > 0) {
					/* calculate length from number of steps on stairs */
					lengthToAdd = navPathEdges.get(config.edgePointer).getLength() / navPathEdges.get(config.edgePointer).getSteps();
				} else if (navPathEdges.get(config.edgePointer).getSteps() == -1) {
					/* naive length for steps on stairs if steps undefined */
					lengthToAdd = navigatorCb.getNaiveStairsWidth();
				} else {
					/* error in data: edge isStairs is defined but the number of steps are undefined/invalid */
					lengthToAdd = config.stepSize;
				}
			} else {
				lengthToAdd = config.stepSize;
			}
			
			if (edgeLength <= (o.count - oldCount) * lengthToAdd) {
				/* return true if whole the edge has been traveled along */
				return true;
			}
		}

		if (oldCount != o.count) {
			return true;
		} else {
			return false;
		}
	}

	private double amountInSameDirection(int historyPointer, GraphEdge edge, FpConfig config) {
		double retLength = 0.0;
		
		while (historyPointer >= 0
				&& historyPointer >= config.lastMatchedStep  
				&& isInRange(edge.getCompDir(), dirHistory.get(historyPointer), navigatorCb.getAcceptanceWidth())){

			historyPointer--;
			double lengthToAdd = 0.0;

			if (edge.isStairs()) {
				/* don't use step length because of stairs */
				if (edge.getSteps() > 0) {
					/* calculate length from number of steps on stairs */
					lengthToAdd = edge.getLength() / edge.getSteps();
				} else if (edge.getSteps() == -1) {
					/* naive length for steps on stairs if steps undefined */
					lengthToAdd= navigatorCb.getNaiveStairsWidth();
				} else {
					/* error in data: edge isStairs is defined but the number of steps are undefined/invalid */
					lengthToAdd = config.stepSize;
				}
			} else {
				lengthToAdd = config.stepSize;
			}
			retLength += lengthToAdd;
		}
		return retLength;
	}

	private class UglyObject {
		int count;
		int historyPointer;
	}
}
