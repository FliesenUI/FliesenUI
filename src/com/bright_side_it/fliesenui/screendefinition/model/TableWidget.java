package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class TableWidget implements CellItem, EventParameterContainer {
    public enum Style {
        NORMAL, SMALL
    }

    private String id;
    private String dto;
    private String idDTOField;
    private String backgroundColorDTOField;
    private List<TableWidgetColumn> columns;
    private Style style;
    private NodePath nodePath;
    private Integer contentHeight;
    private List<EventParameter> eventParameters;
    private boolean showColumnHeader;
    private boolean showFilter;
    private boolean rowCheckboxes;

    @Override
    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }

    public String getDTO() {
        return dto;
    }

    public void setDTO(String dto) {
        this.dto = dto;
    }

    public String getIDDTOField() {
        return idDTOField;
    }

    public void setIDDTOField(String idField) {
        this.idDTOField = idField;
    }

    public List<TableWidgetColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<TableWidgetColumn> columns) {
        this.columns = columns;
    }

    @Override
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public Integer getContentHeight() {
        return contentHeight;
    }

    public void setContentHeight(Integer contentHeight) {
        this.contentHeight = contentHeight;
    }

    @Override
    public List<EventParameter> getEventParameters() {
        return eventParameters;
    }

    @Override
    public void setEventParameters(List<EventParameter> eventParameters) {
        this.eventParameters = eventParameters;
    }

	public String getBackgroundColorDTOField() {
		return backgroundColorDTOField;
	}

	public void setBackgroundColorDTOField(String backgroundColorDTOField) {
		this.backgroundColorDTOField = backgroundColorDTOField;
	}

	public boolean isShowColumnHeader() {
		return showColumnHeader;
	}

	public void setShowColumnHeader(boolean showColumnHeader) {
		this.showColumnHeader = showColumnHeader;
	}

	public boolean isShowFilter() {
		return showFilter;
	}

	public void setShowFilter(boolean showFilter) {
		this.showFilter = showFilter;
	}

	public boolean isRowCheckboxes() {
		return rowCheckboxes;
	}

	public void setRowCheckboxes(boolean rowCheckboxes) {
		this.rowCheckboxes = rowCheckboxes;
	}
	
}
