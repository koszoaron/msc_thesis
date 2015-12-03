/**
 * Copyright (C) 2015 Aron Koszo
 *
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
