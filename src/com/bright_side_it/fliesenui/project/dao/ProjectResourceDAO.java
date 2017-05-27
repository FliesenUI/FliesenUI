package com.bright_side_it.fliesenui.project.dao;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.project.model.ProjectResource;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceFormat;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceType;
import com.bright_side_it.fliesenui.project.model.ProjectDefinition;
import com.bright_side_it.fliesenui.project.model.ProjectDefinitionDAOResult;
import com.bright_side_it.fliesenui.project.service.ProjectReaderService;

public class ProjectResourceDAO {
	public static final String STRING_RESOURCE_PARENT_FILE_NAME_PREFIX = "values";
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");

    public File createScreenHistoryFile(File baseDir, String id) {
        File dir = new File(getHistoryDir(baseDir), BaseConstants.SCREEN_DIR_NAME);
        File file = new File(dir, id + "_" + createTimestamp() + BaseConstants.SCREEN_DEFINITION_FILE_ENDING);
        return file;
    }

    public File createStringResourceHistoryFile(File baseDir, String id) {
    	File dir = new File(getHistoryDir(baseDir), BaseConstants.STRING_RESOURCE_HISTORY_DIR_NAME);
    	File file = new File(dir, id + "_" + createTimestamp() + BaseConstants.STRING_RESOURCE_HISTORY_FILE_ENDING);
    	return file;
    }
    
    public File createDTOHistoryFile(File baseDir, String id) {
        File dir = new File(getHistoryDir(baseDir), BaseConstants.DTO_DIR_NAME);
        File file = new File(dir, id + "_" + createTimestamp() + BaseConstants.DTO_DEFINITION_FILE_ENDING);
        return file;
    }

    public File createPluginHistoryFile(File baseDir, String id) {
        File dir = new File(getHistoryDir(baseDir), BaseConstants.PLUGIN_DIR_NAME);
        File file = new File(dir, id + "_" + createTimestamp() + BaseConstants.PLUGIN_DEFINITION_FILE_ENDING);
        return file;
    }

    private String createTimestamp() {
        return TIMESTAMP_FORMAT.format(new Date());
    }

    private File getHistoryDir(File baseDir) {
        return new File(baseDir, BaseConstants.HISTORY_DIR_NAME);
    }


    public File getScreenFile(File baseDir, String id) {
        File dir = new File(baseDir, BaseConstants.SCREEN_DIR_NAME);
        File file = new File(dir, id + BaseConstants.SCREEN_DEFINITION_FILE_ENDING);
        return file;
    }

    public File createImageAssetFileWithEnding(File baseDir, String id, String fileEnding) {
    	File dir = new File(baseDir, BaseConstants.IMAGE_ASSET_DIR_NAME);
    	File file = new File(dir, id + fileEnding);
    	return file;
    }
    
    public File getDTOFile(File baseDir, String id) {
        File dir = new File(baseDir, BaseConstants.DTO_DIR_NAME);
        File file = new File(dir, id + BaseConstants.DTO_DEFINITION_FILE_ENDING);
        return file;
    }

    public File getPluginFile(File baseDir, String id) {
        File dir = new File(baseDir, BaseConstants.PLUGIN_DIR_NAME);
        File file = new File(dir, id + BaseConstants.PLUGIN_DEFINITION_FILE_ENDING);
        return file;
    }

    public File getImageAssetFile(File baseDir, ImageAssetDefinition image) {
        File dir = new File(baseDir, BaseConstants.IMAGE_ASSET_DIR_NAME);
        File file = new File(dir, image.getFilename());
        return file;
    }

    public File getProjectDefinitionFile(File baseDir) {
        File file = new File(baseDir, BaseConstants.PROJECT_FILE_NAME);
        return file;
    }

    public File getProjectDefinitionHistoryFile(File baseDir) {
        File dir = new File(getHistoryDir(baseDir), "project");
        File file = new File(dir, createTimestamp() + "_" + BaseConstants.PROJECT_FILE_NAME);
        return file;
    }

