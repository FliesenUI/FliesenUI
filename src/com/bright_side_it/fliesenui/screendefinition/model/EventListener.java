package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class EventListener implements EventParameterContainer{
	public enum EventListenType{BACK_ACTION, KEY_PRESS, KEY_DOWN}
    private NodePath nodePath;
	private EventListenType eventListenType;
	private List<EventParameter> eventParameters;
	private String id;
	private Character keyChar;
	private Integer keyCode;
	private Boolean shift;
	private Boolean alt;
	private Boolean control;

	public EventListenType getEventListenType() {
		return eventListenType;
	}

	public void setEventListenType(EventListenType eventListenType) {
		this.eventListenType = eventListenType;
	}

	public NodePath getNodePath() {
		return nodePath;
	}

	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}

	public List<EventParameter> getEventParameters() {
		return eventParameters;
	}

	public void setEventParameters(List<EventParameter> eventParameters) {
		this.eventParameters = eventParameters;
	}

	@Override
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public Character getKeyChar() {
		return keyChar;
	}

	public void setKeyChar(Character keyChar) {
		this.keyChar = keyChar;
	}

	public Integer getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(Integer keyCode) {
		this.keyCode = keyCode;
	}

	public Boolean getShift() {
		return shift;
	}

	public void setShift(Boolean shift) {
		this.shift = shift;
	}

	public Boolean getAlt() {
		return alt;
	}

	public void setAlt(Boolean alt) {
		this.alt = alt;
	}

	public Boolean getControl() {
		return control;
	}

	public void setControl(Boolean control) {
		this.control = control;
	}

}
