package com.bright_side_it.fliesenui.generator.service;

import java.io.File;
import java.util.Set;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.logic.ContainerHTMLGeneratorLogic;
import com.bright_side_it.fliesenui.generator.logic.HTMLTagLogic;
import com.bright_side_it.fliesenui.generator.logic.JSGetClientPropertiesFunctionCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JSStringResourceFunctionsCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.PluginInstanceHTMLGeneratorLogic;
import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.logic.DefinitionResourceLogic;
import com.bright_side_it.fliesenui.project.model.ProjectResource;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectDefinition;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceFormat;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceType;
import com.bright_side_it.fliesenui.res.dao.ResourceDAO;
import com.bright_side_it.fliesenui.res.dao.ResourceDAO.Resource;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenTopElement;

public class MultiPageAppHTMLGeneratorService {
    private static final CharSequence TITLE_PLACEHOLDER = "§{title}";
    private static final CharSequence JAVASCRIPT_PART_1_FILENAME_PLACEHOLDER = "§{jsFilenamePart1}";
    private static final CharSequence JAVASCRIPT_PART_2_FILENAME_PLACEHOLDER = "§{jsFilenamePart2}";
    private static final CharSequence CONTENT_PLACEHOLDER = "§{content}";
    private static final CharSequence MARGIN_PLACEHOLDER = "§{margin}";
    private static final CharSequence HTML_JAVASCRIPT_PLACEHOLDER = "§{htmlJavaScript}";
    private static final CharSequence CONTROLLER_NAME_PLACEHOLDER = "§{controllerName}";
    private static final CharSequence SCREEN_PANEL_NAME_PLACEHOLDER = "§{screenPanelName}";
    private static final CharSequence BACK_BUTTON_SCRIPT_PLACEHOLDER = "§{backButtonScript}";

    public void generateHTML(Project project, Set<ProjectResource> upToDateResources, File dir) throws Exception {
        DefinitionResourceLogic logic = new DefinitionResourceLogic();
        for (BrowserType browserType : BaseConstants.BrowserType.values()) {
            for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
                if (!upToDateResources.contains(logic.create(ResourceType.SCREEN, ResourceFormat.XML, screenDefinition.getID()))) {
                    log("screen '" + screenDefinition.getID() + "' has changed and is generated");
                    generateHTML(project, screenDefinition, browserType, project.getProjectDefinition(), dir);
                } else {
                    log("screen '" + screenDefinition.getID() + "' has NOT changed and is NOT generated");
                }
            }
        }
    }

    public void generateHTML(Project project, ScreenDefinition screenDefinition, BrowserType browserType, ProjectDefinition projectDefinition, File dir)
            throws Exception {
        String html = new ResourceDAO().readTemplateAsString(Resource.SCREEN_HTML);
        html = html.replace(TITLE_PLACEHOLDER, screenDefinition.getTitle());
        html = html.replace(CONTROLLER_NAME_PLACEHOLDER, GeneratorUtil.getAngularControllerName(screenDefinition));
        html = html.replace(SCREEN_PANEL_NAME_PLACEHOLDER, GeneratorUtil.getHTMLScreenPanelName(screenDefinition));
        html = html.replace(MARGIN_PLACEHOLDER, "" + projectDefinition.getMargin());
        html = html.replace(JAVASCRIPT_PART_1_FILENAME_PLACEHOLDER, GeneratorUtil.getJSPart1Filename(screenDefinition));
        html = html.replace(JAVASCRIPT_PART_2_FILENAME_PLACEHOLDER, GeneratorUtil.getJSPart2Filename(screenDefinition));
        html = html.replace(CONTENT_PLACEHOLDER, generateHTMLBodyContentText(project, screenDefinition, browserType, false));
        html = html.replace(HTML_JAVASCRIPT_PLACEHOLDER, generateHTMLJS(project, browserType));
        html = html.replace(BACK_BUTTON_SCRIPT_PLACEHOLDER, GeneratorUtil.generateHTMLJSBackButtonLogic(project, browserType, screenDefinition.getID()));

        File file = new File(dir, GeneratorUtil.createHTMLFilename(screenDefinition, browserType));
        FileUtil.writeStringToFile(file, html);
    }

    private StringBuilder generateHTMLJS(Project project, BrowserType browserType) throws Exception {
    	StringBuilder result = new StringBuilder();
    	result.append(GeneratorUtil.generateHTMLJSText(project, browserType, false) + "\n");
    	result.append("<script type=\"text/javascript\">\n");
    	result.append(new JSGetClientPropertiesFunctionCreatorLogic().createGetClientPropertiesFunction(browserType));
    	result.append("\n\n\n");
    	result.append(new JSStringResourceFunctionsCreatorLogic().createStringResourceFunctions());
    	result.append("        </script>");
		return result;
	}

	public CharSequence generateHTMLBodyContentText(Project project, ScreenDefinition screenDefinition, BrowserType browserType, boolean embeddedInSinglePage) throws Exception {
        StringBuilder result = new StringBuilder();
        if (!embeddedInSinglePage){
        	result.append("        " + GeneratorConstants.BOX_1_CM_HTML + "\n");
        }
        for (ScreenTopElement i : screenDefinition.getTopElements()) {
            HTMLTag htmlTag = null;
            if (i instanceof LayoutContainer) {
                htmlTag = new ContainerHTMLGeneratorLogic().generateHTML(project, screenDefinition, (LayoutContainer) i, browserType);
            } else if (i instanceof PluginInstance) {
                htmlTag = new PluginInstanceHTMLGeneratorLogic().generateHTML(project, screenDefinition, (PluginInstance) i, browserType);
            } else {
                throw new Exception("Unexpected type: " + i.getClass().getSimpleName());
            }
            new HTMLTagLogic().writeTag(htmlTag, result, 3);
        }

        return result.toString();
    }

    private void log(String message) {
        System.out.println("HTMLGeneratorService: " + message);
    }

}
