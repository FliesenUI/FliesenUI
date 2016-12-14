package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class SelectBox implements CellItem, EventParameterContainer{
	private String id;
    private String dto;
    private String idDTOField;
    private String selectedIDDTOField;
    private String labelDTOField;
    private NodePath nodePath;
    private List<EventParameter> eventParameters;
    
    @Override
	public String getID() {
		return id;
	}
	public void setID(String id) {
		this.id = id;
	}
	public String getDTO() {
		return dto;
	}
	public void setDTO(String dto) {
		this.dto = dto;
	}
	public String getIDDTOField() {
		return idDTOField;
	}
	public void setIDDTOField(String idDTOField) {
		this.idDTOField = idDTOField;
	}
	public String getLabelDTOField() {
		return labelDTOField;
	}
	public void setLabelDTOField(String labelDTOField) {
		this.labelDTOField = labelDTOField;
	}
	public NodePath getNodePath() {
		return nodePath;
	}
	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}
	@Override
	public List<EventParameter> getEventParameters() {
		return eventParameters;
	}
	@Override
	public void setEventParameters(List<EventParameter> eventParameters) {
		this.eventParameters = eventParameters;
	}
	public String getSelectedIDDTOField() {
		return selectedIDDTOField;
	}
	public void setSelectedIDDTOField(String selectedIDDTOField) {
		this.selectedIDDTOField = selectedIDDTOField;
	}

}
