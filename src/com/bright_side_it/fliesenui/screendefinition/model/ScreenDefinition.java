package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;
import java.util.Map;

import com.bright_side_it.fliesenui.project.model.ResourceDefinition;

public class ScreenDefinition implements ResourceDefinition {
    private List<ScreenTopElement> topElements;
    private Map<String, DTODeclaration> dtoDeclarations;
    private Map<String, Timer> timers;
    private String title;
    private String parameterDTOID;
    private String id;
    private NodePath nodePath;

    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }

    public List<ScreenTopElement> getTopElements() {
        return topElements;
    }

    public void setTopElements(List<ScreenTopElement> layoutContainers) {
        this.topElements = layoutContainers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Map<String, DTODeclaration> getDTODeclarations() {
        return dtoDeclarations;
    }

    public void setDTODeclarations(Map<String, DTODeclaration> dtoDeclarations) {
        this.dtoDeclarations = dtoDeclarations;
    }

	public String getParameterDTOID() {
		return parameterDTOID;
	}

	public void setParameterDTOID(String parameterDTOID) {
		this.parameterDTOID = parameterDTOID;
	}

	public Map<String, Timer> getTimers() {
		return timers;
	}

	public void setTimers(Map<String, Timer> timers) {
		this.timers = timers;
	}

}
