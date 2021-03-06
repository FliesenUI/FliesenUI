package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.LanguageFlavor;
import com.bright_side_it.fliesenui.project.dao.ProjectDefinitionDAO;
import com.bright_side_it.fliesenui.project.dao.ProjectOutputDAO;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectOutput;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class ProjectPropertiesValidationLogic {
    public void validate(Project project) {
        if (project.getScreenDefinitionsMap().values().isEmpty()) {
            ValidationUtil.addProjectDefinitionError(project, null, null, ProblemType.PROJECT_CONTAINS_NO_SCREENS, "The project must contain at least one screen");
        }
        if (BaseUtil.toEmptyCollectionIfNull(project.getProjectDefinition().getOutputs()).isEmpty()){
        	ValidationUtil.addProjectDefinitionError(project, null, null, ProblemType.PROJECT_CONTAINS_NO_OUTPUTS, "The project must contain at least one output");
        } else {
        	for (ProjectOutput i: (project.getProjectDefinition().getOutputs())){
        		validateProjectOutput(project, i);
        	}
        }
        
        validateColorPalette(project, project.getProjectDefinition().getThemeAccentePalette(), ProjectDefinitionDAO.THEME_ACCENT_PALETTE_ATTRIBUTE_NAME);
        validateColorPalette(project, project.getProjectDefinition().getThemeBackgroundPalette(), ProjectDefinitionDAO.THEME_BACKGROUND_PALETTE_ATTRIBUTE_NAME);
        validateColorPalette(project, project.getProjectDefinition().getThemePrimaryPalette(), ProjectDefinitionDAO.THEME_PRIMARY_PALETTE_ATTRIBUTE_NAME);
        validateColorPalette(project, project.getProjectDefinition().getThemeWarnPalette(), ProjectDefinitionDAO.THEME_WARN_PALETTE_ATTRIBUTE_NAME);
    }

	private void validateColorPalette(Project project, String colorPaletteName, String attributeName) {
		if (ProjectDefinitionDAO.DEFAULT_PALATTE_NAMES.contains(colorPaletteName)){
			return;
		}
		if (project.getColorPaletteMap().containsKey(colorPaletteName)){
			return;
		}
    	ValidationUtil.addProjectDefinitionError(project, project.getProjectDefinition().getNodePath(), attributeName
    			, ProblemType.PROJECT_UNKNOWN_COLOR_PALETTE
    			, "This color palette is neither a default palette nor a custom palette: '" + colorPaletteName + "'");
	}

	private void validateProjectOutput(Project project, ProjectOutput output) {
		if (output.getLanguageFlavor() == LanguageFlavor.JAVA){
			if (output.getSourceDirPath() == null){
	        	ValidationUtil.addProjectDefinitionError(project, output.getNodePath(), null, ProblemType.PROJECT_OUTPUT_MISSING_SOURCE_DIR_PATH
	        			, "If the output flavor is Java, the attribute " + ProjectOutputDAO.SOURCE_DIR_PATH_ATTRIBUTE_NAME + " must be provided");
			}
			if (output.getAndroidProjectPath() != null){
	        	ValidationUtil.addProjectDefinitionError(project, output.getNodePath(), ProjectOutputDAO.ANDROID_PROJECT_PATH_ATTRIBUTE_NAME
	        			, ProblemType.PROJECT_OUTPUT_UNEXPECTED_ANDROID_PROJECT_PATH
	        			, "If the output flavor is Java, the attribute the attribute Android project path may not be used");
			}
		} else if (output.getLanguageFlavor() == LanguageFlavor.ANDROID){
			if (output.getAndroidProjectPath() == null){
				ValidationUtil.addProjectDefinitionError(project, output.getNodePath(), null, ProblemType.PROJECT_OUTPUT_MISSING_ANDROID_PROJECT_PATH
						, "If the output flavor is Android, the attribute the attribute '" + ProjectOutputDAO.ANDROID_PROJECT_PATH_ATTRIBUTE_NAME + "' must be provided");
			}
			if (output.getSourceDirPath() != null){
	        	ValidationUtil.addProjectDefinitionError(project, output.getNodePath(), ProjectOutputDAO.SOURCE_DIR_PATH_ATTRIBUTE_NAME
	        			, ProblemType.PROJECT_OUTPUT_UNEXPECTED_SOURCE_DIR_PATH
	        			, "If the output flavor is Android, the attribute " + ProjectOutputDAO.SOURCE_DIR_PATH_ATTRIBUTE_NAME + " may not be used");
			}
		} else {
        	ValidationUtil.addProjectDefinitionError(project, output.getNodePath(), ProjectOutputDAO.FLAVOR_ATTRIBUTE_NAME, ProblemType.PROJECT_OUTPUT_UNKNOWN_FLAVOR
        			, "Unknown project output language flavor: " + output.getLanguageFlavor());
		}
	}

}