    public List<ProjectResource> readProjectResources(File baseDir) throws Exception {
        List<ProjectResource> result = new ArrayList<ProjectResource>();
        ProjectReaderService service = new ProjectReaderService();
        File projectDefinitionFile = service.getProjectDefinitionFile(baseDir);
        ProjectDefinitionDAOResult projectDefinitionResults = new ProjectDefinitionDAO().readProjectDefiontion(baseDir, projectDefinitionFile);
        ProjectDefinition projectDefinition = projectDefinitionResults.getProjectDefinition();

        ProjectResource item = new ProjectResource();
        item.setId("");
        item.setResourceType(ResourceType.PROJECT);
        item.setResourceFormat(ResourceFormat.XML);
        item.setFilePath(projectDefinitionFile.getAbsolutePath());
        result.add(item);

//        for (File i : service.getAllPluginFiles(baseDir)) {
//            item = new ProjectResource();
//            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
//            item.setResourceType(ResourceType.PLUGIN);
//            item.setResourceFormat(ResourceFormat.XML);
//            result.add(item);
//        }
//        for (File i : service.getAllDTOFiles(baseDir)) {
//            item = new ProjectResource();
//            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
//            item.setResourceType(ResourceType.DTO);
//            item.setResourceFormat(ResourceFormat.XML);
//            result.add(item);
//        }
//        for (File i : service.getAllScreenFiles(baseDir)) {
//            item = new ProjectResource();
//            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
//            item.setResourceType(ResourceType.SCREEN);
//            item.setResourceFormat(ResourceFormat.XML);
//            result.add(item);
//        }
//    	for (File i : service.getAllImageAssetFiles(baseDir)) {
//    		item = new ProjectResource();
//    		item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
//    		item.setResourceType(ResourceType.IMAGE_ASSET);
//    		item.setResourceFormat(ResourceFormat.IMAGE);
//    		result.add(item);
//    	}
        result.addAll(getAllPlugins(baseDir));
        result.addAll(getAllDTOs(baseDir));
        result.addAll(getAllScreens(baseDir));
    	result.addAll(getAllImageAssets(baseDir));
    	result.addAll(getAllStringResources(baseDir, projectDefinition));
        return result;
    }

    public File getFile(File projectDir, ProjectDefinition projectDefinition, ResourceType resourceType, String resourceID) throws Exception {
        switch (resourceType) {
        case PROJECT:
            return getProjectDefinitionFile(projectDir);
        case SCREEN:
            return getScreenFile(projectDir, resourceID);
        case DTO:
            return getDTOFile(projectDir, resourceID);
        case PLUGIN:
            return getPluginFile(projectDir, resourceID);
        case IMAGE_ASSET:
            return getImageAssetFile(projectDir, resourceID);
        case STRING_RESOURCE:
        	return getStringResourceFile(projectDir, projectDefinition, resourceID);
        default:
            throw new Exception("Unexpected resource type: " + resourceType);
        }
    }
    
    public File getFile(File projectDir, ProjectDefinition projectDefinition, ProjectResource definitionResource) throws Exception {
    	return getFile(projectDir, projectDefinition, definitionResource.getResourceType(), definitionResource.getId());
    }

    public List<ProjectResource> getAllScreens(File dir) {
        List<ProjectResource> result = new ArrayList<>();
        for (File i : new File(dir, BaseConstants.SCREEN_DIR_NAME).listFiles()) {
            ProjectResource item = new ProjectResource();
            item.setResourceType(ResourceType.SCREEN);
            item.setResourceFormat(ResourceFormat.XML);
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
            item.setFilePath(i.getAbsolutePath());
            result.add(item);
        }
        Collections.sort(result);
        return result;
    }

    public List<ProjectResource> getAllDTOs(File dir) {
        List<ProjectResource> result = new ArrayList<>();
        File dtoDir = new File(dir, BaseConstants.DTO_DIR_NAME);
        if (!dtoDir.exists()){
        	return result;
        }
        for (File i : dtoDir.listFiles()) {
            ProjectResource item = new ProjectResource();
            item.setResourceType(ResourceType.DTO);
            item.setResourceFormat(ResourceFormat.XML);
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
            item.setFilePath(i.getAbsolutePath());
            result.add(item);
        }
        Collections.sort(result);
        return result;
    }

