package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.project.dao.ProjectDefinitionDAO;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class ProjectDefinitionValidationLogic {
    public void validate(Project project) {
    	
    	ProjectDefinition projectDefinition = project.getProjectDefinition();
    	
    	if (projectDefinition.getStartScreenID() == null){
    		ValidationUtil.addProjectDefinitionError(project, projectDefinition.getNodePath(), ProjectDefinitionDAO.START_SCREEN_ID_ATTRIBUTE_NAME, ProblemType.PROJECT_START_SCREEN_MISSING, "a start screen has to be specified");
    	} else if (!project.getScreenDefinitionsMap().containsKey(projectDefinition.getStartScreenID())){
    		ValidationUtil.addProjectDefinitionError(project, projectDefinition.getNodePath(), ProjectDefinitionDAO.START_SCREEN_ID_ATTRIBUTE_NAME, ProblemType.PROJECT_START_SCREEN_UNKNOWN, "Unknown screen: '" + projectDefinition.getStartScreenID() + "'");
    	}
    }

}
