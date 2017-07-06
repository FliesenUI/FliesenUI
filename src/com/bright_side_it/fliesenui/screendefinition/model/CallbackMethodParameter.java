package com.bright_side_it.fliesenui.screendefinition.model;

public class CallbackMethodParameter {
	public enum ParameterType{NULLABLE_LONG, NON_NULLABE_LONG, STRING, NULLABLE_BOOLEAN
		, NON_NULLABLE_BOOLEAN, NULLABLE_INT, NON_NULLABLE_INT, DTO, LIST_OF_STRING}

	private String name;
	private ParameterType type;
	private String dtoClassName;
	private NodePath nodePath;
	
	public ParameterType getType() {
		return type;
	}
	public void setType(ParameterType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDTOClassName() {
		return dtoClassName;
	}
	public void setDTOClassName(String dtoClassName) {
		this.dtoClassName = dtoClassName;
	}
	public NodePath getNodePath() {
		return nodePath;
	}
	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}
	
}
