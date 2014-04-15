package com.thoughttechnician.gtbusroutes;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class Route {
	private LatLng maxLatLng = null;
	private LatLng minLatLng = null;
	private String oppositeColor = null;
	private String tag = "";
	private String title = "";
	private String color = null;
	private List<Stop> stopList = null;
	private List<Direction> directionList = null;
	private List<List<LatLng>> pathList = null;
	public Route(LatLng maxLatLng, LatLng minLatLng, String oppositeColor,
			String tag, String title, String color, List<Stop> stopList,
			List<Direction> directionList, List<List<LatLng>> pathList){
		this.maxLatLng = maxLatLng;
		this.minLatLng = minLatLng;
		this.oppositeColor = oppositeColor;
		this.tag = tag;
		this.title = title;
		this.color = "#" + color;
		this.stopList = stopList;
		this.directionList = directionList;
		this.pathList = pathList;
	}
	public String getColor() {
		return color;
	}
	public List<List<LatLng>> getPaths() {
		return pathList;
	}
	public List<Stop> getStops() {
		return stopList;
	}
}