package com.bright_side_it.fliesenui.base.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {
    private static final String VALUE_TRUE = "true";
    private static final String VALUE_FALSE = "false";

    public static List<Node> getChildrenWithoutTextNodes(Node parent) {
        List<Node> result = new ArrayList<>();
        NodeList nodeList = parent.getChildNodes();
        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node node = nodeList.item(i);
            //            log("type = " + node.getNodeType());
            if ((node.getNodeType() != Node.TEXT_NODE) && (node.getNodeType() != Node.COMMENT_NODE)) {
                result.add(node);
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    private static void log(String message) {
        System.out.println("XMLUtil: " + message);
    }

    public static String getStringAttributeRequired(Node node, String attributeName) throws Exception {
        NamedNodeMap attributes = node.getAttributes();
        Node value = attributes.getNamedItem(attributeName);
        if (value == null) {
            throw new Exception("Missing attribute '" + attributeName + "' in node '" + node.getNodeName() + "'");
        }
        String text = value.getTextContent();
        if (text == null) {
            throw new Exception("Missing text  '" + attributeName + "' in node '" + node.getNodeName() + "'");
        }

        return text;
    }

    public static int getIntAttributeRequired(Node node, String attributeName) throws Exception {
        String string = getStringAttributeRequired(node, attributeName);
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            throw new Exception(
                    "Could not read integer from value '" + string + "' in attribute '" + attributeName + "' in node '" + node.getLocalName() + "'");
        }
    }

    public static boolean getBooleanAttributeRequired(Node node, String attributeName) throws Exception {
        String string = getStringAttributeRequired(node, attributeName);
        if (VALUE_TRUE.equals(string)) {
            return true;
        } else if (VALUE_FALSE.equals(string)) {
            return false;
        }
        throw new Exception(
                "Could not read boolean from value '" + string + "' in attribute '" + attributeName + "' in node '" + node.getLocalName() + "'");
    }

    public static String getStringAttributeOptional(Node node, String attributeName, String valueIfEmpty) {
        NamedNodeMap attributes = node.getAttributes();
        Node value = attributes.getNamedItem(attributeName);
        if (value == null) {
            return valueIfEmpty;
        }
        String text = value.getTextContent();
        if (text == null) {
            return valueIfEmpty;
        }
        return text;
    }

    public static Boolean getBooleanAttributeOptional(Node node, String attributeName, Boolean valueIfEmpty) throws Exception {
        String string = getStringAttributeOptional(node, attributeName, null);
        if (string == null) {
            return valueIfEmpty;
        }
        if (VALUE_TRUE.equals(string)) {
            return true;
        } else if (VALUE_FALSE.equals(string)) {
            return false;
        }
        throw new Exception(
                "Could not read boolean from value '" + string + "' in attribute '" + attributeName + "' in node '" + node.getLocalName() + "'");
    }

    public static Integer getIntegerAttributeOptional(Node node, String attributeName, Integer valueIfEmpty) throws Exception {
        String string = getStringAttributeOptional(node, attributeName, null);
        if (string == null) {
            return valueIfEmpty;
        }
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            throw new Exception(
                    "Could not read integer from value '" + string + "' in attribute '" + attributeName + "' in node '" + node.getLocalName() + "'");
        }
    }

}
