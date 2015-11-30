package com.github.koszoaron.uninav.footpath.graph;

import java.util.LinkedList;

import com.github.koszoaron.uninav.pojo.LatLonPos;

/**
 * A class to represent a node in the map/graph.
 * 
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class GraphNode {
	
	private double lat;
	private double lon;
	private String name;
	private boolean isInDoors;
	private float level;
	private long id;
	private String mergeId;
	private int numSteps = 0;
	private LinkedList<GraphEdge> locEdges;
	
	/**
	 * Constructor to create an empty node.
	 */
	public GraphNode() {
		this.lon = 0.0;
		this.lat = 0.0;
		this.name = null;
		this.isInDoors = false;
		this.level = 0;
		this.mergeId = null;
		locEdges = new LinkedList<>();
	}
	
	/**
	 * Creates a LatLonPos object to return the location.
	 * 
	 * @return the new LatLonPos object of this node's location
	 */
	public LatLonPos getPos() {
		LatLonPos ret = new LatLonPos();
		ret.setLat(lat);
		ret.setLon(lon);
		ret.setLevel(level);
		return ret;
	}
	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean isIndoors() {
		return isInDoors;
	}
	
	public void setIndoors(boolean indoors) {
		this.isInDoors = indoors;
	}

	public float getLevel() {
		return level;
	}
	
	public void setLevel(float level) {
		this.level = level;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getMergeId() {
		return this.mergeId;
	}
	
	public void setMergeId(String mergeId) {
		this.mergeId = mergeId;
	}

	public int getSteps() {
		return numSteps;
	}
	
	public void setSteps(int numSteps) {
		this.numSteps = numSteps;
	}

	public LinkedList<GraphEdge> getLocEdges() {
		return locEdges;
	}
	
	public boolean equals(GraphNode node) {
		return (node != null && (this.getId() == node.getId()));
	}
	
	public String toString() {
		String ret = "\nNode(" + this.id +"): ";
		ret += name!=null?name:"N/A";
		ret += isInDoors ? " (indoors)" : " (outdoors)";
		ret += "\n    Level: " + this.level;
		ret += "\n    Lat: " + this.lat;
		ret += "\n    Lon: " + this.lon;
		if (getMergeId() != null) {
			ret +="\n    Merges with: " + getMergeId();
		}
		
		return ret;
	}
}
