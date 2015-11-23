package com.github.koszoaron.uninav.fp.graph;

import java.util.LinkedList;

/**
 * A class to represent a way in the map/graph.
 * 
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class GraphWay {
	
	/** All nodes on this path (ref0 -> ref1 -> ref2  -> ...) */
	private LinkedList<Long> refs;
	private int id;

	/* >0 := number correct steps given
	 *  0 := no steps
	 * -1 := undefined number of steps
	 * -2 := elevator */
	private int numSteps = 0;	
	
	private float level;  /* Float.MAX_VALUE == undefined */
	private boolean isIndoor;
	private boolean footway = false;
	private boolean wall = false;
	
	/**
	 * Constructor to create an empty way.
	 */
	public GraphWay() {
		this.refs = new LinkedList<>();
		this.id = 0;
		this.level = Float.MAX_VALUE;
	}
	
	public LinkedList<Long> getRefs() {
		return refs;
	}

	public void addRef(long ref) {
		this.refs.add(ref);
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public float getLevel() {
		return level;
	}
	
	public void setLevel(float level) {
		this.level = level;
	}

	public int getSteps() {
		return numSteps;
	}

	public void setSteps(int numSteps) {
		this.numSteps = numSteps;
	}

	public boolean isIndoor() {
		return isIndoor;
	}
	
	public void setIndoor(boolean isIndoor) {
		this.isIndoor = isIndoor;
	}
	
	public boolean isFootway() {
		return this.footway;
	}
	
	public void setFootway(boolean footway) {
		this.footway = footway;
	}
	
	public boolean isWall() {
		return this.wall;
	}
	
	public void setWall(boolean wall) {
		this.wall = wall;
	}
	
	public String toString(){
		String ret = "\nWay(" + this.id +"): ";
		ret += "\nRefs:";
		for (Long ref : refs) {
			ret += "\n    " + ref.intValue();
		}
		
		return ret;
	}
}
