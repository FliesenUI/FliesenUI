package com.bright_side_it.fliesenui.project.model;

import java.util.List;

import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;

public class ProjectDefinitionDAOResult {
    private ProjectDefinition projectDefinition;
    private List<ResourceDefinitionProblem> problems;

    public List<ResourceDefinitionProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<ResourceDefinitionProblem> problems) {
        this.problems = problems;
    }

    public ProjectDefinition getProjectDefinition() {
        return projectDefinition;
    }

    public void setProjectDefinition(ProjectDefinition projectDefinition) {
        this.projectDefinition = projectDefinition;
    }



}
