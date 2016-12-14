package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;

public class PluginInstanceDAO {
    private static final String NODE_NAME = "plugin";
    public static final String TYPE_ATTRIBUTE_NAME = "type";

    public boolean isPluginDeclarationNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readPluginInstance(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, LayoutCell layoutCell) throws Exception {
        if ("#text".equals(node.getNodeName())) {
            log("Found text node. Type = " + node.getNodeType() + ", text content = '" + node.getTextContent() + "'");
        }

        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        PluginInstance pluginInstance = new PluginInstance();
        pluginInstance.setNodePath(nodePath);

        readAttributes(pluginInstance, node);

        pluginInstance.setID(XMLUtil.getStringAttributeRequired(node, BaseConstants.ID_ATTRIBUTE_NAME));
        pluginInstance.setPluginType(XMLUtil.getStringAttributeRequired(node, TYPE_ATTRIBUTE_NAME));

        addToParent(result, layoutCell, pluginInstance);

        EventParameterDAO eventParameterDAO = new EventParameterDAO();

        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(node)) {
            NodePath childNodePath = new NodePathLogic().createChildNodePath(nodePath, nodeIndex);
            try {
                eventParameterDAO.readEventParameter(i, childNodePath, result, pluginInstance);
            } catch (Exception e) {
                ScreenDefinitionDAO.addError(result, childNodePath, e);
            }
            nodeIndex++;
        }
        
        // TODO: Later: add attribute validation (some work, because the attribute names of the plugin must be passed to this function)
    }

    private void readAttributes(PluginInstance plugin, Node node) {
        plugin.setParameterValues(new TreeMap<String, String>());
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String attributeName = attribute.getNodeName();
            String attributeValue = attribute.getNodeValue();
            if (attributeValue == null) {
                attributeValue = "";
            }
            log("readAttributes: attributeName = '" + attributeName + "' , attributeValue = '" + attributeValue + "'");
            if ((!attributeName.equals(BaseConstants.ID_ATTRIBUTE_NAME)) && (!attributeName.equals(TYPE_ATTRIBUTE_NAME))) {
                plugin.getParameterValues().put(attributeName, attributeValue);
            }
        }
    }

    private void addToParent(ScreenDefinitionDAOResult result, LayoutCell layoutCell, PluginInstance plugin) {
        if (isPartOfCell(layoutCell)) {
            if (layoutCell.getCellItems() == null) {
                layoutCell.setCellItems(new ArrayList<>());
            }
            layoutCell.getCellItems().add(plugin);
        } else {
            if (result.getScreenDefinition().getTopElements() == null) {
                result.getScreenDefinition().setTopElements(new ArrayList<>());
            }
            result.getScreenDefinition().getTopElements().add(plugin);
        }
    }

    private boolean isPartOfCell(LayoutCell layoutCell) {
        return layoutCell != null;
    }


    private void log(String message) {
        System.out.println("PluginInstanceDAO: " + message);
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, BaseConstants.ID_ATTRIBUTE_NAME, "ID of the plugin 'instance' that can be used in the screen"));
        result.add(BaseUtil.createAssistValue(true, TYPE_ATTRIBUTE_NAME, "Type of plugin (which is the name of the XML file of the plugin)"));
        return result;
    }

}
