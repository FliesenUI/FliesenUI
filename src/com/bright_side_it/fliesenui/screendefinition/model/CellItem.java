package com.bright_side_it.fliesenui.screendefinition.model;

/**
 * a cell item is what can be inside a cell: a basic widget, a table widget or a container
 */
public interface CellItem {
    String getID();

    NodePath getNodePath();
}
