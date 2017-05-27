package com.bright_side_it.fliesenui.stringres.model;

import java.util.List;

import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;

public class StringResourceDAOResult {
    private StringResource stringResource;
    private List<ResourceDefinitionProblem> problems;

    public StringResource getStringResource() {
		return stringResource;
	}

	public void setStringResource(StringResource stringResource) {
		this.stringResource = stringResource;
	}

	public List<ResourceDefinitionProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<ResourceDefinitionProblem> problems) {
        this.problems = problems;
    }


}
