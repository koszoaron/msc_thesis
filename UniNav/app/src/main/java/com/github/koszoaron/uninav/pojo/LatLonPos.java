package com.github.koszoaron.uninav.pojo;

/**
 * A class to maintain a coordinate consisting of latitude and longitude, with
 * a given level.
 *  
 * TODO merge with {@link Location2}
 *  
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class LatLonPos {
	private double lat;
	private double lon;
	
	private float level;  /* Float.MAX_VALUE == undefined */ 
	
	/** The planet radius in meters. */
	private static final int R = 6378137; 					
	/** Meters per degree. */
	private static final double SCALE = (Math.PI * R)/180.0;
	
	/**
	 * Constructor to create a 0/0/undefined level coordinate.
	 */
	public LatLonPos() {
		this.lat = 0.0;
		this.lon = 0.0;
		this.level = Float.MAX_VALUE;
	}
	
	/**
	 * Constructor to create a coordinate with the specified parameters.
	 * 
	 * @param lat the latitude
	 * @param lon the longitude
	 * @param level the level
	 */
	public LatLonPos(double lat, double lon, float level) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.level = level;
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

	public float getLevel() {
		return level;
	}
	
	public void setLevel(float level) {
		this.level = level;
	}
	
	/**
	 * Calculate the X value of this coordinate in meters using the mercator projection.
	 * 
	 * @return x value in meters
	 */
	public double getMercatorX(){
		/* source: http://mathworld.wolfram.com/MercatorProjection.html */
		
		double x = lon;
		
		/* translate into meters */
		x *= SCALE; 
		return x;
	}
	
	/**
	 * Calculate the Y value of this coordinate in meters using the mercator projection.
	 * 
	 * @return y value in meters
	 */
	public double getMercatorY(){
		/* source: http://mathworld.wolfram.com/MercatorProjection.html */
		
		double y = 0.5 * Math.log((1 + Math.sin(Math.toRadians(lat))) / (1 - Math.sin(Math.toRadians(lat))));
		
		/* radians to degrees */
		y = Math.toDegrees(y);
		/* translate into meters */
		y *= SCALE;
		return y;
	}
	
	public void moveIntoDirection(LatLonPos nextNode, double factor) {
		/* First step: Do Mercator Projection with latitude. */
		lat = lat + (nextNode.lat - lat) * factor;
		lon = lon + (nextNode.lon - lon) * factor;
	}
}
