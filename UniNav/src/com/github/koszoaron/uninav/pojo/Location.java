package com.github.koszoaron.uninav.pojo;

/**
 * Class describing a location by its latitude, longitude and elevation.
 * 
 * @author aronk
 */
public class Location {
	private double latitude;
	private double longitude;
	private double elevation;
	
	/**
	 * Creates a new instance of the {@link Location} class.<br>
	 * The elevation is unspecified (set to {@link Double#NaN}).
	 * 
	 * @param latitude The latitude in degrees.
	 * @param longitude The longitude in degrees.
	 */
	public Location(double latitude, double longitude) {
		this(latitude, longitude, Double.NaN);
	}
	
	/**
	 * Creates a new instance of the {@link Location} class.
	 * 
	 * @param latitude The latitude in degrees.
	 * @param longitude The longitude in degrees.
	 * @param elevation The elevation above sea level in meters.
	 */
	public Location(double latitude, double longitude, double elevation) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
	}
	
	/**
	 * Returns the latitude in degrees.
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * Returns the longitude in degrees.
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Returns the elevation above sea level in meters.
	 */
	public double getElevation() {
		return elevation;
	}
	
	@Override
	public String toString() {
		return "lat: " + latitude + ", lon: " + longitude + ", elev: " + elevation;
	}
}
