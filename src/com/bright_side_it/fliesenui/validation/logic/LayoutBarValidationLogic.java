package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.LayoutBarDAO;
import com.bright_side_it.fliesenui.screendefinition.dao.LayoutCellDAO;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class LayoutBarValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (LayoutBar i : BaseUtil.getAllLayoutBars(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, LayoutBar bar) {
    	validateVisibilityWithoutID(project, screenDefinition, bar);
    	validatePositionInBorderLayout(project, screenDefinition, bar);
    }

	private void validatePositionInBorderLayout(Project project, ScreenDefinition screenDefinition, LayoutBar bar) {
		if (bar.isInBorderLayout()){
			if (bar.getPosition() == null){
				ValidationUtil.addError(project, screenDefinition, bar.getNodePath(), null
						, ProblemType.LAYOUT_BAR_POSITION_MISSING_IN_BORDER_LAYOUT
						, "This bar is located inside a container with border layout. In this case a position needs to be specified.  "
						 + "Attribute: " + LayoutBarDAO.POSITION_ATTRIBUTE_NAME);
			}
		} else {
			if (bar.getPosition() != null){
				ValidationUtil.addError(project, screenDefinition, bar.getNodePath(), LayoutBarDAO.POSITION_ATTRIBUTE_NAME
						, ProblemType.LAYOUT_BAR_POSITION_BUT_NO_BORDER_LAYOUT
						, "This bar is not located inside a container with border layout. In this case no position should be specified");
			}
		}
	}

	private void validateVisibilityWithoutID(Project project, ScreenDefinition screenDefinition, LayoutBar bar) {
		if ((!bar.isVisible()) && (bar.getID() == null)){
			ValidationUtil.addError(project, screenDefinition, bar.getNodePath(), LayoutBarDAO.VISIBLE_ATTRIBUTE_NAME,
					ProblemType.LAYOUT_BAR_INVISIBLE_WITHOUT_ID, "If the layot bar is made invisible, it needs to have an ID");
		}
	}
}
