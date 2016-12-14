package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.LayoutCellDAO;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class LayoutContainerValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (LayoutContainer i : BaseUtil.getAllLayoutContainers(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, LayoutContainer container) {
    	if ((!container.isVisible()) && (container.getID() == null)){
    		ValidationUtil.addError(project, screenDefinition, container.getNodePath(), LayoutCellDAO.VISIBLE_ATTRIBUTE_NAME,
                    ProblemType.LAYOUT_CONTAINER_INVISIBLE_WITHOUT_ID, "If the layot container is made invisible, it needs to have an ID");
    	}
    }
}
