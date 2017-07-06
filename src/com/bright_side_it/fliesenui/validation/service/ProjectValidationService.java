package com.bright_side_it.fliesenui.validation.service;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectResource;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.validation.logic.BasicWidgetValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.CallbackMethodParameterValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.CallbackMethodValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.CodeEditorWidgetValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.ColorPaletteValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.DTODefinitionValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.EventHandlerValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.EventListenerValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.EventParameterValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.IDValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.ImageAssetValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.LayoutBarValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.LayoutCellValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.LayoutContainerValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.PluginInstanceValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.ProjectDefinitionValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.ProjectProblemsStringWriterLogic;
import com.bright_side_it.fliesenui.validation.logic.ProjectPropertiesValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.ScreenValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.SelectBoxDTOValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.SharedReplyInterfaceValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.StringResourceValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.TableWidgetDTOValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.TableWidgetPropertiesValidationLogic;
import com.bright_side_it.fliesenui.validation.logic.TimerValidationLogic;

public class ProjectValidationService {
    public void validateProject(Project project, Set<ProjectResource> upToDateResources) {
        log("current implementation state: always validate everything and ignore up to date resources");
        long startTime = System.currentTimeMillis();

        new ProjectPropertiesValidationLogic().validate(project);
        new SharedReplyInterfaceValidationLogic().validate(project);
        new ProjectDefinitionValidationLogic().validate(project);
        new IDValidationLogic().validate(project);
        new ColorPaletteValidationLogic().validate(project);
        new StringResourceValidationLogic().validate(project);
        new DTODefinitionValidationLogic().validate(project);
        new CallbackMethodValidationLogic().validate(project);
        new CallbackMethodParameterValidationLogic().validate(project);
        new ImageAssetValidationLogic().validate(project);
        new TableWidgetPropertiesValidationLogic().validate(project);
        new TableWidgetDTOValidationLogic().validate(project);
        new SelectBoxDTOValidationLogic().validate(project);
        new BasicWidgetValidationLogic().validate(project);
        new ScreenValidationLogic().validate(project);
        new PluginInstanceValidationLogic().validate(project);
        new CodeEditorWidgetValidationLogic().validate(project);
        new LayoutCellValidationLogic().validate(project);
        new LayoutContainerValidationLogic().validate(project);
        new LayoutBarValidationLogic().validate(project);
        new EventParameterValidationLogic().validate(project);
        new EventHandlerValidationLogic().validate(project);
        new EventListenerValidationLogic().validate(project);
        new TimerValidationLogic().validate(project);

        
        writeStringResourceProblemsToLog(project);
        log("Validation duration: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    private void writeStringResourceProblemsToLog(Project project) {
        if (project.getStringResourceProblemsMap() == null){
        	log("project.getStringResourceProblemsMap() is null!");
        }
        boolean anyEntries = false;
        for (Entry<String, List<ResourceDefinitionProblem>> i: project.getStringResourceProblemsMap().entrySet()){
        	for (ResourceDefinitionProblem problem: i.getValue()){
        		log("string resource problem. resource = " + i.getKey() + ", problem: " + problem.getType() + ", " + problem.getMessage() + " resorce id = " + problem.getNodePath().getTopElementID());
        		anyEntries = true;
        	}
        }
        if (!anyEntries){
        	log("string resource problem map contains no entries");
        }
	}

	private void log(String message) {
        System.out.println("ProjectValidationService> " + message);
    }

    public String problemsToString(Project project) {
        return new ProjectProblemsStringWriterLogic().problemsToString(project);
    }

    public boolean containsProblems(Project project) {
        return (BaseUtil.isNotNullAndNotEmpty(project.getDTODefinitionProblemsMap())) || (BaseUtil.isNotNullAndNotEmpty(project.getProjectDefinitionProblems()))
                || (BaseUtil.isNotNullAndNotEmpty(project.getScreenDefinitionProblemsMap())) || (BaseUtil.isNotNullAndNotEmpty(project.getPluginDefinitionProblemsMap()));
    }


}
