package com.bright_side_it.fliesenui.dto.dao;

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
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTODefinitionDAOResult;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath.DefinitionDocumentType;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class DTODefinitionDAO {
    private static final String ROOT_NODE_NAME = "dto";

    public DTODefinitionDAOResult readDTODefinition(File file) throws Exception {
        DTODefinitionDAOResult result = new DTODefinitionDAOResult();

        NodePathLogic nodePathLogic = new NodePathLogic();
        NodePath nodePath = new NodePath();
        nodePath.setNodeIndexChain(new ArrayList<>());
        nodePath.setTopElementID("");
        nodePath.setDefinitionDocumentFile(file);
        nodePath.setDefinitionDocumentType(DefinitionDocumentType.DTO);

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

        result.setDTODefinition(new DTODefinition());

        String id = BaseUtil.idToFirstCharLowerCase(FileUtil.getFilenameWithoutEnding(file.getName()));
        result.getDTODefinition().setID(id);
        result.getDTODefinition().setDerived(false);
        result.getDTODefinition().setNodePath(nodePath);

        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(root)) {
            NodePath childNodePath = nodePathLogic.createChildNodePath(nodePath, nodeIndex);
            try {
                new DTOFieldDAO().readDTOField(i, childNodePath, result);
            } catch (ScreenDefionitionReadException e) {
                addError(result, e);
            } catch (Exception e) {
                addError(result, childNodePath, null, "Could not read node #" + nodeIndex + ": " + e.getMessage());
            }
            nodeIndex++;
        }

        ValidationUtil.validateAllowedAttributes(root, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
        
        return result;
    }

    public static boolean assertName(Node node, String expectedName, NodePath nodePath, DTODefinitionDAOResult result) {
        String name = node.getNodeName();
        if (name.equals(expectedName)) {
            return true;
        } else {
            addError(result, nodePath, "", "Expected name named '" + expectedName + "', but found name '" + name + "'");
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

    public static void addError(DTODefinitionDAOResult result, ScreenDefionitionReadException e) {
        addError(result, e.getNodePath(), e.getAttributeName(), e.getMessage());
    }

    public static void addError(DTODefinitionDAOResult result, NodePath nodePath, String attribute, String message) {
        addError(result, nodePath, attribute, null, message);
    }

    public static void addError(DTODefinitionDAOResult result, NodePath nodePath, String attribute, ProblemType problemType, String message) {
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

    public static void addError(DTODefinitionDAOResult result, NodePath nodePath, String attribute, Exception e) {
        addError(result, nodePath, attribute, "" + e);
    }

    public List<AssistValue> getRootPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, ROOT_NODE_NAME, "top node"));
        return result;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, DTOFieldDAO.getNodeName(), "Field"));
        return result;
    }

    public static String getNodeName() {
        return ROOT_NODE_NAME;
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        return result;
    }
}
