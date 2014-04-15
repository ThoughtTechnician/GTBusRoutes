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
}