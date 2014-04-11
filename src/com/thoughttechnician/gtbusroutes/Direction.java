package com.thoughttechnician.gtbusroutes;

import java.util.List;

public class Direction {
	String tag = "";
	String title = "";
	List<String> stopTags = null;
	public Direction(String tag, String title, List<String> stopTags) {
		this.tag = tag;
		this.title = title;
		this.stopTags = stopTags;
	}
}