package com.bright_side_it.fliesenui.validation.logic;

import java.util.Map;

import com.bright_side_it.fliesenui.colorpalette.dao.ColorPaletteDAO;
import com.bright_side_it.fliesenui.colorpalette.model.ColorPalette;
import com.bright_side_it.fliesenui.project.dao.ProjectDefinitionDAO;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class ColorPaletteValidationLogic {
    public void validate(Project project) {
    	validateNoDefaultPaletteNameUsedAsID(project);
    	validateExtendedColorPalettesValid(project);
    }

	private void validateExtendedColorPalettesValid(Project project) {
		for (Map.Entry<String, ColorPalette> i : project.getColorPaletteMap().entrySet()) {
			if (!ProjectDefinitionDAO.DEFAULT_PALATTE_NAMES.contains(i.getValue().getExtendedPalette())){
				ValidationUtil.addError(project, i.getValue(), i.getValue().getNodePath(), ColorPaletteDAO.EXTENDED_PALETTE_ATTRIBUTE_NAME, ProblemType.COLOR_PALETTE_UNKNOWN_PALETTE_EXTENDED,
						"The color palette to be extended is unknown. These are possible palettes to extend: " + ProjectDefinitionDAO.DEFAULT_PALATTE_NAMES);
			}
		}
	}

	private void validateNoDefaultPaletteNameUsedAsID(Project project) {
		for (Map.Entry<String, ColorPalette> i : project.getColorPaletteMap().entrySet()) {
			if (ProjectDefinitionDAO.DEFAULT_PALATTE_NAMES.contains(i.getKey())){
				ValidationUtil.addError(project, i.getValue(), i.getValue().getNodePath(), null, ProblemType.COLOR_PALETTE_DEFAULT_PALETTE_ID_USED,
						"The ID of the color palette may not be one of the default palette names " + ProjectDefinitionDAO.DEFAULT_PALATTE_NAMES);
			}
		}
	}

}
