package com.bright_side_it.fliesenui.project.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.model.AssistValueList;
import com.bright_side_it.fliesenui.base.model.AssistValueListProvider;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.LanguageFlavor;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.project.model.ProjectDefinitionDAOResult;
import com.bright_side_it.fliesenui.project.model.ProjectOutput;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class ProjectOutputDAO {
    private static final String NODE_NAME = "output";
    public static final String SOURCE_DIR_PATH_ATTRIBUTE_NAME = "sourceDirPath";
    public static final String ANDROID_PROJECT_PATH_ATTRIBUTE_NAME = "androidProjectPath";
    
    public static final String FLAVOR_ATTRIBUTE_NAME = "flavor";
    public static final String FLAVOR_VALUE_JAVA = "Java";
    public static final String FLAVOR_VALUE_ANDROID = "Android";
    
    
    public boolean isProjectOutputNode(Node node) {
        return node.getNodeName().equals(NODE_NAME);
    }

    public void readProjectOutputNode(Node node, NodePath nodePath, ProjectDefinitionDAOResult result, File projectDir) throws Exception {
        ProjectOutput projectOutput = new ProjectOutput();
        projectOutput.setNodePath(nodePath);
        projectOutput.setLanguageFlavor(parseFlavor(XMLUtil.getStringAttributeRequired(node, FLAVOR_ATTRIBUTE_NAME)));
        
        projectOutput.setSourceDirPath(XMLUtil.getStringAttributeOptional(node, SOURCE_DIR_PATH_ATTRIBUTE_NAME, null));
        projectOutput.setAndroidProjectPath(XMLUtil.getStringAttributeOptional(node, ANDROID_PROJECT_PATH_ATTRIBUTE_NAME, null));

        if (projectOutput.getSourceDirPath() != null){
        	projectOutput.setSourceDirFileObject(FileUtil.getFileFromBaseFileAndPathThatMayBeRelative(projectDir, projectOutput.getSourceDirPath()));
        }
        if (projectOutput.getAndroidProjectPath() != null){
        	projectOutput.setAndroidProjectFileObject(FileUtil.getFileFromBaseFileAndPathThatMayBeRelative(projectDir, projectOutput.getAndroidProjectPath()));
        }
        
        
        ValidationUtil.validateAllowedAttributes(node, nodePath, BaseUtil.getTextSet(getTagAttributes()), result);
        if (result.getProjectDefinition().getOutputs() == null){
        	result.getProjectDefinition().setOutputs(new ArrayList<>());
        }
        result.getProjectDefinition().getOutputs().add(projectOutput);
    }

    private LanguageFlavor parseFlavor(String string) throws Exception{
    	if (FLAVOR_VALUE_JAVA.equals(string)){
    		return LanguageFlavor.JAVA;
    	} else if (FLAVOR_VALUE_ANDROID.equals(string)){
    		return LanguageFlavor.ANDROID;
    	} else {
    		throw new Exception("Unkonwn flavor value: '" + string + "'");
    	}
	}

    public static String getNodeName() {
        return NODE_NAME;
    }

    public List<AssistValue> getPossibleChildTags() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        return result;
    }
    
    public AssistValueListProvider createPossibleFlavorAttributeValues() {
        List<AssistValue> assistValues = new ArrayList<AssistValue>();
        assistValues.add(BaseUtil.createAssistValue(null, FLAVOR_VALUE_JAVA, "Java project (desktop application or web server)"));
        assistValues.add(BaseUtil.createAssistValue(null, FLAVOR_VALUE_ANDROID, "Android project"));
        return new AssistValueList(assistValues);
    }


    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<AssistValue>();
        result.add(BaseUtil.createAssistValue(true, FLAVOR_ATTRIBUTE_NAME, "flavor of the created classes and resources"));
        result.add(BaseUtil.createAssistValue(null, SOURCE_DIR_PATH_ATTRIBUTE_NAME, "Required if the flavor is 'Java' path where the generated source files and resources are written to"));
        result.add(BaseUtil.createAssistValue(null, ANDROID_PROJECT_PATH_ATTRIBUTE_NAME, "Required if the flavor is 'Android' path where the Android project is located."));
        return result;
    }


}
