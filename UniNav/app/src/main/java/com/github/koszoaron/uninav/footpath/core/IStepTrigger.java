package com.github.koszoaron.uninav.footpath.core;

/**
 * An interface to be notified about detected steps and their directions. Also
 * there are hooks to to obtain values from sensors.
 * 
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public interface IStepTrigger {
	
	/**
	 * Called each time a step is triggered.
	 * 
	 * @param nowMs The time stamp of the detected step in milliseconds.
	 * @param compDir The compass bearing.
	 */
	public void onStepTriggered(long nowMs, double compDir);
	
	/**
	 * Called each time the accelerometer sensor values change.
	 * 
	 * @param nowMs The time stamp of the changed values
	 * @param x The X axis value.
	 * @param y The Y axis value.
	 * @param z The Z axis value.
	 */
	public void onAccelerometerValueChanged(long nowMs, double x, double y, double z);
	
	/**
	 * Called each time the compass sensor values change.
	 * 
	 * @param nowMs The time stamp of the changed values
	 * @param x The X axis value.
	 * @param y The Y axis value.
	 * @param z The Z axis value.
	 */
	public void onCompassValueChanged(long nowMs, double x, double y, double z);
	
	/**
	 * Called each time a sample is used to detect steps.
	 * 
	 * @param nowMs The time stamp of the sample.
	 * @param acc The accelerometer value (z-axis).
	 * @param comp The compass bearing.
	 */
	public void onTimeSampleDetectionUsed(long nowMs, double[] acc, double[] comp);
}
