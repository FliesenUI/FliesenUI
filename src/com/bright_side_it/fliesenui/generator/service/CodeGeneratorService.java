package com.bright_side_it.fliesenui.generator.service;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.LanguageFlavor;
import com.bright_side_it.fliesenui.generator.logic.StringJSCreatorLogic;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.imageasset.dao.ImageAssetDefinitionDAO;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.project.model.ProjectResource;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectOutput;
import com.bright_side_it.fliesenui.project.service.ProjectReaderService;
import com.bright_side_it.fliesenui.res.dao.ResourceDAO;
import com.bright_side_it.fliesenui.res.dao.ResourceDAO.Resource;
import com.bright_side_it.fliesenui.validation.service.ProjectValidationService;

public class CodeGeneratorService {
    private static final boolean DEV_IN_PROGRESS = false;
	private static final String FLUI_CSS_ACCENT_COLOR_PLACEHOLDER = "§{accentColor}";

    public Project generateCode(File projectDir) throws Exception {
        log("Project dir: '" + projectDir.getAbsolutePath() + "'");
        Project project = new ProjectReaderService().readProject(projectDir, null, null);
        validateProject(project);
        for (ProjectOutput projectOutput: project.getProjectDefinition().getOutputs()){
        	generateCodeFromValidatedProject(project, projectDir, null, projectOutput);
        }
        return project;
    }

    /**
     *
     * @param project
     * @param projectDir
     * @param upToDateResources may be null
     * @param projectOutput 
     * @throws Exception
     */
    public void generateCodeFromValidatedProject(Project project, File projectDir, Set<ProjectResource> upToDateResources, ProjectOutput projectOutput) throws Exception {
        Set<ProjectResource> useUpToDateResources = upToDateResources;
        if (useUpToDateResources == null) {
            useUpToDateResources = new TreeSet<>();
        }
        
        File sourcesOutputDir = null;
        File androidAssetsOutputDir = null;
        if (projectOutput.getLanguageFlavor() == LanguageFlavor.JAVA){
        	sourcesOutputDir = projectOutput.getSourceDirFileObject();
        } else if (projectOutput.getLanguageFlavor() == LanguageFlavor.ANDROID){
        	sourcesOutputDir =  new File(new File(new File(new File(projectOutput.getAndroidProjectFileObject(), "app"), "src"), "main"), "java");
        	androidAssetsOutputDir = new File(new File(new File(new File(projectOutput.getAndroidProjectFileObject(), "app"), "src"), "main"), "assets");
        } else {
        	throw new Exception("Unknown language flavor: " + projectOutput.getLanguageFlavor());
        }
        
        updateJavaSources(project, projectDir, useUpToDateResources, sourcesOutputDir, projectOutput.getLanguageFlavor());
        updateResources(project, projectDir, useUpToDateResources, sourcesOutputDir, projectOutput.getLanguageFlavor());
        
        if (androidAssetsOutputDir != null){
        	updateResources(project, projectDir, useUpToDateResources, androidAssetsOutputDir, projectOutput.getLanguageFlavor());
        }
    }


	private void updateResources(Project project, File projectDir, Set<ProjectResource> upToDateResources, File sourcesOutputDir, LanguageFlavor languageFlavor) throws Exception {
        File webDir = GeneratorUtil.getWebOutputDir(sourcesOutputDir);
        webDir.mkdirs();
        File webLibDir = GeneratorUtil.getWebLibOutputDir(sourcesOutputDir);
        webLibDir.mkdirs();
        File imageAssetOutpuDir = GeneratorUtil.getImageAssetOutputDir(sourcesOutputDir);
        if (upToDateResources.isEmpty()) {
            if (imageAssetOutpuDir.exists()) {
                FileUtil.removeFilesAndDirsInDir(imageAssetOutpuDir);
            }
            clearGeneratedWebFiles(webDir);
        }
        new MultiPageAppHTMLGeneratorService().generateHTML(project, upToDateResources, webDir);
        new SinglePageAppHTMLGeneratorService().generateHTML(project, upToDateResources, webDir);
        if (upToDateResources.isEmpty()) {
            copyWebResources(sourcesOutputDir, webDir);
        }
        copyFLUICSS(project, sourcesOutputDir, webDir);
        
        new StringJSCreatorLogic().generateStringJS(project, webLibDir);
        new JSGeneratorService().generateJS(project, upToDateResources, webDir);
        imageAssetOutpuDir.mkdirs();
        log("Image asset output dir: '" + imageAssetOutpuDir.getAbsolutePath() + "'");
        if (!imageAssetOutpuDir.exists()) {
            throw new Exception("Could not create image asset output dir '" + imageAssetOutpuDir.getAbsolutePath() + "'");
        }
        copyImageAssets(imageAssetOutpuDir, project, projectDir);

	}

