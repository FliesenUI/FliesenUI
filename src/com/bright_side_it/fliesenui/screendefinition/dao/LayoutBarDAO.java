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
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar.Position;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer.Orientation;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class LayoutBarDAO {
    private static final String NODE_NAME = "bar";
    public static final String VISIBLE_ATTRIBUTE_NAME = "visible";
    public static final String POSITION_ATTRIBUTE_NAME = "position";
    public static final String POSITION_VALUR_NAME_LEFT = "left";
    public static final String POSITION_VALUR_NAME_TOP = "top";
    public static final String POSITION_VALUR_NAME_RIGHT = "right";
    public static final String POSITION_VALUR_NAME_BOTTOM = "bottom";
    public static final String POSITION_VALUR_NAME_CENTER = "center";

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
        String positionString = XMLUtil.getStringAttributeOptional(node, POSITION_ATTRIBUTE_NAME, null);
        if (positionString != null){
        	layoutBar.setPosition(parsePosition(positionString));
        }
        
        layoutBar.setNodePath(nodePath);
        layoutBar.setInBorderLayout(layoutContainer.isTopContainer() && layoutContainer.getOrientation() == Orientation.BORDER_LAYOUT);
        
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

    private Position parsePosition(String positionString) throws Exception {
    	switch (positionString) {
		case POSITION_VALUR_NAME_CENTER:
			return Position.CENTER;
		case POSITION_VALUR_NAME_LEFT:
			return Position.LEFT;
		case POSITION_VALUR_NAME_RIGHT:
			return Position.RIGHT;
		case POSITION_VALUR_NAME_TOP:
			return Position.TOP;
		case POSITION_VALUR_NAME_BOTTOM:
			return Position.BOTTOM;
		default:
			throw new Exception("Unkonwn position: '" + positionString + "'");
		}
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
        result.add(BaseUtil.createAssistValue(false, POSITION_ATTRIBUTE_NAME, "if part of border layout: position on the bar"));
        return result;
    }

    public static AssistValueListProvider getPossiblePositionAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, POSITION_VALUR_NAME_TOP, "top"));
        assistValues.add(BaseUtil.createAssistValue(null, POSITION_VALUR_NAME_BOTTOM, "bottom"));
        assistValues.add(BaseUtil.createAssistValue(null, POSITION_VALUR_NAME_LEFT, "left"));
        assistValues.add(BaseUtil.createAssistValue(null, POSITION_VALUR_NAME_RIGHT, "right"));
        assistValues.add(BaseUtil.createAssistValue(null, POSITION_VALUR_NAME_CENTER, "center"));
        return new AssistValueList(assistValues);
    }

}
