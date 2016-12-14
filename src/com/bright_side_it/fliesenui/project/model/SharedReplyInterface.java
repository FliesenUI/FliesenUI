package com.bright_side_it.fliesenui.project.model;

import java.util.List;

import com.bright_side_it.fliesenui.screendefinition.model.NodePath;

public class SharedReplyInterface {
	private String id;
	private List<String> screenIDs;
	private NodePath nodePath;
	
	public String getID() {
		return id;
	}
	public void setID(String id) {
		this.id = id;
	}
	public List<String> getScreenIDs() {
		return screenIDs;
	}
	public void setScreenIDs(List<String> screenIDs) {
		this.screenIDs = screenIDs;
	}
	public NodePath getNodePath() {
		return nodePath;
	}
	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}
	
}
