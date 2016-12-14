package com.bright_side_it.fliesenui.screendefinition.model;

public class DTODeclaration {
    private String id;
    private String type;
    private NodePath nodePath;

    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
