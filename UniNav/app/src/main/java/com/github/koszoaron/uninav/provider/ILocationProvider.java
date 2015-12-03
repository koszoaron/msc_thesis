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

/**
 * Common interface for location provider classes.
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public interface ILocationProvider {
	/**
	 * Returns the last known location detected by the provider.
	 * 
	 * @return An array of three double precision numbers: latitude, longitude and height above sea level (in meters).
	 */
	public Location getLastLoc();
}
