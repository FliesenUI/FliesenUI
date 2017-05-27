package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.model.EventHandler;
import com.bright_side_it.fliesenui.screendefinition.model.EventHandlerContainer;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.EventHandler.EventType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class EventHandlerDAO {
	private static final String NODE_NAME_ON_CLICK = "onClick";
	public static final String NODE_NAME_ON_ENTER = "onEnter";
	
    public static final String PROPERTY_NAME_URL_TO_OPEN = "urlToOpen";
    public static final String PROPERTY_NAME_OPEN_URL_IN_NEW_WINDOW = "openURLInNewWindow";
    public static final String PROPERTY_NAME_SCREEN_TO_OPEN = "screenToOpen";
    public static final String PROPERTY_NAME_OPEN_SCREEN_PARAMETER_DTO = "openScreenParameterDTO";
    public static final String PROPERTY_NAME_BUTTON_TO_CLICK = "buttonToClick";

	public boolean isEventHandlerNode(String nodeName) {
		return getNodeNames().contains(nodeName);
	}
    
    public void readEventHandler(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, EventHandlerContainer container) throws Exception {
        EventHandler eventHandler = new EventHandler();

        if (container.getEventHandlers() == null) {
            container.setEventHandlers(new ArrayList<>());
        }
        container.getEventHandlers().add(eventHandler);
        
        if (node.getNodeName().equals(NODE_NAME_ON_CLICK)){
        	eventHandler.setEventType(EventType.CLICK);	
        } else if (node.getNodeName().equals(NODE_NAME_ON_ENTER)){
        		eventHandler.setEventType(EventType.ENTER);	
        } else {
        	throw new Exception("Unknown event type: " + node.getNodeName());
        }
        
        eventHandler.setNodePath(nodePath);
        
        eventHandler.setURLToOpen(XMLUtil.getStringAttributeOptional(node, PROPERTY_NAME_URL_TO_OPEN, null));
        eventHandler.setOpenURLInNewWindow(XMLUtil.getBooleanAttributeOptional(node, PROPERTY_NAME_OPEN_URL_IN_NEW_WINDOW, false));
        eventHandler.setScreenToOpen(XMLUtil.getStringAttributeOptional(node, PROPERTY_NAME_SCREEN_TO_OPEN, null));
        eventHandler.setOpenScreenParameterDTO(XMLUtil.getStringAttributeOptional(node, PROPERTY_NAME_OPEN_SCREEN_PARAMETER_DTO, null));
        eventHandler.setButtonToClick(XMLUtil.getStringAttributeOptional(node, PROPERTY_NAME_BUTTON_TO_CLICK, null));
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes(node.getNodeName())), result);
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        return result;
    }

    public List<AssistValue> getTagAttributes(String nodeName) {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(false, PROPERTY_NAME_BUTTON_TO_CLICK, "ID of the button to be clicked"));
        result.add(BaseUtil.createAssistValue(false, PROPERTY_NAME_URL_TO_OPEN, "URL to open"));
        result.add(BaseUtil.createAssistValue(false, PROPERTY_NAME_OPEN_URL_IN_NEW_WINDOW, "true: opens the URL in a new window"));
        result.add(BaseUtil.createAssistValue(false, PROPERTY_NAME_SCREEN_TO_OPEN, "open a screen with this ID"));
        result.add(BaseUtil.createAssistValue(false, PROPERTY_NAME_OPEN_SCREEN_PARAMETER_DTO, "if the screen to open requires a parameter object it has to be specified here"));
        return result;
    }

	public static List<String> getNodeNames() {
		return Arrays.asList(NODE_NAME_ON_CLICK, NODE_NAME_ON_ENTER);
	}

}
