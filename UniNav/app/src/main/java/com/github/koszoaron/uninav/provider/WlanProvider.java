package com.github.koszoaron.uninav.provider;

import com.github.koszoaron.uninav.pojo.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom location provider using WLAN networks.
 *
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class WlanProvider implements ILocationProvider {

    private static WlanProvider INSTANCE = null;

    private Location lastLocation = new Location(Double.NaN, Double.NaN);
    private Map<String, Location> locationsList;

    private WlanProvider() {
        locationsList = new HashMap<>();

        //TODO load the wlan locations data into the list;
    }

    /**
     * Returns an initialized instance of the {@link WlanProvider} class.
     */
    public static WlanProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WlanProvider();
        }

        return INSTANCE;
    }

    @Override
    public Location getLastLoc() {
        return lastLocation;
    }

    /**
     * Handles the detection of a new WLAN network.
     *
     * @param bssid The BSSID of the WLAN.
     */
    public void onWlanNetworkDetected(String bssid) {
        if (bssid != null) {
            Location wlanLoc = locationsList.get(bssid);
            if (wlanLoc != null) {
                lastLocation = wlanLoc;
            }
        }
    }

}
