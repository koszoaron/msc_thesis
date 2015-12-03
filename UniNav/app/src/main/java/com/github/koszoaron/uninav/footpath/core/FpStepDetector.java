/**
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

package com.github.koszoaron.uninav.footpath.core;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.github.koszoaron.uninav.Util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * This class is fed with data from the Accelerometer and Compass sensors. If a step is detected on the 
 * accelerometer data it calls the trigger function on its interface StepTrigger with the given direction.
 * 
 * Usage:
 * Create an object: stepDetection = new StepDetection(this, this, a, peak, step_timeout_ms);
 * 
 * @author Paul Smith
 * @author Aron Koszo <koszoaron@gmail.com>
 *
 */
public class FpStepDetector {
	public static final long INTERVAL_MS = 1000/30;
	
	/** Hold an interface to notify the outside world of detected steps */
	private IStepTrigger stepTrigger;
	/** Context needed to get access to sensor service */
	private Context context;
	/** Holds a reference to the SensorManager */
	private SensorManager sensorManager;
	/** A list of all sensors */
	private List<Sensor> sensorsList;		

	private static final int VALUES_HISTORY_SIZE = 6;
	private double[] valuesHistory = new double[VALUES_HISTORY_SIZE];
	private int valuesHistoryPointer = 0;
	
	private double a;
	private double peak;
	private int stepTimeoutMs;
	private long lastStepTimestamp = 0;
	
	/* last acc is low pass filtered */
	private double[] lastAcc = new double[] {0.0, 0.0, 0.0};
	/* last comp is untouched */
	private double[] lastComp = new double[] {0.0, 0.0, 0.0};
	
	private int round = 0;
	
	private Timer timer;
	
	/** Handles sensor events and updates the sensor */
	public SensorEventListener mySensorEventListener = new SensorEventListener() {
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@SuppressWarnings("deprecation")
		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
				case Sensor.TYPE_ACCELEROMETER:
					stepTrigger.onAccelerometerValueChanged(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]);
					
					/* just update the oldest Z value */
					lastAcc[0] = Util.lowpassFilter(lastAcc[0], event.values[0], a);
					lastAcc[1] = Util.lowpassFilter(lastAcc[1], event.values[1], a);
					lastAcc[2] = Util.lowpassFilter(lastAcc[2], event.values[2], a);
					break;
				case Sensor.TYPE_ORIENTATION:
					stepTrigger.onCompassValueChanged(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]);
					
					lastComp[0] = event.values[0];
					lastComp[1] = event.values[1];
					lastComp[2] = event.values[2];
					break;
				default:
			}
		}
		
	};
	
	public FpStepDetector(Context context, IStepTrigger stepTrigger, double a, double peak, int stepTimeoutMs) {
		this.context = context;
		this.stepTrigger = stepTrigger;
		this.a = a;
		this.peak = peak;
		this.stepTimeoutMs = stepTimeoutMs;
	}
	
	public double getA() {
		return a;
	}

	public double getPeak() {
		return peak;
	}

	public int getStepTimeout() {
		return stepTimeoutMs;
	}

	public void setA(double a) {
		this.a = a;
	}

	public void setPeak(double peak) {
		this.peak = peak;
	}

	public void setStepTimeout(int stepTimeoutMs) {
		this.stepTimeoutMs = stepTimeoutMs;
	}

	/**
	 * Enables step detection.
	 */
	@SuppressWarnings("deprecation")
	public void enableStepDetection() {
		/* sensors */
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensorsList = sensorManager.getSensorList(Sensor.TYPE_ALL);

		for (int i = 0; i < sensorsList.size(); i++) {
			/* register only the compass and the accelerometer */
			if (sensorsList.get(i).getType() == Sensor.TYPE_ACCELEROMETER || sensorsList.get(i).getType() == Sensor.TYPE_ORIENTATION) {
				sensorManager.registerListener(mySensorEventListener, sensorsList.get(i), SensorManager.SENSOR_DELAY_FASTEST);
			}
		}
		
		/* register a timer */
		timer = new Timer("UpdateData", false);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateData();
			}
		}, 0, INTERVAL_MS);
	}
	
	/**
	 * Disables step detection.
	 */
	public void disableStepDetection() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		
		if (sensorManager != null) {
			sensorManager.unregisterListener(mySensorEventListener);
		}
	}
			
	/**
	 * This is called every INTERVAL_MS ms from the TimerTask. 
	 */
	private void updateData() {
		/* get the current time for time stamps */
		long nowMs = System.currentTimeMillis();
		
		/* create local value for compass and old Z, such that it is consistent during logs
		 * (it might change in between, which is circumvented by this) */
		double[] oldAcc = new double[3];
		System.arraycopy(lastAcc, 0, oldAcc, 0, 3);
		double[] oldComp = new double[3];
		System.arraycopy(lastComp, 0, oldComp, 0, 3);
		double compass = oldComp[0];
		double oldZ = oldAcc[2];
		stepTrigger.onTimeSampleDetectionUsed(nowMs, oldAcc, oldComp);
		
		/* add the old Z to the values history */
		addData(oldZ);
		
		/* check if a step is detected upon data */
		if ((nowMs - lastStepTimestamp) > stepTimeoutMs && checkForStep(peak)) {
			/* set latest detected step to now */
			lastStepTimestamp = nowMs;
			/* call the algorithm for navigation/position update */
			stepTrigger.onStepTriggered(nowMs, compass);
			
			Util.logi("Detected step in round " + round  + " at " + nowMs + "ms");
		}
		
		/* increase the round counter */
		round++;
	}

	private void addData(double value) {
		valuesHistory[valuesHistoryPointer % VALUES_HISTORY_SIZE] = value;
		valuesHistoryPointer++;
		valuesHistoryPointer = valuesHistoryPointer % VALUES_HISTORY_SIZE;
	}
	
	private boolean checkForStep(double peakSize) {
		int lookahead = 5;
		double diff = peakSize;
		
		for (int t = 1; t <= lookahead; t++) {
			if ((valuesHistory[(valuesHistoryPointer - 1 - t + VALUES_HISTORY_SIZE + VALUES_HISTORY_SIZE) % VALUES_HISTORY_SIZE] - 
					valuesHistory[(valuesHistoryPointer - 1 + VALUES_HISTORY_SIZE) % VALUES_HISTORY_SIZE] > diff)) {
				
				Util.logi("Detected step with t = " + t + ", diff = " + diff + " < " + (valuesHistory[(valuesHistoryPointer - 1 - t + VALUES_HISTORY_SIZE + VALUES_HISTORY_SIZE) % VALUES_HISTORY_SIZE] - valuesHistory[(valuesHistoryPointer - 1 + VALUES_HISTORY_SIZE) % VALUES_HISTORY_SIZE]));
				return true;
			}
		}
		return false;
	}
}
