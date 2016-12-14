package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class LayoutBarDAO {
    private static final String NODE_NAME = "bar";
    public static final String VISIBLE_ATTRIBUTE_NAME = "visible";

    public void readLayoutBar(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutContainer layoutContainer) throws Exception {
        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        if (layoutContainer.getBars() == null) {
            layoutContainer.setBars(new ArrayList<>());
        }
        LayoutBar layoutBar = new LayoutBar();
        layoutBar.setID(XMLUtil.getStringAttributeOptional(node, BaseConstants.ID_ATTRIBUTE_NAME, null));
        layoutBar.setVisible(XMLUtil.getBooleanAttributeOptional(node, VISIBLE_ATTRIBUTE_NAME, true));
        layoutBar.setNodePath(nodePath);
        layoutContainer.getBars().add(layoutBar);

        LayoutCellDAO layoutCellDAO = new LayoutCellDAO();

        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
                layoutCellDAO.readLayoutCell(i, childNodePath, result, layoutBar);
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
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
        result.add(BaseUtil.createAssistValue(null, LayoutCellDAO.getNodeName(), "cell"));


        return result;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(false, BaseConstants.ID_ATTRIBUTE_NAME, "id"));
        result.add(BaseUtil.createAssistValue(false, VISIBLE_ATTRIBUTE_NAME, "visibility"));
        return result;
    }

}
