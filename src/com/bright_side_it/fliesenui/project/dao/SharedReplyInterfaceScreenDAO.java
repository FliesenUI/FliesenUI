package com.bright_side_it.fliesenui.project.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.project.model.ProjectDefinitionDAOResult;
import com.bright_side_it.fliesenui.project.model.SharedReplyInterface;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class SharedReplyInterfaceScreenDAO {
    private static final String NODE_NAME = "screen";
    public static final String SCREEN_ID_ATTRIBUTE_NAME = "screenID";
    
    public boolean isSharedReplyInterfaceScreenNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readSharedReplyInterfaceScreenNode(Node node, NodePath nodePath, ProjectDefinitionDAOResult result, SharedReplyInterface parent) throws Exception {
    	String screenID = XMLUtil.getStringAttributeRequired(node, SCREEN_ID_ATTRIBUTE_NAME);
    	if (parent.getScreenIDs() == null){
    		parent.setScreenIDs(new ArrayList<>());
    	}
    	parent.getScreenIDs().add(screenID);
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        return result;
    }
    
    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, SCREEN_ID_ATTRIBUTE_NAME, "ID of the screen to be included"));
        return result;
    }

}
