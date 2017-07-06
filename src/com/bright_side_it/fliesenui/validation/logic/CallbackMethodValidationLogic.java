package com.bright_side_it.fliesenui.validation.logic;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.CallbackMethodDAO;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethod;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class CallbackMethodValidationLogic {

    public void validate(Project project) {
    	for (ScreenDefinition i: project.getScreenDefinitionsMap().values()){
    		validate(project, i);
    	}
    }

	private void validate(Project project, ScreenDefinition screenDefinition) {
		if ((screenDefinition.getCallbackMethods() == null) || (screenDefinition.getCallbackMethods().isEmpty())){
			return;
		}
		validateMethodNamesValid(project, screenDefinition, screenDefinition.getCallbackMethods());
		validateMethodNamesUnique(project, screenDefinition, screenDefinition.getCallbackMethods());
	}

	private void validateMethodNamesValid(Project project, ScreenDefinition screenDefinition, List<CallbackMethod> callbackMethods) {
		for (CallbackMethod i: callbackMethods){
			if (i.getName().isEmpty()){
				ValidationUtil.addError(project, screenDefinition, i.getNodePath(), CallbackMethodDAO.NAME_ATTRIBUTE_NAME, ProblemType.CALLBACK_METHOD_NAME_EMPTY,
						"The name may not be empty");
			}
			if (!ValidationUtil.isValidJavaVariableName(i.getName())){
				ValidationUtil.addError(project, screenDefinition, i.getNodePath(), CallbackMethodDAO.NAME_ATTRIBUTE_NAME, ProblemType.CALLBACK_METHOD_NAME_WRONG,
						"The name is invalid. The name must start with a lowercase character and contain only a-z, A-Z and 0-9");
			}
		}
	}

	private void validateMethodNamesUnique(Project project, ScreenDefinition screenDefinition, List<CallbackMethod> callbackMethods) {
		Set<String> usedNames = new TreeSet<String>();
		for (CallbackMethod i: callbackMethods){
			if (usedNames.contains(i.getName().toLowerCase())){
				ValidationUtil.addError(project, screenDefinition, i.getNodePath(), CallbackMethodDAO.NAME_ATTRIBUTE_NAME, ProblemType.CALLBACK_METHOD_NAME_USED_MULTIPLE_TIMES,
						"The name '" + i.getName() + "' is used multiple times");
			}
			usedNames.add(i.getName().toLowerCase());
		}
	}


}
