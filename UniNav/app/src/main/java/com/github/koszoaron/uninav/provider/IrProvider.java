package com.github.koszoaron.uninav.provider;

import com.github.koszoaron.uninav.pojo.IrMessage;
import com.github.koszoaron.uninav.pojo.Location2;

import android.util.SparseArray;

/**
 * Custom location provider using infrared signals.
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class IrProvider implements ILocationProvider {
	
	private static IrProvider INSTANCE = null;
	
	private Location2 lastLocation = new Location2(Double.NaN, Double.NaN);
	private SparseArray<Location2> irLocationsList;
	
	private IrProvider() {
		irLocationsList = new SparseArray<Location2>();
		
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
	public double[] getLastLoc() {
		double[] res = { lastLocation.getLatitude(), lastLocation.getLongitude(), lastLocation.getElevation() };
		
		return res;
	}
	
	/**
	 * Handles the arrival of an IR message.
	 * 
	 * @param msg The {@link IrMessage} last arrived.
	 */
	public void onIrMessageArrival(IrMessage msg) {
		if (msg != null) {
			Location2 irLoc = irLocationsList.get(msg.getCode());
			if (irLoc != null) {
				lastLocation = irLoc;
			}
		}
	}
}
