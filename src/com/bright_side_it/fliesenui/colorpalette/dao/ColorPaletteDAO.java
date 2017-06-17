package com.bright_side_it.fliesenui.colorpalette.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.colorpalette.model.ColorPalette;
import com.bright_side_it.fliesenui.colorpalette.model.ColorPalette.Shade;
import com.bright_side_it.fliesenui.project.dao.ProjectDefinitionDAO;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.NodePathLogic;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath.DefinitionDocumentType;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class ColorPaletteDAO {
	private static final String ROOT_NODE_NAME = "ColorPalette";

	public static final String EXTENDED_PALETTE_ATTRIBUTE_NAME = "extendedPalette";
    public static final String SHADE_NODE_NAME = "shade";
    public static final String SHADE_NAME_ATTRIBUTE_NAME = "name";
    public static final Map<String, Shade> SHADE_NAME_TO_SHADE_MAP = createShadeNameToShadeMap();
    
    public ColorPaletteDAOResult readColorPalette(File file) throws Exception {
    	ColorPaletteDAOResult result = new ColorPaletteDAOResult();
        result.setColorPalette(new ColorPalette());

        NodePathLogic nodePathLogic = new NodePathLogic();
        NodePath nodePath = new NodePath();
        nodePath.setNodeIndexChain(new ArrayList<>());
        nodePath.setTopElementID("");
        nodePath.setDefinitionDocumentFile(file);
        nodePath.setDefinitionDocumentType(DefinitionDocumentType.COLOR_PALETTE);

        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
        } catch (Exception e) {
            addError(result, nodePath, null, ProblemType.INVALID_XML_DATA, new Exception("Could not read file '" + file.getAbsolutePath() + "' as XML", e));
            return result;
        }

        String filename = FileUtil.getFilenameWithoutEnding(file.getName());
        if (!filename.equals(BaseUtil.idToFirstCharLowerCase(filename))) {
            throw new Exception("Color palette file names must start with a lowercase character");
        }
        String id = BaseUtil.idToFirstCharLowerCase(filename);
        result.getColorPalette().setID(id);
        nodePath.setTopElementID(id);

        
        Node root = doc.getDocumentElement();
        nodePath = nodePathLogic.createChildNodePath(nodePath, 0); //:the root node is the first node in the XML document so the top node path already starts with 1
        assertName(root, ROOT_NODE_NAME, nodePath, result);
        result.getColorPalette().setNodePath(nodePath);
        result.getColorPalette().setExtendedPalette(XMLUtil.getStringAttributeOptional(root, EXTENDED_PALETTE_ATTRIBUTE_NAME, null));
        
        int nodeIndex = 0;
        for (Node i : XMLUtil.getChildrenWithoutTextNodes(root)) {
            NodePath childNodePath = nodePathLogic.createChildNodePath(nodePath, nodeIndex);
            try {
            	if (SHADE_NODE_NAME.equals(i.getNodeName())){
            		readShadeNode(result, childNodePath, i);
            	} else {
                    throw new Exception("Unknown node type: " + i.getNodeName());
                }
            } catch (Exception e) {
                addError(result, childNodePath, null, ProblemType.COLOR_PALETTE_COULD_NOT_READ_CHILD_NODES , e);
            }
            nodeIndex++;
        }
        
        ValidationUtil.validateAllowedAttributes(root, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
        
        return result;
    }

    private static Map<String, Shade> createShadeNameToShadeMap() {
		Map<String, Shade> result = new TreeMap<String, ColorPalette.Shade>();

		result.put("50", Shade.SHADE_50);
		result.put("100", Shade.SHADE_100);
		result.put("200", Shade.SHADE_200);
		result.put("300", Shade.SHADE_300);
		result.put("400", Shade.SHADE_400);
		result.put("500", Shade.SHADE_500);
		result.put("600", Shade.SHADE_600);
		result.put("700", Shade.SHADE_700);
		result.put("800", Shade.SHADE_800);
		result.put("900", Shade.SHADE_900);
		result.put("A100", Shade.SHADE_A100);
		result.put("A200", Shade.SHADE_A200);
		result.put("A400", Shade.SHADE_A400);
		result.put("A700", Shade.SHADE_A700);
		
		return result;
	}

	private void readShadeNode(ColorPaletteDAOResult result, NodePath nodePath, Node node) {
    	try{
	    	String name = XMLUtil.getStringAttributeRequired(node, SHADE_NAME_ATTRIBUTE_NAME);
	    	Shade shade = SHADE_NAME_TO_SHADE_MAP.get(name);
	    	if (shade == null){
	    		addError(result, nodePath, SHADE_NAME_ATTRIBUTE_NAME, ProblemType.COLOR_PALETTE_UNKNOWN_SHADE, "Unknown shade name: '" + name + "'");
	    		return;
	    	}
	    	
	    	String color = node.getTextContent();
	    	if (color == null){
	    		color = "";
	    	}
    		color = color.trim();

    		if (color.isEmpty()){
	    		addError(result, nodePath, "", ProblemType.COLOR_PALETTE_MISSING_COLOR, "No color value has been specified");
	    		return;
    		}
    		
    		if (!BaseUtil.isValidColor(color)){
	    		addError(result, nodePath, "", ProblemType.COLOR_PALETTE_WRONG_COLOR_VALUE, "Invalid color value: '" + color + "' use format #123456");
	    		return;
    		}
    		
    		if (result.getColorPalette().getColors() == null){
    			result.getColorPalette().setColors(new EnumMap<>(Shade.class));
    		}
    		if (result.getColorPalette().getColors().containsKey(shade)){
	    		addError(result, nodePath, "", ProblemType.COLOR_PALETTE_SHADE_MULTIPLE_OCCURENCES, "Shade " + shade + " has already been defined");
	    		return;
    		}
    		result.getColorPalette().getColors().put(shade, color);
    	} catch (Exception e){
    		addError(result, nodePath, null, ProblemType.COLOR_PALETTE_COULD_NOT_READ_SHADE, "Could not read shade: " + e.getMessage());
    	}
	}

	public static boolean assertName(Node node, String expectedName, NodePath nodePath, ColorPaletteDAOResult result) {
        String name = node.getNodeName();
        if (name.equals(expectedName)) {
            return true;
        } else {
            addError(result, nodePath, "", ProblemType.WRONG_XML_NODE_NAME, "Expected name named '" + expectedName + "', but found name '" + name + "'");
            return false;
        }
    }

    public static void addError(ColorPaletteDAOResult result, NodePath nodePath, String attribute, ProblemType problemType, String message) {
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

    public static void addError(ColorPaletteDAOResult result, NodePath nodePath, String attribute, ProblemType problemType, Exception e) {
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

    public AssistValueListProvider getPossibleExtendedPaletteAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        for (String i : ProjectDefinitionDAO.DEFAULT_PALATTE_NAMES) {
            assistValues.add(BaseUtil.createAssistValue(null, i, null));
        }
        return new AssistValueList(assistValues);
    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, EXTENDED_PALETTE_ATTRIBUTE_NAME, "Color palette to extend"));
        return result;
    }
    
    public List<AssistValue> getShadeNodeTagAttributes() {
    	List<AssistValue> result = new ArrayList<AssistValue>();
    	result.add(BaseUtil.createAssistValue(true, SHADE_NAME_ATTRIBUTE_NAME, "Name of the shade (e.g. '200'"));
    	return result;
    }
    
    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(null, SHADE_NODE_NAME, "Shade"));
        return result;
    }

}
