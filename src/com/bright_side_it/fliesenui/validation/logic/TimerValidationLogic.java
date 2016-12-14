package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.TimerDAO;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.Timer;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class TimerValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            validate(project, screenDefinition);
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition) {
    	for (Timer i: BaseUtil.getAllTimers(screenDefinition)){
    		if (i.getIntervalInMillis() < 100){
    			ValidationUtil.addError(project, screenDefinition, i.getNodePath(), TimerDAO.INTERVAL_IN_MILLIS_ATTRIBUTE_NAME, ProblemType.TIMER_INTERVAL_TOO_SHORT,
    					"The timer interval must be at least 100 milliseconds");
    		}
    	}
    }

}
