package com.thoughttechnician.gtbusroutes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class PlaceSearchActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_search);
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.search_container);
		if (fragment == null) {
			fragment = new PlaceSearchFragment();
			fm.beginTransaction()
			.add(R.id.search_container, fragment)
			.commit();
		}
		
	}
}
