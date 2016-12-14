package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.TableWidgetColumnDAO;
import com.bright_side_it.fliesenui.screendefinition.dao.TableWidgetDAO;
import com.bright_side_it.fliesenui.screendefinition.dao.TableWidgetItemDAO;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem.TableWidgetType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class TableWidgetPropertiesValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (TableWidget i : BaseUtil.getAllTableWidgets(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, TableWidget table) {
        validateColumnWidthGreaterZero(project, screenDefinition, table);
        validateTotalColumnWidth(project, screenDefinition, table);
        validateColumnContent(project, screenDefinition, table);
        validateWidgetItems(project, screenDefinition, table);
    }

    private void validateWidgetItems(Project project, ScreenDefinition screenDefinition, TableWidget table) {
        for (TableWidgetItem i : BaseUtil.getAllTableWidgetItemsOfType(screenDefinition, TableWidgetType.BUTTON, TableWidgetType.IMAGE_BUTTON)) {
            if (i.getID() == null) {
                ValidationUtil.addError(project, screenDefinition, i.getNodePath(), BaseConstants.ID_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_ITEM_MISSING_ID,
                        "The table button needs to have an ID");
            }
        }
        for (TableWidgetItem i : BaseUtil.getAllTableWidgetItemsOfType(screenDefinition, TableWidgetType.IMAGE, TableWidgetType.IMAGE_BUTTON)) {
        	if (i.getText() != null){
                ValidationUtil.addError(project, screenDefinition, i.getNodePath(), TableWidgetItemDAO.TEXT_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_ITEM_UNEXPECTED_TEXT,
                        "Image table items may not have a text");
        	}
        	if (i.getTextDTOField() != null){
                ValidationUtil.addError(project, screenDefinition, i.getNodePath(), TableWidgetItemDAO.TEXT_DTO_FIELD_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_ITEM_UNEXPECTED_TEXT_DTO_FIELD,
                        "Image table items may not have a DTO field text");
        	}
        	if (i.getImageSource() == null){
                ValidationUtil.addError(project, screenDefinition, i.getNodePath(), null, ProblemType.WIDGET_TABLE_ITEM_MISSING_IMAGE_SOURCE,
                        "For image table items an image source needs to be specified");
        	}
        }

    }

    private void validateColumnContent(Project project, ScreenDefinition screenDefinition, TableWidget table) {
        for (TableWidgetColumn column : BaseUtil.toEmptyCollectionIfNull(table.getColumns())) {
            if ((column.getTableItems() == null) || (column.getTableItems().isEmpty())) {
                ValidationUtil.addError(project, screenDefinition, column.getNodePath(), null, ProblemType.WIDGET_TABLE_COLUMN_WITHOUT_ITEMS,
                        "The column does not contain any items (like buttons or labels)");
            }
        }

    }

    private void validateColumnWidthGreaterZero(Project project, ScreenDefinition screenDefinition, TableWidget table) {
        for (TableWidgetColumn i : BaseUtil.toEmptyCollectionIfNull(table.getColumns())) {
            if (i.getSize() <= 0) {
                ValidationUtil.addError(project, screenDefinition, i.getNodePath(), TableWidgetColumnDAO.SIZE_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_WRONG_SIZE,
                        "Table column size must be greater 0");
            }
        }
        if (table.getContentHeight() != null) {
            if (table.getContentHeight() <= 0) {
                ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.CONTENT_HEIGHT_ATTRIBUTE_NAME,
                        ProblemType.WIDGET_TABLE_WRONG_HEIGHT, "Table height must be > 0");
            }
        }

    }

    private void validateTotalColumnWidth(Project project, ScreenDefinition screenDefinition, TableWidget table) {
        int totalSize = 0;
        for (TableWidgetColumn i : BaseUtil.toEmptyCollectionIfNull(table.getColumns())) {
            totalSize += i.getSize();
        }
        if (totalSize != 100) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), null, ProblemType.WIDGET_TABLE_WRONG_TOTAL_COLUMN_SIZE,
                    "The sum of the column sizes must be 100, but it is " + totalSize);
        }
    }

}
