package com.bright_side_it.fliesenui.project.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.project.model.ProjectDefinitionDAOResult;
import com.bright_side_it.fliesenui.project.model.SharedReplyInterface;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class SharedReplyInterfaceDAO {
    private static final String NODE_NAME = "sharedReplyInterface";
    
    public boolean isSharedReplyInterfaceNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readSharedReplyInterfaceNode(Node node, NodePath nodePath, ProjectDefinitionDAOResult result) throws Exception {
        SharedReplyInterface sharedReplyInterface = new SharedReplyInterface();
        sharedReplyInterface.setNodePath(nodePath);
        sharedReplyInterface.setID(XMLUtil.getStringAttributeRequired(node, BaseConstants.ID_ATTRIBUTE_NAME));
        
        if (result.getProjectDefinition().getSharedReplyInterfaces() == null){
        	result.getProjectDefinition().setSharedReplyInterfaces(new TreeMap<>());
        }
        if (result.getProjectDefinition().getSharedReplyInterfaces().containsKey(sharedReplyInterface.getID())){
        	ProjectDefinitionDAO.addError(result, nodePath, BaseConstants.ID_ATTRIBUTE_NAME, ProblemType.SHARED_REPLY_INTERFACE_ID_USED_MULTIPLE_TIMES, "There already is a shared reply interface with this ID");
        	return;
        }
        result.getProjectDefinition().getSharedReplyInterfaces().put(sharedReplyInterface.getID(), sharedReplyInterface);
        
        int nodeIndex = 0;
        NodePathLogic nodePathLogic = new NodePathLogic();
        SharedReplyInterfaceScreenDAO sharedReplyInterfaceScreenDAO = new SharedReplyInterfaceScreenDAO();
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = nodePathLogic.createChildNodePath(nodePath, nodeIndex);
            try {
                if (sharedReplyInterfaceScreenDAO.isSharedReplyInterfaceScreenNode(i)) {
                	sharedReplyInterfaceScreenDAO.readSharedReplyInterfaceScreenNode(i, childNodePath, result, sharedReplyInterface);
                } else {
                    throw new Exception("Unknown node type: " + i.getNodeName());
                }
            } catch (Exception e) {
            	e.printStackTrace();
            	ProjectDefinitionDAO.addError(result, childNodePath, null, ProblemType.SHARED_REPLY_INTERFACE_COULD_NOT_READ_CHILD_NODES , e);
            }
            nodeIndex++;
        }

        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
        
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, SharedReplyInterfaceScreenDAO.getNodeName(), "screen"));
        return result;
    }
    
    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, BaseConstants.ID_ATTRIBUTE_NAME, "id - name of the interface"));
        return result;
    }


}
