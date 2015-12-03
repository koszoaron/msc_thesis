/**
 * This file is part of UniNav.
 *
 * UniNav is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UniNav is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UniNav.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.koszoaron.uninav.footpath.core;

import java.util.LinkedList;
import java.util.List;

import com.github.koszoaron.uninav.footpath.graph.GraphEdge;

/**
 * Best fit positioner algorithm.
 * 
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class BestFitPositioner {
	
	private static final int INITIAL_DYN_SIZE = 2;
	
	private FpConfig conf = null;
	private List<GraphEdge> edges = null;
	
	private double[][] c = null;
	private double allDist = 0.0;
	private double averageStepLength = 0.0;
	private double[] fromMap = null;
	private LinkedList<Double> s = null;
	private double[][] dyn = null;
	private int currentStep = 0;
	private double progress = 0.0;
	
	private boolean firstStep = false;

	public BestFitPositioner(double stepLength, List<GraphEdge> edges, FpConfig conf) {
		this.edges = edges;
		this.conf = conf;
		
		/* setup c */
		c = new double[edges.size()][2];
		double tempLength = 0.0;
		for (int i = 0; i < edges.size(); i++) {
			GraphEdge temp = edges.get(i);
			tempLength += temp.getLength();
			c[i][0] = tempLength;
			c[i][1] = temp.getCompDir();
		}
		
		/* setup all dist. */
		allDist = c[edges.size()-1][0];
		
		/* setup average step length */
		this.averageStepLength = stepLength;
		
		/* setup n */
		double[] n = new double[(int)(allDist / this.averageStepLength)];
		for (int i = 0; i < n.length; i++) {
			n[i] = this.averageStepLength * i;
		}
		
		/* setup fromMap */
		fromMap = new double[n.length];
		for (int i = 0; i < n.length; i++) {
			/* this code below uses directions directly from edges */
			int edge_i = 0;
			while ( !(c[edge_i][0] > n[i]) ) {
				edge_i++;
			}
			fromMap[i] = c[edge_i][1];
		}
		
		/* s: a list to store detected step headings */
		s = new LinkedList<Double>();
		
		/* dyn: the last two lines from the matrix D */
		dyn = new double[INITIAL_DYN_SIZE][fromMap.length + 1];
		
		/* initialization */
		for (int x = 0; x < INITIAL_DYN_SIZE; x++) {
			for (int y = 0; y < dyn[0].length; y++) {
				if (x == 0 && y == 0) {
					dyn[x][y] = 0.0;
				} else if (x == 0) {
					dyn[x][y] = Double.POSITIVE_INFINITY;
				} else if (y == 0) {
					dyn[x][y] = Double.POSITIVE_INFINITY;
				}
			}
		}		
	}

	/**
	 * Check if the difference of the given angles in degrees is less than the given alowed difference
	 * @param v the first angle
	 * @param t the second angle
	 * @param diff the allowed difference
	 * @return true if v <= diff away from t
	 */
	public static boolean isInRange(double v, double t, double diff) {
		if (Math.abs(v - t) <= diff) {
			return true;
		}
		if (Math.abs((v + diff) % 360 - (t + diff) % 360) <= diff) {
			return true;
		}
		return false;
	}

	public void addStep(double direction) {
		if (firstStep) {
			dyn[0][0] = Double.POSITIVE_INFINITY;
		}
		firstStep = true;
		
		double t1, t2, t3;
		
		currentStep++;
		int x = currentStep;
		
		//s.add(new Double(direction));
		s.add(Double.valueOf(direction));
		
		/* calculate new line of the matrix D */
		for (int y = 1; y < dyn[0].length; y++) {
			/* top */
			t1 = dyn[x % 2][y-1] + score(getFromS(x - 1), getFromMap(y - 2), false);
			/* left */
			t2 = dyn[(x - 1) % 2][y] + score(getFromS(x - 2), getFromMap(y - 1), false);
			/* diagonal */
			t3 = dyn[(x - 1) % 2][y - 1] + score(getFromS(x - 1), getFromMap(y - 1), true);
			
			dyn[x % 2][y] = Math.min(Math.min(t1, t2), t3);
		}
		
		int yMin = -1;
		double fMin = Double.POSITIVE_INFINITY;
		for (int y_ = 1; y_ < dyn[0].length - 1; y_++) {
			if (fMin > dyn[x % 2][y_]) {
				fMin = dyn[x % 2][y_];
				yMin = y_;
			}
		}
		
		/* yMin + 1: index i is step i + 1 (array starting at 0) */
		progress = (yMin + 1) * averageStepLength;
		
		/* update fields in the config:
		 * find out which edge we are on and how far on that edge */
		double tempLen = edges.get(0).getLength();
		int edgeIndex = 0;
		while (tempLen < progress && edgeIndex < edges.size()) {
			edgeIndex++;
			tempLen += edges.get(edgeIndex).getLength();
		}
		
		conf.currentLen = progress;
		for (int i = 0; i < edgeIndex; i++) {
			conf.currentLen -= edges.get(i).getLength();
		}
		
		conf.edgePointer = edgeIndex;
		conf.lastMatchedStep = yMin;
		conf.matchedSteps++;
		conf.unmatchedSteps = conf.matchedSteps - yMin;
	}
	
	public double getProgress() {
		return progress;
	}

	private double getFromMap(int i) {
		if (i < 0) {
			return 0.0;
		} else { 
			return fromMap[i];
		}
	}
	
	private double getFromS(int i) {
		if (i < 0) { 
			return 0.0;
		} else {
			return s.get(i).doubleValue();
		}
	}
	
	private double score(double x, double y, boolean diagonal) {
		double ret = 2.0; /* penalty */
		
		/* sanitize */
		double t = Math.abs(x - y);
		t = (t > 180.0) ? 360.0 - t : t;
		
		/* score */
		if (t < 45.0) {
			ret = 0.0;
		} else if (t < 90.0) {
			ret = 1.0;
		} else if (t < 120.0) {
			ret = 2.0;
		} else {
			ret = 10.0;
		}
			
		return (diagonal ? ret : (ret + 1.5));
	}

	/**
	 * Holder for the navigation configuration.
	 *
	 * @author Paul Smith
	 * @author Aron Koszo <koszoaron@gmail.com>
	 */
	public class FpConfig {
		/** Points to the current edge we are walking on. */
		public int edgePointer;
		/** How far we have come on this edge. */
		public double currentLen;
		/** How many total matched steps we have. */
		public int matchedSteps;
		/** How many unmatched steps we have since the last matched step. */
		public int unmatchedSteps;
		/** Which step the last matched step is. */
		public int lastMatchedStep;
		/** The step size. */
		public double stepSize;

		public FpConfig() {
			this.edgePointer = 0;
			this.currentLen = 0.0;
			this.unmatchedSteps = 0;
			this.lastMatchedStep = 0;
			this.matchedSteps = 0;
			this.stepSize = 1.0; /* 1.0 meter */
		}

		public FpConfig(FpConfig conf) {
			this.currentLen = conf.currentLen;
			this.lastMatchedStep = conf.lastMatchedStep;
			this.matchedSteps = conf.matchedSteps;
			this.edgePointer = conf.edgePointer;
			this.stepSize = conf.stepSize;
			this.unmatchedSteps = conf.unmatchedSteps;
		}
	}
	
}
