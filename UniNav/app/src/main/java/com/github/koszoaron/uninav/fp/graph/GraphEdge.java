package com.github.koszoaron.uninav.fp.graph;

/**
 * A class to represent an edge in the map/graph.
 * 
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class GraphEdge {
	private GraphNode node0;
	private GraphNode node1;
	private double length;
	private double bearing;
	private boolean isStairs = false;
	private boolean isElevator = false;
	
	/* >0 := number correct steps given
	 *  0 := no steps
	 * -1 := undefined number of steps
	 * -2 := elevator */
	private int numSteps = 0;
	
	private float level;
	private boolean isIndoor;
	
	/**
	 * Constructor to create an empty edge.
	 */
	public GraphEdge() {
		this.node0 = null;
		this.node1 = null;
		this.length = 0.0;
		this.level = Float.MAX_VALUE;
		this.isIndoor = false;
	}
	
	/**
	 * Constructor to create an edge with the specified parameters.
	 * 
	 * @param node0 The first node of the graph.
	 * @param node1 The second node of the graph.
	 * @param length The length of the edge.
	 * @param compassDir The direction of the edge (from node0 towards node1).
	 * @param level The level of the edge.
	 * @param isIndoor Whether is the edge indoors.
	 */
	public GraphEdge(GraphNode node0, GraphNode node1, double length, double compassDir, float level, boolean isIndoor) {
		this.node0 = node0;
		this.node1 = node1;
		this.length = length;
		this.bearing = compassDir;
		this.level = level;
		this.isIndoor = isIndoor;
	}
	
	public GraphNode getNode0() {
		return node0;
	}
	
	public void setNode0(GraphNode node0) {
		this.node0 = node0;
	}

	public GraphNode getNode1() {
		return node1;
	}
	
	public void setNode1(GraphNode node1) {
		this.node1 = node1;
	}

	public double getLength() {
		return length;
	}
	
	public void setLength(double length) {
		this.length = length;
	}

	public double getCompDir() {
		return bearing;
	}

	public void setCompDir(double compDir) {
		this.bearing = compDir;
	}

	public float getLevel() {
		return level;
	}

	public void setLevel(float level) {
		this.level = level;
	}

	public boolean isIndoor() {
		return isIndoor;
	}

	public void setIndoor(boolean isIndoor) {
		this.isIndoor = isIndoor;
	}

	public boolean isStairs() {
		return isStairs;
	}
	
	public void setStairs(boolean isStairs) {
		this.isStairs = isStairs;
	}

	public boolean isElevator() {
		return isElevator;
	}
	
	public void setElevator(boolean isElevator) {
		this.isElevator = isElevator;
	}

	public int getSteps() {
		return numSteps;
	}
	
	public void setSteps(int numSteps) {
		this.numSteps = numSteps;
	}
	
	public boolean equals(GraphEdge edge) {
		if (edge == null) {
			return false;
		}
		
		return (this.node0.equals(edge.getNode0()) && this.node1.equals(edge.getNode1())
				|| this.node0.equals(edge.getNode1()) && this.node1.equals(edge.getNode0()));
	}
	
	public boolean contains(GraphNode node) {
		return (getNode0().equals(node) || getNode1().equals(node));
	}
	
	public String toString() {
		String ret = "\nEdge(" + this.node0.getId() + " to " + this.node1.getId() + "): ";
		ret += "\n    Length: " + this.length;
		ret += "\n    Bearing: " + this.bearing;
		if (isStairs()) {
			ret += "\n    Staircase with: " + this.getSteps() + " steps";
		}
		if (isElevator()) {
			ret += "\n    Elevator: yes";
		}
		ret+="\n    Level: " + level;
		
		return ret;
	}
}
