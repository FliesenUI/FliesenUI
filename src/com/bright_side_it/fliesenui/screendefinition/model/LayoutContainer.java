package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class LayoutContainer implements CellItem, ScreenTopElement {
    public enum Orientation {
        ROW, COLUMN
    }

    private List<LayoutBar> bars;

    private Orientation orientation;
    private NodePath nodePath;
    private String id;
    private boolean visible;


    public List<LayoutBar> getBars() {
        return bars;
    }

    public void setBars(List<LayoutBar> bars) {
        this.bars = bars;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }

    @Override
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
