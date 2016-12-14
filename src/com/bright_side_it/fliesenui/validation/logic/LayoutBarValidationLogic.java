package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
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
    	if ((!bar.isVisible()) && (bar.getID() == null)){
    		ValidationUtil.addError(project, screenDefinition, bar.getNodePath(), LayoutCellDAO.VISIBLE_ATTRIBUTE_NAME,
                    ProblemType.LAYOUT_BAR_INVISIBLE_WITHOUT_ID, "If the layot bar is made invisible, it needs to have an ID");
    	}
    }
}
