package com.bright_side_it.fliesenui.project.model;

public class AssistValue {
    private String label;
    private String apiDocLabel;
    private String text;
    private Boolean mandatory;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getApiDocLabel() {
		return apiDocLabel;
	}

	public void setApiDocLabel(String apiDocLabel) {
		this.apiDocLabel = apiDocLabel;
	}
	
}
