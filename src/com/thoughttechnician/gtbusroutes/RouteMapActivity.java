package com.thoughttechnician.gtbusroutes;

import java.io.BufferedInputStream;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

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
	private XMLHandler xmlHandler = null;
	private List<Route> routes = null;

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
				routes = xmlHandler.parse(new BufferedInputStream(getResources().openRawResource(R.raw.my_xml)));
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
		savedInstanceState.
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
