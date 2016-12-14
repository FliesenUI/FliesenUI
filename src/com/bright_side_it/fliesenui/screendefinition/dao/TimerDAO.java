package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.Timer;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class TimerDAO {
    private static final String NODE_NAME = "timer";
    public static final String ACTIVE_ATTRIBUTE_NAME = "active";
    public static final String INTERVAL_IN_MILLIS_ATTRIBUTE_NAME = "intervalInMillis";
    
    public boolean isTimerNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readTimerNode(Node node, NodePath nodePath, ScreenDefinitionDAOResult result) throws Exception {
        if ("#text".equals(node.getNodeName())) {
            log("Found text node. Type = " + node.getNodeType() + ", text content = '" + node.getTextContent() + "'");
        }

        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        
        Timer timer = new Timer();
        timer.setNodePath(nodePath);
        timer.setID(XMLUtil.getStringAttributeRequired(node, BaseConstants.ID_ATTRIBUTE_NAME));
        timer.setActive(XMLUtil.getBooleanAttributeOptional(node, ACTIVE_ATTRIBUTE_NAME, true));
        timer.setIntervalInMillis(XMLUtil.getIntAttributeRequired(node, INTERVAL_IN_MILLIS_ATTRIBUTE_NAME));
        
        if (result.getScreenDefinition() == null) {
            result.setScreenDefinition(new ScreenDefinition());
        }
        if (result.getScreenDefinition().getTimers() == null) {
            result.getScreenDefinition().setTimers(new TreeMap<String, Timer>());
        }
        if (result.getScreenDefinition().getTimers().containsKey(timer.getID())) {
            throw new Exception("A timer with the id '" + timer.getID() + "' has already been declared");
        }
        
        EventParameterDAO eventParameterDAO = new EventParameterDAO();
        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
            	if (EventParameterDAO.getNodeName().equals(i.getNodeName())){
            		eventParameterDAO.readEventParameter(i, childNodePath, result, timer);
            	} else {
            		throw new Exception("Unexpected child node: '" + i.getNodeName() + "'");
            	}
            	
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }
        
        
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
        result.getScreenDefinition().getTimers().put(timer.getID(), timer);
    }

    private void log(String message) {
        System.out.println("TimerDAO: " + message);
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, EventParameterDAO.getNodeName(), "event parameter"));
        return result;
    }
    
    public AssistValueListProvider createPossibleActiveAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.TRUE, "timer is active"));
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.FALSE, "timer is deactivated"));
        return new AssistValueList(assistValues);
    }


    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, BaseConstants.ID_ATTRIBUTE_NAME, "ID of the timer 'instance' that determines the name of the called method"));
        result.add(BaseUtil.createAssistValue(true, INTERVAL_IN_MILLIS_ATTRIBUTE_NAME, "timer interval in milliseconds"));
        result.add(BaseUtil.createAssistValue(false, ACTIVE_ATTRIBUTE_NAME, "set timer to active/inactive"));
        return result;
    }

}
