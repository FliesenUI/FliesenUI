package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.DTODeclarationDAO;
import com.bright_side_it.fliesenui.screendefinition.dao.ScreenDefinitionDAO;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class ScreenValidationLogic {
    public void validate(Project project) {
    	for (ScreenDefinition i: project.getScreenDefinitionsMap().values()){
    		validateScreen(project, i);
    	}
    }

	private void validateScreen(Project project, ScreenDefinition screenDefinition) {
		for (DTODeclaration i: BaseUtil.toEmptyMapIfNull(screenDefinition.getDTODeclarations()).values()){
			validateDTODeclaration(project, screenDefinition, i);
		}
		
		String dtoInstance = screenDefinition.getParameterDTOID();
		if (dtoInstance != null){
			if (!BaseUtil.toEmptyMapIfNull(screenDefinition.getDTODeclarations()).keySet().contains(dtoInstance)){
        		ValidationUtil.addError(project, screenDefinition, screenDefinition.getNodePath(), ScreenDefinitionDAO.PARAMETER_DTO_ATTRIBUTE_NAME,
        				ProblemType.SCREEN_DEFINITION_UNKNOWN_PARAMETER_DTO, "No DTO with the id '" + dtoInstance + "' has been declared in the screen");
			}
		}
	}

	private void validateDTODeclaration(Project project, ScreenDefinition screenDefinition, DTODeclaration dtoDeclaration) {
		String type = dtoDeclaration.getType();
		if (!BaseUtil.toEmptyMapIfNull(project.getDTODefinitionsMap()).keySet().contains(type)){
    		ValidationUtil.addError(project, screenDefinition, dtoDeclaration.getNodePath(), DTODeclarationDAO.TYPE_ATTRIBUTE_NAME,
    				ProblemType.SCREEN_DEFINITION_UNKNOWN_DTO_TYPE, "No DTO with the type '" + type + "' exists in the project in the screen");
		}
	}
}
