package com.bright_side_it.fliesenui.screendefinition.model;

import java.util.List;

public class ScreenDefinitionDAOResult {
    private ScreenDefinition screenDefinition;
    private List<ResourceDefinitionProblem> problems;

    public List<ResourceDefinitionProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<ResourceDefinitionProblem> problems) {
        this.problems = problems;
    }

    public ScreenDefinition getScreenDefinition() {
        return screenDefinition;
    }

    public void setScreenDefinition(ScreenDefinition screenDefinition) {
        this.screenDefinition = screenDefinition;
    }

}
