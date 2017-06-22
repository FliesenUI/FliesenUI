package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class LayoutCell {
	public enum AlignType {
        LEFT, RIGHT, CENTER
    }
	public enum CellStyle {CARD}

	private List<CellItem> cellItems;
    private int size;
    private UnitValue height;
    private NodePath nodePath;
    private String backgroundColor;
    private String id;
    private boolean visible;
    private AlignType contentAlign;
    private CellStyle cellStyle;
    private String headlineText;
    private String subheadText;
    
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

	public UnitValue getHeight() {
		return height;
	}

	public void setHeight(UnitValue height) {
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

	public AlignType getContentAlign() {
		return contentAlign;
	}

	public void setContentAlign(AlignType contentAlign) {
		this.contentAlign = contentAlign;
	}

	public CellStyle getCellStyle() {
		return cellStyle;
	}

	public void setCellStyle(CellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}

	public String getHeadlineText() {
		return headlineText;
	}

	public void setHeadlineText(String headlineText) {
		this.headlineText = headlineText;
	}

	public String getSubheadText() {
		return subheadText;
	}

	public void setSubheadText(String subheadText) {
		this.subheadText = subheadText;
	}

}
