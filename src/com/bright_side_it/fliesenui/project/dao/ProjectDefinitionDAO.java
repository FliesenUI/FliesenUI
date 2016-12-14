package com.bright_side_it.fliesenui.project.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.project.model.ProjectDefinition;
import com.bright_side_it.fliesenui.project.model.ProjectDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath.DefinitionDocumentType;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class ProjectDefinitionDAO {
	private static final String ROOT_NODE_NAME = "FliesenUIProject";
    
    private static final String FORMAT_VERSION_VALUE = "1";
    
    public static final String FORMAT_VERSION_ATTRIBUTE_NAME = "formatVersion";
    public static final String TITLE_ATTRIBUTE_NAME = "title";
    public static final String DARK_THEME_ATTRIBUTE_NAME = "darkTheme";
    public static final String THEME_PRIMARY_PALETTE_ATTRIBUTE_NAME = "themePrimaryPalette";
    public static final String THEME_ACCENT_PALETTE_ATTRIBUTE_NAME = "themeAccentPalette";
    public static final String THEME_BACKGROUND_PALETTE_ATTRIBUTE_NAME = "themeBackgroundPalette";
    public static final String THEME_WARN_PALETTE_ATTRIBUTE_NAME = "themeWarnPalette";
    public static final String MARGIN_ATTRIBUTE_NAME = "margin";
    public static final String START_SCREEN_ID_ATTRIBUTE_NAME = "startScreenID";

    /**
     * possible colors themes (https://material.google.com/style/color.html#color-color-palette):
     */
    private static final Set<String> POSSIBLE_PALATTES = new TreeSet<String>(Arrays.asList("red", "pink", "purple", "deep-purple", "indigo", "blue", "light-blue", "cyan",
            "teal", "green", "light-green", "lime", "yellow", "amber", "orange", "deep-orange", "brown", "grey", "blue-grey"));


    public ProjectDefinitionDAOResult readProjectDefiontion(File projectDir, File file) throws Exception {
        ProjectDefinitionDAOResult result = new ProjectDefinitionDAOResult();
        result.setProjectDefinition(new ProjectDefinition());

        NodePathLogic nodePathLogic = new NodePathLogic();
        NodePath nodePath = new NodePath();
        nodePath.setNodeIndexChain(new ArrayList<>());
        nodePath.setTopElementID("");
        nodePath.setDefinitionDocumentFile(file);
        nodePath.setDefinitionDocumentType(DefinitionDocumentType.PROJECT);


        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
        } catch (Exception e) {
            addError(result, nodePath, null, ProblemType.INVALID_XML_DATA, new Exception("Could not read file '" + file.getAbsolutePath() + "' as XML", e));
            return result;
        }

        Node root = doc.getDocumentElement();


        nodePath = nodePathLogic.createChildNodePath(nodePath, 0); //:the root node is the first node in the XML document so the top node path already starts with 1

        assertName(root, ROOT_NODE_NAME, nodePath, result);

        result.getProjectDefinition().setNodePath(nodePath);

        String formatVersion = XMLUtil.getStringAttributeRequired(root, FORMAT_VERSION_ATTRIBUTE_NAME);
        if (!FORMAT_VERSION_VALUE.equals(formatVersion)){
        	throw new Exception("Unknown format version. Expected: " + FORMAT_VERSION_VALUE + ", found: '" + formatVersion + "'");
        }
        
        
        result.getProjectDefinition().setStartScreenID(XMLUtil.getStringAttributeOptional(root, START_SCREEN_ID_ATTRIBUTE_NAME, null));
        result.getProjectDefinition().setDarkTheme(XMLUtil.getBooleanAttributeRequired(root, DARK_THEME_ATTRIBUTE_NAME));
        result.getProjectDefinition().setThemePrimaryPalette(XMLUtil.getStringAttributeRequired(root, THEME_PRIMARY_PALETTE_ATTRIBUTE_NAME));
        result.getProjectDefinition().setThemeAccentePalette(XMLUtil.getStringAttributeRequired(root, THEME_ACCENT_PALETTE_ATTRIBUTE_NAME));
        result.getProjectDefinition().setThemeBackgroundPalette(XMLUtil.getStringAttributeRequired(root, THEME_BACKGROUND_PALETTE_ATTRIBUTE_NAME));
        result.getProjectDefinition().setThemeWarnPalette(XMLUtil.getStringAttributeRequired(root, THEME_WARN_PALETTE_ATTRIBUTE_NAME));
        result.getProjectDefinition().setTitle(XMLUtil.getStringAttributeRequired(root, TITLE_ATTRIBUTE_NAME));
        result.getProjectDefinition().setMargin(XMLUtil.getIntAttributeRequired(root, MARGIN_ATTRIBUTE_NAME));

        
        int nodeIndex = 0;
        ProjectOutputDAO projectOutputDAO = new ProjectOutputDAO();
        SharedReplyInterfaceDAO sharedReplyInterfaceDAO = new SharedReplyInterfaceDAO();
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(root)) {
            NodePath childNodePath = nodePathLogic.createChildNodePath(nodePath, nodeIndex);
            try {
                if (projectOutputDAO.isProjectOutputNode(i)) {
                	projectOutputDAO.readProjectOutputNode(i, childNodePath, result, projectDir);
                } else if (sharedReplyInterfaceDAO.isSharedReplyInterfaceNode(i)){
                	sharedReplyInterfaceDAO.readSharedReplyInterfaceNode(i, childNodePath, result);
                } else {
                    throw new Exception("Unknown node type: " + i.getNodeName());
                }
            } catch (Exception e) {
                addError(result, childNodePath, null, ProblemType.PROJECT_DEFINITION_COULD_NOT_READ_CHILD_NODES , e);
            }
            nodeIndex++;
        }
        
        ValidationUtil.validateAllowedAttributes(root, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
        
        return result;
    }

    public static boolean assertName(Node node, String expectedName, NodePath nodePath, ProjectDefinitionDAOResult result) {
        String name = node.getNodeName();
        if (name.equals(expectedName)) {
            return true;
        } else {
            addError(result, nodePath, "", ProblemType.WRONG_XML_NODE_NAME, "Expected name named '" + expectedName + "', but found name '" + name + "'");
            return false;
        }
    }

    public static void addError(ProjectDefinitionDAOResult result, NodePath nodePath, String attribute, ProblemType problemType, String message) {
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

    public static void addError(ProjectDefinitionDAOResult result, NodePath nodePath, String attribute, ProblemType problemType, Exception e) {
        addError(result, nodePath, attribute, problemType, "" + e);
    }

    public static String getNodeName() {
        return ROOT_NODE_NAME;
    }

    public List<AssistValue> getRootPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, ROOT_NODE_NAME, "top node"));
        return result;
    }

    public AssistValueListProvider getPossiblePaletteAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        for (String i : POSSIBLE_PALATTES) {
            assistValues.add(BaseUtil.createAssistValue(null, i, null));
        }
        return new AssistValueList(assistValues);
    }

    public AssistValueListProvider getPossibleDarkThemeAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.TRUE, "dark theme"));
        assistValues.add(BaseUtil.createAssistValue(null, "" + Boolean.FALSE, "light theme"));
        return new AssistValueList(assistValues);
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();

        result.add(BaseUtil.createAssistValue(true, TITLE_ATTRIBUTE_NAME, "Project title"));
        result.add(BaseUtil.createAssistValue(true, FORMAT_VERSION_ATTRIBUTE_NAME, "format version of this FliesenUI project"));
        result.add(BaseUtil.createAssistValue(true, START_SCREEN_ID_ATTRIBUTE_NAME, "ID of the start screen (screen that is shown when the application starts)"));
        result.add(BaseUtil.createAssistValue(false, DARK_THEME_ATTRIBUTE_NAME, "dark theme: true/false"));
        result.add(BaseUtil.createAssistValue(true, THEME_PRIMARY_PALETTE_ATTRIBUTE_NAME, "primary palette"));
        result.add(BaseUtil.createAssistValue(true, THEME_ACCENT_PALETTE_ATTRIBUTE_NAME, "accent palette"));
        result.add(BaseUtil.createAssistValue(true, THEME_BACKGROUND_PALETTE_ATTRIBUTE_NAME, "background palette"));
        result.add(BaseUtil.createAssistValue(true, THEME_WARN_PALETTE_ATTRIBUTE_NAME, "warn palette"));
        result.add(BaseUtil.createAssistValue(true, MARGIN_ATTRIBUTE_NAME, "margin in pixels to the left and right side of every screen"));
        return result;
    }
    
    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, ProjectOutputDAO.getNodeName(), "Project output"));
        result.add(BaseUtil.createAssistValue(null, SharedReplyInterfaceDAO.getNodeName(), "Shared Reply Interface (contains all methods that exist in all specified screens)"));
        return result;
    }

}
