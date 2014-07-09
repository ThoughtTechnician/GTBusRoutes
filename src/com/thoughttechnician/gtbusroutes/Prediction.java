package com.thoughttechnician.gtbusroutes;

public class Prediction {
	
	private int minutes;
	private int seconds;
	private int vehicle;
	
	public Prediction(int minutes, int seconds, int vehicle) {
		this.minutes = minutes;
		this.seconds = seconds;
		this.vehicle = vehicle;
	}
	public int getMinutes() {
		return minutes;
	}
	public int getSeconds() {
		return seconds;
	}
	public int getVehicle() {
		return vehicle;
	}
}
