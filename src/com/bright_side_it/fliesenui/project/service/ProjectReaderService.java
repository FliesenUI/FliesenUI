package com.bright_side_it.fliesenui.project.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.dto.dao.DTODefinitionDAO;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTODefinitionDAOResult;
import com.bright_side_it.fliesenui.imageasset.dao.ImageAssetDefinitionDAO;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.plugin.dao.PluginDefinitionDAO;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinitionDAOResult;
import com.bright_side_it.fliesenui.project.dao.DefinitionResourceDAO;
import com.bright_side_it.fliesenui.project.dao.ProjectDefinitionDAO;
import com.bright_side_it.fliesenui.project.model.DefinitionResource;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectDefinitionDAOResult;
import com.bright_side_it.fliesenui.project.model.DefinitionResource.ResourceFormat;
import com.bright_side_it.fliesenui.project.model.DefinitionResource.ResourceType;
import com.bright_side_it.fliesenui.screendefinition.dao.ScreenDefinitionDAO;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;

public class ProjectReaderService {

    /**
     *
     * @param baseDir
     * @param lastReadProject
     * @param updatedResources null means all, empty list means none
     * @return
     * @throws Exception
     */
    public Project readProject(File baseDir, Project lastReadProject, Set<DefinitionResource> upToDateResources) throws Exception {
        Project result = null;

        Set<DefinitionResource> useUpToDateResources = upToDateResources;
        if (useUpToDateResources == null) {
            useUpToDateResources = new TreeSet<>();
        }
        if (lastReadProject != null) {
            result = lastReadProject;
        } else {
            result = new Project();
            result.setScreenDefinitionsMap(new TreeMap<>());
            result.setScreenDefinitionProblemsMap(new TreeMap<>());
            result.setDTODefinitionsMap(new TreeMap<>());
            result.setDTODefinitionProblemsMap(new TreeMap<String, List<ResourceDefinitionProblem>>());
            result.setPluginDefinitionsMap(new TreeMap<>());
            result.setPluginDefinitionProblemsMap(new TreeMap<String, List<ResourceDefinitionProblem>>());
            result.setImageAssetDefinitionsMap(new TreeMap<>());
            result.setImageAssetDefinitionProblemsMap(new TreeMap<String, ResourceDefinitionProblem>());
            useUpToDateResources = new TreeSet<>();
        }

        readProjectDefinition(baseDir, useUpToDateResources, result);
        readPluginDefinitions(baseDir, useUpToDateResources, result);
        readImageAssetDefinitions(baseDir, useUpToDateResources, result);
        readDTODefinitions(baseDir, useUpToDateResources, result);
        readScreenDefinitions(baseDir, useUpToDateResources, result);

        return result;
    }

    public List<File> getAllDTOFiles(File dir) throws Exception {
        File itemDir = new File(dir, BaseConstants.DTO_DIR_NAME);
        if (!itemDir.exists()) {
            return new ArrayList<File>();
        }
        ArrayList<File> result = new ArrayList<File>(Arrays.asList(itemDir.listFiles()));
        Collections.sort(result);
        return result;
    }

    public List<File> getAllPluginFiles(File dir) throws Exception {
        File itemDir = new File(dir, BaseConstants.PLUGIN_DIR_NAME);
        if (!itemDir.exists()) {
            return new ArrayList<File>();
        }
        ArrayList<File> result = new ArrayList<File>(Arrays.asList(itemDir.listFiles()));
        Collections.sort(result);
        return result;
    }

    public List<File> getAllScreenFiles(File dir) throws Exception {
        ArrayList<File> result = new ArrayList<File>(Arrays.asList(new File(dir, BaseConstants.SCREEN_DIR_NAME).listFiles()));
        Collections.sort(result);
        return result;
    }

    public List<File> getAllImageAssetFiles(File dir) throws Exception {
    	ArrayList<File> foundFiles = new ArrayList<File>(Arrays.asList(new File(dir, BaseConstants.IMAGE_ASSET_DIR_NAME).listFiles()));
    	Collections.sort(foundFiles);
    	
    	ArrayList<File> result = new ArrayList<>();
    	for (File i: foundFiles){
    		if (!i.getName().equals(BaseConstants.IMAGE_ASSET_FILE_TO_IGNORE)){
    			result.add(i);
    		}
    	}
    	
    	return result;
    }
    
