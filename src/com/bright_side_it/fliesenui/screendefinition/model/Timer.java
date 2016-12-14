package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class Timer implements EventParameterContainer{
    private String id;
    private NodePath nodePath;
    private boolean active;
    private long intervalInMillis;
    private List<EventParameter> eventParameters;
    
	@Override
	public String getID() {
		return id;
	}
	public void setID(String id) {
		this.id = id;
	}
	@Override
	public NodePath getNodePath() {
		return nodePath;
	}
	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public long getIntervalInMillis() {
		return intervalInMillis;
	}
	public void setIntervalInMillis(long intervalInMillis) {
		this.intervalInMillis = intervalInMillis;
	}
	@Override
	public List<EventParameter> getEventParameters() {
		return eventParameters;
	}
	@Override
	public void setEventParameters(List<EventParameter> eventParameters) {
		this.eventParameters = eventParameters;
	}
    
    
}
