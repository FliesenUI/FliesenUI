package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class TableWidgetColumnDAO {
    public static final String SIZE_ATTRIBUTE_NAME = "size";
    public static final String TEXT_ATTRIBUTE_NAME = "text";

    private static final String NODE_NAME = "column";

    public void readTableWidgetColumn(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, TableWidget tableWidget) throws Exception {
        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }

        if (tableWidget.getColumns() == null) {
            tableWidget.setColumns(new ArrayList<>());
        }
        TableWidgetColumn tableWidgetColumn = new TableWidgetColumn();
        tableWidget.getColumns().add(tableWidgetColumn);

        tableWidgetColumn.setNodePath(nodePath);
        tableWidgetColumn.setSize(XMLUtil.getIntAttributeRequired(node, SIZE_ATTRIBUTE_NAME));
        tableWidgetColumn.setText(XMLUtil.getStringAttributeRequired(node, TEXT_ATTRIBUTE_NAME));

        TableWidgetItemDAO tableItemDAO = new TableWidgetItemDAO();

        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
                if (tableItemDAO.isTableItemNode(i)) {
                    tableItemDAO.readTableItem(i, childNodePath, result, tableWidgetColumn);
                } else {
                    throw new Exception("Unexpected node: '" + i.getNodeName() + "'");
                }
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
    }

    public boolean isTableWidgetColumnNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        for (String i : TableWidgetItemDAO.getNodeNames()) {
            result.add(BaseUtil.createAssistValue(null, i, ""));
        }
        return result;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, SIZE_ATTRIBUTE_NAME, "width of the column in pixels"));
        result.add(BaseUtil.createAssistValue(null, TEXT_ATTRIBUTE_NAME, "column title"));
        return result;
    }
}