    private void readPluginDefinitions(File dir, Set<DefinitionResource> upToDateResources, Project result) throws Exception {
        DefinitionResourceDAO definitionResourceDAO = new DefinitionResourceDAO();
        Set<String> existingIDs = new TreeSet<String>();

        for (DefinitionResource i : definitionResourceDAO.getAllPlugins(dir)) {
            if (!upToDateResources.contains(i)) {
                File file = definitionResourceDAO.getFile(dir, i);
                PluginDefinitionDAOResult readResult = new PluginDefinitionDAO().readPluginDefinition(file);
                if ((readResult.getProblems() != null) && (!readResult.getProblems().isEmpty())) {
                    result.getPluginDefinitionProblemsMap().put(i.getId(), readResult.getProblems());
                }
                PluginDefinition pluginDefinition = readResult.getPluginDefinition();
                if (pluginDefinition != null) {
                    result.getPluginDefinitionsMap().put(pluginDefinition.getID(), pluginDefinition);
                }
            }
            existingIDs.add(i.getId());
        }
        Set<String> deletedIDs = new TreeSet<>(result.getPluginDefinitionsMap().keySet());
        deletedIDs.removeAll(existingIDs);

        for (String i : deletedIDs) {
            result.getPluginDefinitionProblemsMap().remove(i);
            result.getPluginDefinitionsMap().remove(i);
        }
    }

    private void readImageAssetDefinitions(File dir, Set<DefinitionResource> upToDateResources, Project result) throws Exception {
        DefinitionResourceDAO definitionResourceDAO = new DefinitionResourceDAO();
        Set<String> existingIDs = new TreeSet<String>();

        for (DefinitionResource i : definitionResourceDAO.getAllImageAssets(dir)) {
            if (!upToDateResources.contains(i)) {
                File file = definitionResourceDAO.getFile(dir, i);
                ImageAssetDefinition imageAssetDefinition = null;
                try {
                    imageAssetDefinition = new ImageAssetDefinitionDAO().readImageAssetDefinition(file);
                    if (imageAssetDefinition != null){
                    	result.getImageAssetDefinitionsMap().put(i.getId(), imageAssetDefinition);
                    }
                } catch (Exception e) {
                    ResourceDefinitionProblem problem = new ResourceDefinitionProblem();
                    if ((e != null) && (e.getMessage() != null)) {
                        problem.setMessage("Error: " + e.getMessage());
                    } else {
                        problem.setMessage("Error: " + e);
                    }
                    result.getImageAssetDefinitionProblemsMap().put(i.getId(), problem);
                }
            }
            existingIDs.add(i.getId());
        }

        Set<String> deletedIDs = new TreeSet<>(result.getImageAssetDefinitionsMap().keySet());
        deletedIDs.removeAll(existingIDs);

        for (String i : deletedIDs) {
            result.getImageAssetDefinitionProblemsMap().remove(i);
            result.getImageAssetDefinitionsMap().remove(i);
        }

    }

    private void readDTODefinitions(File dir, Set<DefinitionResource> upToDateResources, Project result) throws Exception {
        DefinitionResourceDAO definitionResourceDAO = new DefinitionResourceDAO();
        Set<String> existingIDs = new TreeSet<String>();

        for (DefinitionResource i : definitionResourceDAO.getAllDTOs(dir)) {
            if (!upToDateResources.contains(i)) {
                File file = definitionResourceDAO.getFile(dir, i);
                DTODefinitionDAOResult readResult = new DTODefinitionDAO().readDTODefinition(file);
                if ((readResult.getProblems() != null) && (!readResult.getProblems().isEmpty())) {
                    result.getDTODefinitionProblemsMap().put(i.getId(), readResult.getProblems());
                }
                DTODefinition dtoDefinition = readResult.getDTODefinition();
                if (dtoDefinition != null) {
                    result.getDTODefinitionsMap().put(dtoDefinition.getID(), dtoDefinition);
                }
            }
            existingIDs.add(i.getId());
        }
        Set<String> deletedIDs = new TreeSet<>(result.getDTODefinitionsMap().keySet());
        deletedIDs.removeAll(existingIDs);

        for (String i : deletedIDs) {
            result.getDTODefinitionProblemsMap().remove(i);
            result.getDTODefinitionsMap().remove(i);
        }







        //
        //
        //
        //        for (File i : getAllDTOFiles(dir)) {
        //            DTODefinitionDAOResult readResult = new DTODefinitionDAO().readDTODefinition(i);
        //            String id = FileUtil.getFilenameWithoutEnding(i.getName());
        //            if (readResult.getDTODefinition() != null) {
        //                id = readResult.getDTODefinition().getID();
        //            }
        //            if ((readResult.getProblems() != null) && (!readResult.getProblems().isEmpty())) {
        //                //                log("Problems:\n" + dtoDefinitionProblemsToString(readResult.getProblems()));
        //                result.getDTODefinitionProblemsMap().put(id, readResult.getProblems());
        //            }
        //            DTODefinition dtoDefinition = readResult.getDTODefinition();
        //            if (dtoDefinition != null) {
        //                result.getDTODefinitionsMap().put(dtoDefinition.getID(), dtoDefinition);
        //            }
        //        }
    }

