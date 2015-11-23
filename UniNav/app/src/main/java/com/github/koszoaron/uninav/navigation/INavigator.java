package com.github.koszoaron.uninav.navigation;

import java.util.List;

import com.github.koszoaron.uninav.fp.graph.GraphEdge;
import com.github.koszoaron.uninav.fp.graph.GraphNode;
import com.github.koszoaron.uninav.pojo.LatLonPos;

import org.osmdroid.bonuspack.overlays.Polyline;

/**
 * Interface for navigation callbacks.
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public interface INavigator {

	public void storeGlobalScale(float scale);
	
	public List<GraphEdge> getNavPathEdges();
	
	public List<Polyline> getWalls(float level);
	
	public LatLonPos getPosition();
	
	public double getCompassValue();
	
	public double getNavPathDir();
	
	public double getAcceptanceWidth();
	
	public double getNavPathEdgeLengthRemaining();
	
	public double getNavPathLength();
	
	public double getNavPathLengthRemaining();
	
	public NextTurn getNextTurn();
	
	public double getEstimatedStepLength();
	
	public double getStepLengthInMeters();
	
	public GraphNode getRouteEnd();
	
	public double getNaiveStairsWidth();
	
	public void setNavigating(boolean navigating);
		
	
	public enum NextTurn {
		LEFT,
		STRAIGHT_ON,
		RIGHT
	}
}
