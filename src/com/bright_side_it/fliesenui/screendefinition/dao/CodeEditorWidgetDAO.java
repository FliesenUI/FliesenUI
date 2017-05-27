package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget.CodeEditorWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class CodeEditorWidgetDAO {

    public static final String NODE_NAME_CODE_EDITOR = "codeEditor";
    public static final String NODE_NAME_TEXT_EDITOR = "textEditor";

    private static final String TEXT_ATTRIBUTE_NAME = "text";
    public static final String HEIGHT_ATTRIBUTE_NAME = "height";
    
    public boolean isCodeEditorNode(Node node) {
        return node.getNodeName().equals(NODE_NAME_CODE_EDITOR) || node.getNodeName().equals(NODE_NAME_TEXT_EDITOR);
    }

    public void readCodeEditorWidget(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutCell layoutCell) throws Exception {
        if (layoutCell.getCellItems() == null) {
            layoutCell.setCellItems(new ArrayList<>());
        }

        CodeEditorWidget widget = new CodeEditorWidget();
        widget.setType(getNodeNameToTypeMap().get(node.getNodeName()));
        if (widget.getType() == null) {
            throw new ScreenDefionitionReadException("Unknown widget type: '" + node.getNodeName() + "'", nodePath, "");
        }

        layoutCell.getCellItems().add(widget);

        widget.setNodePath(nodePath);
        widget.setText(XMLUtil.getStringAttributeOptional(node, TEXT_ATTRIBUTE_NAME, null));
        widget.setID(XMLUtil.getStringAttributeOptional(node, BaseConstants.ID_ATTRIBUTE_NAME, null));
        widget.setHeight(XMLUtil.getIntegerAttributeOptional(node, HEIGHT_ATTRIBUTE_NAME, null));
        
        EventListenerDAO eventListenerDAO = new EventListenerDAO();
        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
            	if (EventListenerDAO.NODE_NAME_LISTEN_TO_KEY_PRESS.equals(i.getNodeName())){
            		eventListenerDAO.readEventListener(i, childNodePath, result, widget);
            	} else if (EventListenerDAO.NODE_NAME_LISTEN_TO_KEY_DOWN.equals(i.getNodeName())){
            		eventListenerDAO.readEventListener(i, childNodePath, result, widget);
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

    private static Map<String, CodeEditorWidgetType> getNodeNameToTypeMap() {
		Map<String, CodeEditorWidgetType> result = new TreeMap<>();
		result.put(NODE_NAME_CODE_EDITOR, CodeEditorWidgetType.CODE_EDITOR);
		result.put(NODE_NAME_TEXT_EDITOR, CodeEditorWidgetType.TEXT_EDITOR);
		return result;
	}

    public static Collection<String> getNodeNames() {
        return getNodeNameToTypeMap().keySet();
    }


    public List<AssistValue> getPossibleChildTags(String nodeName) {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, EventListenerDAO.NODE_NAME_LISTEN_TO_KEY_PRESS, "Key press event that should be listened to"));
        result.add(BaseUtil.createAssistValue(null, EventListenerDAO.NODE_NAME_LISTEN_TO_KEY_DOWN, "Key down event that should be listened to"));
        return result;
    }

    public List<AssistValue> getTagAttributes(String nodeName) {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, BaseConstants.ID_ATTRIBUTE_NAME, "id of the widget"));
        result.add(BaseUtil.createAssistValue(false, TEXT_ATTRIBUTE_NAME, "text in the widget on loading"));
        result.add(BaseUtil.createAssistValue(false, HEIGHT_ATTRIBUTE_NAME, "height in pixels"));
        return result;
    }
}
