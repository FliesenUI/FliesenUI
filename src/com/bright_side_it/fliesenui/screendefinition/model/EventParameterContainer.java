package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public interface EventParameterContainer {
    List<EventParameter> getEventParameters();

    void setEventParameters(List<EventParameter> eventParameters);

    String getID();

    NodePath getNodePath();


}
