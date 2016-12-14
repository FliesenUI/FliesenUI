package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer.Orientation;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class LayoutContainerDAO {
    private static final String LAYOUT_CONTAINER_NODE_NAME = "container";

    private static final String VISIBLE_ATTRIBUTE_NAME = "visible";
    public static final String ORIENTATION_ATTRIBUTE_NAME = "orientation";
    private static final String ORIENTATION_VALUE_ROW = "row";
    private static final String ORIENTATION_VALUE_COLUMN = "column";
    
    public boolean isLayoutContainerNode(Node node) {
        return node.getNodeName().equals(LAYOUT_CONTAINER_NODE_NAME);
    }

    public void readLayoutContainer(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutCell layoutCell) throws Exception {
        if ("#text".equals(node.getNodeName())) {
            log("Found text node. Type = " + node.getNodeType() + ", text content = '" + node.getTextContent() + "'");
        }

        if (!ScreenDefinitionDAO.assertName(node, LAYOUT_CONTAINER_NODE_NAME, nodePath, result)) {
            return;
        }
        LayoutContainer layoutContainer = new LayoutContainer();
        layoutContainer.setNodePath(nodePath);
        layoutContainer.setID(XMLUtil.getStringAttributeOptional(node, BaseConstants.ID_ATTRIBUTE_NAME, null));
        layoutContainer.setVisible(XMLUtil.getBooleanAttributeOptional(node, VISIBLE_ATTRIBUTE_NAME, true));


        addToParent(result, layoutCell, layoutContainer);


        String orientationString = XMLUtil.getStringAttributeRequired(node, ORIENTATION_ATTRIBUTE_NAME);
        try {
            Orientation orientation = parseOrientation(orientationString);
            layoutContainer.setOrientation(orientation);
        } catch (Exception e) {
            ScreenDefinitionDAO.addError(result, nodePath, e);
            return;
        }

        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
                new LayoutBarDAO().readLayoutBar(i, childNodePath, result, layoutContainer);
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }

        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
    }

    private void addToParent(ScreenDefinitionDAOResult result, LayoutCell layoutCell, LayoutContainer layoutContainer) {
        if (isPartOfCell(layoutCell)) {
            if (layoutCell.getCellItems() == null) {
                layoutCell.setCellItems(new ArrayList<>());
            }
            layoutCell.getCellItems().add(layoutContainer);
        } else {
            if (result.getScreenDefinition().getTopElements() == null) {
                result.getScreenDefinition().setTopElements(new ArrayList<>());
            }
            result.getScreenDefinition().getTopElements().add(layoutContainer);
        }
    }

    private boolean isPartOfCell(LayoutCell layoutCell) {
        return layoutCell != null;
    }

    private void log(String message) {
        System.out.println("LayoutContainerReader: " + message);
    }

    private Orientation parseOrientation(String string) throws Exception {
        if (string.equals(ORIENTATION_VALUE_ROW)) {
            return Orientation.ROW;
        } else if (string.equals(ORIENTATION_VALUE_COLUMN)) {
            return Orientation.COLUMN;
        }
        throw new Exception("Unknown orientation: '" + string + "'");
    }

    public static String getNodeName() {
        return LAYOUT_CONTAINER_NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, LayoutBarDAO.getNodeName(), "Bar (either row or column depening on the container orientation)"));
        return result;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, ORIENTATION_ATTRIBUTE_NAME, "which may be either '" + ORIENTATION_VALUE_ROW + "' or '" + ORIENTATION_VALUE_COLUMN + "'"));
        result.add(BaseUtil.createAssistValue(false, BaseConstants.ID_ATTRIBUTE_NAME, "id"));
        result.add(BaseUtil.createAssistValue(false, VISIBLE_ATTRIBUTE_NAME, "visibility"));
        return result;
    }

    public static AssistValueListProvider getPossibleOrientationAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, ORIENTATION_VALUE_ROW, "Orientation in rows"));
        assistValues.add(BaseUtil.createAssistValue(null, ORIENTATION_VALUE_COLUMN, "Orientation in columns"));
        return new AssistValueList(assistValues);
    }

}