	private void updateJavaSources(Project project, File projectDir, Set<ProjectResource> upToDateResources, File sourcesOutputDir, LanguageFlavor languageFlavor) throws Exception {
		log("updateJavaSources: languageFlavor = " + languageFlavor);
		
        File webDir = GeneratorUtil.getWebOutputDir(sourcesOutputDir);
        webDir.mkdirs();
        if (upToDateResources.isEmpty()) {
            clearGeneratedFilesInJavaDir(sourcesOutputDir);
        }
        
        new JavaGeneratorService().generateJava(project, upToDateResources, sourcesOutputDir, languageFlavor);


        if (upToDateResources.isEmpty()) {
            copyJavaResources(sourcesOutputDir, languageFlavor);
        }
	}

	private void copyImageAssets(File imageAssetOutpuDir, Project project, File projectDir) throws Exception {
        for (ImageAssetDefinition i : project.getImageAssetDefinitionsMap().values()) {
            new ImageAssetDefinitionDAO().copyToDir(projectDir, i, imageAssetOutpuDir);
        }
    }

    private void validateProject(Project project) throws Exception {
        ProjectValidationService validation = new ProjectValidationService();
        validation.validateProject(project, null);
        if (validation.containsProblems(project)) {
            String problemsString = validation.problemsToString(project);
            log("Problems:\n" + problemsString);
            throw new Exception("Project contains errors:\n" + problemsString);
        }
    }


    private void clearGeneratedFilesInJavaDir(File outputDir) throws Exception {
        File packageDir = GeneratorUtil.getCorePackageDir(outputDir);
        FileUtil.removeFilesAndDirsInDir(packageDir);

        packageDir = GeneratorUtil.getScreenPackageDir(outputDir);
        FileUtil.removeFilesAndDirsInDir(packageDir);
    }

    private void clearGeneratedWebFiles(File webDir) throws Exception {
        FileUtil.removeFilesAndDirsInDir(webDir);
    }

    private void copyFLUICSS(Project project, File baseDir, File webDir) throws Exception {
        ResourceDAO resourceDA = new ResourceDAO();
        File libDir = GeneratorUtil.getWebLibOutputDir(baseDir);
        if (!libDir.exists()) {
            libDir.mkdir();
        }
        if (!libDir.exists()) {
            throw new Exception("Could not create lib-dir: '" + libDir.getAbsolutePath() + "'");
        }
        String fluiCSS = resourceDA.readTemplateAsString(Resource.FLUI_CSS);
        fluiCSS = fluiCSS.replace(FLUI_CSS_ACCENT_COLOR_PLACEHOLDER, project.getAccentColor());
        FileUtil.writeStringToFile(new File(libDir, "flui.css"), fluiCSS);
	}