    public List<ProjectResource> getAllPlugins(File dir) {
        List<ProjectResource> result = new ArrayList<>();
        File definitionDir = new File(dir, BaseConstants.PLUGIN_DIR_NAME);
        if (!definitionDir.exists()) {
            return result;
        }
        for (File i : definitionDir.listFiles()) {
            ProjectResource item = new ProjectResource();
            item.setResourceType(ResourceType.PLUGIN);
            item.setResourceFormat(ResourceFormat.XML);
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
            item.setFilePath(i.getAbsolutePath());
            result.add(item);
        }
        Collections.sort(result);
        return result;
    }

    public File getImageAssetFile(File projectDir, String id) {
        File dir = new File(projectDir, BaseConstants.IMAGE_ASSET_DIR_NAME);

        for (File i : dir.listFiles()) {
            if (FileUtil.getFilenameWithoutEnding(i.getName()).equals(id)) {
                return i;
            }
        }
        return null;
    }
    
    public File getStringResourceDir(File baseDir, ProjectDefinition projectDefinition) throws IOException {
    	String stringResourceDir = BaseConstants.DEFAULT_STRING_RESOURCE_DIR;
    	if (projectDefinition.getStringResourceDir() != null){
    		stringResourceDir = projectDefinition.getStringResourceDir();
    	}
    	File result = FileUtil.getFileFromBaseFileAndPathThatMayBeRelative(baseDir, stringResourceDir);
    	return result;
    }
    
    public File getStringResourceFile(File baseDir, ProjectDefinition projectDefinition, String id) throws IOException {
    	log("getStringResourceFile. ID = >>" + id + "<<");
    	File dir = getStringResourceDir(baseDir, projectDefinition);
    	String useID = id;
    	if (useID.equals(BaseConstants.DEFAULT_LANGUAGE_ID)){
    		useID = "";
    	} else {
    		useID = "-" + id; 
    	}
    	File parentDir = new File(dir, STRING_RESOURCE_PARENT_FILE_NAME_PREFIX + useID);
    	File file = new File(parentDir, BaseConstants.STRING_RESOURCE_FILE_NAME);
    	log("getStringResourceFile. result = >>" + file.getAbsolutePath() + "'");
    	return file;
    }

    private void log(String message) {
    	System.out.println("ProjectResourceDAO> " + message);
	}

	public List<ProjectResource> getAllStringResources(File baseDir, ProjectDefinition projectDefinition) throws IOException {
    	File stringResourceDir = getStringResourceDir(baseDir, projectDefinition);
    	
        List<ProjectResource> result = new ArrayList<>();
        if (!stringResourceDir.exists()) {
            return result;
        }
        for (File parentFile : stringResourceDir.listFiles()) {
        	if ((parentFile.isDirectory()) && (parentFile.getName().startsWith(STRING_RESOURCE_PARENT_FILE_NAME_PREFIX))){
        		File stringResourceFile = new File(parentFile, BaseConstants.STRING_RESOURCE_FILE_NAME);
        		if (stringResourceFile.exists()){
                    ProjectResource item = new ProjectResource();
                    item.setResourceType(ResourceType.STRING_RESOURCE);
                    item.setResourceFormat(ResourceFormat.XML);
                    String id = parentFile.getName().substring(STRING_RESOURCE_PARENT_FILE_NAME_PREFIX.length());
                    if (id.startsWith("-")){
                    	id = id.substring(1);
                    }
                    if (id.isEmpty()){
                    	id = BaseConstants.DEFAULT_LANGUAGE_ID;
                    }
                    id = id.replace("-", "_").toUpperCase();
                    item.setId(id);
                    item.setFilePath(stringResourceFile.getAbsolutePath());
                    result.add(item);
        		}
        	}
        }
        Collections.sort(result);
        return result;
    }
    
    public List<ProjectResource> getAllImageAssets(File dir) {
    	List<ProjectResource> result = new ArrayList<>();
    	File definitionDir = new File(dir, BaseConstants.IMAGE_ASSET_DIR_NAME);
    	if (!definitionDir.exists()) {
    		return result;
    	}
    	for (File i : definitionDir.listFiles()) {
    		ProjectResource item = new ProjectResource();
    		item.setResourceType(ResourceType.IMAGE_ASSET);
    		item.setResourceFormat(ResourceFormat.IMAGE);
    		item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
    		item.setFilePath(i.getAbsolutePath());
    		result.add(item);
    	}
    	Collections.sort(result);
    	return result;
    }

}
