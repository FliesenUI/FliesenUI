package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class CodeEditorWidget implements CellItem, EventListenerContainer {
    public enum CodeEditorWidgetType {
        CODE_EDITOR, TEXT_EDITOR
    }
	
    private String text;
    private String id;
    private NodePath nodePath;
    private List<EventListener> eventListeners;
    private CodeEditorWidgetType type;
    private Integer height;
    
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
    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }

	@Override
	public List<EventListener> getEventListeners() {
		return eventListeners;
	}

	@Override
	public void setEventListeners(List<EventListener> eventListeners) {
		this.eventListeners = eventListeners;
	}

	public CodeEditorWidgetType getType() {
		return type;
	}

	public void setType(CodeEditorWidgetType type) {
		this.type = type;
	}

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
