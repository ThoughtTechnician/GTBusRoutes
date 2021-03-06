package com.thoughttechnician.gtbusroutes;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteMapFragment extends Fragment {
	// private static final LatLng ATLANTA = new LatLng(33.7765,-84.4002);
	private static final LatLng BEST_TARGET = new LatLng(33.77801923888944,
			-84.397401139140130);
	private static final String TAG = "RouteMapFragment";
	static final String ROUTE_CONFIG_FILENAME = "route_config.xml";

	private List<Route> routes = null;
	// private List<Vehicle> vehicles = null;
	private XMLHandler xmlHandler = null;

	private Polyline[] redLines = null;
	private Polyline[] blueLines = null;
	private Polyline[] greenLines = null;
	private Polyline[] emoryLines = null;
	private Polyline[] trolleyLines = null;
	private Polyline[] ramblerLines = null;

	private List<Vehicle> addedRedVehicles = null;
	private List<Vehicle> addedBlueVehicles = null;
	private List<Vehicle> addedGreenVehicles = null;
	private List<Vehicle> addedEmoryVehicles = null;
	private List<Vehicle> addedTrolleyVehicles = null;
	private List<Vehicle> addedRamblerVehicles = null;

	private CheckBox redCheckBox = null;
	private CheckBox blueCheckBox = null;
	private CheckBox greenCheckBox = null;
	private CheckBox emoryCheckBox = null;
	private CheckBox trolleyCheckBox = null;
	private CheckBox ramblerCheckBox = null;

	private List<List<LatLng>> redPaths = null;
	private List<List<LatLng>> bluePaths = null;
	private List<List<LatLng>> greenPaths = null;
	private List<List<LatLng>> emoryPaths = null;
	private List<List<LatLng>> trolleyPaths = null;
	private List<List<LatLng>> ramblerPaths = null;

	private List<Vehicle> redVehicles = null;
	private List<Vehicle> blueVehicles = null;
	private List<Vehicle> greenVehicles = null;
	private List<Vehicle> trolleyVehicles = null;
	private List<Vehicle> emoryVehicles = null;
	private List<Vehicle> ramblerVehicles = null;

	private GoogleMap map = null;
	private ScheduledFuture scheduledBusUpdater = null;
	private ScheduledExecutorService scheduler = null;

	private final Handler handler = new Handler(Looper.getMainLooper());

	private boolean networkConnected;
	private NetworkReceiver networkReceiver;
	private boolean summerTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		try {
			if (routes == null) {
				xmlHandler = new XMLHandler();
				// routes = xmlHandler.parseRouteConfig(new
				// BufferedInputStream(getResources().openRawResource(R.raw.my_route_config)));
				// vehicles = xmlHandler.parseVehicleLocation(new
				// BufferedInputStream(getResources().openRawResource(R.raw.vehicle_location)));
				File downloadedFile = new File(getActivity().getFilesDir()
						+ "/" + RouteMapFragment.ROUTE_CONFIG_FILENAME);
				if (downloadedFile.exists()) {
					// Log.d(TAG, "Downloaded File exists");
					routes = xmlHandler
							.parseRouteConfig(new BufferedInputStream(
									getActivity().openFileInput(
											"route_config.xml")));
				} else {
					// Log.d(TAG, "Downloaded File does not exist");
					routes = xmlHandler
							.parseRouteConfig(new BufferedInputStream(
									getResources().openRawResource(
											R.raw.my_route_config)));
				}
				summerTime = (routes.size() == 5);
				// vehicles = xmlHandler.parseVehicleLocation(new
				// BufferedInputStream(getResources().openRawResource(R.raw.vehicle_location)));
			}

		} catch (Exception e) {
			Log.e(TAG, "Couldn't Do it!");
			e.printStackTrace();
		}
		if (summerTime) {
			v = inflater.inflate(R.layout.fragment_route_map_without_rambler,
					container, false);
		} else {
			v = inflater.inflate(R.layout.fragment_route_map, container, false);
		}
		setHasOptionsMenu(true);
		FragmentManager fm = getFragmentManager();
		Fragment mapFragment = fm.findFragmentById(R.id.mapFragmentContainer);
		if (mapFragment == null) {
			GoogleMapOptions options = new GoogleMapOptions();
			options.mapType(GoogleMap.MAP_TYPE_TERRAIN);
			options.compassEnabled(true);
			mapFragment = SupportMapFragment.newInstance(options);
			fm.beginTransaction().add(R.id.mapFragmentContainer, mapFragment)
					.commit();
		}
		map = ((SupportMapFragment) mapFragment).getMap();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			map.setPadding(0, 70, 0, 0);

		}
		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(ATLANTA, 14));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(BEST_TARGET, 14));
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

			@Override
			public boolean onMyLocationButtonClick() {
				// TODO Auto-generated method stub
				return false;
			}
		});

		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				Log.d(TAG, "CamPos: " + map.getCameraPosition());
			}
		});
		redCheckBox = (CheckBox) v.findViewById(R.id.checkbox_red);
		blueCheckBox = (CheckBox) v.findViewById(R.id.checkbox_blue);
		greenCheckBox = (CheckBox) v.findViewById(R.id.checkbox_green);
		emoryCheckBox = (CheckBox) v.findViewById(R.id.checkbox_emory);
		trolleyCheckBox = (CheckBox) v.findViewById(R.id.checkbox_trolley);
		ramblerCheckBox = (CheckBox) v.findViewById(R.id.checkbox_rambler);

		// gives you the paths from the default file
		Log.d(TAG, "routes: " + routes);
		redrawPaths();
		// initialize the stops list
		((RouteMapActivity) getActivity()).updateSideBar(routes);

		redCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (redLines[0].isVisible() != isChecked) {
					for (int i = 0; i < redLines.length; i++) {
						redLines[i].setVisible(isChecked);
					}
				}
				if (addedRedVehicles != null) {
					for (int i = 0; i < addedRedVehicles.size(); i++) {
						addedRedVehicles.get(i).setVisible(isChecked);
					}
				}
			}
		});

		blueCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (blueLines[0].isVisible() != isChecked) {
					for (int i = 0; i < blueLines.length; i++) {
						blueLines[i].setVisible(isChecked);
					}
				}
				if (addedBlueVehicles != null) {
					for (int i = 0; i < addedBlueVehicles.size(); i++) {
						addedBlueVehicles.get(i).setVisible(isChecked);
					}
				}
			}
		});

		greenCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				for (int i = 0; i < greenLines.length; i++) {
					greenLines[i].setVisible(isChecked);
				}
				if (addedGreenVehicles != null) {
					for (int i = 0; i < addedGreenVehicles.size(); i++) {
						addedGreenVehicles.get(i).setVisible(isChecked);
					}
				}
			}
		});

		emoryCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				for (int i = 0; i < emoryLines.length; i++) {
					emoryLines[i].setVisible(isChecked);
				}
				if (addedEmoryVehicles != null) {
					for (int i = 0; i < addedEmoryVehicles.size(); i++) {
						addedEmoryVehicles.get(i).setVisible(isChecked);
					}
				}
			}
		});

		trolleyCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						for (int i = 0; i < trolleyLines.length; i++) {
							trolleyLines[i].setVisible(isChecked);
						}
						if (addedTrolleyVehicles != null) {
							for (int i = 0; i < addedTrolleyVehicles.size(); i++) {
								addedTrolleyVehicles.get(i).setVisible(
										isChecked);
							}
						}
					}
				});
		if (!summerTime) {
			ramblerCheckBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							for (int i = 0; i < ramblerLines.length; i++) {
								ramblerLines[i].setVisible(isChecked);
							}
							if (addedRamblerVehicles != null) {
								for (int i = 0; i < addedRamblerVehicles.size(); i++) {
									addedRamblerVehicles.get(i).setVisible(
											isChecked);
								}
							}
						}
					});
		}

		// is this just a revival?
		if (savedInstanceState != null) {

		} else {
			checkAllBoxes();
		}

		return v;
	}

	@Override
	public void onCreate(Bundle onSavedInstanceState) {
		super.onCreate(onSavedInstanceState);
		networkConnected = connected();
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		networkReceiver = new NetworkReceiver();
		getActivity().registerReceiver(networkReceiver, filter);

		// start updating route config in the background
		downloadRouteConfig();

		scheduler = Executors.newScheduledThreadPool(1);
	}

	@Override
	public void onPause() {
		super.onPause();
		scheduledBusUpdater.cancel(true);
		Log.e(TAG, "canceled bus updater");
	}

	@Override
	public void onResume() {
		super.onResume();
		networkConnected = connected();
		if (networkConnected) {
			startUpdatingBuses();
			Log.e(TAG, "Started Updating Buses");
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.route_map, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		// this is where you used to handle mDrawerToggle
		// Handle your other action bar items...
		switch (item.getItemId()) {
		case R.id.search_button:
			return getActivity().onSearchRequested();
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void checkAllBoxes() {
		redCheckBox.setChecked(true);
		blueCheckBox.setChecked(true);
		greenCheckBox.setChecked(true);
		emoryCheckBox.setChecked(true);
		trolleyCheckBox.setChecked(true);
		if (!summerTime) {
			ramblerCheckBox.setChecked(true);
		}
	}

	private void downloadRouteConfig() {
		Log.e(TAG, "Called downloadRouteConfig Method");
		new DownloadRouteConfigTask().execute();
	}

	private void redrawBuses() {
		// System.out.println("red: " + redVehicles);
		// System.out.println("blue: " + blueVehicles);
		// System.out.println("green: " + greenVehicles);
		// System.out.println("trolley: " + trolleyVehicles);
		// System.out.println("emory: " + emoryVehicles);
		// System.out.println("rambler: " + ramblerVehicles);
		if (redVehicles != null) {
			if (addedRedVehicles != null) {
				for (int i = 0; i < addedRedVehicles.size(); i++) {
					Vehicle curr = addedRedVehicles.get(i);
					int ind = redVehicles.indexOf(curr);
					if (ind == -1) {
						// if the current already added vehicles is not in the
						// new data
						// remove it from the map and from the list of added
						// vehicles
						addedRedVehicles.get(i).marker.remove();
						addedRedVehicles.remove(i);
					} else {
						// if it is in the new data, update its position
						// and remove its new counterpart from the data list
						addedRedVehicles.get(i).marker.setPosition(redVehicles
								.get(ind).coords);
						redVehicles.remove(ind);
					}
				}
			} else {
				addedRedVehicles = new ArrayList<Vehicle>();
			}
			for (int i = 0; i < redVehicles.size(); i++) {
				redVehicles.get(i).marker = map
						.addMarker(new MarkerOptions()
								.position(redVehicles.get(i).coords)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_RED))
								.visible(redCheckBox.isChecked()));
				addedRedVehicles.add(redVehicles.get(i));
			}
		}
		if (blueVehicles != null) {
			if (addedBlueVehicles != null) {
				for (int i = 0; i < addedBlueVehicles.size(); i++) {
					Vehicle curr = addedBlueVehicles.get(i);
					int ind = blueVehicles.indexOf(curr);
					if (ind == -1) {
						// if the current already added vehicles is not in the
						// new data
						// remove it from the map and from the list of added
						// vehicles
						addedBlueVehicles.get(i).marker.remove();
						addedBlueVehicles.remove(i);
					} else {
						// if it is in the new data, update its position
						// and remove its new counterpart from the data list
						addedBlueVehicles.get(i).marker
								.setPosition(blueVehicles.get(ind).coords);
						blueVehicles.remove(ind);
					}
				}
			} else {
				addedBlueVehicles = new ArrayList<Vehicle>();
			}
			for (int i = 0; i < blueVehicles.size(); i++) {
				blueVehicles.get(i).marker = map
						.addMarker(new MarkerOptions()
								.position(blueVehicles.get(i).coords)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
								.visible(blueCheckBox.isChecked()));
				addedBlueVehicles.add(blueVehicles.get(i));
			}
		}
		if (greenVehicles != null) {
			if (addedGreenVehicles != null) {
				for (int i = 0; i < addedGreenVehicles.size(); i++) {
					Vehicle curr = addedGreenVehicles.get(i);
					int ind = greenVehicles.indexOf(curr);
					if (ind == -1) {
						// if the current already added vehicles is not in the
						// new data
						// remove it from the map and from the list of added
						// vehicles
						addedGreenVehicles.get(i).marker.remove();
						addedGreenVehicles.remove(i);
					} else {
						// if it is in the new data, update its position
						// and remove its new counterpart from the data list

						addedGreenVehicles.get(i).marker
								.setPosition(greenVehicles.get(ind).coords);
						greenVehicles.remove(ind);
					}
				}
			} else {
				addedGreenVehicles = new ArrayList<Vehicle>();
			}
			for (int i = 0; i < greenVehicles.size(); i++) {
				greenVehicles.get(i).marker = map
						.addMarker(new MarkerOptions()
								.position(greenVehicles.get(i).coords)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
								.visible(greenCheckBox.isChecked()));
				addedGreenVehicles.add(greenVehicles.get(i));
			}
		}
		if (trolleyVehicles != null) {
			if (addedTrolleyVehicles != null) {
				for (int i = 0; i < addedTrolleyVehicles.size(); i++) {
					Vehicle curr = addedTrolleyVehicles.get(i);
					int ind = trolleyVehicles.indexOf(curr);
					if (ind == -1) {
						// if the current already added vehicles is not in the
						// new data
						// remove it from the map and from the list of added
						// vehicles
						addedTrolleyVehicles.get(i).marker.remove();
						addedTrolleyVehicles.remove(i);
					} else {
						// if it is in the new data, update its position
						// and remove its new counterpart from the data list
						addedTrolleyVehicles.get(i).marker
								.setPosition(trolleyVehicles.get(ind).coords);
						trolleyVehicles.remove(ind);
					}
				}
			} else {
				addedTrolleyVehicles = new ArrayList<Vehicle>();
			}
			for (int i = 0; i < trolleyVehicles.size(); i++) {
				trolleyVehicles.get(i).marker = map
						.addMarker(new MarkerOptions()
								.position(trolleyVehicles.get(i).coords)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
								.visible(trolleyCheckBox.isChecked()));
				addedTrolleyVehicles.add(trolleyVehicles.get(i));
			}
		}
		if (emoryVehicles != null) {
			if (addedEmoryVehicles != null) {
				for (int i = 0; i < addedEmoryVehicles.size(); i++) {
					Vehicle curr = addedEmoryVehicles.get(i);
					int ind = emoryVehicles.indexOf(curr);
					if (ind == -1) {
						// if the current already added vehicles is not in the
						// new data
						// remove it from the map and from the list of added
						// vehicles
						addedEmoryVehicles.get(i).marker.remove();
						addedEmoryVehicles.remove(i);
					} else {
						// if it is in the new data, update its position
						// and remove its new counterpart from the data list
						addedEmoryVehicles.get(i).marker
								.setPosition(emoryVehicles.get(ind).coords);
						emoryVehicles.remove(ind);
					}
				}
			} else {
				addedEmoryVehicles = new ArrayList<Vehicle>();
			}
			for (int i = 0; i < emoryVehicles.size(); i++) {
				emoryVehicles.get(i).marker = map
						.addMarker(new MarkerOptions()
								.position(emoryVehicles.get(i).coords)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
								.visible(emoryCheckBox.isChecked()));
				addedEmoryVehicles.add(emoryVehicles.get(i));
			}
		}
		if (ramblerVehicles != null) {
			if (addedRamblerVehicles != null) {
				for (int i = 0; i < addedRamblerVehicles.size(); i++) {
					Vehicle curr = addedRamblerVehicles.get(i);
					int ind = ramblerVehicles.indexOf(curr);
					if (ind == -1) {
						// if the current already added vehicles is not in the
						// new data
						// remove it from the map and from the list of added
						// vehicles
						addedRamblerVehicles.get(i).marker.remove();
						addedRamblerVehicles.remove(i);
					} else {
						// if it is in the new data, update its position
						// and remove its new counterpart from the data list
						addedRamblerVehicles.get(i).marker
								.setPosition(ramblerVehicles.get(ind).coords);
						ramblerVehicles.remove(ind);
					}
				}
			} else {
				addedRamblerVehicles = new ArrayList<Vehicle>();
			}
			for (int i = 0; i < ramblerVehicles.size(); i++) {
				ramblerVehicles.get(i).marker = map
						.addMarker(new MarkerOptions()
								.position(ramblerVehicles.get(i).coords)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
								.visible(ramblerCheckBox.isChecked()));
				addedRamblerVehicles.add(ramblerVehicles.get(i));
			}
		}

	}

	private void redrawPaths() {
		if (routes == null || routes.get(0) == null) {
			Log.e(TAG, "THERE ARE NO ROUTES!!!!");
			return;
		}
		int redPathColor = Color.parseColor(routes.get(0).getColor());
		int bluePathColor = Color.parseColor(routes.get(1).getColor());
		int greenPathColor = Color.parseColor(routes.get(2).getColor());
		int trolleyPathColor = Color.parseColor(routes.get(3).getColor());
		int emoryPathColor = Color.parseColor(routes.get(4).getColor());
		int ramblerPathColor = 0;
		if (!summerTime) {
			ramblerPathColor = Color.parseColor(routes.get(5).getColor());
		}

		redPaths = routes.get(0).getPaths();
		bluePaths = routes.get(1).getPaths();
		greenPaths = routes.get(2).getPaths();
		trolleyPaths = routes.get(3).getPaths();
		emoryPaths = routes.get(4).getPaths();
		if (!summerTime) {
			ramblerPaths = routes.get(5).getPaths();
		}
		// if (redLines == null) {
		// redLines = new Polyline[redPaths.size()];
		// }
		// if (blueLines == null) {
		// blueLines = new Polyline[bluePaths.size()];
		// }
		// if (greenLines == null) {
		// greenLines = new Polyline[greenPaths.size()];
		// }
		// if (emoryLines == null) {
		// emoryLines = new Polyline[emoryPaths.size()];
		// }
		// if (trolleyLines == null) {
		// trolleyLines = new Polyline[trolleyPaths.size()];
		// }
		// if (ramblerLines == null) {
		// ramblerLines = new Polyline[ramblerPaths.size()];
		// }

		// update red route paths
		if (redLines != null) {
			for (int i = 0; i < redLines.length; i++) {
				redLines[i].remove();
			}
		}
		redLines = new Polyline[redPaths.size()];
		for (int i = 0; i < redPaths.size(); i++) {
			redLines[i] = map.addPolyline(new PolylineOptions().color(
					redPathColor).addAll(redPaths.get(i)));
			redLines[i].setVisible(redCheckBox.isChecked());
		}

		// update blue route paths
		if (blueLines != null) {
			for (int i = 0; i < blueLines.length; i++) {
				blueLines[i].remove();
			}
		}
		blueLines = new Polyline[bluePaths.size()];
		for (int i = 0; i < bluePaths.size(); i++) {
			blueLines[i] = map.addPolyline(new PolylineOptions().color(
					bluePathColor).addAll(bluePaths.get(i)));
			blueLines[i].setVisible(blueCheckBox.isChecked());
		}

		// update green route paths
		if (greenLines != null) {
			for (int i = 0; i < greenLines.length; i++) {
				greenLines[i].remove();
			}
		}
		greenLines = new Polyline[greenPaths.size()];
		for (int i = 0; i < greenPaths.size(); i++) {
			greenLines[i] = map.addPolyline(new PolylineOptions().color(
					greenPathColor).addAll(greenPaths.get(i)));
			greenLines[i].setVisible(greenCheckBox.isChecked());
		}

		// update emory route paths
		if (emoryLines != null) {
			for (int i = 0; i < emoryLines.length; i++) {
				emoryLines[i].remove();
			}
		}
		emoryLines = new Polyline[emoryPaths.size()];
		for (int i = 0; i < emoryPaths.size(); i++) {
			emoryLines[i] = map.addPolyline(new PolylineOptions().color(
					emoryPathColor).addAll(emoryPaths.get(i)));
			emoryLines[i].setVisible(emoryCheckBox.isChecked());
		}

		// update trolley route paths
		if (trolleyLines != null) {
			for (int i = 0; i < trolleyLines.length; i++) {
				trolleyLines[i].remove();
			}
		}
		trolleyLines = new Polyline[trolleyPaths.size()];
		for (int i = 0; i < trolleyPaths.size(); i++) {
			trolleyLines[i] = map.addPolyline(new PolylineOptions().color(
					trolleyPathColor).addAll(trolleyPaths.get(i)));
			trolleyLines[i].setVisible(trolleyCheckBox.isChecked());
		}
		if (!summerTime) {
			// update rambler route paths
			if (ramblerLines != null) {
				for (int i = 0; i < ramblerLines.length; i++) {
					ramblerLines[i].remove();
				}
			}
			ramblerLines = new Polyline[ramblerPaths.size()];
			for (int i = 0; i < ramblerPaths.size(); i++) {
				ramblerLines[i] = map.addPolyline(new PolylineOptions().color(
						ramblerPathColor).addAll(ramblerPaths.get(i)));
				ramblerLines[i].setVisible(ramblerCheckBox.isChecked());
			}
		}

	}

	private class DownloadRouteConfigTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... voids) {
			InputStream is = null;
			FileOutputStream os = null;
			try {
				URL url = new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=routeConfig");
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setReadTimeout(10000);
				connection.setConnectTimeout(15000);
				connection.setRequestMethod("GET");
				connection.setDoInput(true);

				connection.connect();
				int response = connection.getResponseCode();
				Log.d(TAG, "Connection response is: " + response);
				is = connection.getInputStream();
				File file = new File(getActivity().getFilesDir(),
						ROUTE_CONFIG_FILENAME);
				file.createNewFile();
				os = getActivity().openFileOutput(ROUTE_CONFIG_FILENAME,
						Context.MODE_PRIVATE);
				byte[] buffer = new byte[1024];
				int charsRead;
				while ((charsRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, charsRead);
				}
				os.close();
				is.close();
				Log.e(TAG, "Just updated route info");
			} catch (Exception e) {
				Log.e(TAG,
						"Could not do something within the route config task");
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						Log.e(TAG, "Could not close route info instream");
					}
				}
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						Log.e(TAG, "Could not close route info outstream");
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				routes = xmlHandler.parseRouteConfig(new BufferedInputStream(
						getActivity().openFileInput(ROUTE_CONFIG_FILENAME)));
				summerTime = (routes.size() == 5);

			} catch (Exception e) {
				Log.e(TAG, "Could not put updated route config into place!");
				e.printStackTrace();
			}
			redrawPaths();
			((RouteMapActivity) getActivity()).updateSideBar(routes);
		}
	}

	private class BusUpdateClass extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... voids) {
			try {
				redVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=red"));
				blueVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=blue"));
				greenVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=green"));
				trolleyVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=trolley"));
				emoryVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=emory"));
				ramblerVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=rambler"));
				// It is apparently illegal to edit UI from within worker thread
				// redrawBuses();
			} catch (Exception e) {
				Log.e(TAG, "Could not update buses");
				e.printStackTrace();
				scheduledBusUpdater.cancel(true);
				Log.e(TAG, "Canceled bus updater");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.e(TAG, "Just called my wonderful onPostExecute method");
			redrawBuses();
		}
	}

	private List<Vehicle> acquireVehicleLocations(URL url) throws Exception {
		InputStream is = null;
		List<Vehicle> vehicleResults = null;
		try {
			// this is where you were working
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setReadTimeout(10000);
			connection.setConnectTimeout(15000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);

			connection.connect();
			int response = connection.getResponseCode();
			if (response != 200) {
				throw new Exception("Response Code was " + response);
			}
			// Log.d(TAG, "Connection response is: " + response);
			is = new BufferedInputStream(connection.getInputStream());
			vehicleResults = xmlHandler.parseVehicleLocation(is);
			is.close();

		} // catch (Exception e) {
			// Log.e(TAG, "Could not acquire vehicle locations");
			// e.printStackTrace();
		// }
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, "Could not close route info instream");
				}
			}
		}
		return vehicleResults;
	}

	private boolean connected() {
		ConnectivityManager manager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	final Runnable updater = new Runnable() {
		public void run() {
			try {
				redVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=red"));
				blueVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=blue"));
				greenVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=green"));
				trolleyVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=trolley"));
				emoryVehicles = acquireVehicleLocations(new URL(
						"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=emory"));
				if (!summerTime) {
					ramblerVehicles = acquireVehicleLocations(new URL(
							"http://gtwiki.info/nextbus/nextbus.php?a=georgia-tech&command=vehicleLocations&r=rambler"));
				}
				Log.e(TAG, "Updated Buses");
				handler.post(new Runnable() {
					public void run() {
						redrawBuses();
					}
				});
			} catch (Exception e) {
				Log.e(TAG, "Bus Update Failed");
				e.printStackTrace();
				scheduledBusUpdater.cancel(true);
				if (networkConnected) {
					scheduledBusUpdater = scheduler.scheduleAtFixedRate(this,
							3, 10, SECONDS);
					Log.e(TAG, "Failed but connected: restarting in 3 seconds");
				} else {
					Log.e(TAG,
							"Failed and not connected: cancelling bus updates");
				}
			}
		}
	};

	private void startUpdatingBuses() {
		Log.d(TAG, "Started Updating Buses");
		if (scheduledBusUpdater != null && !scheduledBusUpdater.isCancelled()) {
			Log.e(TAG,
					"startUpdatingBuses called but buses were already updating");
		}
		scheduledBusUpdater = scheduler.scheduleAtFixedRate(updater, 0, 8,
				SECONDS);
	}

	private class NetworkReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager man = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = man.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				networkConnected = true;
				if (scheduledBusUpdater.isCancelled()) {
					scheduledBusUpdater = scheduler.scheduleAtFixedRate(
							updater, 0, 8, SECONDS);
					Log.e(TAG, "Network now connected: restarting bus updates");
				}
			} else {
				networkConnected = false;
				scheduledBusUpdater.cancel(true);
				Log.e(TAG, "Network now disconnected: cancelling bus updates");
			}
		}

	}

	public List<Route> getRoutes() {
		return routes;
	}
}