    private void readScreenDefinitions(File dir, Set<DefinitionResource> upToDateResources, Project result) throws Exception {
        DefinitionResourceDAO definitionResourceDAO = new DefinitionResourceDAO();
        Set<String> existingIDs = new TreeSet<String>();
        for (DefinitionResource i : definitionResourceDAO.getAllScreens(dir)) {
            if (!upToDateResources.contains(i)) {
                File file = definitionResourceDAO.getFile(dir, i);
                ScreenDefinitionDAOResult readResult = new ScreenDefinitionDAO().readScreenDefiontion(file);
                if ((readResult.getProblems() != null) && (!readResult.getProblems().isEmpty())) {
                    result.getScreenDefinitionProblemsMap().put(i.getId(), readResult.getProblems());
                }
                ScreenDefinition screenDefinition = readResult.getScreenDefinition();
                if (screenDefinition != null) {
                    result.getScreenDefinitionsMap().put(screenDefinition.getID(), screenDefinition);
                }
            }
            existingIDs.add(i.getId());
        }
        Set<String> deletedIDs = new TreeSet<>(result.getScreenDefinitionsMap().keySet());
        deletedIDs.removeAll(existingIDs);

        for (String i : deletedIDs) {
            result.getScreenDefinitionProblemsMap().remove(i);
            result.getScreenDefinitionsMap().remove(i);
        }

        //        for (File i : getAllScreenFiles(dir)) {
        //            ScreenDefinitionDAOResult readResult = new ScreenDefinitionDAO().readScreenDefiontion(i);
        //            String id = FileUtil.getFilenameWithoutEnding(i.getName());
        //            if (readResult.getScreenDefinition() != null) {
        //                id = readResult.getScreenDefinition().getID();
        //            }
        //            if ((readResult.getProblems() != null) && (!readResult.getProblems().isEmpty())) {
        //                result.getScreenDefinitionProblemsMap().put(id, readResult.getProblems());
        //            }
        //            ScreenDefinition screenDefinition = readResult.getScreenDefinition();
        //            if (screenDefinition != null) {
        //                result.getScreenDefinitionsMap().put(screenDefinition.getID(), screenDefinition);
        //            }
        //        }
    }

    public File getProjectDefinitionFile(File dir) {
        return new File(dir, BaseConstants.PROJECT_FILE_NAME);
    }

    private void readProjectDefinition(File dir, Set<DefinitionResource> upToDateResources, Project project) throws Exception {
        DefinitionResource resource = new DefinitionResource();
        resource.setId("");
        resource.setResourceType(ResourceType.PROJECT);
        resource.setResourceFormat(ResourceFormat.XML);
        if (upToDateResources.contains(resource)) {
            log("project resource does NOT need to be updated");
            return;
        }
        log("project resource NEEDS to be updated");
        File projectDefinitionFile = getProjectDefinitionFile(dir);
        ProjectDefinitionDAOResult projectDefinitionResults = new ProjectDefinitionDAO().readProjectDefiontion(dir, projectDefinitionFile);
        project.setProjectDefinitionProblems(projectDefinitionResults.getProblems());
        project.setProjectDefinition(projectDefinitionResults.getProjectDefinition());
    }

    private void log(String message) {
        System.out.println("ProjectReaderService: " + message);
    }



}
