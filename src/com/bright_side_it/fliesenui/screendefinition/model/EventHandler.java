package com.bright_side_it.fliesenui.screendefinition.model;

/**
 * event that is triggered by the UI, like button-click, sreen-load, etc.
 * and which may execute an action like open a screen/URL, set a DTO-value etc.
 * @author me
 *
 */
public class EventHandler {
	public enum EventType{CLICK}
	
	private EventType eventType;
	private String urlToOpen;
	private boolean openURLInNewWindow;
	private String screenToOpen;
	private String openScreenParameterDTO;
	
	public String getURLToOpen() {
		return urlToOpen;
	}
	public void setURLToOpen(String urlToOpen) {
		this.urlToOpen = urlToOpen;
	}
	public boolean isOpenURLInNewWindow() {
		return openURLInNewWindow;
	}
	public void setOpenURLInNewWindow(boolean openURLInNewWindow) {
		this.openURLInNewWindow = openURLInNewWindow;
	}
	public String getScreenToOpen() {
		return screenToOpen;
	}
	public void setScreenToOpen(String screenToOpen) {
		this.screenToOpen = screenToOpen;
	}
	public String getOpenScreenParameterDTO() {
		return openScreenParameterDTO;
	}
	public void setOpenScreenParameterDTO(String openScreenParameterDTO) {
		this.openScreenParameterDTO = openScreenParameterDTO;
	}
	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}


}
