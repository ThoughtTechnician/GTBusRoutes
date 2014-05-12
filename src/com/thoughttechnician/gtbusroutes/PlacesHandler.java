package com.thoughttechnician.gtbusroutes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class PlacesHandler {
	private static String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	private static final LatLng ATLANTA = new LatLng(33.7765,-84.4002);
	
	double latitude;
	double longitude;
	Context context;
	public PlacesHandler(Context context, LatLng origin) {
		this.context = context;
		latitude = origin.latitude;
		longitude = origin.longitude;
	}
	public List<Place> acquirePlaces(String keyword) {
		StringBuilder urlBuilder = new StringBuilder(baseUrl);
		urlBuilder.append("location=");
		urlBuilder.append(latitude);
		urlBuilder.append(",");
		urlBuilder.append(longitude);
		urlBuilder.append("&radius=50000");
		if (keyword != null && !keyword.equals("")) {
			urlBuilder.append("&keyword=" + keyword);
		}
		urlBuilder.append("&sensor=false");
		Log.e("PlacesHandler", "URL is: " + urlBuilder.toString());
		urlBuilder.append("&key=");
		urlBuilder.append(context.getString(R.string.maps_api_key));
		StringBuilder jsonBuilder = new StringBuilder();
		try {
			URL url = new URL(urlBuilder.toString());
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setReadTimeout(10000);
			connection.setConnectTimeout(15000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()),8);
			String line;
			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line + "\n");
			}
			reader.close();
		} catch (Exception e) {
			Log.e("PlacesHandler", "Could not connect to places server");
			e.printStackTrace();
		}
		List<Place> list = new ArrayList<Place>();
		try {
			JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			
			for (int i = 0; i < jsonArray.length(); i++) {
				Place currPlace = new Place((JSONObject) jsonArray.get(i));
				Log.v("PlacesHandler", "" + currPlace);
				list.add(currPlace);
			}
		} catch (Exception e) {
			Log.e("PlacesHandler", "Could do the json business");
		}
		return list;
	}
}
