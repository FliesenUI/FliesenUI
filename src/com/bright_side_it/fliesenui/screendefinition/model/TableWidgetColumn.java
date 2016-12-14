package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class TableWidgetColumn {
    private int size;
    private String text;
    private List<TableWidgetItem> tableItems;
    private NodePath nodePath;

    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TableWidgetItem> getTableItems() {
        return tableItems;
    }

    public void setTableItems(List<TableWidgetItem> tableItems) {
        this.tableItems = tableItems;
    }


}
