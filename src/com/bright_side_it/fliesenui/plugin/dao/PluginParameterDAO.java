package com.bright_side_it.fliesenui.plugin.dao;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinitionDAOResult;
import com.bright_side_it.fliesenui.plugin.model.PluginParameter;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class PluginParameterDAO {
    private static final String NODE_NAME = "parameter";

    public void readPluginParameter(Node node, NodePath nodePath, PluginDefinitionDAOResult result) throws ScreenDefionitionReadException, Exception {
        if (!PluginDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        if (result.getPluginDefinition().getParameters() == null) {
            result.getPluginDefinition().setParameters(new TreeMap<>());
        }

        PluginParameter value = new PluginParameter();

        value.setID(XMLUtil.getStringAttributeRequired(node, BaseConstants.ID_ATTRIBUTE_NAME));

        ValidationUtil.validateAllowedAttributes(node, nodePath, getAttributeNames(), result);
        
        result.getPluginDefinition().getParameters().put(value.getID(), value);
    }
    
    private Set<String> getAttributeNames() {
    	return new TreeSet<String>(Arrays.asList(BaseConstants.ID_ATTRIBUTE_NAME));
	}


    public static String getNodeName() {
        return NODE_NAME;
    }

}
