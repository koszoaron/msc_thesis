package com.github.koszoaron.uninav.provider;

import com.github.koszoaron.uninav.pojo.IrMessage;
import com.github.koszoaron.uninav.pojo.Location;

import android.util.SparseArray;

/**
 * Custom location provider using infrared signals.
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class IrProvider implements ILocationProvider {
	
	private static IrProvider INSTANCE = null;
	
	private Location lastLocation = new Location(Double.NaN, Double.NaN);
	private SparseArray<Location> locationsList;
	
	private IrProvider() {
		locationsList = new SparseArray<Location>();
		
		//TODO load the ir locations data into the list;
	}
	
	/**
	 * Returns an initialized instance of the {@link IrProvider} class.
	 */
	public static IrProvider getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new IrProvider();
		}
		
		return INSTANCE;
	}
	
	@Override
	public Location getLastLoc() {
		return lastLocation;
	}
	
	/**
	 * Handles the arrival of an IR message.
	 * 
	 * @param msg The {@link IrMessage} last arrived.
	 */
	public void onIrMessageArrival(IrMessage msg) {
		if (msg != null) {
			Location irLoc = locationsList.get(msg.getCode());
			if (irLoc != null) {
				lastLocation = irLoc;
			}
		}
	}
}
