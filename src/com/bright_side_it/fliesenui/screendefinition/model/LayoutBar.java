package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class LayoutBar {
	public enum Position {LEFT, TOP, RIGHT, BOTTOM, CENTER}
	
    private List<LayoutCell> cells;
    private NodePath nodePath;
    private boolean visible;
    private String id;
    private boolean inBorderLayout;
    private Position position;
//    private UnitValue height;

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

	public boolean isInBorderLayout() {
		return inBorderLayout;
	}

	public void setInBorderLayout(boolean inBorderLayout) {
		this.inBorderLayout = inBorderLayout;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

//	public UnitValue getHeight() {
//		return height;
//	}
//
//	public void setHeight(UnitValue height) {
//		this.height = height;
//	}
	
}
