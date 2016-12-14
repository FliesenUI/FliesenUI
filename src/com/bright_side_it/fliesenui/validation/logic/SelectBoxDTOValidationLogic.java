package com.bright_side_it.fliesenui.validation.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTOField;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.SelectBoxDAO;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class SelectBoxDTOValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (SelectBox i : BaseUtil.getAllSelectBoxes(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, SelectBox selectBox) {
        Map<String, DTODeclaration> dtoDeclarations = screenDefinition.getDTODeclarations();
        if (selectBox.getDTO() == null) {
            ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.DTO_ATTRIBUTE_NAME, ProblemType.SElECT_BOX_NO_DTO_SPECIFIED,
                    "No DTO is specified for the select box");
            return;
        }
        String dtoInstanceName = BaseUtil.getDTOInstanceName(selectBox.getDTO());
        DTODeclaration dtoDeclaration = dtoDeclarations.get(dtoInstanceName);
        //: instance that has not been declared?
        if (dtoDeclaration == null) {
            ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.DTO_ATTRIBUTE_NAME, ProblemType.SELECT_BOX_UNKNOWN_DTO_SPECIFIED,
                    "DTO not defined in screen: '" + dtoInstanceName + "'");
            return;
        }

        //: the instance was specified but no field?
        List<String> fieldChain = BaseUtil.getDTOFieldChain(selectBox.getDTO());
        if (fieldChain.isEmpty()) {
            ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.DTO_ATTRIBUTE_NAME, ProblemType.SELECT_BOX_NO_FIELD_IN_DTO_SPECIFIED,
                    "No field in DTO speciefied: '" + dtoInstanceName + "'");
            return;
        }

        String dtoTypeName = dtoDeclaration.getType();
        DTODefinition dtoClass = project.getDTODefinitionsMap().get(dtoTypeName);

        //: DTO type not specified
        if (dtoClass == null) {
            ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.DTO_ATTRIBUTE_NAME, ProblemType.SELECT_BOX_DTO_TYPE_UNKNOWN,
                    "DTO is specified in screen, but there is no definition for the DTO type: '" + dtoTypeName + "'");
            return;
        }

        DTODefinition currentDTOClass = dtoClass;
        DTOField currentField = null;
        int posInFieldChain = 0;
        for (String fieldName : fieldChain) {
            currentField = currentDTOClass.getFields().get(fieldName);
            if (currentField == null) {
                ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.DTO_ATTRIBUTE_NAME, ProblemType.SELECT_BOX_UNKNOWN_DTO_FIELD,
                        "Unknown DTO field: '" + fieldName + "' in DTO type '" + currentDTOClass.getID() + "'");
                return;
            }

            BasicType basicType = currentField.getBasicType();
            if ((basicType != null) && (posInFieldChain < fieldChain.size() - 1)) {
                ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.DTO_ATTRIBUTE_NAME,
                        ProblemType.SELECT_BOXDTO_BASIC_TYPE_WITH_NESTED_TYPE,
                        "Field '" + fieldName + "' has basic type '" + basicType + "' which cannot have nested types");
                return;
            }

            if (basicType == null) {
                currentDTOClass = project.getDTODefinitionsMap().get(currentField.getDTOType());
                if (currentDTOClass == null) {
                    ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.DTO_ATTRIBUTE_NAME,
                            ProblemType.SELECT_BOX_DTO_SUB_TYPE_UNKNOWN,
                            "Field '" + fieldName + "' has DTO type '" + currentField.getDTOType() + "' which is not defined");
                    return;
                }
            }
            posInFieldChain++;
        }

        DTOField dtoFieldUsedInTableRows = currentField;
        DTODefinition dtoClassUsedInRows = currentDTOClass;
        if (!dtoFieldUsedInTableRows.isList()) {
            ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.DTO_ATTRIBUTE_NAME, ProblemType.SELECT_BOX_DTO_IS_NOT_A_LIST,
                    "Field '" + currentField.getID() + "' is not a list, but a list is required for the select box");
            return;
        }

        validateIDField(project, screenDefinition, selectBox, dtoClassUsedInRows);
        validateLabelField(project, screenDefinition, selectBox, dtoClassUsedInRows);
        
        if (selectBox.getSelectedIDDTOField() != null){
        	if (!ValidationUtil.doesDTOFieldExist(project, screenDefinition, selectBox.getSelectedIDDTOField())){
                ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.SELECTED_ID_DTO_FIELD_ATTRIBUTE_NAME, ProblemType.SELECT_BOX_SELECTED_ID_DTO_FIELD_DOES_NOT_EXIST,
                        "No such DTO field: '" + selectBox.getSelectedIDDTOField() + "'");
        	}
        }
    }

    private void validateIDField(Project project, ScreenDefinition screenDefinition, SelectBox selectBox, DTODefinition dtoClassUsedInRows) {
        if (selectBox.getIDDTOField() == null) {
            ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.ID_DTO_FIELD_ATTRIBUTE_NAME,
                    ProblemType.SELECT_BOX_ID_DTO_FIELD_IS_MISSING, "The DTO id field has not been specified");
            return;
        }
        DTOField idField = dtoClassUsedInRows.getFields().get(selectBox.getIDDTOField());
        if (idField == null) {
            ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.ID_DTO_FIELD_ATTRIBUTE_NAME,
                    ProblemType.SELECT_BOX_ID_DTO_FIELD_DOES_NOT_EXIST,
                    "Select box uses unknonwn id field '" + selectBox.getIDDTOField() + "' which does not exist in DTO type '" + dtoClassUsedInRows.getID() + "'");
        } else if (!isTypeAndNotListOf(idField, BasicType.STRING, BasicType.LONG)) {
            ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.ID_DTO_FIELD_ATTRIBUTE_NAME,
                    ProblemType.SELECT_BOX_ID_DTO_FIELD_HAS_WRONG_TYPE, "Select box uses id field '" + selectBox.getIDDTOField() + "' which is of type '" + toTypeLabel(idField)
                            + "', but needs to be string or long and may not be a list");
        }
    }

    private void validateLabelField(Project project, ScreenDefinition screenDefinition, SelectBox selectBox, DTODefinition dtoClassUsedInRows) {
    	if (selectBox.getIDDTOField() == null) {
    		ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.LABEL_DTO_FIELD_ATTRIBUTE_NAME,
    				ProblemType.SELECT_BOX_LABEL_DTO_FIELD_IS_MISSING, "The DTO label field has not been specified");
    		return;
    	}
    	DTOField idField = dtoClassUsedInRows.getFields().get(selectBox.getIDDTOField());
    	if (idField == null) {
    		ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.LABEL_DTO_FIELD_ATTRIBUTE_NAME,
    				ProblemType.SELECT_BOX_LABEL_DTO_FIELD_DOES_NOT_EXIST,
    				"Select box uses unknonwn id field '" + selectBox.getIDDTOField() + "' which does not exist in DTO type '" + dtoClassUsedInRows.getID() + "'");
    	} else if (!isTypeAndNotListOf(idField, BasicType.STRING, BasicType.LONG)) {
    		ValidationUtil.addError(project, screenDefinition, selectBox.getNodePath(), SelectBoxDAO.LABEL_DTO_FIELD_ATTRIBUTE_NAME,
    				ProblemType.SELECT_BOX_LABEL_DTO_FIELD_HAS_WRONG_TYPE, "Select box uses id field '" + selectBox.getIDDTOField() + "' which is of type '" + toTypeLabel(idField)
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
