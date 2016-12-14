package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;

public class ProjectProblemsStringWriterLogic {

    public String problemsToString(Project project) {
        StringBuilder sb = new StringBuilder();
        if (BaseUtil.isNotNullAndNotEmpty(project.getProjectDefinitionProblems())) {
            sb.append("Project definition problems:\n");
            for (ResourceDefinitionProblem problem : project.getProjectDefinitionProblems()) {
                sb.append(toString(problem));
            }
        }
        if (BaseUtil.isNotNullAndNotEmpty(project.getDTODefinitionProblemsMap())) {
            sb.append("DTO definition problems:\n");
            for (String dtoProblemKey : project.getDTODefinitionProblemsMap().keySet()) {
                for (ResourceDefinitionProblem problem : project.getDTODefinitionProblemsMap().get(dtoProblemKey)) {
                    sb.append(toString(problem));
                }
            }
        }
        if (BaseUtil.isNotNullAndNotEmpty(project.getPluginDefinitionProblemsMap())) {
            sb.append("Plugin definition problems:\n");
            for (String pluginProblemKey : project.getPluginDefinitionProblemsMap().keySet()) {
                for (ResourceDefinitionProblem problem : project.getPluginDefinitionProblemsMap().get(pluginProblemKey)) {
                    sb.append(toString(problem));
                }
            }
        }
        if (BaseUtil.isNotNullAndNotEmpty(project.getScreenDefinitionProblemsMap())) {
            sb.append("Screen definition problems:\n");
            for (String screenProblemKey : project.getScreenDefinitionProblemsMap().keySet()) {
                for (ResourceDefinitionProblem problem : project.getScreenDefinitionProblemsMap().get(screenProblemKey)) {
                    sb.append(toString(problem));
                }
            }
        }
        return sb.toString();
    }

    private String toString(ResourceDefinitionProblem problem) {
        return " - [Location: " + BaseUtil.nodePathToString(problem.getNodePath()) + "] [Type: " + problem.getType() + "] " + problem.getMessage() + "\n";
    }


}
