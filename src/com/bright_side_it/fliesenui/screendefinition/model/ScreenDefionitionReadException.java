package com.bright_side_it.fliesenui.screendefinition.model;

public class ScreenDefionitionReadException extends Exception {
    private static final long serialVersionUID = -7522834312479895234L;
    private NodePath nodePath;
    private String attributeName;

    public ScreenDefionitionReadException(String message, NodePath nodePath, String attributeName) {
        super(message);
        this.nodePath = nodePath;
        this.attributeName = attributeName;
    }

    public NodePath getNodePath() {
        return nodePath;
    }

    public String getAttributeName() {
        return attributeName;
    }

}
