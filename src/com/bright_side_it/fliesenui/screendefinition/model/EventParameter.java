package com.bright_side_it.fliesenui.screendefinition.model;

/** a parameter that is send when an event occurs */
public class EventParameter {
    public enum WidgetProperty {
        TEXT, LINE, POS_IN_LINE, SELECTED, SELECTED_ID
    }

    private String dtoID;
    private String widgetID;
    private String pluginInstanceID;
    private String pluginVariableName;
    private WidgetProperty widgetProperty;

    public String getDTOID() {
        return dtoID;
    }

    public void setDTOID(String dtoID) {
        this.dtoID = dtoID;
    }

    public String getWidgetID() {
        return widgetID;
    }

    public void setWidgetID(String widgetID) {
        this.widgetID = widgetID;
    }

    public WidgetProperty getWidgetProperty() {
        return widgetProperty;
    }

    public void setWidgetProperty(WidgetProperty widgetProperty) {
        this.widgetProperty = widgetProperty;
    }

    public String getPluginVariableName() {
        return pluginVariableName;
    }

    public void setPluginVariableName(String pluginVariableName) {
        this.pluginVariableName = pluginVariableName;
    }

    public String getPluginInstanceID() {
        return pluginInstanceID;
    }

    public void setPluginInstanceID(String pluginInstanceID) {
        this.pluginInstanceID = pluginInstanceID;
    }

}
