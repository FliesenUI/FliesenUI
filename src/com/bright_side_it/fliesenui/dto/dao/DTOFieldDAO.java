package com.bright_side_it.fliesenui.dto.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.dto.model.DTODefinitionDAOResult;
import com.bright_side_it.fliesenui.dto.model.DTOField;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class DTOFieldDAO {
    private static final String FIELD_NODE_NAME = "field";
    public static final String TYPE_ATTRIBUTE_NAME = "type";
    public static final String LIST_ATTRIBUTE_NAME = "list";
    public static final String PREVIEW_VALUE_ATTRIBUTE_NAME = "previewValue";
    private static final String BASIC_TYPE_NAME_LONG = "long";
    private static final String BASIC_TYPE_NAME_STRING = "string";
    private static final String BASIC_TYPE_NAME_BOOLEAN = "boolean";

    public void readDTOField(Node node, NodePath nodePath, DTODefinitionDAOResult result) throws ScreenDefionitionReadException, Exception {
        if (!DTODefinitionDAO.assertName(node, FIELD_NODE_NAME, nodePath, result)) {
            return;
        }
        if (result.getDTODefinition().getFields() == null) {
            result.getDTODefinition().setFields(new LinkedHashMap<>());
        }

        DTOField field = new DTOField();

        field.setPreviewValue(XMLUtil.getStringAttributeOptional(node, PREVIEW_VALUE_ATTRIBUTE_NAME, null));
        field.setID(XMLUtil.getStringAttributeRequired(node, BaseConstants.ID_ATTRIBUTE_NAME));
        field.setList(XMLUtil.getBooleanAttributeOptional(node, LIST_ATTRIBUTE_NAME, false));



        String typeString = XMLUtil.getStringAttributeRequired(node, TYPE_ATTRIBUTE_NAME);
        if (BASIC_TYPE_NAME_BOOLEAN.equals(typeString)) {
            field.setBasicType(BasicType.BOOLEAN);
        } else if (BASIC_TYPE_NAME_STRING.equals(typeString)) {
            field.setBasicType(BasicType.STRING);
        } else if (BASIC_TYPE_NAME_LONG.equals(typeString)) {
            field.setBasicType(BasicType.LONG);
        } else {
            field.setDTOType(typeString);
        }

        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
        
        if (result.getDTODefinition().getFields().containsKey(field.getID())){
        	DTODefinitionDAO.addError(result, nodePath, BaseConstants.ID_ATTRIBUTE_NAME, "There already is a field with id '" + field.getID() + "'");
        }
        
        result.getDTODefinition().getFields().put(field.getID(), field);
    }

    public static String getNodeName() {
        return FIELD_NODE_NAME;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, BaseConstants.ID_ATTRIBUTE_NAME, "id / name of the field"));
        result.add(BaseUtil.createAssistValue(true, TYPE_ATTRIBUTE_NAME, "either a basic type or another DTO"));
        result.add(BaseUtil.createAssistValue(false, LIST_ATTRIBUTE_NAME, "specifies if this field is a list of values (array)"));
        result.add(BaseUtil.createAssistValue(false, PREVIEW_VALUE_ATTRIBUTE_NAME, "preview value used for the IDE"));
        return result;
    }

    public static AssistValueListProvider getPossibleListAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.TRUE, "List of items"));
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.FALSE, "single items"));
        return new AssistValueList(assistValues);
    }

    public static List<AssistValue> getPossibleBasicTypes() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();

        assistValues.add(BaseUtil.createAssistValue(null, BASIC_TYPE_NAME_LONG, "number"));
        assistValues.add(BaseUtil.createAssistValue(null, BASIC_TYPE_NAME_STRING, "text"));
        assistValues.add(BaseUtil.createAssistValue(null, BASIC_TYPE_NAME_BOOLEAN, "true or false"));
        return assistValues;
    }


}
