package com.bright_side_it.fliesenui.plugin.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinitionDAOResult;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath.DefinitionDocumentType;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class PluginDefinitionDAO {
    private static final String ROOT_NODE_NAME = "plugin-may-become-invalid-in-different-versions";

    public PluginDefinitionDAOResult readPluginDefinition(File file) throws Exception {
        PluginDefinitionDAOResult result = new PluginDefinitionDAOResult();

        NodePathLogic nodePathLogic = new NodePathLogic();
        NodePath nodePath = new NodePath();
        nodePath.setNodeIndexChain(new ArrayList<>());
        nodePath.setTopElementID("");
        nodePath.setDefinitionDocumentFile(file);
        nodePath.setDefinitionDocumentType(DefinitionDocumentType.PLUGIN);

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

        result.setPluginDefinition(new PluginDefinition());

        String id = BaseUtil.idToFirstCharLowerCase(FileUtil.getFilenameWithoutEnding(file.getName()));
        result.getPluginDefinition().setID(id);

        PluginParameterDAO parameterDAO = new PluginParameterDAO();
        PluginVariableDAO variableDAO = new PluginVariableDAO();
        PluginEventDAO eventDAO = new PluginEventDAO();
        PluginHTMLCodeDAO htmlCodeDAO = new PluginHTMLCodeDAO();

        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(root)) {
            NodePath childNodePath = nodePathLogic.createChildNodePath(nodePath, nodeIndex);
            try {
                if (i.getNodeName().equals(PluginParameterDAO.getNodeName())) {
                    parameterDAO.readPluginParameter(i, childNodePath, result);
                } else if (i.getNodeName().equals(PluginVariableDAO.getNodeName())) {
                    variableDAO.readPluginVariable(i, childNodePath, result);
                } else if (i.getNodeName().equals(PluginEventDAO.getNodeName())) {
                    eventDAO.readPluginEvent(i, childNodePath, result);
                } else if (i.getNodeName().equals(PluginHTMLCodeDAO.getNodeName())) {
                    htmlCodeDAO.readPluginHTMLCode(i, childNodePath, result);
                } else {
                    throw new Exception("Unexpected child node: '" + i.getNodeName() + "'");
                }
            } catch (ScreenDefionitionReadException e) {
                addError(result, e);
            } catch (Exception e) {
                addError(result, childNodePath, null, "Could not read node #" + nodeIndex + ": " + e.getMessage());
            }
            nodeIndex++;
        }

        ValidationUtil.validateAllowedAttributes(root, nodePath, new TreeSet<String>(), result);
        
        return result;
    }

    public static boolean assertName(Node node, String expectedName, NodePath nodePath, PluginDefinitionDAOResult result) {
        String name = node.getNodeName();
        if (name.equals(expectedName)) {
            return true;
        } else {
            addError(result, nodePath, "", "Expected name named '" + expectedName + "', but found name '" + name + "'");
            return false;
        }
    }

    public static void addError(PluginDefinitionDAOResult result, ScreenDefionitionReadException e) {
        addError(result, e.getNodePath(), e.getAttributeName(), e.getMessage());
    }

    public static void addError(PluginDefinitionDAOResult result, NodePath nodePath, String attribute, String message) {
        addError(result, nodePath, attribute, null, message);
    }

    public static void addError(PluginDefinitionDAOResult result, NodePath nodePath, String attribute, ProblemType problemType, String message) {
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

    public static void addError(PluginDefinitionDAOResult result, NodePath nodePath, String attribute, Exception e) {
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
