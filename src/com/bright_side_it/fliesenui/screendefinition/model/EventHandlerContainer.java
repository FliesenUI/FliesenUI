package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public interface EventHandlerContainer {
    List<EventHandler> getEventHandlers();

    void setEventHandlers(List<EventHandler> eventHandlers);

    String getID();

    NodePath getNodePath();


}
