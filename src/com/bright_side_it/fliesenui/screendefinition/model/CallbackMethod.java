package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class CallbackMethod {
	public enum CallbackType {LIST_CHOOSER, STRING_INPUT, CONFIRM}
	
	private CallbackType type;
	private String name;
	private List<CallbackMethodParameter> parameters;
	private NodePath nodePath;
	
	public CallbackType getType() {
		return type;
	}
	public void setType(CallbackType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CallbackMethodParameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<CallbackMethodParameter> parameters) {
		this.parameters = parameters;
	}
	public NodePath getNodePath() {
		return nodePath;
	}
	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}

}
