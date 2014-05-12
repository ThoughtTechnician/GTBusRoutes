package com.thoughttechnician.gtbusroutes;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.res.Configuration;
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
import android.widget.ListAdapter;
import android.widget.ListView;

public class RouteMapActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private List<Route> routes = null;
	private XMLHandler xmlHandler = null;
	
	private static final String TAG = "RouteMapActivity";
	
	//package-visible summertime variable
	static boolean summerTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
			
		try {
			if (routes == null) {
				xmlHandler = new XMLHandler();
//				routes = xmlHandler.parseRouteConfig(new BufferedInputStream(getResources().openRawResource(R.raw.my_route_config)));
//				vehicles = xmlHandler.parseVehicleLocation(new BufferedInputStream(getResources().openRawResource(R.raw.vehicle_location)));
				File downloadedFile = new File(getFilesDir() + "/" + RouteMapFragment.ROUTE_CONFIG_FILENAME);
				if (downloadedFile.exists()) {
//					Log.d(TAG, "Downloaded File exists");
					routes = xmlHandler.parseRouteConfig(new BufferedInputStream(openFileInput("route_config.xml")));
				} else {
//					Log.d(TAG, "Downloaded File does not exist");
					routes = xmlHandler.parseRouteConfig(new BufferedInputStream(getResources().openRawResource(R.raw.my_route_config)));
				}
				summerTime = (routes.size() == 5);
				//vehicles = xmlHandler.parseVehicleLocation(new BufferedInputStream(getResources().openRawResource(R.raw.vehicle_location)));
			}

		} catch (Exception e) {
			Log.e(TAG, "Couldn't Do it!");
			e.printStackTrace();
		}
		FragmentManager fm = getSupportFragmentManager();
		Fragment mainFragment = fm.findFragmentById(R.id.main_fragment_container);
		if (mainFragment == null) {
			mainFragment = new RouteMapFragment();
			fm.beginTransaction()
			.add(R.id.main_fragment_container, mainFragment)
			.commit();
		}
		
		
		//maps tags to titles
		Map<String, String> titleMap = new Hashtable<String, String>();
		for (Route route : routes) {
			for (Stop stop : route.getStops()) {
//				Log.d(TAG, "Tag: " + stop.getTag() + " Title: " + stop.getTitle());
				String title = processTitle(stop.getTitle());
//				Log.d(TAG, "Tag: " + stop.getTag() + " Processed Title: " + title);
				if (!titleMap.values().contains(title)) {
					titleMap.put(stop.getTag(), title);
				}
				
			}
		}
		
		
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
		
		
		
		Collection<String> values = titleMap.values();
		ListView sideBar = (ListView) findViewById(R.id.left_drawer);
		String[] titleArr = values.toArray(new String[values.size()]);
		Arrays.sort(titleArr);
//		Log.d(TAG, "Array: " + titleArr);
		Adapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titleArr);
//		Log.d(TAG, "Adapter: " + adapter);
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
//			Log.d(TAG, newTitle);
//			Log.d(TAG, "ind: " + ind);
//			Log.d(TAG, "ind + 6 : " + (ind + 6));
//			Log.d(TAG, "newTitle.length() - 1: " + (newTitle.length() - 1));
			if (ind + 6 == newTitle.length()) {
				newTitle = newTitle.substring(0, ind) + "St";
			} else {
				newTitle = newTitle.substring(0, ind) + "St" + newTitle.substring(ind + 6, newTitle.length() - 1);
			}
//			Log.d(TAG, "fixed: " + newTitle);
		}
		return newTitle;
	}

}
