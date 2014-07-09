package com.thoughttechnician.gtbusroutes;

import java.util.List;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlaceSearchFragment extends ListFragment {
	
	PlacesHandler placesHandler;
	private List<Place> results;
	private String query;
	private ArrayAdapter<Place> adapter;
	private ProgressDialog progress;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		v = inflater.inflate(R.layout.fragment_place_search, container, false);
		progress = new ProgressDialog(getActivity());
		progress.setTitle("Loading");
		progress.setMessage("Wait while loading...");
		if (results == null) {
			progress.show();
		}
		return v;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    placesHandler = new PlacesHandler(getActivity(), PlacesHandler.SMITH);
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
			adapter = new SearchAdapter(results);
			progress.dismiss();

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
		      setListAdapter(adapter);
		}
	}
	
	private class SearchAdapter extends ArrayAdapter<Place> {
		public SearchAdapter(List<Place> results) {
			super(getActivity(), 0, results);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) 
				convertView = getActivity().getLayoutInflater()
				.inflate(R.layout.list_item_search, null);
			Place currPlace = results.get(position);
			TextView nameTextView = 
					(TextView) convertView.findViewById(R.id.search_list_item_nameTextView);
			nameTextView.setText(currPlace.getName());
			
			return convertView;
		}
	}

}
