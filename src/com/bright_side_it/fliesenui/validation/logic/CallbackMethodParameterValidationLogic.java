package com.bright_side_it.fliesenui.validation.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.CallbackMethodDAO;
import com.bright_side_it.fliesenui.screendefinition.dao.CallbackMethodParameterDAO;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethod;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethodParameter;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethodParameter.ParameterType;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class CallbackMethodParameterValidationLogic {

    public void validate(Project project) {
    	Set<String> dtoNames = new HashSet<>(project.getDTODefinitionsMap().keySet());
    	
    	for (ScreenDefinition screenDefinition: project.getScreenDefinitionsMap().values()){
    		for (CallbackMethod callbackMethod: BaseUtil.toEmptyCollectionIfNull(screenDefinition.getCallbackMethods())){
    			validate(project, screenDefinition, callbackMethod, dtoNames);
    		}
    	}
    }

	private void validate(Project project, ScreenDefinition screenDefinition, CallbackMethod callbackMethod, Set<String> dtoNames) {
		validateParameterNamesUniqu(project, screenDefinition, callbackMethod.getParameters());
		for (CallbackMethodParameter i: callbackMethod.getParameters()){
			validateParameterName(project, screenDefinition, i);
			validateParameterType(project, screenDefinition, i, dtoNames);
		}
	}

	private void validateParameterType(Project project, ScreenDefinition screenDefinition, CallbackMethodParameter parameter, Set<String> dtoNames) {
		if (parameter.getType() != ParameterType.DTO){
			return;
		}
		if ((parameter.getDTOClassName() == null) || (parameter.getDTOClassName().isEmpty())){
			ValidationUtil.addError(project, screenDefinition, parameter.getNodePath(), CallbackMethodParameterDAO.TYPE_ATTRIBUTE_NAME, ProblemType.CALLBACK_METHOD_PARAMETER_TYPE_MISSING,
					"No type has been specified");
		}
		if (!dtoNames.contains(parameter.getDTOClassName())){
			ValidationUtil.addError(project, screenDefinition, parameter.getNodePath(), CallbackMethodParameterDAO.TYPE_ATTRIBUTE_NAME, ProblemType.CALLBACK_METHOD_PARAMETER_TYPE_UNKNOWN,
					"This type is unknown. It needs to be either the name of a DTO class (not declaration in screen) or " + new CallbackMethodParameterDAO().getUnaryTypeNames());
		}
	}

	private void validateParameterNamesUniqu(Project project, ScreenDefinition screenDefinition, List<CallbackMethodParameter> parameters) {
		Set<String> usedNames = new TreeSet<String>();
		for (CallbackMethodParameter i: parameters){
			if (usedNames.contains(i.getName().toLowerCase())){
				ValidationUtil.addError(project, screenDefinition, i.getNodePath(), CallbackMethodParameterDAO.NAME_ATTRIBUTE_NAME, ProblemType.CALLBACK_METHOD_PARAMETER_NAME_USED_MULTIPLE_TIMES,
						"The name '" + i.getName() + "' is used multiple times");
			}
			usedNames.add(i.getName().toLowerCase());
		}
	}

	private void validateParameterName(Project project, ScreenDefinition screenDefinition, CallbackMethodParameter parameter) {
		if (!ValidationUtil.isValidJavaVariableName(parameter.getName())){
			ValidationUtil.addError(project, screenDefinition, parameter.getNodePath(), CallbackMethodParameterDAO.NAME_ATTRIBUTE_NAME, ProblemType.CALLBACK_METHOD_PARAMETER_NAME_WRONG,
					"The name is invalid. The name must start with a lowercase character and contain only a-z, A-Z and 0-9");
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
			if (ValidationUtil.isValidJavaVariableName(i.getName())){
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
