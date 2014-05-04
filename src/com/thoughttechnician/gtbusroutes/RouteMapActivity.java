package com.thoughttechnician.gtbusroutes;

import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteMapActivity extends ActionBarActivity {
	private static final LatLng ATLANTA = new LatLng(33.7765,-84.4002);
	private static final String TAG = "RouteMapActivity";
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private XMLHandler xmlHandler = null;
	private List<Route> routes = null;
	private List<Vehicle> vehicles = null;

	private Polyline[] redLines = null;
	private Polyline[] blueLines = null;
	private Polyline[] greenLines = null;
	private Polyline[] emoryLines = null;
	private Polyline[] trolleyLines = null;
	private Polyline[] ramblerLines = null;
	
	private CheckBox redCheckBox = null;
	private CheckBox blueCheckBox = null;
	private CheckBox greenCheckBox = null;
	private CheckBox emoryCheckBox = null;
	private CheckBox trolleyCheckBox = null;
	private CheckBox ramblerCheckBox = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.container);
		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				//getSupportActionBar().setTitle(title);
			}
			public void onDrawerOpened(View view) {
				super.onDrawerOpened(view);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment mapFragment = fm.findFragmentById(R.id.mapFragmentContainer);
		if (mapFragment == null) {
			GoogleMapOptions options = new GoogleMapOptions();
			options.mapType(GoogleMap.MAP_TYPE_TERRAIN);
			options.compassEnabled(true);
			mapFragment = SupportMapFragment.newInstance(options);
			fm.beginTransaction().add(R.id.mapFragmentContainer, mapFragment).commit();
		}
		GoogleMap map = ((SupportMapFragment)mapFragment).getMap();
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(ATLANTA, 14));
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				Log.d(TAG, "LatLng: " + arg0);

			}
		});
		redCheckBox = (CheckBox) findViewById(R.id.checkbox_red);
		blueCheckBox = (CheckBox) findViewById(R.id.checkbox_blue);
		greenCheckBox = (CheckBox) findViewById(R.id.checkbox_green);
		emoryCheckBox = (CheckBox) findViewById(R.id.checkbox_emory);
		trolleyCheckBox = (CheckBox) findViewById(R.id.checkbox_trolley);
		ramblerCheckBox = (CheckBox) findViewById(R.id.checkbox_rambler);
		
		List<List<LatLng>> redPaths = null;
		List<List<LatLng>> bluePaths = null;
		List<List<LatLng>> greenPaths = null;
		List<List<LatLng>> emoryPaths = null;
		List<List<LatLng>> trolleyPaths = null;
		List<List<LatLng>> ramblerPaths = null;
		try {
			if (routes == null) {
				xmlHandler = new XMLHandler();
				routes = xmlHandler.parseRouteConfig(new BufferedInputStream(getResources().openRawResource(R.raw.my_route_config)));
				vehicles = xmlHandler.parseVehicleLocation(new BufferedInputStream(getResources().openRawResource(R.raw.vehicle_location)));
			}

		} catch (Exception e) {
			Log.e(TAG, "Couldn't Do it!");
			e.printStackTrace();
		}
		redPaths = routes.get(0).getPaths();
		bluePaths = routes.get(1).getPaths();
		greenPaths = routes.get(2).getPaths();
		trolleyPaths = routes.get(3).getPaths();
		emoryPaths = routes.get(4).getPaths();
		ramblerPaths = routes.get(5).getPaths();
		redLines = new Polyline[redPaths.size()];
		blueLines = new Polyline[bluePaths.size()];
		greenLines = new Polyline[greenPaths.size()];
		emoryLines = new Polyline[emoryPaths.size()];
		trolleyLines = new Polyline[trolleyPaths.size()];
		ramblerLines = new Polyline[ramblerPaths.size()];
		int redPathColor = Color.parseColor(routes.get(0).getColor());
		int bluePathColor = Color.parseColor(routes.get(1).getColor());
		int greenPathColor = Color.parseColor(routes.get(2).getColor());
		int trolleyPathColor = Color.parseColor(routes.get(3).getColor());
		int emoryPathColor = Color.parseColor(routes.get(4).getColor());
		int ramblerPathColor = Color.parseColor(routes.get(5).getColor());
		for (int i = 0 ; i < redLines.length; i++) {
			redLines[i] = map.addPolyline(new PolylineOptions().color(redPathColor)
			.addAll(redPaths.get(i)));
			redLines[i].setVisible(redCheckBox.isChecked());
		}
		for (int i = 0; i < blueLines.length; i++) {
			blueLines[i] = map.addPolyline(new PolylineOptions().color(bluePathColor)
			.addAll(bluePaths.get(i)));
			blueLines[i].setVisible(blueCheckBox.isChecked());
		}
		for (int i = 0; i < greenLines.length; i++) {
			greenLines[i] = map.addPolyline(new PolylineOptions().color(greenPathColor)
			.addAll(greenPaths.get(i)));
			greenLines[i].setVisible(greenCheckBox.isChecked());
		}
		for (int i = 0; i < emoryLines.length; i++) {
			emoryLines[i] = map.addPolyline(new PolylineOptions().color(emoryPathColor)
			.addAll(emoryPaths.get(i)));
			emoryLines[i].setVisible(emoryCheckBox.isChecked());
		}
		for (int i = 0; i < trolleyLines.length; i++) {
			trolleyLines[i] = map.addPolyline(new PolylineOptions().color(trolleyPathColor)
			.addAll(trolleyPaths.get(i)));
			trolleyLines[i].setVisible(trolleyCheckBox.isChecked());
		}
		for (int i = 0; i < ramblerLines.length; i++) {
			ramblerLines[i] = map.addPolyline(new PolylineOptions().color(ramblerPathColor)
			.addAll(ramblerPaths.get(i)));
			ramblerLines[i].setVisible(ramblerCheckBox.isChecked());
		}

		
		redCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (redLines[0].isVisible() != isChecked) {
					for (int i = 0; i < redLines.length; i++) {
						redLines[i].setVisible(isChecked);
					}
				}
			}
		});
		
		blueCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (blueLines[0].isVisible() != isChecked) {
					for (int i = 0; i < blueLines.length; i++) {
						blueLines[i].setVisible(isChecked);
					}
				}
			}
		});
		
		greenCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (int i = 0; i < greenLines.length; i++) {
					greenLines[i].setVisible(isChecked);
				}
			}
		});
		
		emoryCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (int i = 0; i < emoryLines.length; i++) {
					emoryLines[i].setVisible(isChecked);
				}
			}
		});

		trolleyCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (int i = 0; i < trolleyLines.length; i++) {
					trolleyLines[i].setVisible(isChecked);
				}
			}
		});
		
		ramblerCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (int i = 0; i < ramblerLines.length; i++) {
					ramblerLines[i].setVisible(isChecked);
				}
			}
		});
		//is this just a revival?
		if (savedInstanceState != null) {
			
		} else {
			checkAllBoxes();
		}
		//maps tags to titles
		Map<String, String> titleMap = new Hashtable<String, String>();
		for (Route route : routes) {
			for (Stop stop : route.getStops()) {
				Log.d(TAG, "Tag: " + stop.getTag() + " Title: " + stop.getTitle());
				String title = processTitle(stop.getTitle());
				Log.d(TAG, "Tag: " + stop.getTag() + " Processed Title: " + title);
				if (!titleMap.values().contains(title)) {
					titleMap.put(stop.getTag(), title);
				}
				
			}
		}
		Collection<String> values = titleMap.values();
		ListView sideBar = (ListView) findViewById(R.id.left_drawer);
		String[] titleArr = values.toArray(new String[values.size()]);
		Arrays.sort(titleArr);
		Log.d(TAG, "Array: " + titleArr);
		Adapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titleArr);
		Log.d(TAG, "Adapter: " + adapter);
		sideBar.setAdapter((ListAdapter)adapter);
	}
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
	private void checkAllBoxes() {
		redCheckBox.setChecked(true);
		blueCheckBox.setChecked(true);
		greenCheckBox.setChecked(true);
		emoryCheckBox.setChecked(true);
		trolleyCheckBox.setChecked(true);
		ramblerCheckBox.setChecked(true);
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		//savedInstanceState.
		super.onSaveInstanceState(savedInstanceState);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_map, menu);
		return true;
	}
	private String processTitle(String title) {
		String newTitle = new String(title);
		int ind = newTitle.indexOf(" - Arrival");
		if (ind != -1) {
			if (ind + 10 == newTitle.length()) {
				newTitle = newTitle.substring(0, ind);
			} else {
				newTitle = newTitle.substring(0, ind) + newTitle.substring(ind + 10, newTitle.length() - 1);
			}
		} else {
			ind = newTitle.indexOf(" - Hidden Departure");
			if (ind != -1) {
				if (ind + 19 == newTitle.length()) {
					newTitle = newTitle.substring(0, ind);
				} else {
					newTitle = newTitle.substring(0, ind) + newTitle.substring(ind + 19, newTitle.length() - 1);
				}
			}
		}
		ind = newTitle.indexOf("Street");
		if (ind != -1) {
			Log.d(TAG, newTitle);
			Log.d(TAG, "ind: " + ind);
			Log.d(TAG, "ind + 6 : " + (ind + 6));
			Log.d(TAG, "newTitle.length() - 1: " + (newTitle.length() - 1));
			if (ind + 6 == newTitle.length()) {
				newTitle = newTitle.substring(0, ind) + "St";
			} else {
				newTitle = newTitle.substring(0, ind) + "St" + newTitle.substring(ind + 6, newTitle.length() - 1);
			}
			Log.d(TAG, "fixed: " + newTitle);
		}
		return newTitle;
	}

}
