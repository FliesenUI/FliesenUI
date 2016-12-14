package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class LayoutCell {
    private List<CellItem> cellItems;
    private int size;
    private Integer height;
    private NodePath nodePath;
    private String backgroundColor;
    private String id;
    private boolean visible;

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

    public List<CellItem> getCellItems() {
        return cellItems;
    }

    public void setCellItems(List<CellItem> cellItems) {
        this.cellItems = cellItems;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

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
