package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class BasicWidget implements CellItem, EventParameterContainer, ImageSourceContainer, EventHandlerContainer /*, EventListenerContainer*/ {
    public enum BasicWidgetType {
        LABEL, BUTTON, IMAGE_BUTTON, IMAGE, TEXT_FIELD, TEXT_AREA, PROGRESS_BAR, SPACE, CHECKBOX, SWITCH, FILE_UPLOAD, MARKDOWN_VIEW, HTML_VIEW
    }

    public enum Style {
        NORMAL, TINY, SMALL, MEDIUM, LARGE
    }

    private BasicWidgetType type;
    private String text;
    private String id;
    private String labelText;
    private boolean primary;
    private String textDTOField;
    private NodePath nodePath;
    private Integer height;
    private Style style;
    private boolean visible;
    private Boolean readOnly;
    private ImageSource imageSource;
    private List<EventParameter> eventParameters;
    private List<EventHandler> eventHandlers;
    private Boolean scrollToBottom;
    private Boolean selectOnFocus;
//    private List<EventListener> eventListeners;

    @Override
    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }

    public BasicWidgetType getType() {
        return type;
    }

    public void setType(BasicWidgetType type) {
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

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getTextDTOField() {
        return textDTOField;
    }

    public void setTextDTOField(String dtoField) {
        this.textDTOField = dtoField;
    }

    @Override
    public List<EventParameter> getEventParameters() {
        return eventParameters;
    }

    @Override
    public void setEventParameters(List<EventParameter> eventParameters) {
        this.eventParameters = eventParameters;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public ImageSource getImageSource() {
        return imageSource;
    }

    @Override
    public void setImageSource(ImageSource imageSource) {
        this.imageSource = imageSource;
    }

    @Override
	public List<EventHandler> getEventHandlers() {
		return eventHandlers;
	}

    @Override
	public void setEventHandlers(List<EventHandler> eventHandlers) {
		this.eventHandlers = eventHandlers;
	}

	public Boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Boolean getScrollToBottom() {
		return scrollToBottom;
	}

	public void setScrollToBottom(Boolean scrollToBottom) {
		this.scrollToBottom = scrollToBottom;
	}

	public Boolean getSelectOnFocus() {
		return selectOnFocus;
	}

	public void setSelectOnFocus(Boolean selectOnFocus) {
		this.selectOnFocus = selectOnFocus;
	}
//
//	public List<EventListener> getEventListeners() {
//		return eventListeners;
//	}
//
//	public void setEventListeners(List<EventListener> eventListeners) {
//		this.eventListeners = eventListeners;
//	}

}
