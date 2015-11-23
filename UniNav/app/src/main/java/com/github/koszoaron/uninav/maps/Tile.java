package com.github.koszoaron.uninav.maps;

import com.github.koszoaron.uninav.pojo.LatLonPos;

import android.graphics.Bitmap;

/**
 * Online OpenStreetMaps tile.
 * 
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class Tile {
	
	int zoomlevel;
	int x;
	int y;
	Bitmap bitmap;
	
	public Tile(int zoomlevel, int x, int y, Bitmap bitmap) {
		this.zoomlevel = zoomlevel;
		this.x = x;
		this.y = y;
		this.bitmap = bitmap;
	}
	
	public int getZoomlevel() {
		return zoomlevel;
	}
	
	public void setZoomlevel(int zoomlevel) {
		this.zoomlevel = zoomlevel;
	}

	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public LatLonPos getLatLonPosLeftTop(){
		/* source: http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames */
		
		double lon = 0.0;
		double lat = 0.0;
		double n = Math.pow(2, zoomlevel);
		double latRad = Math.atan(Math.sinh(Math.PI*(1.0-2.0*y/n)));
		
		lon = x/n*360.0-180.0;
		lat = latRad * 180.0/Math.PI;
		
		return new LatLonPos(lat,lon,-1337);
	}
	
	public LatLonPos getLatLonPosRightBottom(){
		/* source: http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames */
		
		double lon = 0.0;
		double lat = 0.0;
		double n = Math.pow(2, zoomlevel);
		double latRad = Math.atan(Math.sinh(Math.PI*(1.0-2.0*(y+1)/n)));
		
		lon = (x+1)/n*360.0-180.0;
		lat = latRad * 180.0/Math.PI;
		
		return new LatLonPos(lat,lon,-1337);
	}
}