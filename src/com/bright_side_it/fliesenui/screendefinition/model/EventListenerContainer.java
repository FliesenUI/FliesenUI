package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public interface EventListenerContainer {
    List<EventListener> getEventListeners();

    void setEventListeners(List<EventListener> eventListeners);

    String getID();

    NodePath getNodePath();


}
