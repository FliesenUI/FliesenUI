package com.bright_side_it.fliesenui.screendefinition.model;

public class CodeEditorWidget implements CellItem {
    private String text;
    private String id;
    private NodePath nodePath;

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


}
