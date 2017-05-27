package com.bright_side_it.fliesenui.validation.logic;

import java.util.HashSet;
import java.util.Set;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.EventListenerDAO;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener.EventListenType;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;
import com.bright_side_it.fliesenui.screendefinition.model.EventListenerContainer;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class EventListenerValidationLogic {

	public void validate(Project project) {
		for (ScreenDefinition screenDefinition: project.getScreenDefinitionsMap().values()){
			for (EventListenerContainer i: BaseUtil.getAllEventListenerContainers(screenDefinition)){
				validate(project, screenDefinition, i);
			}
		}
	}
	
	private void validate(Project project, ScreenDefinition screenDefinition, EventListenerContainer container) {
		if ((container.getEventListeners() == null) || (container.getEventListeners().isEmpty())){
			return;
		}
		
		validateEachCharKeyCodeModifierCombinationMayOccurOnlyOnce(project, screenDefinition, container);
		validateKeyPressNotPossibleWithControl(project, screenDefinition, container);
		validateCharsOnlyUpperCase(project, screenDefinition, container);
		validateEitherCharOrKeyCode(project, screenDefinition, container);
		validateAllKeyPressListenersMustHaveTheSameParameters(project, screenDefinition, container);
	}

	private void validateAllKeyPressListenersMustHaveTheSameParameters(Project project, ScreenDefinition screenDefinition, EventListenerContainer container) {
		String requiredParameterSignature = null;
		for (EventListener listener: BaseUtil.getAllEventListenersOfContainer(container, EventListenType.KEY_PRESS)){
			String parameterSignature = "";
			for (EventParameter parameter: BaseUtil.toEmptyCollectionIfNull(listener.getEventParameters())){
				if (parameter.getDTOID() != null){
					parameterSignature += "(dto:" + parameter.getDTOID() + ")";	
				}
				if (parameter.getPluginInstanceID() != null){
					parameterSignature += "(plugin" + parameter.getPluginInstanceID() + ")";	
				}
				if (parameter.getPluginVariableName() != null){
					parameterSignature += "(p-var" + parameter.getPluginVariableName() + ")";	
				}
				if (parameter.getWidgetID() != null){
					parameterSignature += "(widget" + parameter.getWidgetID() + ")";	
				}
				if (parameter.getWidgetProperty() != null){
					parameterSignature += "(property:" + parameter.getWidgetProperty() + ")";	
				}
				parameterSignature += ";";
			}
			if (requiredParameterSignature == null){
				requiredParameterSignature = parameterSignature;
			} else if (!requiredParameterSignature.equals(parameterSignature)){
				ValidationUtil.addError(project, screenDefinition, listener.getNodePath(), null, ProblemType.EVENT_LISTENER_DIFFERENT_PARAMETERS,
						"All key press listeners of this widgets must use the same event parameters");
			}
		}
	}
	
	private void validateEitherCharOrKeyCode(Project project, ScreenDefinition screenDefinition, EventListenerContainer container) {
		for (EventListener i: BaseUtil.getAllEventListenersOfContainer(container, EventListenType.KEY_PRESS, EventListenType.KEY_DOWN)){
			if ((i.getKeyChar() == null) && (i.getKeyCode() == null)){
				ValidationUtil.addError(project, screenDefinition, i.getNodePath(), null, ProblemType.EVENT_LISTENER_NEITHER_CHAR_NOR_KEY_CODE,
						"Either key char or key code need to be speciefied");
			} else if ((i.getKeyChar() != null) && (i.getKeyCode() != null)){
				ValidationUtil.addError(project, screenDefinition, i.getNodePath(), EventListenerDAO.KEY_CHAR_ATTRIBUTE_NAME, ProblemType.EVENT_LISTENER_CHAR_AND_CODE_SPECIFIED,
						"Either key char or key code need to be speciefied but not both");
			}
		}
	}
	
	private void validateCharsOnlyUpperCase(Project project, ScreenDefinition screenDefinition, EventListenerContainer container) {
		for (EventListener i: BaseUtil.getAllEventListenersOfContainer(container, EventListenType.KEY_PRESS, EventListenType.KEY_DOWN)){
			if ((i.getKeyChar() != null) && (i.getKeyChar().charValue() != Character.toUpperCase(i.getKeyChar().charValue()))){
				ValidationUtil.addError(project, screenDefinition, i.getNodePath(), EventListenerDAO.KEY_CHAR_ATTRIBUTE_NAME, ProblemType.EVENT_LISTENER_CHAR_NOT_UPPER_CASE,
						"The specified key character must be upper case: '" + i.getKeyChar().charValue() + "' -> '" + Character.toUpperCase(i.getKeyChar().charValue()) + "'");
			}
		}
	}
	
	private void validateKeyPressNotPossibleWithControl(Project project, ScreenDefinition screenDefinition, EventListenerContainer container) {
		for (EventListener i: BaseUtil.getAllEventListenersOfContainer(container, EventListenType.KEY_PRESS)){
			if (Boolean.TRUE.equals(i.getControl())){
				ValidationUtil.addError(project, screenDefinition, i.getNodePath(), EventListenerDAO.CONTROL_ATTRIBUTE_NAME, ProblemType.EVENT_LISTENER_KEY_PRESS_WITH_CONTROL,
						"Key combinations with control are only transmitted in key-down events and not in key press events.");
			}
		}
	}

	private void validateEachCharKeyCodeModifierCombinationMayOccurOnlyOnce(Project project, ScreenDefinition screenDefinition, EventListenerContainer container) {
		Set<String> usedCharactersAndModifiers = new HashSet<String>();
		for (EventListener i: BaseUtil.getAllEventListenersOfContainer(container, EventListenType.KEY_PRESS, EventListenType.KEY_DOWN)){
			String combinationString = i.getShift() + ";" + i.getAlt() + ";" + i.getControl() + ";" + i.getKeyCode() + ";" + i.getKeyChar();
			if (usedCharactersAndModifiers.contains(combinationString)){
				ValidationUtil.addError(project, screenDefinition, i.getNodePath(), EventListenerDAO.KEY_CHAR_ATTRIBUTE_NAME, ProblemType.EVENT_LISTENER_CHAR_USED_MULTIPLE_TIMES,
						"The combintion '" + combinationString + "' is used multiple times.");
			} else {
				usedCharactersAndModifiers.add(combinationString);
			}
		}
	}

}
