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
