package com.bright_side_it.fliesenui.screendefinition.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath.DefinitionDocumentType;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class ScreenDefinitionDAO {
    private static final String ROOT_NODE_NAME = "screen";
    private static final String TITLE_ATTRIBUTE_NAME = "title";
    public static final String PARAMETER_DTO_ATTRIBUTE_NAME = "parameterDTO";

    public List<AssistValue> getRootPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, ROOT_NODE_NAME, "top node"));
        return result;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, LayoutContainerDAO.getNodeName(), "Layout Container"));
        result.add(BaseUtil.createAssistValue(null, DTODeclarationDAO.getNodeName(), "DTO (Data Transfer Object)"));
        result.add(BaseUtil.createAssistValue(null, TimerDAO.getNodeName(), "Timer"));
        result.add(BaseUtil.createAssistValue(null, PluginInstanceDAO.getNodeName(), "Plugin (Defined in separate file)"));
        return result;
    }

    public ScreenDefinitionDAOResult readScreenDefiontion(File file) throws Exception {
        ScreenDefinitionDAOResult result = new ScreenDefinitionDAOResult();

        NodePathLogic nodePathLogic = new NodePathLogic();
        NodePath nodePath = new NodePath();
        nodePath.setNodeIndexChain(new ArrayList<>());
        nodePath.setTopElementID("");
        nodePath.setDefinitionDocumentFile(file);
        nodePath.setDefinitionDocumentType(DefinitionDocumentType.SCREEN);


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

        result.setScreenDefinition(new ScreenDefinition());
        result.getScreenDefinition().setNodePath(nodePath);
        result.getScreenDefinition().setTitle(XMLUtil.getStringAttributeRequired(root, TITLE_ATTRIBUTE_NAME));
        result.getScreenDefinition().setParameterDTOID(XMLUtil.getStringAttributeOptional(root, PARAMETER_DTO_ATTRIBUTE_NAME, null));
        String filename = FileUtil.getFilenameWithoutEnding(file.getName());
        if (!filename.equals(BaseUtil.idToFirstCharLowerCase(filename))) {
            throw new Exception("Screen definition file names must start with a lowercase character");
        }
        String id = BaseUtil.idToFirstCharLowerCase(filename);
        result.getScreenDefinition().setID(id);
        nodePath.setTopElementID(id);

        int nodeIndex = 0;
        LayoutContainerDAO layoutContainerDAO = new LayoutContainerDAO();
        DTODeclarationDAO dtoDeclarationDAO = new DTODeclarationDAO();
        TimerDAO timerDAO = new TimerDAO();
        PluginInstanceDAO pluginInstanceDAO = new PluginInstanceDAO();
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(root)) {
            NodePath childNodePath = nodePathLogic.createChildNodePath(nodePath, nodeIndex);
            try {
                if (layoutContainerDAO.isLayoutContainerNode(i)) {
                    layoutContainerDAO.readLayoutContainer(i, childNodePath, result, null);
                } else if (dtoDeclarationDAO.isDTODeclarationNode(i)) {
                    dtoDeclarationDAO.readDTODeclaration(i, childNodePath, result);
                } else if (timerDAO.isTimerNode(i)) {
                	timerDAO.readTimerNode(i, childNodePath, result);
                } else if (pluginInstanceDAO.isPluginDeclarationNode(i)) {
                	pluginInstanceDAO.readPluginInstance(i, childNodePath, result, null);
                } else {
                    throw new Exception("Unknown node type: " + i.getNodeName());
                }
            } catch (Exception e) {
                addError(result, childNodePath, null, "Could not read node #" + nodeIndex + ": " + e.getMessage());
            }
            nodeIndex++;
        }
        
        ValidationUtil.validateAllowedAttributes(root, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);

        return result;
    }

    public static boolean assertName(Node node, String expectedName, NodePath nodePath, ScreenDefinitionDAOResult result) {
        String name = node.getNodeName();
        if (name.equals(expectedName)) {
            return true;
        } else {
            addError(result, nodePath, null, "Expected name named '" + expectedName + "', but found name '" + name + "'");
            return false;
        }
    }

    //    private String getStringAttributeRequired(ScreenDefinitionReaderResult result, Node node, String attributeName) {
    //        try {
    //            XMLUtil.getStringAttributeRequired(node, attributeName);
    //        } catch (Exception e) {
    //            addError(result, "Missing attribute '" + attributeName + "', but in node '" + node.getNodeType() + "'");
    //            return null;
    //        }
    //        return true;
    //    }

    public static void addError(ScreenDefinitionDAOResult result, NodePath nodePath, String attribute, String message) {
        addError(result, nodePath, attribute, null, message);
    }

    public static void addError(ScreenDefinitionDAOResult result, NodePath nodePath, String attribute, ProblemType problemType, String message) {
        if (result.getProblems() == null) {
            result.setProblems(new ArrayList<>());
        }
        ResourceDefinitionProblem problem = new ResourceDefinitionProblem();
        problem.setMessage(message);
        problem.setNodePath(nodePath);
        problem.setAttribute(attribute);
        problem.setType(problemType);
        result.getProblems().add(problem);
    }

    public static void addError(ScreenDefinitionDAOResult result, ScreenDefionitionReadException e) {
        addError(result, e.getNodePath(), e.getAttributeName(), "" + e.getMessage());
    }

    public static void addError(ScreenDefinitionDAOResult result, NodePath nodePath, Exception e) {
        addError(result, nodePath, null, "" + e);
    }

    public static String getNodeName() {
        return ROOT_NODE_NAME;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, TITLE_ATTRIBUTE_NAME, "Screen title"));
        result.add(BaseUtil.createAssistValue(false, PARAMETER_DTO_ATTRIBUTE_NAME, "a DTO that is passed as a parameter when the screen is opened"));
        return result;
    }
}
