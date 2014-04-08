package com.thoughttechnician.gtbusroutes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteMapActivity extends ActionBarActivity {
	private static final LatLng ATLANTA = new LatLng(33.755,-84.39);
	private static final String TAG = "RouteMapActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_map);
		
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
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				Log.d(TAG, "LatLng: " + arg0);
			}
		});
		PolylineOptions polyOptions = new PolylineOptions()
			.add(new LatLng(33.781779953549396,-84.40672282129526))
			.add(new LatLng(33.768557503688875,-84.39630880951881));
		Polyline polyline = map.addPolyline(polyOptions);
		CheckBox redCheckBox = (CheckBox) findViewById(R.id.checkbox_red);
		redCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
		});
	}
	public void onCheckboxClicked(View view) {
		GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapFragmentContainer)).getMap();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
