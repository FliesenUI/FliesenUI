package com.bright_side_it.fliesenui.project.dao;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.project.model.DefinitionResource;
import com.bright_side_it.fliesenui.project.model.DefinitionResource.ResourceFormat;
import com.bright_side_it.fliesenui.project.model.DefinitionResource.ResourceType;
import com.bright_side_it.fliesenui.project.service.ProjectReaderService;

public class DefinitionResourceDAO {
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");

    public File createScreenHistoryFile(File baseDir, String id) {
        File dir = new File(getHistoryDir(baseDir), BaseConstants.SCREEN_DIR_NAME);
        File file = new File(dir, id + "_" + createTimestamp() + BaseConstants.SCREEN_DEFINITION_FILE_ENDING);
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

    public List<DefinitionResource> readDefinitionResources(File baseDir) throws Exception {
        List<DefinitionResource> result = new ArrayList<DefinitionResource>();
        ProjectReaderService service = new ProjectReaderService();

        DefinitionResource item = new DefinitionResource();
        item.setId("");
        item.setResourceType(ResourceType.PROJECT);
        item.setResourceFormat(ResourceFormat.XML);
        result.add(item);

        for (File i : service.getAllPluginFiles(baseDir)) {
            item = new DefinitionResource();
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
            item.setResourceType(ResourceType.PLUGIN);
            item.setResourceFormat(ResourceFormat.XML);
            result.add(item);
        }
        for (File i : service.getAllDTOFiles(baseDir)) {
            item = new DefinitionResource();
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
            item.setResourceType(ResourceType.DTO);
            item.setResourceFormat(ResourceFormat.XML);
            result.add(item);
        }
        for (File i : service.getAllScreenFiles(baseDir)) {
            item = new DefinitionResource();
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
            item.setResourceType(ResourceType.SCREEN);
            item.setResourceFormat(ResourceFormat.XML);
            result.add(item);
        }
    	for (File i : service.getAllImageAssetFiles(baseDir)) {
    		item = new DefinitionResource();
    		item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
    		item.setResourceType(ResourceType.IMAGE_ASSET);
    		item.setResourceFormat(ResourceFormat.IMAGE);
    		result.add(item);
    	}

        return result;
    }

    public File getFile(File projectDir, DefinitionResource definitionResource) throws Exception {
        switch (definitionResource.getResourceType()) {
        case PROJECT:
            return getProjectDefinitionFile(projectDir);
        case SCREEN:
            return getScreenFile(projectDir, definitionResource.getId());
        case DTO:
            return getDTOFile(projectDir, definitionResource.getId());
        case PLUGIN:
            return getPluginFile(projectDir, definitionResource.getId());
        case IMAGE_ASSET:
            return getImageAssetFile(projectDir, definitionResource.getId());
        default:
            throw new Exception("Unexpected resource type: " + definitionResource.getResourceType());
        }
    }

    public List<DefinitionResource> getAllScreens(File dir) {
        List<DefinitionResource> result = new ArrayList<>();
        for (File i : new File(dir, BaseConstants.SCREEN_DIR_NAME).listFiles()) {
            DefinitionResource item = new DefinitionResource();
            item.setResourceType(ResourceType.SCREEN);
            item.setResourceFormat(ResourceFormat.XML);
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
            result.add(item);
        }
        Collections.sort(result);
        return result;
    }

    public List<DefinitionResource> getAllDTOs(File dir) {
        List<DefinitionResource> result = new ArrayList<>();
        for (File i : new File(dir, BaseConstants.DTO_DIR_NAME).listFiles()) {
            DefinitionResource item = new DefinitionResource();
            item.setResourceType(ResourceType.DTO);
            item.setResourceFormat(ResourceFormat.XML);
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
            result.add(item);
        }
        Collections.sort(result);
        return result;
    }

    public List<DefinitionResource> getAllPlugins(File dir) {
        List<DefinitionResource> result = new ArrayList<>();
        File definitionDir = new File(dir, BaseConstants.PLUGIN_DIR_NAME);
        if (!definitionDir.exists()) {
            return result;
        }
        for (File i : definitionDir.listFiles()) {
            DefinitionResource item = new DefinitionResource();
            item.setResourceType(ResourceType.PLUGIN);
            item.setResourceFormat(ResourceFormat.XML);
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
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


    public List<DefinitionResource> getAllImageAssets(File dir) {
        List<DefinitionResource> result = new ArrayList<>();
        File definitionDir = new File(dir, BaseConstants.IMAGE_ASSET_DIR_NAME);
        if (!definitionDir.exists()) {
            return result;
        }
        for (File i : definitionDir.listFiles()) {
            DefinitionResource item = new DefinitionResource();
            item.setResourceType(ResourceType.IMAGE_ASSET);
            item.setResourceFormat(ResourceFormat.IMAGE);
            item.setId(FileUtil.getFilenameWithoutEnding(i.getName()));
            result.add(item);
        }
        Collections.sort(result);
        return result;
    }

}
