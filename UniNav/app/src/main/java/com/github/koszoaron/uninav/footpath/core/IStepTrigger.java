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
