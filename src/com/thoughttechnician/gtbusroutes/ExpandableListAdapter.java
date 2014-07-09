package com.thoughttechnician.gtbusroutes;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	private List<Stop> stopList;
	private HashMap<Stop, List<Prediction>> childrenList;
	private Context context;
	
	public ExpandableListAdapter(Context context, List<Stop> stopList,
			HashMap<Stop, List<Prediction>> childrenList) {
		this.stopList = stopList;
		this.childrenList = childrenList;
		this.context = context;
	}

	@Override
	public int getGroupCount() {
		return stopList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		Stop stop = stopList.get(groupPosition);
		List<Prediction> children = childrenList.get(stop);
		System.out.println("Dealing with " + stop + " with " + children + " children");
		if (children == null) {
			System.out.println("Null children list!");
			return 0;
		}
		System.out.println(stop.getTag() + " has " + children.size() + " children");
		return children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// This method is not necessary in this implementation
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// this method is not necessary in this implementation
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// this method is not necessary in this implementation
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// this method is not necessary in this implementation
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String header = stopList.get(groupPosition).getTitle();
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.expandable_list_group, null);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.headerView);
		textView.setTypeface(null, Typeface.BOLD);
		textView.setText(header);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String header = childrenList.get(stopList.get(groupPosition)).get(childPosition).getMinutes() + " minutes";
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.expandable_list_item, null);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.headerView);
		textView.setTypeface(null, Typeface.NORMAL);
		textView.setText(header);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
