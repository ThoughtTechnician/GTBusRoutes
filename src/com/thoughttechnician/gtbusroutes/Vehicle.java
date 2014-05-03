package com.thoughttechnician.gtbusroutes;

import org.xmlpull.v1.XmlPullParser;

import com.google.android.gms.maps.model.LatLng;

public class Vehicle {
	int id;
	String routeTag;
	String dirTag;
	LatLng coords;
	int heading;
	public Vehicle(int id, String routeTag, String dirTag, LatLng coords, int heading) {
		this.id = id;
		this.routeTag = routeTag;
		this.dirTag = dirTag;
		this.coords = coords;
		this.heading = heading;
	}
}
