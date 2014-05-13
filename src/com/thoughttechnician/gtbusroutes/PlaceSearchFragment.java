package com.thoughttechnician.gtbusroutes;

import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;

public class PlaceSearchFragment extends ListFragment {
	
	PlacesHandler placesHandler;
	private List<Place> results;
	private String query;
	private ArrayAdapter<Place> adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    placesHandler = new PlacesHandler(getActivity(), PlacesHandler.ATLANTA);
	    // Get the intent, verify the action and get the query
	    Intent intent = getActivity().getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	query = intent.getStringExtra(SearchManager.QUERY);
	    	new GetPlacesTask().execute();
	    }
	}
	private class GetPlacesTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void...voids) {
			Log.d("PlaceSearchFragment", "In the GetPlacesTask with query: " + query);
			results = placesHandler.acquirePlaces(query);
			adapter = new ArrayAdapter<Place>(
		    		  getActivity(),
		    		  android.R.layout.simple_list_item_1,
		    		  results);

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
		      setListAdapter(adapter);
		}
	}
}
