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
