package com.bright_side_it.fliesenui.validation.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTOField;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.TableWidgetDAO;
import com.bright_side_it.fliesenui.screendefinition.dao.TableWidgetItemDAO;
import com.bright_side_it.fliesenui.screendefinition.logic.ImageSourceLogic;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSource;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class TableWidgetDTOValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (TableWidget i : BaseUtil.getAllTableWidgets(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, TableWidget table) {
        Map<String, DTODeclaration> dtoDeclarations = screenDefinition.getDTODeclarations();
        if (table.getDTO() == null) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.DTO_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_NO_DTO_SPECIFIED,
                    "No DTO is specified for the table");
            return;
        }
        String dtoInstanceName = BaseUtil.getDTOInstanceName(table.getDTO());
        DTODeclaration dtoDeclaration = BaseUtil.toEmptyMapIfNull(dtoDeclarations).get(dtoInstanceName);
        //: instance that has not been declared?
        if (dtoDeclaration == null) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.DTO_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_UNKNOWN_DTO_SPECIFIED,
                    "DTO not defined in screen: '" + dtoInstanceName + "'");
            return;
        }

        //: the instance was specified but no field?
        List<String> fieldChain = BaseUtil.getDTOFieldChain(table.getDTO());
        if (fieldChain.isEmpty()) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.DTO_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_NO_FIELD_IN_DTO_SPECIFIED,
                    "No field in DTO speciefied: '" + dtoInstanceName + "'");
            return;
        }

        String dtoTypeName = dtoDeclaration.getType();
        DTODefinition dtoClass = project.getDTODefinitionsMap().get(dtoTypeName);

        //: DTO type not specified
        if (dtoClass == null) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.DTO_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_DTO_TYPE_UNKNOWN,
                    "DTO is specified in screen, but there is no definition for the DTO type: '" + dtoTypeName + "'");
            return;
        }

        DTODefinition currentDTOClass = dtoClass;
        DTOField currentField = null;
        int posInFieldChain = 0;
        for (String fieldName : fieldChain) {
            currentField = currentDTOClass.getFields().get(fieldName);
            if (currentField == null) {
                ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.DTO_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_UNKNOWN_DTO_FIELD,
                        "Unknown DTO field: '" + fieldName + "' in DTO type '" + currentDTOClass.getID() + "'");
                return;
            }

            BasicType basicType = currentField.getBasicType();
            if ((basicType != null) && (posInFieldChain < fieldChain.size() - 1)) {
                ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.DTO_ATTRIBUTE_NAME,
                        ProblemType.WIDGET_TABLE_DTO_BASIC_TYPE_WITH_NESTED_TYPE,
                        "Field '" + fieldName + "' has basic type '" + basicType + "' which cannot have nested types");
                return;
            }

            if (basicType == null) {
                currentDTOClass = project.getDTODefinitionsMap().get(currentField.getDTOType());
                if (currentDTOClass == null) {
                    ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.DTO_ATTRIBUTE_NAME,
                            ProblemType.WIDGET_TABLE_DTO_SUB_TYPE_UNKNOWN,
                            "Field '" + fieldName + "' has DTO type '" + currentField.getDTOType() + "' which is not defined");
                    return;
                }
            }
            posInFieldChain++;
        }

        DTOField dtoFieldUsedInTableRows = currentField;
        DTODefinition dtoClassUsedInTableRows = currentDTOClass;
        if (!dtoFieldUsedInTableRows.isList()) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.DTO_ATTRIBUTE_NAME, ProblemType.WIDGET_TABLE_DTO_IS_NOT_A_LIST,
                    "Field '" + currentField.getID() + "' is not a list, but a list is required for the table");
            return;
        }

        validateIDField(project, screenDefinition, table, dtoClassUsedInTableRows);

        validateColumns(project, screenDefinition, table, dtoClassUsedInTableRows);
    }

    private void validateColumns(Project project, ScreenDefinition screenDefinition, TableWidget table, DTODefinition dtoClassUsedInTableRows) {
        if ((table.getColumns() == null) || (table.getColumns().isEmpty())) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), null, ProblemType.WIDGET_TABLE_NO_COLUMNS, "Table widget has no columns");
            return;
        }
        ImageSourceLogic imageSourceLogic = new ImageSourceLogic();
        for (TableWidgetColumn column : table.getColumns()) {
            for (TableWidgetItem tableItem : BaseUtil.toEmptyCollectionIfNull(column.getTableItems())) {

                if (tableItem.getTextDTOField() != null) {
                    DTOField fieldInTableItem = dtoClassUsedInTableRows.getFields().get(tableItem.getTextDTOField());
                    if (fieldInTableItem == null) {
                        ValidationUtil.addError(project, screenDefinition, tableItem.getNodePath(), TableWidgetItemDAO.TEXT_DTO_FIELD_ATTRIBUTE_NAME,
                                ProblemType.WIDGET_TABLE_ITEM_DTO_FIELD_UNKNOWN, "Table item uses unknonwn fiel '" + tableItem.getTextDTOField()
                                        + "' which does not exist in DTO type '" + dtoClassUsedInTableRows.getID() + "'");
                    } else if (!isTypeAndNotListOf(fieldInTableItem, BasicType.STRING, BasicType.LONG, BasicType.BOOLEAN)) {
                        ValidationUtil.addError(project, screenDefinition, tableItem.getNodePath(), TableWidgetItemDAO.TEXT_DTO_FIELD_ATTRIBUTE_NAME,
                                ProblemType.WIDGET_TABLE_ITEM_FIELD_HAS_WRONG_TYPE, "Table uses dto field '" + tableItem.getTextDTOField() + "' in table item '"
                                        + fieldInTableItem.getID() + "' which needs to be string, long or boolean and may not be a list");
                    }
                }
 
                String dtoField = null;
                ImageSource imageSource = tableItem.getImageSource();
                if (imageSource != null){
                	dtoField = imageSourceLogic.getDTOField(imageSource);
                }
                if (dtoField != null) {
                    DTOField fieldInTableItem = dtoClassUsedInTableRows.getFields().get(dtoField);
                    if (fieldInTableItem == null) {
                        ValidationUtil.addError(project, screenDefinition, tableItem.getNodePath(), null,
                                ProblemType.WIDGET_TABLE_ITEM_DTO_FIELD_UNKNOWN, "Table item uses unknonwn field '" + dtoField
                                        + "' as image source which does not exist in DTO type '" + dtoClassUsedInTableRows.getID() + "'");
                    } else if (!isTypeAndNotListOf(fieldInTableItem, BasicType.STRING)) {
                        ValidationUtil.addError(project, screenDefinition, tableItem.getNodePath(), TableWidgetItemDAO.TEXT_DTO_FIELD_ATTRIBUTE_NAME,
                                ProblemType.WIDGET_TABLE_ITEM_IMAGE_DTO_FIELD_HAS_WRONG_TYPE, "Table uses dto field '" + dtoField + "' as image source in table item '"
                                        + fieldInTableItem.getID() + "' which needs to be string and may not be a list");
                    }
                }
            }
        }
    }

    private void validateIDField(Project project, ScreenDefinition screenDefinition, TableWidget table, DTODefinition dtoClassUsedInTableRows) {
        if (table.getIDDTOField() == null) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.ID_DTO_FIELD_ATTRIBUTE_NAME,
                    ProblemType.WIDGET_TABLE_ID_DTO_FIELD_IS_MISSING, "The DTO id field has not been specified");
            return;
        }
        DTOField idField = dtoClassUsedInTableRows.getFields().get(table.getIDDTOField());
        if (idField == null) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.ID_DTO_FIELD_ATTRIBUTE_NAME,
                    ProblemType.WIDGET_TABLE_ID_DTO_FIELD_DOES_NOT_EXIST,
                    "Table uses unknonwn id field '" + table.getIDDTOField() + "' which does not exist in DTO type '" + dtoClassUsedInTableRows.getID() + "'");
        } else if (!isTypeAndNotListOf(idField, BasicType.STRING, BasicType.LONG)) {
            ValidationUtil.addError(project, screenDefinition, table.getNodePath(), TableWidgetDAO.ID_DTO_FIELD_ATTRIBUTE_NAME,
                    ProblemType.WIDGET_TABLE_ID_DTO_FIELD_HAS_WRONG_TYPE, "Table uses id field '" + table.getIDDTOField() + "' which is of type '" + toTypeLabel(idField)
                            + "', but needs to be string or long and may not be a list");
        }
    }

    private String toTypeLabel(DTOField field) {
        String result = "";
        if (field.isList()) {
            result = "list of ";
        }
        if (field.getBasicType() != null) {
            result += field.getBasicType();
        } else {
            result += field.getDTOType();
        }
        return result;
    }

    private boolean isTypeAndNotListOf(DTOField field, BasicType... basicTypes) {
        if (field.isList()) {
            return false;
        }
        if (field.getBasicType() == null) {
            return false;
        }
        for (BasicType i : Arrays.asList(basicTypes)) {
            if (i == field.getBasicType()) {
                return true;
            }
        }
        return false;
    }
}
