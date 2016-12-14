package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameterContainer;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter.WidgetProperty;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class EventParameterDAO {
    public static final String WIDGET_PROPERTY_NAME_TEXT = "text";
    public static final String WIDGET_PROPERTY_NAME_SELECTED = "selected";
    public static final String WIDGET_PROPERTY_NAME_SELECTED_ID = "selectedID";
    public static final String WIDGET_PROPERTY_NAME_LINE = "line";
    public static final String WIDGET_PROPERTY_NAME_POS_IN_LINE = "posInLine";
    private static final String NODE_NAME = "eventParameter";
    public static final String VALUE_ATTRIBUTE_NAME = "value";

    public void readEventParameter(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, EventParameterContainer container) throws Exception {
        if (!ScreenDefinitionDAO.assertName(node, NODE_NAME, nodePath, result)) {
            return;
        }
        EventParameter eventParameter = new EventParameter();

        if (container.getEventParameters() == null) {
            container.setEventParameters(new ArrayList<>());
        }
        container.getEventParameters().add(eventParameter);

        String text = XMLUtil.getStringAttributeRequired(node, VALUE_ATTRIBUTE_NAME);
        int posOfDot = text.indexOf(".");
        int posOfColumn = text.indexOf(":");
        if ((posOfDot < 0) && (posOfColumn < 0)) {
            eventParameter.setDTOID(text);
        } else if (posOfDot >= 0) {
            eventParameter.setWidgetID(text.substring(0, posOfDot));
            eventParameter.setWidgetProperty(parseProperty(text.substring(posOfDot + 1)));
        } else {
            eventParameter.setPluginInstanceID(text.substring(0, posOfColumn));
            eventParameter.setPluginVariableName(text.substring(posOfColumn + 1));
        }
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
    }

    private WidgetProperty parseProperty(String string) throws Exception {
        if (WIDGET_PROPERTY_NAME_TEXT.equals(string)) {
            return WidgetProperty.TEXT;
        } else if (WIDGET_PROPERTY_NAME_SELECTED.equals(string)) {
            return WidgetProperty.SELECTED;
        } else if (WIDGET_PROPERTY_NAME_SELECTED_ID.equals(string)) {
        	return WidgetProperty.SELECTED_ID;
        } else if (WIDGET_PROPERTY_NAME_LINE.equals(string)) {
            return WidgetProperty.LINE;
        } else if (WIDGET_PROPERTY_NAME_POS_IN_LINE.equals(string)) {
            return WidgetProperty.POS_IN_LINE;
        }
        throw new Exception("Unknown widget property: '" + string + "'");
    }

    public boolean isEventParameterNode(Node node) {
        return (node.getNodeName().equals(NODE_NAME));
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        return result;
    }

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, VALUE_ATTRIBUTE_NAME, "value that is passed as parameter"));
        return result;
    }
}
