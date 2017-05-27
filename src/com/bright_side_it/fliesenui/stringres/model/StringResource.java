package com.bright_side_it.fliesenui.stringres.model;

import java.util.Map;

import com.bright_side_it.fliesenui.screendefinition.model.NodePath;

public class StringResource {
	private Map<String, StringResourceItem> strings;
	private String id;
	private NodePath nodePath;

	public Map<String, StringResourceItem> getStrings() {
		return strings;
	}

	public void setStrings(Map<String, StringResourceItem> strings) {
		this.strings = strings;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public NodePath getNodePath() {
		return nodePath;
	}

	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}
	
	
}
