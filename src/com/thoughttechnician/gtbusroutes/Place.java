package com.thoughttechnician.gtbusroutes;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class Place {
	private static final String PLACE_ID = "place";
	private static final String PLACE_NAME = "name";
	private static final String PLACE_LAT = "lat";
	private static final String PLACE_LON = "lon";
	
	private String id;
	private String name;
	private double lat;
	private double lon;
	public Place(String id, String name, double lat, double lon) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}
	public Place(JSONObject jsonObject) throws JSONException {
		id = jsonObject.getString(PLACE_ID);
		name = jsonObject.getString(PLACE_NAME);
		lat = jsonObject.getDouble(PLACE_LAT);
		lon = jsonObject.getDouble(PLACE_LON);
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public double getLatitude() {
		return lat;
	}
	public double getLongitude() {
		return lon;
	}
	public LatLng getLocation() {
		return new LatLng(lat, lon);
	}
}
