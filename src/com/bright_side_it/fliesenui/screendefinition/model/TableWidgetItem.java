package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class TableWidgetItem implements EventParameterContainer, ImageSourceContainer {
    public enum TableWidgetType {
        LABEL, BUTTON, IMAGE, IMAGE_BUTTON
    }

    private TableWidgetType type;
    private String text;
    private String id;
    private String textDTOField;
    private String tooltipDTOField;
    private boolean onlyShowOnHover;
    
    private NodePath nodePath;
    private List<EventParameter> eventParameters;
	private ImageSource imageSource;

    @Override
    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }

    public String getTextDTOField() {
        return textDTOField;
    }

    public void setTextDTOField(String textDTOField) {
        this.textDTOField = textDTOField;
    }

    public TableWidgetType getType() {
        return type;
    }

    public void setType(TableWidgetType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    @Override
    public List<EventParameter> getEventParameters() {
        return eventParameters;
    }

    @Override
    public void setEventParameters(List<EventParameter> eventParameters) {
        this.eventParameters = eventParameters;
    }

	@Override
	public void setImageSource(ImageSource imageSource) {
		this.imageSource = imageSource;
	}

	@Override
	public ImageSource getImageSource() {
		return imageSource;
	}

	public String getTooltipDTOField() {
		return tooltipDTOField;
	}

	public void setTooltipDTOField(String tooltipDTOField) {
		this.tooltipDTOField = tooltipDTOField;
	}

	public boolean isOnlyShowOnHover() {
		return onlyShowOnHover;
	}

	public void setOnlyShowOnHover(boolean onlyShowOnHover) {
		this.onlyShowOnHover = onlyShowOnHover;
	}

	
}
