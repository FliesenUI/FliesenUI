package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class CodeEditorWidgetDAO {

    private static final String NODE_NAME = "codeEditor";
    private static final String TEXT_ATTRIBUTE_NAME = "text";

    public boolean isCodeEditorNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readCodeEditorWidget(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutCell layoutCell) throws Exception {
        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        if (layoutCell.getCellItems() == null) {
            layoutCell.setCellItems(new ArrayList<>());
        }

        CodeEditorWidget widget = new CodeEditorWidget();
        layoutCell.getCellItems().add(widget);

        widget.setNodePath(nodePath);
        widget.setText(XMLUtil.getStringAttributeOptional(node, TEXT_ATTRIBUTE_NAME, null));
        widget.setID(XMLUtil.getStringAttributeOptional(node, BaseConstants.ID_ATTRIBUTE_NAME, null));
        
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
        result.add(BaseUtil.createAssistValue(true, BaseConstants.ID_ATTRIBUTE_NAME, "id of the widget"));
        result.add(BaseUtil.createAssistValue(false, TEXT_ATTRIBUTE_NAME, "text in the widget on loading"));
        return result;
    }
}
