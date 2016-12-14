package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;
import java.util.Map;

public class PluginInstance implements ScreenTopElement, CellItem, EventParameterContainer {
    private String id;
    private String pluginType;
    private Map<String, String> parameterValues;
    private NodePath nodePath;
    private List<EventParameter> eventParameters;

    @Override
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getPluginType() {
        return pluginType;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    public Map<String, String> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(Map<String, String> parameterValues) {
        this.parameterValues = parameterValues;
    }

    @Override
    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
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
