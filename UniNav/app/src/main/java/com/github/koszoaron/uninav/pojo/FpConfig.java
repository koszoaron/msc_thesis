package com.github.koszoaron.uninav.pojo;

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
	
	public FpConfig(){
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
