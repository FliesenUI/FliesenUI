package com.bright_side_it.fliesenui.validation.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.colorpalette.dao.ColorPaletteDAO;
import com.bright_side_it.fliesenui.colorpalette.dao.ColorPaletteDAOResult;
import com.bright_side_it.fliesenui.colorpalette.model.ColorPalette;
import com.bright_side_it.fliesenui.dto.dao.DTODefinitionDAO;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTODefinitionDAOResult;
import com.bright_side_it.fliesenui.plugin.dao.PluginDefinitionDAO;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinitionDAOResult;
import com.bright_side_it.fliesenui.project.dao.ProjectDefinitionDAO;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.dao.ScreenDefinitionDAO;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.stringres.model.StringResource;
import com.bright_side_it.fliesenui.stringres.model.StringResourceItem;

public class ValidationUtil {
    private static final Set<Character> ALLOWED_CHARS_IN_IDS = createAllowedCharsInIDs();

    private static Set<Character> createAllowedCharsInIDs() {
        Set<Character> result = new HashSet<>();
        for (char i = 'a'; i <= 'z'; i++) {
            result.add(i);
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            result.add(i);
        }
        for (char i = '0'; i <= '9'; i++) {
            result.add(i);
        }
        return result;
    }
	
    public static void addError(Project project, ScreenDefinition screenDefinition, NodePath nodePath, String attribute,
            ProblemType type, String message) {
        if (project.getScreenDefinitionProblemsMap() == null) {
            project.setScreenDefinitionProblemsMap(new TreeMap<String, List<ResourceDefinitionProblem>>());
        }
        if (screenDefinition == null){
        	throw new RuntimeException("screenDefinition is null");
        }
        if (project.getScreenDefinitionProblemsMap() == null){
        	throw new RuntimeException("project.getScreenDefinitionProblemsMap() is null");
        }
        
        if (project.getScreenDefinitionProblemsMap().get(screenDefinition.getID()) == null) {
            project.getScreenDefinitionProblemsMap().put(screenDefinition.getID(), new ArrayList<ResourceDefinitionProblem>());
        }
        List<ResourceDefinitionProblem> problemList = project.getScreenDefinitionProblemsMap().get(screenDefinition.getID());
        problemList.add(createResourceDefinitionProblem(nodePath, attribute, type, message));
    }
    
    public static void addError(Project project, ColorPalette colorPalette, NodePath nodePath, String attribute,
    		ProblemType type, String message) {
    	if (project.getColorPaletteProblemsMap() == null) {
    		project.setScreenDefinitionProblemsMap(new TreeMap<String, List<ResourceDefinitionProblem>>());
    	}
    	if (project.getColorPaletteProblemsMap() == null){
    		throw new RuntimeException("project.getColorPaletteProblemsMap() is null");
    	}
    	
    	if (project.getColorPaletteProblemsMap().get(colorPalette.getID()) == null) {
    		project.getColorPaletteProblemsMap().put(colorPalette.getID(), new ArrayList<ResourceDefinitionProblem>());
    	}
    	List<ResourceDefinitionProblem> problemList = project.getColorPaletteProblemsMap().get(colorPalette.getID());
    	problemList.add(createResourceDefinitionProblem(nodePath, attribute, type, message));
    }
    
    public static void addError(Project project, StringResource stringResource, NodePath nodePath, String attribute,
    		ProblemType type, String message) {
    	if (project.getStringResourceProblemsMap() == null) {
    		project.setStringResourceProblemsMap(new TreeMap<String, List<ResourceDefinitionProblem>>());
    	}
    	if (project.getStringResourceProblemsMap().get(stringResource.getID()) == null) {
    		project.getStringResourceProblemsMap().put(stringResource.getID(), new ArrayList<ResourceDefinitionProblem>());
    	}
    	List<ResourceDefinitionProblem> problemList = project.getStringResourceProblemsMap().get(stringResource.getID());
    	problemList.add(createResourceDefinitionProblem(nodePath, attribute, type, message));
    }
    
    public static void addError(Project project, DTODefinition dtoDefinition, NodePath nodePath, String attribute,
    		ProblemType type, String message) {
    	if (project.getDTODefinitionProblemsMap() == null) {
    		project.setDTODefinitionProblemsMap(new TreeMap<String, List<ResourceDefinitionProblem>>());
    	}
    	
    	if (project.getDTODefinitionProblemsMap().get(dtoDefinition.getID()) == null) {
    		project.getDTODefinitionProblemsMap().put(dtoDefinition.getID(), new ArrayList<ResourceDefinitionProblem>());
    	}
    	List<ResourceDefinitionProblem> problemList = project.getDTODefinitionProblemsMap().get(dtoDefinition.getID());
    	problemList.add(createResourceDefinitionProblem(nodePath, attribute, type, message));
    }
    
    public static void addProjectDefinitionError(Project project, NodePath nodePath, String attribute,
    		ProblemType type, String message) {
    	if (project.getProjectDefinitionProblems() == null) {
    		project.setProjectDefinitionProblems(new ArrayList<ResourceDefinitionProblem>());
    	}
    	List<ResourceDefinitionProblem> problemList = project.getProjectDefinitionProblems();
    	problemList.add(createResourceDefinitionProblem(nodePath, attribute, type, message));
    }

	private static ResourceDefinitionProblem createResourceDefinitionProblem(NodePath nodePath, String attribute, ProblemType type, String message) {
		ResourceDefinitionProblem problem = new ResourceDefinitionProblem();
    	problem.setAttribute(attribute);
    	problem.setNodePath(nodePath);
    	problem.setMessage(message);
    	problem.setType(type);
    	return problem;
	}
    
