package com.bright_side_it.fliesenui.colorpalette.dao;

import java.util.List;

import com.bright_side_it.fliesenui.colorpalette.model.ColorPalette;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;

public class ColorPaletteDAOResult {
    private ColorPalette colorPalette;
    private List<ResourceDefinitionProblem> problems;

    public List<ResourceDefinitionProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<ResourceDefinitionProblem> problems) {
        this.problems = problems;
    }

	public ColorPalette getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(ColorPalette colorPalette) {
		this.colorPalette = colorPalette;
	}

}
