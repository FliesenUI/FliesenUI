package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethod;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethod.CallbackType;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class CallbackMethodDAO {
    private static final String NODE_NAME = "callbackMethod";
    public static final String NAME_ATTRIBUTE_NAME = "name";
    public static final String TYPE_ATTRIBUTE_NAME = "type";

    private static final String TYPE_STRING_INPUT = "stringInput";
    private static final String TYPE_LIST_CHOOSER = "listChooser";
    private static final String TYPE_CONFIRM = "confirm";

    
    public boolean isTimerNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readCallbackMethod(Node node, NodePath nodePath, ScreenDefinitionDAOResult result) throws Exception {
        if ("#text".equals(node.getNodeName())) {
            log("Found text node. Type = " + node.getNodeType() + ", text content = '" + node.getTextContent() + "'");
        }

        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        
        CallbackMethod callbackMethod = new CallbackMethod();
        callbackMethod.setParameters(new ArrayList<>());
        callbackMethod.setNodePath(nodePath);
        callbackMethod.setName(XMLUtil.getStringAttributeRequired(node, NAME_ATTRIBUTE_NAME));
        String typeString = XMLUtil.getStringAttributeRequired(node, TYPE_ATTRIBUTE_NAME);
        callbackMethod.setType(parseType(typeString));

        if (result.getScreenDefinition() == null) {
            result.setScreenDefinition(new ScreenDefinition());
        }
        if (result.getScreenDefinition().getCallbackMethods() == null) {
            result.getScreenDefinition().setCallbackMethods(new ArrayList<CallbackMethod>());
        }
        
        CallbackMethodParameterDAO parameterDAO = new CallbackMethodParameterDAO();
        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
            	if (CallbackMethodParameterDAO.getNodeName().equals(i.getNodeName())){
            		parameterDAO.readParameter(i, childNodePath, result, callbackMethod);
            	} else {
            		throw new Exception("Unexpected child node: '" + i.getNodeName() + "'");
            	}
            	
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
        result.getScreenDefinition().getCallbackMethods().add(callbackMethod);
    }

    private CallbackType parseType(String string) throws Exception{
    	if (TYPE_STRING_INPUT.equals(string)){
    		return CallbackType.STRING_INPUT;
    	} else if (TYPE_CONFIRM.equals(string)){
    		return CallbackType.CONFIRM;
    	} else if (TYPE_LIST_CHOOSER.equals(string)){
    		return CallbackType.LIST_CHOOSER;
    	}
		throw new Exception("Unknown callback type: '" + string + "'");
	}

	private void log(String message) {
        System.out.println("CallbackMethodDAO: " + message);
    }

    public static String getNodeName() {
        return NODE_NAME;
    }
    
    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, CallbackMethodParameterDAO.getNodeName(), "method parameter"));
        return result;
    }
    
    public AssistValueListProvider createPossibleTypeAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_LIST_CHOOSER, "list chooser dialog"));
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_CONFIRM, "confirm dialog"));
        assistValues.add(BaseUtil.createAssistValue(null, TYPE_STRING_INPUT, "string input dialog"));
        return new AssistValueList(assistValues);
    }


    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, NAME_ATTRIBUTE_NAME, "name of the callback method"));
        result.add(BaseUtil.createAssistValue(true, TYPE_ATTRIBUTE_NAME, "type of callback method (e.g. for list chooser or confirm dialog)"));
        return result;
    }

	public boolean isCallbackMethodNode(Node node) {
		return node.getNodeName().equals(NODE_NAME);
	}


}
