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

package com.github.koszoaron.uninav.pojo;

/**
 * Class describing a message coming via IR signals
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class IrMessage {
	private int code;
	private int extraCode;
	
	/**
	 * Creates a new instance of the {@link IrMessage} class.
	 * 
	 * @param code The infrared message code.
	 */
	public IrMessage(int code) {
		this(code, 0);
	}
	
	/**
	 * Creates a new instance of the {@link IrMessage} class.
	 * 
	 * @param code The infrared message code.
	 * @param extraCode The extra message code.
	 */
	public IrMessage(int code, int extraCode) {
		this.code = code;
		this.extraCode = extraCode;
	}
	
	/**
	 * Returns the IR message code.
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Returns the extra message code.
	 */
	public int getExtraCode() {
		return extraCode;
	}
	
	@Override
	public String toString() {
		return "ir code: " + code + ", extra code: " + extraCode;
	}
}