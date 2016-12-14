package com.bright_side_it.fliesenui.plugin.dao;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinitionDAOResult;
import com.bright_side_it.fliesenui.plugin.model.PluginVariable;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class PluginVariableDAO {
    private static final String NODE_NAME = "variable";

    public static final String TYPE_ATTRIBUTE_NAME = "type";
    private static final String BASIC_TYPE_NAME_LONG = "long";
    private static final String BASIC_TYPE_NAME_STRING = "string";
    private static final String BASIC_TYPE_NAME_BOOLEAN = "boolean";


    public void readPluginVariable(Node node, NodePath nodePath, PluginDefinitionDAOResult result) throws ScreenDefionitionReadException, Exception {
        if (!PluginDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        if (result.getPluginDefinition().getVariables() == null) {
            result.getPluginDefinition().setVariables(new TreeMap<>());
        }

        PluginVariable value = new PluginVariable();

        value.setID(XMLUtil.getStringAttributeRequired(node, BaseConstants.ID_ATTRIBUTE_NAME));
        String typeString = XMLUtil.getStringAttributeRequired(node, TYPE_ATTRIBUTE_NAME);
        if (BASIC_TYPE_NAME_BOOLEAN.equals(typeString)) {
            value.setType(BasicType.BOOLEAN);
        } else if (BASIC_TYPE_NAME_STRING.equals(typeString)) {
            value.setType(BasicType.STRING);
        } else if (BASIC_TYPE_NAME_LONG.equals(typeString)) {
            value.setType(BasicType.LONG);
        } else {
            throw new Exception("Unknown type: '" + typeString + "'");
        }

        ValidationUtil.validateAllowedAttributes(node, nodePath, getAttributeNames(), result);
        
        result.getPluginDefinition().getVariables().put(value.getID(), value);
    }
    
    private Set<String> getAttributeNames() {
    	return new TreeSet<String>(Arrays.asList(BaseConstants.ID_ATTRIBUTE_NAME, TYPE_ATTRIBUTE_NAME));
	}

    public static String getNodeName() {
        return NODE_NAME;
    }

}
