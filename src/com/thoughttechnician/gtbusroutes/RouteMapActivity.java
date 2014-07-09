package com.thoughttechnician.gtbusroutes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

public class RouteMapActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] titleArr;
	
	private static final String TAG = "RouteMapActivity";
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		FragmentManager fm = getSupportFragmentManager();
		RouteMapFragment mainFragment = (RouteMapFragment) fm.findFragmentById(R.id.main_fragment_container);
		if (mainFragment == null) {
			mainFragment = new RouteMapFragment();
			fm.beginTransaction()
			.add(R.id.main_fragment_container, mainFragment)
			.commit();
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

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        //after fragments have all been initialized
        
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


	void updateSideBar(List<Route> routes, List<Stop> stopList, HashMap<Stop,
			List<Prediction>> stopPredictionMap) {
		if (routes == null) {
			return;
		}
		//maps tags to titles
//		Map<String, String> titleMap = new Hashtable<String, String>();
//		for (Route route : routes) {
//			for (Stop stop : route.getStops()) {
////				Log.d(TAG, "Tag: " + stop.getTag() + " Title: " + stop.getTitle());
//				String title = processTitle(stop.getTitle());
////				Log.d(TAG, "Tag: " + stop.getTag() + " Processed Title: " + title);
//				if (!titleMap.values().contains(title)) {
//					titleMap.put(stop.getTag(), title);
//				}
//				
//			}
//		}

		//right here we need to fill this hash map with all the time predictions for each stop
		ExpandableListView sideBar = (ExpandableListView) findViewById(R.id.left_drawer);
		//titleArr = values.toArray(new String[values.size()]);
		//Arrays.sort(titleArr);
//		Log.d(TAG, "Array: " + titleArr);
		//Adapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titleArr);
		//ArrayList<String> headers = new ArrayList<String>(titleMap.values());
		System.out.println("tang: " + stopPredictionMap);
		ExpandableListAdapter adapter = new ExpandableListAdapter(this, stopList, stopPredictionMap);
//		Log.d(TAG, "Adapter: " + adapter);
		//sideBar.setAdapter((ListAdapter) adapter);
		sideBar.setAdapter(adapter);
	}
}
