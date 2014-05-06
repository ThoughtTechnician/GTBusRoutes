package com.thoughttechnician.gtbusroutes;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Vehicle {
	int id;
	String routeTag;
	String dirTag;
	LatLng coords;
	int heading;
	Marker marker;
	public Vehicle(int id, String routeTag, String dirTag, LatLng coords, int heading) {
		this.id = id;
		this.routeTag = routeTag;
		this.dirTag = dirTag;
		this.coords = coords;
		this.heading = heading;
		marker = null;
	}
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof Vehicle)) {
			return false;
		}
		Vehicle otherVehicle = (Vehicle) other;
		return id == otherVehicle.id;
	}
	@Override
	public int hashCode() {
		return Integer.parseInt(id + "" + Math.round(Math.abs(coords.latitude)) + "" + Math.round(Math.abs(coords.longitude)));
	}
	public void setVisible(boolean visible) {
		marker.setVisible(visible);
	}
}
