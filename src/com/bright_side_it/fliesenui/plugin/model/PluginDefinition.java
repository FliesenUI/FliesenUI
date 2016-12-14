package com.bright_side_it.fliesenui.plugin.model;

import java.util.EnumMap;
import java.util.Map;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;

public class PluginDefinition {
    private String id;
    private Map<String, PluginParameter> parameters;
    private Map<String, PluginVariable> variables;
    private Map<String, PluginEvent> events;
    private EnumMap<BrowserType, String> htmlCode;

    public EnumMap<BrowserType, String> getHtmlCode() {
        return htmlCode;
    }

    public void setHtmlCode(EnumMap<BrowserType, String> htmlCode) {
        this.htmlCode = htmlCode;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Map<String, PluginParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, PluginParameter> parameters) {
        this.parameters = parameters;
    }

    public Map<String, PluginVariable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, PluginVariable> variables) {
        this.variables = variables;
    }

    public Map<String, PluginEvent> getEvents() {
        return events;
    }

    public void setEvents(Map<String, PluginEvent> events) {
        this.events = events;
    }


}
