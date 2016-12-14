package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class LayoutBar {
    private List<LayoutCell> cells;
    private NodePath nodePath;
    private boolean visible;
    private String id;

    public List<LayoutCell> getCells() {
        return cells;
    }

    public void setCells(List<LayoutCell> cells) {
        this.cells = cells;
    }

    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	
}
