package com.thoughttechnician.gtbusroutes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Color;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;

import com.google.android.gms.maps.model.LatLng;

public class XMLHandler {
	private static final String ns = null;
	private static final String TAG = "XMLHandler";
	
	public List<Route> parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}
	private List<Route> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<Route> routes = new ArrayList<Route>();
		parser.require(XmlPullParser.START_TAG, ns, "body");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("route")) {
				routes.add(readRoute(parser));
			} else {
				skip(parser);
			}
			
		}
		return routes;
	}
	private Route readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG,  ns,  "route");
		List<Stop> stopList = new ArrayList<Stop>();
		List<Direction> directionList = new ArrayList<Direction>();
		List<List<LatLng>> pathList = new ArrayList<List<LatLng>>();
		LatLng maxLatLng = new LatLng(Double.parseDouble(parser.getAttributeValue(ns, "latMax")),
				Double.parseDouble(parser.getAttributeValue(ns, "lonMax")));
		LatLng minLatLng = new LatLng(Double.parseDouble(parser.getAttributeValue(ns, "latMin")),
				Double.parseDouble(parser.getAttributeValue(ns, "lonMin")));
		String oppositeColor = parser.getAttributeValue(ns, "oppositeColor");
		String tag = parser.getAttributeValue(ns, "tag");
		String title = parser.getAttributeValue(ns, "title");
		String color = parser.getAttributeValue(ns, "color");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("stop")) {
				stopList.add(readStop(parser));
			} else if (name.equals("direction")) {
				directionList.add(readDirection(parser));
			} else if (name.equals("path")) {
				pathList.add(readPath(parser));
			} else {
				skip(parser);
			}
		}

		Log.d(TAG, "Parsed route: " + tag);
		return new Route(maxLatLng, minLatLng, oppositeColor, tag, title, color, stopList, directionList, pathList);
	}
	private Stop readStop(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "stop");
		double lat = Double.parseDouble(parser.getAttributeValue(ns, "lat"));
		double lon = Double.parseDouble(parser.getAttributeValue(ns, "lon"));
		String stopId = parser.getAttributeValue(ns, "stopId");
		String tag = parser.getAttributeValue(ns, "tag");
		String title = parser.getAttributeValue(ns, "title");
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, ns, "stop");
		return new Stop(new LatLng(lat, lon), stopId, tag, title);
	}
	private Direction readDirection(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "direction");
		String tag = parser.getAttributeValue(ns, "tag");
		String title = parser.getAttributeValue(ns, "title");
		List<String> stopTags = new ArrayList<String>();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("stop")) {
				stopTags.add(parser.getAttributeValue(ns, "tag"));
				parser.nextTag();
			} else {
				skip(parser);
			}
		}


		parser.require(XmlPullParser.END_TAG, ns, "direction");

		return new Direction(tag, title, stopTags);
	}
	private List<LatLng> readPath(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "path");
		List<LatLng> list = new ArrayList<LatLng>();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("point")) {
				list.add(readPoint(parser));
			} else {
				skip(parser);
			}
		}

		parser.require(XmlPullParser.END_TAG, ns, "path");
		return list;
	}
	private LatLng readPoint(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "point");
		double lat = Double.parseDouble(parser.getAttributeValue(ns, "lat"));
		double lon = Double.parseDouble(parser.getAttributeValue(ns, "lon"));
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, ns, "point");
		return new LatLng(lat,lon);
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }


}
