package com.bright_side_it.fliesenui.plugin.model;

import java.util.List;

import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;

public class PluginDefinitionDAOResult {
    private PluginDefinition pluginDefinition;
    private List<ResourceDefinitionProblem> problems;

    public List<ResourceDefinitionProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<ResourceDefinitionProblem> problems) {
        this.problems = problems;
    }

    public PluginDefinition getPluginDefinition() {
        return pluginDefinition;
    }

    public void setPluginDefinition(PluginDefinition pluginDefinition) {
        this.pluginDefinition = pluginDefinition;
    }

}
