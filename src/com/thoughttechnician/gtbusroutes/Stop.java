package com.thoughttechnician.gtbusroutes;

import com.google.android.gms.maps.model.LatLng;

public class Stop {
	LatLng loc = null;
	String stopId = "";
	String tag = null;
	String title = null;
	public Stop(LatLng loc, String stopId, String tag, String title) {
		this.loc = loc;
		this.stopId = stopId;
		this.tag = tag;
		this.title = title;
	}
}