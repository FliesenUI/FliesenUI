package com.bright_side_it.fliesenui.dto.model;

import java.util.Map;

import com.bright_side_it.fliesenui.project.model.ResourceDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;

public class DTODefinition implements ResourceDefinition {
    private String id;
    private Map<String, DTOField> fields;
    private NodePath nodePath;
    private boolean derived;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Map<String, DTOField> getFields() {
        return fields;
    }

    public void setFields(Map<String, DTOField> fields) {
        this.fields = fields;
    }

	public NodePath getNodePath() {
		return nodePath;
	}

	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}

	public boolean isDerived() {
		return derived;
	}

	public void setDerived(boolean derived) {
		this.derived = derived;
	}

}