    public static void validateAllowedAttributes(Node node, NodePath nodePath, Set<String> allowedAttributeNames, ScreenDefinitionDAOResult result){
    	NamedNodeMap attributes = node.getAttributes();
    	for (int i = 0; i < attributes.getLength(); i ++){
    		String foundAttribute = attributes.item(i).getNodeName();
    		if (!allowedAttributeNames.contains(foundAttribute)){
    			ScreenDefinitionDAO.addError(result, nodePath, foundAttribute, ProblemType.UNEXPECTED_XML_ATTRIBUTE
    					, "The attribute '" + foundAttribute + "' was not expected in this tag ('" + node.getNodeName() + "'). " + createAllowedAttributesInfo(allowedAttributeNames));
    		}
    	}
    }
    
    public static void validateAllowedAttributes(Node node, NodePath nodePath, Set<String> allowedAttributeNames, ColorPaletteDAOResult result){
    	NamedNodeMap attributes = node.getAttributes();
    	for (int i = 0; i < attributes.getLength(); i ++){
    		String foundAttribute = attributes.item(i).getNodeName();
    		if (!allowedAttributeNames.contains(foundAttribute)){
    			ColorPaletteDAO.addError(result, nodePath, foundAttribute, ProblemType.UNEXPECTED_XML_ATTRIBUTE
    					, "The attribute '" + foundAttribute + "' was not expected in this tag ('" + node.getNodeName() + "'). " + createAllowedAttributesInfo(allowedAttributeNames));
    		}
    	}
    }
    
    public static void validateAllowedAttributes(Node node, NodePath nodePath, Set<String> allowedAttributeNames, ProjectDefinitionDAOResult result){
    	NamedNodeMap attributes = node.getAttributes();
    	for (int i = 0; i < attributes.getLength(); i ++){
    		String foundAttribute = attributes.item(i).getNodeName();
    		if (!allowedAttributeNames.contains(foundAttribute)){
    			ProjectDefinitionDAO.addError(result, nodePath, foundAttribute, ProblemType.UNEXPECTED_XML_ATTRIBUTE
    					, "The attribute '" + foundAttribute + "' was not expected in this tag ('" + node.getNodeName() + "'). " + createAllowedAttributesInfo(allowedAttributeNames));
    		}
    	}
    }
    
    public static void validateAllowedAttributes(Node node, NodePath nodePath, Set<String> allowedAttributeNames, PluginDefinitionDAOResult result){
    	NamedNodeMap attributes = node.getAttributes();
    	for (int i = 0; i < attributes.getLength(); i ++){
    		String foundAttribute = attributes.item(i).getNodeName();
    		if (!allowedAttributeNames.contains(foundAttribute)){
    			PluginDefinitionDAO.addError(result, nodePath, foundAttribute, ProblemType.UNEXPECTED_XML_ATTRIBUTE
    					, "The attribute '" + foundAttribute + "' was not expected in this tag ('" + node.getNodeName() + "'). " + createAllowedAttributesInfo(allowedAttributeNames));
    		}
    	}
    }
    
    
	public static void validateAllowedAttributes(Node node, NodePath nodePath, Set<String> allowedAttributeNames, DTODefinitionDAOResult result){
    	NamedNodeMap attributes = node.getAttributes();
    	for (int i = 0; i < attributes.getLength(); i ++){
    		String foundAttribute = attributes.item(i).getNodeName();
    		if (!allowedAttributeNames.contains(foundAttribute)){
    			DTODefinitionDAO.addError(result, nodePath, foundAttribute, ProblemType.UNEXPECTED_XML_ATTRIBUTE
    					, "The attribute '" + foundAttribute + "' was not expected in this tag ('" + node.getNodeName() + "'). " + createAllowedAttributesInfo(allowedAttributeNames));
    		}
    	}
    }
	
    private static String createAllowedAttributesInfo(Set<String> allowedAttributeNames) {
    	if (allowedAttributeNames.isEmpty()){
    		return "The tag may not contain any attributes";
    	} else{
    		return "Possible attributes: " + allowedAttributeNames;
    	}
	}
    
	public static boolean doesDTOFieldExist(Project project, ScreenDefinition screenDefinition, String dtoString) {
		return BaseUtil.getDTOFieldBasicType(project, screenDefinition, dtoString) != null;
	}

	public static boolean isTextOrTextResourceValid(Project project, String text){
		if (text == null){
			return true;
		}
		if (text.startsWith(BaseConstants.STRING_RESOURCE_PREFIX)){
			StringResource map = project.getStringResourceMap().get(BaseConstants.DEFAULT_LANGUAGE_ID);
			if (map == null){
				return false;
			}
			Map<String, StringResourceItem> strings = map.getStrings();
			if (strings == null){
				return false;
			}
			return strings.containsKey(text.substring(BaseConstants.STRING_RESOURCE_PREFIX.length()));
		}
		return true;
	}
	
	public static boolean isValidJavaVariableName(String string){
		if (string == null){
			return false;
		}
		if (string.isEmpty()){
			return false;
		}
        char firstChar = string.charAt(0);
        if ((firstChar < 'a') || (firstChar > 'z')) {
        	return false;
        }

        for (char i : string.toCharArray()) {
            if (!ALLOWED_CHARS_IN_IDS.contains(i)) {
                return false;
            }
        }

        return true;
	}
	
}
