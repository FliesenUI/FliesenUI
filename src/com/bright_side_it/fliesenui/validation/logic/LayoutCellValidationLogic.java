package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.LayoutCellDAO;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class LayoutCellValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (LayoutCell i : BaseUtil.getAllLayoutCells(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, LayoutCell cell) {
    	if ((!cell.isVisible()) && (cell.getID() == null)){
    		ValidationUtil.addError(project, screenDefinition, cell.getNodePath(), LayoutCellDAO.VISIBLE_ATTRIBUTE_NAME,
                    ProblemType.CELL_INVISIBLE_WITHOUT_ID, "If the cell is made invisible, it needs to have an ID");
    	}
    	
        if (cell.getBackgroundColor() != null) {
            validateColor(project, screenDefinition, cell, cell.getBackgroundColor(), LayoutCellDAO.BACKGROUND_COLOR_ATTRIBUTE_NAME);
        }
        if (cell.getHeight() != null) {
            validateHeight(project, screenDefinition, cell);
        }
    }

    private void validateHeight(Project project, ScreenDefinition screenDefinition, LayoutCell cell) {
        if (cell.getHeight() <= 0) {
            ValidationUtil.addError(project, screenDefinition, cell.getNodePath(), LayoutCellDAO.HEIGHT_ATTRIBUTE_NAME,
                    ProblemType.CELL_WRONG_HEIGHT, "Wrong cell height. It must be > 0 but was: " + cell.getHeight());
        }
    }

    private void validateColor(Project project, ScreenDefinition screenDefinition, LayoutCell cell, String color, String attributeName) {
        if (!color.startsWith("#")) {
            ValidationUtil.addError(project, screenDefinition, cell.getNodePath(), attributeName, ProblemType.CELL_WRONG_COLOR_VALUE,
                    "Wrong color value: " + color + "' in attribute '" + attributeName + ": colors must start with '#'");
        }
        if (color.length() != 7) {
            ValidationUtil.addError(project, screenDefinition, cell.getNodePath(), attributeName, ProblemType.CELL_WRONG_COLOR_VALUE,
                    "Wrong color value: " + color + "' in attribute '" + attributeName + ": color length must be 7");
        }
    }

}
