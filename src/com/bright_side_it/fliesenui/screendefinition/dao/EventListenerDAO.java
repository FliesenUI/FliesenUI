package com.bright_side_it.fliesenui.screendefinition.dao;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener.EventListenType;
import com.bright_side_it.fliesenui.screendefinition.model.EventListenerContainer;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class EventListenerDAO {
	public static final String NODE_NAME_LISTEN_TO_BACK_ACTION = "listenToBackAction";
	public static final String NODE_NAME_LISTEN_TO_KEY_PRESS = "listenToKeyPress";
	public static final String NODE_NAME_LISTEN_TO_KEY_DOWN = "listenToKeyDown";
	
	public static final String KEY_CHAR_ATTRIBUTE_NAME = "keyChar";
	public static final String KEY_CODE_ATTRIBUTE_NAME = "keyCode";
	public static final String CONTROL_ATTRIBUTE_NAME = "control";
	public static final String ALT_ATTRIBUTE_NAME = "alt";
    public static final String SHIFT_ATTRIBUTE_NAME = "shift";
    
    public static final Map<String, Integer> KEY_NAME_TO_KEY_CODE_MAP = createKeyNameToKeyCodeMap();

	public boolean isEventListenerNode(String nodeName) {
		return getNodeNames().contains(nodeName);
	}
    
	private static Map<String, Integer> createKeyNameToKeyCodeMap() {
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		result.put("CursorUp", 38);
		result.put("CursorDown", 40);
		result.put("CursorLeft", 37);
		result.put("CursorRight", 39);
		result.put("Enter", 13);
		result.put("Tab", 9);
		result.put("Esc", 27);
		result.put("PageUp", 33);
		result.put("PageDown", 34);
		result.put("F1", 112);
		result.put("F2", 113);
		result.put("F3", 114);
		result.put("F4", 115);
		result.put("F5", 116);
		result.put("F6", 117);
		result.put("F7", 118);
		result.put("F8", 119);
		result.put("F9", 120);
		result.put("F10", 121);
		result.put("F11", 122);
		result.put("F12", 123);
		return result;
	}

	public void readEventListener(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, EventListenerContainer container) throws Exception {
        EventListener eventListener = new EventListener();
        eventListener.setNodePath(nodePath);
        eventListener.setID(container.getID());

        if (container.getEventListeners() == null) {
            container.setEventListeners(new ArrayList<>());
        }
        container.getEventListeners().add(eventListener);
        
        if (node.getNodeName().equals(NODE_NAME_LISTEN_TO_BACK_ACTION)){
        	eventListener.setEventListenType(EventListenType.BACK_ACTION);
        } else if (node.getNodeName().equals(NODE_NAME_LISTEN_TO_KEY_PRESS)){
        	eventListener.setEventListenType(EventListenType.KEY_PRESS);
        } else if (node.getNodeName().equals(NODE_NAME_LISTEN_TO_KEY_DOWN)){
        	eventListener.setEventListenType(EventListenType.KEY_DOWN);
        } else {
        	throw new Exception("Unknown event listen type: " + node.getNodeName());
        }
        String keyCharString = XMLUtil.getStringAttributeOptional(node, KEY_CHAR_ATTRIBUTE_NAME, null);
        if (keyCharString != null){
        	if (keyCharString.length() > 1){
    			ScreenDefinitionDAO.addError(result, nodePath, KEY_CHAR_ATTRIBUTE_NAME, ProblemType.EVENT_LISTENER_KEY_CHAR_TOO_LONG
    					, "The key character may be only one character");
        	}
        	eventListener.setKeyChar(keyCharString.charAt(0));
        }
        String keyCodeString = XMLUtil.getStringAttributeOptional(node, KEY_CODE_ATTRIBUTE_NAME, null);
        Integer keyCode = null;
        try{
        	keyCode = parseKeyCode(keyCodeString);
        } catch (Exception e){
			ScreenDefinitionDAO.addError(result, nodePath, KEY_CODE_ATTRIBUTE_NAME, ProblemType.EVENT_LISTENER_KEY_CODE_UNKNOWN
					, "Unknown key code: '" + keyCodeString + "'");
        }
        eventListener.setKeyCode(keyCode);
        
        eventListener.setControl(XMLUtil.getBooleanAttributeOptional(node, CONTROL_ATTRIBUTE_NAME, null));
        eventListener.setAlt(XMLUtil.getBooleanAttributeOptional(node, ALT_ATTRIBUTE_NAME, null));
        eventListener.setShift(XMLUtil.getBooleanAttributeOptional(node, SHIFT_ATTRIBUTE_NAME, null));
        
        EventParameterDAO eventParameterDAO = new EventParameterDAO();
        
        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
            	if (EventParameterDAO.getNodeName().equals(i.getNodeName())){
            		eventParameterDAO.readEventParameter(i, childNodePath, result, eventListener);
            	} else {
            		throw new Exception("Unexpected child node: '" + i.getNodeName() + "'");
            	}
            	
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }
        
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes(node.getNodeName())), result);
    }

    private Integer parseKeyCode(String keyCodeString) throws Exception{
    	if ((keyCodeString == null) || (keyCodeString.isEmpty())){
    		return null;
    	}
    	Integer result = KEY_NAME_TO_KEY_CODE_MAP.get(keyCodeString);
    	if (result == null){
    		throw new Exception("Unknown key code name");
    	}
		return result;
	}

	public List<AssistValue> getPossibleChildTags(String nodeName) {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, EventParameterDAO.getNodeName(), "Parameter that is passed to the method when event occurs"));
        return result;
    }

    public List<AssistValue> getTagAttributes(String nodeName) {
        List<AssistValue> result = new ArrayList<AssistValue>();
        if ((nodeName.equals(NODE_NAME_LISTEN_TO_KEY_PRESS)) || (nodeName.equals(NODE_NAME_LISTEN_TO_KEY_DOWN))){
        	result.add(BaseUtil.createAssistValue(true, KEY_CHAR_ATTRIBUTE_NAME, "Character of the key that is pressed"));
        	result.add(BaseUtil.createAssistValue(true, KEY_CODE_ATTRIBUTE_NAME, "Code of the key that is pressed"));
        	result.add(BaseUtil.createAssistValue(false, CONTROL_ATTRIBUTE_NAME, "set to true/false to filter only with/without contol key"));
        	result.add(BaseUtil.createAssistValue(false, ALT_ATTRIBUTE_NAME, "set to true/false to filter only with/without alt key"));
        	result.add(BaseUtil.createAssistValue(false, SHIFT_ATTRIBUTE_NAME, "set to true/false to filter only with/without shift key"));
        }
        return result;
    }

	public static List<String> getNodeNames() {
		return Arrays.asList(NODE_NAME_LISTEN_TO_BACK_ACTION, NODE_NAME_LISTEN_TO_KEY_PRESS, NODE_NAME_LISTEN_TO_KEY_DOWN);
	}

	public static AssistValueListProvider getPossibleKeyCoddeAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        for (String i: KEY_NAME_TO_KEY_CODE_MAP.keySet()){
        	assistValues.add(BaseUtil.createAssistValue(null, "" + i, "" + i));
        }
        return new AssistValueList(assistValues);
	}
}
