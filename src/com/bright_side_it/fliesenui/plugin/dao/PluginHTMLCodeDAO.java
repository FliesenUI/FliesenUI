package com.bright_side_it.fliesenui.plugin.dao;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class PluginHTMLCodeDAO {
    private static final String NODE_NAME = "html-code";

    public static final String TYPE_ATTRIBUTE_NAME = "type";
    private static final String BROWSER_TYPE_NAME_JAVA_FX = "Java-FX";
    private static final String BROWSER_TYPE_NAME_WEB = "Web";


    public void readPluginHTMLCode(Node node, NodePath nodePath, PluginDefinitionDAOResult result) throws ScreenDefionitionReadException, Exception {
        if (!PluginDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        if (result.getPluginDefinition().getHtmlCode() == null) {
            result.getPluginDefinition().setHtmlCode(new EnumMap<BrowserType, String>(BrowserType.class));
        }

        String text = node.getTextContent().trim();

        String typeString = XMLUtil.getStringAttributeRequired(node, TYPE_ATTRIBUTE_NAME);
        BrowserType browserType = null;
        if (BROWSER_TYPE_NAME_JAVA_FX.equals(typeString)) {
            browserType = BrowserType.JAVA_FX;
        } else if (BROWSER_TYPE_NAME_WEB.equals(typeString)) {
            browserType = BrowserType.WEB;
        } else {
            throw new Exception("Unknown type: '" + typeString + "'");
        }
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, getAttributeNames(), result);

        result.getPluginDefinition().getHtmlCode().put(browserType, text);
    }

    private Set<String> getAttributeNames() {
    	return new TreeSet<String>(Arrays.asList(TYPE_ATTRIBUTE_NAME));
	}

    public static String getNodeName() {
        return NODE_NAME;
    }

}
