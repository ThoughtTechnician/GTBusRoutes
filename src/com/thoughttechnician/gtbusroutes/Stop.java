package com.thoughttechnician.gtbusroutes;

import com.google.android.gms.maps.model.LatLng;

public class Stop {
	private LatLng loc = null;
	private String stopId = "";
	private String tag = null;
	private String title = null;
	public Stop(LatLng loc, String stopId, String tag, String title) {
		this.loc = loc;
		this.stopId = stopId;
		this.tag = tag;
		this.title = title;
	}
	public String getTag() {
		return tag;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return "Stop: " + title;
	}
	@Override
	public boolean equals(Object other) {
		Stop otherStop = (Stop) other;
		return tag.equals(otherStop.getTag());
	}
	@Override
	public int hashCode() {
		char[] buffer = new char[tag.length()];
		tag.getChars(0, tag.length(),buffer, 0);
		int code = 0;
		for (int i = 0; i < buffer.length; i++) {
			code += buffer[i] * Math.pow(10, 2 * i);
		}
		return code;
	}
}