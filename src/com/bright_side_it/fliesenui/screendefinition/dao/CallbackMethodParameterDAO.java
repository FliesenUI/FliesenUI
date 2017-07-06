package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethod;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethodParameter;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethodParameter.ParameterType;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class CallbackMethodParameterDAO {
	public static final String TYPE_NAME_NULLABLE_LONG = "long nullable";
	public static final String TYPE_NAME_NON_NULLABLE_LONG = "long non-nullable";
	public static final String TYPE_NAME_STRING = "string";
	public static final String TYPE_NAME_NULLABLE_BOOLEAN = "boolean nullable";
	public static final String TYPE_NAME_NON_NULLABLE_BOOLEAN = "boolean non-nullable";
	public static final String TYPE_NAME_NULLABLE_INTEGER = "integer nullable";
	public static final String TYPE_NAME_NON_NULLABLE_INTEGER = "integer non-nullable";
	public static final String TYPE_NAME_LIST_OF_STRING = "list of string";

    private static final String NODE_NAME = "parameter";
    public static final String TYPE_ATTRIBUTE_NAME = "type";
    public static final String NAME_ATTRIBUTE_NAME = "name";

    public void readParameter(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, CallbackMethod callbackMethod) throws Exception {
        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        CallbackMethodParameter parameter = new CallbackMethodParameter();
        parameter.setNodePath(nodePath);

        if (callbackMethod.getParameters() == null) {
        	callbackMethod.setParameters(new ArrayList<>());
        }

        parameter.setName(XMLUtil.getStringAttributeRequired(node, NAME_ATTRIBUTE_NAME));
        String typeString = XMLUtil.getStringAttributeRequired(node, TYPE_ATTRIBUTE_NAME);
        
        ParameterType type = parseType(typeString);
        if (type != null){
        	parameter.setType(type);
        } else {
        	parameter.setType(ParameterType.DTO);
        	parameter.setDTOClassName(typeString);
        }
        
        callbackMethod.getParameters().add(parameter);
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
    }

    private ParameterType parseType(String string) throws Exception {
        if (TYPE_NAME_NULLABLE_LONG.equals(string)) {
            return ParameterType.NULLABLE_LONG;
        } else if (TYPE_NAME_NON_NULLABLE_LONG.equals(string)) {
        	return ParameterType.NON_NULLABE_LONG;
        } else if (TYPE_NAME_NULLABLE_INTEGER.equals(string)) {
            return ParameterType.NULLABLE_INT;
        } else if (TYPE_NAME_NON_NULLABLE_INTEGER.equals(string)) {
        	return ParameterType.NON_NULLABLE_INT;
        } else if (TYPE_NAME_NULLABLE_BOOLEAN.equals(string)) {
            return ParameterType.NULLABLE_BOOLEAN;
        } else if (TYPE_NAME_NON_NULLABLE_BOOLEAN.equals(string)) {
        	return ParameterType.NON_NULLABLE_BOOLEAN;
        } else if (TYPE_NAME_STRING.equals(string)) {
            return ParameterType.STRING;
        } else if (TYPE_NAME_LIST_OF_STRING.equals(string)) {
        	return ParameterType.LIST_OF_STRING;
        }
        return null;
    }

//    public boolean isCallbackMethodParameterNode(Node node) {
//        return (node.getNodeName().equals(NODE_NAME));
//    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        return result;
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, NAME_ATTRIBUTE_NAME, "name of the parameter"));
        result.add(BaseUtil.createAssistValue(true, TYPE_ATTRIBUTE_NAME, "data type: either a unary type or a DTO name"));
        return result;
    }
    
    public List<AssistValue> createUnaryTypeAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_NAME_STRING, "string"));
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_NAME_LIST_OF_STRING, "list of strings"));
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_NAME_NULLABLE_LONG, "long or null"));
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_NAME_NON_NULLABLE_LONG, "long which may not be null"));
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_NAME_NULLABLE_BOOLEAN, "boolean or null"));
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_NAME_NON_NULLABLE_BOOLEAN, "boolean which may not be null"));
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_NAME_NULLABLE_INTEGER, "integer or null"));
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_NAME_NON_NULLABLE_INTEGER, "integer which may not be null"));
        return assistValues;
    }
    
    public List<String> getUnaryTypeNames(){
    	List<String> result = new ArrayList<>();
    	for (AssistValue i: createUnaryTypeAttributeValues()){
    		result.add(i.getText());
    	}
    	return result;
    }

}
