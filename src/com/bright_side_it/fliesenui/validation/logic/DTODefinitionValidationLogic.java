package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTOField;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class DTODefinitionValidationLogic {

    public void validate(Project project) {
    	for (DTODefinition i: BaseUtil.toEmptyMapIfNull(project.getDTODefinitionsMap()).values()){
    		validateDTODefinition(project, i);
    	}
    }

	private void validateDTODefinition(Project project, DTODefinition dtoDefinition) {
		if ((dtoDefinition.getFields() == null) || (dtoDefinition.getFields().isEmpty())){
			ValidationUtil.addError(project, dtoDefinition, dtoDefinition.getNodePath(), null, ProblemType.DTO_NO_FIELDS, "A DTO must have at least one field");
			return;
		}
		
		for (DTOField i: dtoDefinition.getFields().values()){
			if (i.getBasicType() == null){
				String dtoType = i.getDTOType();
				DTODefinition referencedDTODefinition = BaseUtil.toEmptyMapIfNull(project.getDTODefinitionsMap()).get(dtoType);
				if (referencedDTODefinition == null){
					ValidationUtil.addError(project, dtoDefinition, dtoDefinition.getNodePath(), null, ProblemType.DTO_FIELD_TYPE_UNKNOWN, "Unknown field type: '" + dtoType + "'");
				}
			}
		}
	}


}
