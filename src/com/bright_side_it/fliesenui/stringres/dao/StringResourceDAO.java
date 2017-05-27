package com.bright_side_it.fliesenui.stringres.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath.DefinitionDocumentType;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.stringres.model.StringResource;
import com.bright_side_it.fliesenui.stringres.model.StringResourceDAOResult;
import com.bright_side_it.fliesenui.stringres.model.StringResourceItem;

public class StringResourceDAO {
    private static final String ROOT_NODE_NAME = "resources";
    private static final String STRING_NODE_NAME = "string";
    public static final String NAME_ATTRIBUTE_NAME = "name";

    public StringResourceDAOResult readStringResource(File file, String stringResourceID) throws Exception {
    	StringResourceDAOResult result = new StringResourceDAOResult();

        NodePathLogic nodePathLogic = new NodePathLogic();
        NodePath nodePath = new NodePath();
        nodePath.setNodeIndexChain(new ArrayList<>());
        nodePath.setTopElementID("");
        nodePath.setDefinitionDocumentFile(file);
        nodePath.setDefinitionDocumentType(DefinitionDocumentType.STRING_RESOURCE);

        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
        } catch (Exception e) {
            addError(result, nodePath, null, ProblemType.INVALID_XML_DATA, "Could not read file '" + file.getAbsolutePath() + "' as XML: " + e);
            return result;
        }

        nodePath = nodePathLogic.createChildNodePath(nodePath, 0); //:the root node is the first node in the XML document so the top node path already starts with 1

        Node root = doc.getDocumentElement();
        assertName(root, ROOT_NODE_NAME, nodePath, result);
        result.setStringResource(new StringResource());
        result.getStringResource().setNodePath(nodePath);

        result.getStringResource().setID(stringResourceID);
        
        int nodeIndex = 0;
        Map<String, StringResourceItem> strings = new TreeMap<>();
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(root)) {
            NodePath childNodePath = nodePathLogic.createChildNodePath(nodePath, nodeIndex);
            try {
            	if (STRING_NODE_NAME.equals(i.getNodeName())){
            		readItem(i, strings, childNodePath, result);
            	} else {
                    throw new Exception("Unexpected child node: '" + i.getNodeName() + "'");
                }
            } catch (Exception e) {
                addError(result, childNodePath, null, "Could not read node #" + nodeIndex + ": " + e.getMessage());
            }
            nodeIndex++;
        }
		result.getStringResource().setStrings(strings);

        return result;
    }

    private void readItem(Node node, Map<String, StringResourceItem> strings, NodePath nodePath, StringResourceDAOResult result) {
    	try{
	    	String name = XMLUtil.getStringAttributeRequired(node, NAME_ATTRIBUTE_NAME);
	    	StringResourceItem item = new StringResourceItem();
	    	item.setNodePath(nodePath);
	    	item.setString(node.getTextContent().trim());
	    	strings.put(name, item);
    	} catch (Exception e){
    		addError(result, nodePath, null, "Could not read string resource item: " + e.getMessage());
    	}
	}

	public static boolean assertName(Node node, String expectedName, NodePath nodePath, StringResourceDAOResult result) {
        String name = node.getNodeName();
        if (name.equals(expectedName)) {
            return true;
        } else {
            addError(result, nodePath, "", "Expected name named '" + expectedName + "', but found name '" + name + "'");
            return false;
        }
    }

    public static void addError(StringResourceDAOResult result, ScreenDefionitionReadException e) {
        addError(result, e.getNodePath(), e.getAttributeName(), e.getMessage());
    }

    public static void addError(StringResourceDAOResult result, NodePath nodePath, String attribute, String message) {
        addError(result, nodePath, attribute, null, message);
    }

    public static void addError(StringResourceDAOResult result, NodePath nodePath, String attribute, ProblemType problemType, String message) {
        if (result.getProblems() == null) {
            result.setProblems(new ArrayList<>());
        }
        ResourceDefinitionProblem problem = new ResourceDefinitionProblem();
        problem.setMessage(message);
        problem.setAttribute(attribute);
        problem.setNodePath(nodePath);
        problem.setType(problemType);
        result.getProblems().add(problem);
    }

    public static void addError(StringResourceDAOResult result, NodePath nodePath, String attribute, Exception e) {
        addError(result, nodePath, attribute, "" + e);
    }

    public List<AssistValue> getRootPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, ROOT_NODE_NAME, "top node"));
        return result;
    }

    public static String getNodeName() {
        return ROOT_NODE_NAME;
    }


}
