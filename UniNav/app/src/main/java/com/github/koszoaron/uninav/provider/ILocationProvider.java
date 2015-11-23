package com.github.koszoaron.uninav.provider;

/**
 * Common interface for location provider classes.
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public interface ILocationProvider {
	/**
	 * Returns the last known location detected by the provider.
	 * 
	 * @return An array of three double precision numbers: latitude, longitude and height above sea level (in meters).
	 */
	public double[] getLastLoc();
}