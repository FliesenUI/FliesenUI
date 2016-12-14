package com.bright_side_it.fliesenui.validation.logic;

import java.util.Set;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.SharedReplyInterface;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class SharedReplyInterfaceValidationLogic {
	public void validate(Project project) {
		for (SharedReplyInterface i: BaseUtil.toEmptyMapIfNull(project.getProjectDefinition().getSharedReplyInterfaces()).values()){
			validate(project, i);
		}
	}

	private void validate(Project project, SharedReplyInterface sharedReplyInterface) {
		if (BaseUtil.toEmptyCollectionIfNull(sharedReplyInterface.getScreenIDs()).size() == 0){
			ValidationUtil.addProjectDefinitionError(project, sharedReplyInterface.getNodePath(), null, ProblemType.SHARED_REPLY_INTERFACE_NO_SCREENS_DEFINED,
                    "No screens are defined. Define screens in child nodes");
			return;
		}
		
		for (String i: sharedReplyInterface.getScreenIDs()){
			if (!project.getScreenDefinitionsMap().containsKey(i)){
				ValidationUtil.addProjectDefinitionError(project, sharedReplyInterface.getNodePath(), null, ProblemType.SHARED_REPLY_INTERFACE_UNKNOWN_SCREEN,
	                    "There is no screen with ID '" + i + "'");
			}
		}

		Set<String> usedScreenIDs = new TreeSet<>();
		for (String i: sharedReplyInterface.getScreenIDs()){
			if (usedScreenIDs.contains(i)){
				ValidationUtil.addProjectDefinitionError(project, sharedReplyInterface.getNodePath(), null, ProblemType.SHARED_REPLY_INTERFACE_SCREEN_OCCURS_MULTIPLE_TIMES,
	                    "The screen with ID '" + i + "' occurs multiple times");
				
			} else {
				usedScreenIDs.add(i);
			}
		}
	}
}
