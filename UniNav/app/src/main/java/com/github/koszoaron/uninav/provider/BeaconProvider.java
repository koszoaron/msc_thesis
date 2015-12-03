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

import com.github.koszoaron.uninav.pojo.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom location provider using Bluetooth beacons.
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class BeaconProvider implements ILocationProvider {

	private static BeaconProvider INSTANCE = null;

	private Location lastLocation = new Location(Double.NaN, Double.NaN);
	private Map<String, Location> locationsList;

	private BeaconProvider() {
		locationsList = new HashMap<>();

		//TODO load the BT beacons location data into the list;
	}

	/**
	 * Returns an initialized instance of the {@link BeaconProvider} class.
	 */
	public static BeaconProvider getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BeaconProvider();
		}

		return INSTANCE;
	}

	@Override
	public Location getLastLoc() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Handles the detection of a new beacon network.
	 *
	 * @param uuid The unique identifier of the beacon.
	 */
	public void onBeaconDetected(String uuid) {
		if (uuid != null) {
			//TODO check if uuid is in 1E6 format and retrieve the coordinate if possible

			Location beaconLoc = locationsList.get(uuid);
			if (beaconLoc != null) {
				lastLocation = beaconLoc;
			}
		}
	}

}
