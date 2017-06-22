package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class LayoutContainer implements CellItem, ScreenTopElement {
    public enum Orientation {
        ROW, COLUMN, BORDER_LAYOUT
    }

    private List<LayoutBar> bars;

    private Orientation orientation;
    private NodePath nodePath;
    private String id;
    private boolean visible;
    private Double leftSizeInCM;
    private Double rightSizeInCM;
    private Double topSizeInCM;
    private Double bottomSizeInCM;
    private boolean isTopContainer;
    private UnitValue height;

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

	public Double getLeftSizeInCM() {
		return leftSizeInCM;
	}

	public void setLeftSizeInCM(Double leftSizeInCM) {
		this.leftSizeInCM = leftSizeInCM;
	}

	public Double getRightSizeInCM() {
		return rightSizeInCM;
	}

	public void setRightSizeInCM(Double rightSizeInCM) {
		this.rightSizeInCM = rightSizeInCM;
	}

	public Double getTopSizeInCM() {
		return topSizeInCM;
	}

	public void setTopSizeInCM(Double topSizeInCM) {
		this.topSizeInCM = topSizeInCM;
	}

	public Double getBottomSizeInCM() {
		return bottomSizeInCM;
	}

	public void setBottomSizeInCM(Double bottomSizeInCM) {
		this.bottomSizeInCM = bottomSizeInCM;
	}

	public boolean isTopContainer() {
		return isTopContainer;
	}

	public void setTopContainer(boolean isTopContainer) {
		this.isTopContainer = isTopContainer;
	}

	public UnitValue getHeight() {
		return height;
	}

	public void setHeight(UnitValue height) {
		this.height = height;
	}

}
