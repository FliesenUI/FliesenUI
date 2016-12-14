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
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class LayoutCellDAO {
    public static final String SIZE_ATTRIBUTE_NAME = "size";
    public static final String HEIGHT_ATTRIBUTE_NAME = "height";
    public static final String VISIBLE_ATTRIBUTE_NAME = "visible";
    public static final String BACKGROUND_COLOR_ATTRIBUTE_NAME = "backgroundColor";
    private static final String LAYOUT_CELL_NODE_NAME = "cell";

    public void readLayoutCell(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutBar layoutBar) throws Exception {
        if (!ScreenDefinitionDAO.assertName(node, LAYOUT_CELL_NODE_NAME, nodePath, result)) {
            return;
        }
        if (layoutBar.getCells() == null) {
            layoutBar.setCells(new ArrayList<>());
        }
        LayoutCell layoutCell = new LayoutCell();
        layoutCell.setNodePath(nodePath);
        layoutBar.getCells().add(layoutCell);

        int size = XMLUtil.getIntAttributeRequired(node, SIZE_ATTRIBUTE_NAME);
        layoutCell.setSize(size);
        layoutCell.setHeight(XMLUtil.getIntegerAttributeOptional(node, HEIGHT_ATTRIBUTE_NAME, null));
        layoutCell.setBackgroundColor(XMLUtil.getStringAttributeOptional(node, BACKGROUND_COLOR_ATTRIBUTE_NAME, null));
        layoutCell.setID(XMLUtil.getStringAttributeOptional(node, BaseConstants.ID_ATTRIBUTE_NAME, null));
        layoutCell.setVisible(XMLUtil.getBooleanAttributeOptional(node, VISIBLE_ATTRIBUTE_NAME, true));

        BasicWidgetDAO widgetDAO = new BasicWidgetDAO();
        CodeEditorWidgetDAO codeEditorDAO = new CodeEditorWidgetDAO();
        TableWidgetDAO tableWidgetDAO = new TableWidgetDAO();
        SelectBoxDAO selectBoxDAO = new SelectBoxDAO();
        LayoutContainerDAO layoutContainerDAO = new LayoutContainerDAO();
        PluginInstanceDAO pluginInstanceDAO = new PluginInstanceDAO();

        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
                if (widgetDAO.isWidgetNode(i)) {
                    widgetDAO.readWidget(i, childNodePath, result, layoutCell);
                } else if (tableWidgetDAO.isTableWidgetNode(i)) {
                    tableWidgetDAO.readTableWidget(i, childNodePath, result, layoutCell);
                } else if (selectBoxDAO.isSelectBoxNode(i)) {
                	selectBoxDAO.readSelectBox(i, childNodePath, result, layoutCell);
                } else if (layoutContainerDAO.isLayoutContainerNode(i)) {
                    layoutContainerDAO.readLayoutContainer(i, childNodePath, result, layoutCell);
                } else if (pluginInstanceDAO.isPluginDeclarationNode(i)) {
                    pluginInstanceDAO.readPluginInstance(i, childNodePath, result, layoutCell);
                } else if (codeEditorDAO.isCodeEditorNode(i)) {
                    codeEditorDAO.readCodeEditorWidget(i, childNodePath, result, layoutCell);
                } else {
                    throw new Exception("Unexpected node in cell node: '" + i.getNodeName() + "'");
                }
            } catch (ScreenDefionitionReadException e) {
                ScreenDefinitionDAO.addError(result, e);
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }

        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
    }

    public static String getNodeName() {
        return LAYOUT_CELL_NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.addAll(BasicWidgetDAO.createAssistValues());
        result.add(BaseUtil.createAssistValue(null, TableWidgetDAO.getNodeName(), "table"));
        result.add(BaseUtil.createAssistValue(null, SelectBoxDAO.getNodeName(), "select box"));
        result.add(BaseUtil.createAssistValue(null, CodeEditorWidgetDAO.getNodeName(), "code editor"));
        result.add(BaseUtil.createAssistValue(null, LayoutContainerDAO.getNodeName(), "nested layout container"));
        result.add(BaseUtil.createAssistValue(null, PluginInstanceDAO.getNodeName(), "plugin (defined in separate file)"));

        return result;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, SIZE_ATTRIBUTE_NAME,
                "number between 1 and 100 that specifies the size in percent. The sum of all cell sizes in a bar must be 100"));
        result.add(BaseUtil.createAssistValue(false, HEIGHT_ATTRIBUTE_NAME, "height of the cell in pixels."));
        result.add(BaseUtil.createAssistValue(false, BACKGROUND_COLOR_ATTRIBUTE_NAME, "background color of the cel in the hex format (example: #ff0000)."));
        result.add(BaseUtil.createAssistValue(false, BaseConstants.ID_ATTRIBUTE_NAME, "ID. If the ID is set the visibility of the cell may be changed"));
        result.add(BaseUtil.createAssistValue(false, VISIBLE_ATTRIBUTE_NAME, "visibility"));
        return result;
    }

}
