/*
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

package com.github.koszoaron.uninav;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;

/**
 * 
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class Util {
	private static final String DEFAULT_TAG = "UNINAV";
	private static final float DEFAULT_STROKE_WIDTH = 2.0f;
	
	/**
	 * Private constructor.
	 */
	private Util() {}
	
	/**
	 * Puts a V type log message into the logcat.
	 * 
	 * @param tag The log tag.
	 * @param message The message to log.
	 */
	public static void logv(String tag, String message) {
		Log.v(tag, "V - " + message);
	}
	
	/**
	 * Puts a V type log message into the logcat.
	 * 
	 * @param message The message to log.
	 */
	public static void logv(String message) {
		logv(DEFAULT_TAG, message);
	}
	
	/**
	 * Puts an I type log message into the logcat.
	 * 
	 * @param tag The log tag.
	 * @param message The message to log.
	 */
	public static void logi(String tag, String message) {
		Log.i(tag, "I - " + message);
	}
	
	/**
	 * Puts an I type log message into the logcat.
	 * 
	 * @param message The message to log.
	 */
	public static void logi(String message) {
		logi(DEFAULT_TAG, message);
	}
	
	/**
	 * Puts a D type log message into the logcat.
	 * 
	 * @param tag The log tag.
	 * @param message The message to log.
	 */
	public static void logd(String tag, String message) {
		Log.d(tag, "D - " + message);
	}
	
	/**
	 * Puts a D type log message into the logcat.
	 * 
	 * @param message The message to log.
	 */
	public static void logd(String message) {
		logd(DEFAULT_TAG, message);
	}
	
	/**
	 * Puts a W type log message into the logcat.
	 * 
	 * @param tag The log tag.
	 * @param message The message to log.
	 */
	public static void logw(String tag, String message) {
		Log.w(tag, "W - " + message);
	}
	
	/**
	 * Puts a W type log message into the logcat.
	 * 
	 * @param message The message to log.
	 */
	public static void logw(String message) {
		logw(DEFAULT_TAG, message);
	}
	
	/**
	 * Puts an E type log message into the logcat.
	 * 
	 * @param tag The log tag.
	 * @param message The message to log.
	 */
	public static void loge(String tag, String message) {
		Log.e(tag, "E - " + message);
	}
	
	/**
	 * Puts an E type log message into the logcat.
	 * 
	 * @param message The message to log.
	 */
	public static void loge(String message) {
		loge(DEFAULT_TAG, message);
	}
	
	/**
	 * Puts an R type log message into the logcat.
	 * 
	 * @param tag The log tag.
	 * @param message The message to log.
	 */
	public static void logr(String tag, String message) {
		Log.e(tag, "R - " + message);
	}
	
	/**
	 * Puts an R type log message into the logcat.
	 * 
	 * @param message The message to log.
	 */
	public static void logr(String message) {
		logr(DEFAULT_TAG, message);
	}
	
	public static Paint redPaint() {
		Paint p = new Paint();
		p.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		p.setStyle(Style.FILL);
		p.setColor(Color.RED);
		return p;
	}

	public static Paint redPaint(float textSize) {
		Paint p = new Paint();
		p.setTextSize(textSize);
		p.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		p.setStyle(Style.FILL);
		p.setColor(Color.RED);
		return p;
	}

	public static Paint greenPaint() {
		Paint p = new Paint();
		p.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		p.setStyle(Style.FILL);
		p.setColor(Color.GREEN);
		return p;
	}

	public static Paint greenPaint(float textSize) {
		Paint p = new Paint();
		p.setTextSize(textSize);
		p.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		p.setStyle(Style.FILL);
		p.setColor(Color.GREEN);
		return p;
	}

	public static Paint bluePaint() {
		Paint p = new Paint();
		p.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		p.setStyle(Style.FILL);
		p.setColor(Color.BLUE);
		return p;
	}
	
	public static Paint transparentBluePaint() {
		Paint p = new Paint();
		p.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		p.setStyle(Style.STROKE);
		p.setColor(Color.BLUE);
		return p;
	}

	public static Paint bluePaint(float textSize) {
		Paint p = new Paint();
		p.setTextSize(textSize);
		p.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		p.setStyle(Style.FILL);
		p.setColor(Color.BLUE);
		return p;
	}
	

	public static Paint myPaint(int strokeWidth, int color) {
		Paint p = new Paint();
		p.setStrokeWidth(strokeWidth);
		p.setStyle(Style.FILL);
		p.setColor(color);
		return p;
	}
	
	public static Paint myPaint(int strokeWidth, int color, int alpha) {
		Paint p = new Paint();
		p.setStrokeWidth(strokeWidth);
		p.setStyle(Style.FILL);
		p.setColor(color);
		p.setAlpha(alpha);
		return p;
	}
	
	/**
	 * Creates a normal double array from an ArrayList<Double>.
	 * 
	 * @param list The ArrayList<Double> to convert.
	 * @return An array of doubles made out of the array list.
	 */
	public static double[] arrayListToArrayDouble(ArrayList<Double> list) {
		double[] res = new double[list.size()];
		
		for (int i = 0; i < list.size(); i++) {
			res[i] = list.get(i).doubleValue();
		}

		return res;
	}

	/**
	 * Creates an integer array from an ArrayList<Integer>.
	 * 
	 * @param list The ArrayList<Integer> to convert.
	 * @return An array of integers made out of the array list.
	 */
	public static int[] arrayListToArrayInt(ArrayList<Integer> list) {
		int[] res = new int[list.size()];
		
		for (int i = 0; i < list.size(); i++) {
			res[i] = list.get(i).intValue();
		}

		return res;
	}

	/**
	 * Precision to two decimal places behind the period.
	 * @param d
	 * @return
	 */
	public static double tdp(double d) {
		return ((int) (d * 100)) / 100.0;
	}

	public static double[] arrayClone(double[] array) {
		double[] buf = new double[array.length];
		
		for (int i = 0; i < array.length; i++) {
			buf[i] = array[i];
		}
		return buf;
	}
	
	public static double lowpassFilter(double oldValue, double newValue, double coefficient) {
		return oldValue + coefficient * (newValue - oldValue);
	}
}
