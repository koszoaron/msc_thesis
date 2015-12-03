/**
 * Copyright (C) 2015 Aron Koszo
 *
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

package com.github.koszoaron.uninav.navigation;

import java.util.List;

import com.github.koszoaron.uninav.footpath.graph.GraphEdge;
import com.github.koszoaron.uninav.footpath.graph.GraphNode;
import com.github.koszoaron.uninav.pojo.Location;

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
	
	public Location getPosition();
	
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