    private void copyWebResources(File baseDir, File webDir) throws Exception {
        ResourceDAO resourceDA = new ResourceDAO();
        File libDir = GeneratorUtil.getWebLibOutputDir(baseDir);
        if (!libDir.exists()) {
            libDir.mkdir();
        }
        if (!libDir.exists()) {
            throw new Exception("Could not create lib-dir: '" + libDir.getAbsolutePath() + "'");
        }

        

        
//        resourceDA.copyResourceToDir(Resource.FLUI_CSS, libDir);
        resourceDA.copyResourceToDir(Resource.FLUI_UTIL_JS, libDir);
        resourceDA.copyResourceToDir(Resource.ANGULAR_ANIMATE_JS, libDir);
        resourceDA.copyResourceToDir(Resource.ANGULAR_AREA_JS, libDir);
        resourceDA.copyResourceToDir(Resource.ANGULAR_SANITIZE_JS, libDir);
        resourceDA.copyResourceToDir(Resource.ANGULAR_MATERIAL_CSS, libDir);
        resourceDA.copyResourceToDir(Resource.ANGULAR_MATERIAL_JS, libDir);
        resourceDA.copyResourceToDir(Resource.ANGULAR_MESSAGES_JS, libDir);
        resourceDA.copyResourceToDir(Resource.ANGULAR_JS, libDir);
        resourceDA.copyResourceToDir(Resource.SHOWDOWN_JS, libDir);
        resourceDA.copyResourceToDir(Resource.SHOWDOWN_LICENSE_TEXT, libDir);

        File codeMirrorLibDir = new File(webDir, "cmlib");
        if (!codeMirrorLibDir.exists()) {
            codeMirrorLibDir.mkdir();
        }
        if (!codeMirrorLibDir.exists()) {
            throw new Exception("Could not create lib-dir: '" + codeMirrorLibDir.getAbsolutePath() + "'");
        }
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_XML_JS, codeMirrorLibDir);
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_ACTIVE_LINE_JS, codeMirrorLibDir);
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_MATCHBRACKETS_JS, codeMirrorLibDir);
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_CLOSETAG_JS, codeMirrorLibDir);
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_MATCHTAGS_JS, codeMirrorLibDir);
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_XML_FOLD_JS, codeMirrorLibDir);
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_SHOW_HINT_JS, codeMirrorLibDir);
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_SHOW_HINT_CSS, codeMirrorLibDir);
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_JS, codeMirrorLibDir);
        resourceDA.copyResourceToDir(Resource.CODEMIRROR_CSS, codeMirrorLibDir);


        File imgDir = new File(webDir, "img");
        if (!imgDir.exists()) {
        	imgDir.mkdir();
        }
        if (!imgDir.exists()) {
            throw new Exception("Could not create lib-dir: '" + imgDir.getAbsolutePath() + "'");
        }
        resourceDA.copyResourceToDir(Resource.IMAGE_CHECKBOX_CHECKED, imgDir);
        resourceDA.copyResourceToDir(Resource.IMAGE_CHECKBOX_UNCHECKED, imgDir);

    }

    private void copyJavaResources(File outputDir, LanguageFlavor languageFlavor) throws Exception {
        ResourceDAO resourceDA = new ResourceDAO();

        File packageDir = GeneratorUtil.getCorePackageDir(outputDir);

        if (!packageDir.exists()) {
            packageDir.mkdirs();
        }
        if (!packageDir.exists()) {
            throw new Exception("Could not create package-dir: '" + packageDir.getAbsolutePath() + "'");
        }
        if (languageFlavor == LanguageFlavor.JAVA){
        	resourceDA.copyResourceToDir(Resource.FLUI_APPLICATION_JAVA, packageDir);
        	resourceDA.copyResourceToDir(Resource.FLUI_WEB_VIEW_JAVA, packageDir);
        } else if (languageFlavor == LanguageFlavor.ANDROID){
        	resourceDA.copyResourceToDir(Resource.FLUI_ANDROID_WEB_VIEW_JAVA, packageDir);
        }
        resourceDA.copyResourceToDir(Resource.FLUI_SCREEN_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_SCRIPT_EXCEPTION_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_MESSAGE_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_REPLY_DTO_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_CLIENT_PROPERTIES_DTO_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_REPLY_ABSTRACT_REPLY_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_REPLY_ACTION_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_TEST_CLASS_WRITER_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_TEST_REPLY_WRITER_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_REQUEST_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_ACTION_RECORDING_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_IMAGE_STREAM_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_FILE_STREAM_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_WEB_CALL_HANDLER_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_WEB_CALL_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_SCREEN_REQUEST_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.TEXT_HIGHLIGHTING_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.KEY_MODIFIER_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_KEY_EVENT_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.CONFIRM_DIALOG_PARAMETERS_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.INPUT_DIALOG_PARAMETERS_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.CALLBACK_METHOD_CALL_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_ABSTRCT_REPLY_CONTAINER_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.CURSOR_POS_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.CONTEXT_ASSIST_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.CONTEXT_ASSIST_CHOICE_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_SCREEN_MANAGER_LISTENER_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.FLUI_UTIL_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.ID_LABEL_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.ID_LABEL_LIST_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.ID_LABEL_IMAGE_ASSET_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.ID_LABEL_IMAGE_ASSET_LIST_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.LIST_CHOOSER_PARAMETERS_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.LIST_CHOOSER_ITEM_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.HTTP_MULTIPART_REQUEST_READER_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.SIMPLE_MANAGER_LISTENER_JAVA, packageDir);
        resourceDA.copyResourceToDir(Resource.JAR_INPUT_STREAM_URL_CONNECTION_JAVA, packageDir);
    }

    private static void log(String message) {
        System.out.println("CodeGeneratorService: " + message);
    }

    public static void main(String[] args) {
        if (DEV_IN_PROGRESS) {
            log("disabled so that no code is overwritten by accident");
            return;
        }


        try {
            new CodeGeneratorService().generateCode(new File("..\\Data\\Testing001"));
//            new CodeGeneratorService().generateCode(new File("..\\samples\\ContactManagerDemo\\FliesenUIProject"));
            new CodeGeneratorService().generateCode(new File("FliesenUIProject"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println("finished successfully.");
    }
}
