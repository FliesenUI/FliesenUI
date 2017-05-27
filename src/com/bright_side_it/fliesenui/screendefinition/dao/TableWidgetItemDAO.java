package com.bright_side_it.fliesenui.screendefinition.dao;

import static com.bright_side_it.fliesenui.base.util.BaseUtil.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem.TableWidgetType;

public class TableWidgetItemDAO {
    public static final String TEXT_DTO_FIELD_ATTRIBUTE_NAME = "textDTOField";
    public static final String TOOLTIP_DTO_FIELD_ATTRIBUTE_NAME = "tooltipDTOField";
    public static final String TEXT_ATTRIBUTE_NAME = "text";
    public static final String SHOW_ONLY_ON_HOVER_ATTRIBUTE_NAME = "showOnlyOnHover";

    private static final String NODE_NAME_BUTTON = "tableButton";
    private static final String NODE_NAME_LABEL = "tableLabel";
    private static final String NODE_NAME_IMAGE = "tableImage";
    private static final String NODE_NAME_IMAGE_BUTTON = "tableImageButton";

    public boolean isTableItemNode(Node node) {
        Set<String> nodeNames = getNodeNameToTypeMap().keySet();
        return nodeNames.contains(node.getNodeName());
    }

    public void readTableItem(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, TableWidgetColumn tableWidgetColumn) throws Exception {
        if (tableWidgetColumn.getTableItems() == null) {
            tableWidgetColumn.setTableItems(new ArrayList<>());
        }

        TableWidgetItem tableWidgetItem = new TableWidgetItem();
        tableWidgetColumn.getTableItems().add(tableWidgetItem);

        tableWidgetItem.setNodePath(nodePath);

        tableWidgetItem.setType(getNodeNameToTypeMap().get(node.getNodeName()));
        if (tableWidgetItem.getType() == null) {
            throw new ScreenDefionitionReadException("Unknown table item widget type: '" + node.getNodeName() + "'", nodePath, "");
        }

        tableWidgetItem.setTextDTOField(XMLUtil.getStringAttributeOptional(node, TEXT_DTO_FIELD_ATTRIBUTE_NAME, null));
        tableWidgetItem.setTooltipDTOField(XMLUtil.getStringAttributeOptional(node, TOOLTIP_DTO_FIELD_ATTRIBUTE_NAME, null));
        tableWidgetItem.setText(XMLUtil.getStringAttributeOptional(node, TEXT_ATTRIBUTE_NAME, null));
        tableWidgetItem.setOnlyShowOnHover(XMLUtil.getBooleanAttributeOptional(node, SHOW_ONLY_ON_HOVER_ATTRIBUTE_NAME, true));
        tableWidgetItem.setID(XMLUtil.getStringAttributeOptional(node, BaseConstants.ID_ATTRIBUTE_NAME, null));

        new ImageSourceAttributesDAO().readImageSourceFromAttributes(node, nodePath, result, tableWidgetItem);

        EventParameterDAO eventParameterDAO = new EventParameterDAO();
        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
                eventParameterDAO.readEventParameter(i, childNodePath, result, tableWidgetItem);
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }
    }

    private static Map<String, TableWidgetType> getNodeNameToTypeMap() {
        Map<String, TableWidgetType> result = new TreeMap<String, TableWidgetType>();
        result.put(NODE_NAME_BUTTON, TableWidgetType.BUTTON);
        result.put(NODE_NAME_IMAGE_BUTTON, TableWidgetType.IMAGE_BUTTON);
        result.put(NODE_NAME_LABEL, TableWidgetType.LABEL);
        result.put(NODE_NAME_IMAGE, TableWidgetType.IMAGE);
        return result;
    }

    public static Collection<String> getNodeNames() {
        return getNodeNameToTypeMap().keySet();
    }

    public List<AssistValue> getPossibleChildTags(String nodeName) {
        List<AssistValue> result = new ArrayList<>();
        if (in(nodeName, NODE_NAME_BUTTON, NODE_NAME_IMAGE_BUTTON)) {
            result.add(BaseUtil.createAssistValue(null, EventParameterDAO.getNodeName(), "Parameter that is passed to the called on click method of the button"));
        }
        return result;
    }

    public List<AssistValue> getTagAttributes(String nodeName) {
        List<AssistValue> result = new ArrayList<>();
        if (in(nodeName, NODE_NAME_BUTTON, NODE_NAME_IMAGE_BUTTON)) {
        	result.add(BaseUtil.createAssistValue(null, BaseConstants.ID_ATTRIBUTE_NAME, "ID"));
        	result.add(BaseUtil.createAssistValue(false, SHOW_ONLY_ON_HOVER_ATTRIBUTE_NAME, "Default: true = only disply if mouse hovers over row"));
        }
        if (in(nodeName, NODE_NAME_BUTTON)) {
            result.add(BaseUtil.createAssistValue(null, TEXT_ATTRIBUTE_NAME, "Button text"));
        } else if (in(nodeName, NODE_NAME_LABEL)) {
            result.add(BaseUtil.createAssistValue(null, TEXT_DTO_FIELD_ATTRIBUTE_NAME, "Field of the DTO that is used as the label text"));
        } 
        
        result.add(BaseUtil.createAssistValue(null, TOOLTIP_DTO_FIELD_ATTRIBUTE_NAME, "Field of the DTO that is used a tooltip"));
        result.addAll(new ImageSourceAttributesDAO().getTagAttributes());

        return result;
    }



}
