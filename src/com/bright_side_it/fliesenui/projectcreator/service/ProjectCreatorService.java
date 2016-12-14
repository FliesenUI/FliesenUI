package com.bright_side_it.fliesenui.projectcreator.service;

import java.io.File;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.project.dao.DefinitionResourceDAO;
import com.bright_side_it.fliesenui.project.service.ProjectReaderService;
import com.bright_side_it.fliesenui.res.dao.ResourceDAO;
import com.bright_side_it.fliesenui.res.dao.ResourceDAO.Resource;

public class ProjectCreatorService {
    private static final String OUTPUT_DIR_PLACEHOLDER = "§{outputDir}";
    private static final String SCREEN_ID = "myScreen";
    private static final String DTO1_ID = "myDTOItem";
    private static final String DTO2_ID = "myDTOList";

    public void createProject(File projectDir, String outputDirThatMayBeRelative) throws Exception {
        createDirectory(projectDir);
        File outputDir = FileUtil.getFileFromBaseFileAndPathThatMayBeRelative(projectDir, outputDirThatMayBeRelative);
        createDirectory(outputDir);

        createProjectFile(projectDir, outputDirThatMayBeRelative);
        createScreenFile(projectDir);
        createDTOFiles(projectDir);
        
        createImageAssetsDir(projectDir);
        cretePluginsDir(projectDir);
    }

    private void cretePluginsDir(File projectDir) throws Exception {
    	createDirectory(new File(projectDir, BaseConstants.PLUGIN_DIR_NAME));		
	}

	private void createImageAssetsDir(File projectDir) throws Exception {
    	createDirectory(new File(projectDir, BaseConstants.IMAGE_ASSET_DIR_NAME));
	}

	private void createScreenFile(File projectDir) throws Exception {
        File file = new DefinitionResourceDAO().getScreenFile(projectDir, SCREEN_ID);
        createDirectory(file.getParentFile());
        new ResourceDAO().copyResourceToFile(Resource.NEW_PROJECT_SCREEN_TEMPLATE, file);
    }

    private void createDTOFiles(File projectDir) throws Exception {
        File file = new DefinitionResourceDAO().getDTOFile(projectDir, DTO1_ID);
        createDirectory(file.getParentFile());

        new ResourceDAO().copyResourceToFile(Resource.NEW_PROJECT_DTO1_TEMPLATE, file);
        file = new DefinitionResourceDAO().getDTOFile(projectDir, DTO2_ID);
        new ResourceDAO().copyResourceToFile(Resource.NEW_PROJECT_DTO2_TEMPLATE, file);
    }

    private void createDirectory(File dir) throws Exception {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.exists()) {
            throw new Exception("Could not create directory '" + dir.getAbsolutePath() + "'");
        }
    }

    private void createProjectFile(File projectDir, String outputDirThatMayBeRelative) throws Exception {
        String result = new ResourceDAO().readTemplateAsString(Resource.NEW_PROJECT_PROJECT_TEMPLATE);
        result = result.replace(OUTPUT_DIR_PLACEHOLDER, outputDirThatMayBeRelative.replace("\\", "\\\\"));
        File file = new ProjectReaderService().getProjectDefinitionFile(projectDir);
        FileUtil.writeStringToFile(file, result);
    }
}
