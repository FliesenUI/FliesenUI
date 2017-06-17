package com.bright_side_it.fliesenui.project.model;

import java.util.List;
import java.util.Map;

import com.bright_side_it.fliesenui.colorpalette.model.ColorPalette;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.stringres.model.StringResource;

public class Project {
    private ProjectDefinition projectDefinition;
    private Map<String, ScreenDefinition> screenDefinitionsMap;
    private Map<String, DTODefinition> dtoDefinitionsMap;
    private Map<String, PluginDefinition> pluginDefinitionsMap;
    private Map<String, ImageAssetDefinition> imageAssetDefinitionsMap;
    private Map<String, StringResource> stringResourceMap;
    private Map<String, ColorPalette> colorPaletteMap;

    private List<ResourceDefinitionProblem> projectDefinitionProblems;
    private Map<String, List<ResourceDefinitionProblem>> screenDefinitionProblemsMap;
    private Map<String, List<ResourceDefinitionProblem>> dtoDefinitionProblemsMap;
    private Map<String, List<ResourceDefinitionProblem>> pluginDefinitionProblemsMap;
    private Map<String, ResourceDefinitionProblem> imageAssetDefinitionProblemsMap;
    private Map<String, List<ResourceDefinitionProblem>> stringResourceProblemsMap;
    private Map<String, List<ResourceDefinitionProblem>> colorPaletteProblemsMap;

    private String accentColor;
    
    public ProjectDefinition getProjectDefinition() {
        return projectDefinition;
    }

    public void setProjectDefinition(ProjectDefinition projectDefinition) {
        this.projectDefinition = projectDefinition;
    }

    public Map<String, ScreenDefinition> getScreenDefinitionsMap() {
        return screenDefinitionsMap;
    }

    public void setScreenDefinitionsMap(Map<String, ScreenDefinition> screenDefinitionsMap) {
        this.screenDefinitionsMap = screenDefinitionsMap;
    }

    public Map<String, DTODefinition> getDTODefinitionsMap() {
        return dtoDefinitionsMap;
    }

    public void setDTODefinitionsMap(Map<String, DTODefinition> dtoDefinitionsMap) {
        this.dtoDefinitionsMap = dtoDefinitionsMap;
    }

    public Map<String, List<ResourceDefinitionProblem>> getScreenDefinitionProblemsMap() {
        return screenDefinitionProblemsMap;
    }

    public void setScreenDefinitionProblemsMap(Map<String, List<ResourceDefinitionProblem>> screenDefinitionProblemsMap) {
        this.screenDefinitionProblemsMap = screenDefinitionProblemsMap;
    }

    public List<ResourceDefinitionProblem> getProjectDefinitionProblems() {
        return projectDefinitionProblems;
    }

    public void setProjectDefinitionProblems(List<ResourceDefinitionProblem> projectDefinitionProblems) {
        this.projectDefinitionProblems = projectDefinitionProblems;
    }

    public Map<String, List<ResourceDefinitionProblem>> getDTODefinitionProblemsMap() {
        return dtoDefinitionProblemsMap;
    }

    public void setDTODefinitionProblemsMap(Map<String, List<ResourceDefinitionProblem>> dtoDefinitionProblemsMap) {
        this.dtoDefinitionProblemsMap = dtoDefinitionProblemsMap;
    }

    public Map<String, PluginDefinition> getPluginDefinitionsMap() {
        return pluginDefinitionsMap;
    }

    public void setPluginDefinitionsMap(Map<String, PluginDefinition> pluginDefinitionsMap) {
        this.pluginDefinitionsMap = pluginDefinitionsMap;
    }

    public Map<String, List<ResourceDefinitionProblem>> getPluginDefinitionProblemsMap() {
        return pluginDefinitionProblemsMap;
    }

    public void setPluginDefinitionProblemsMap(Map<String, List<ResourceDefinitionProblem>> pluginDefinitionProblemsMap) {
        this.pluginDefinitionProblemsMap = pluginDefinitionProblemsMap;
    }

    public Map<String, ImageAssetDefinition> getImageAssetDefinitionsMap() {
        return imageAssetDefinitionsMap;
    }

    public void setImageAssetDefinitionsMap(Map<String, ImageAssetDefinition> imageAssetDefinitionsMap) {
        this.imageAssetDefinitionsMap = imageAssetDefinitionsMap;
    }

    public Map<String, ResourceDefinitionProblem> getImageAssetDefinitionProblemsMap() {
        return imageAssetDefinitionProblemsMap;
    }

    public void setImageAssetDefinitionProblemsMap(Map<String, ResourceDefinitionProblem> imageAssetDefinitionProblemsMap) {
        this.imageAssetDefinitionProblemsMap = imageAssetDefinitionProblemsMap;
    }

	public Map<String, StringResource> getStringResourceMap() {
		return stringResourceMap;
	}

	public void setStringResourceMap(Map<String, StringResource> stringResourceMap) {
		this.stringResourceMap = stringResourceMap;
	}

	public Map<String, List<ResourceDefinitionProblem>> getStringResourceProblemsMap() {
		return stringResourceProblemsMap;
	}

	public void setStringResourceProblemsMap(Map<String, List<ResourceDefinitionProblem>> stringResourceProblemsMap) {
		this.stringResourceProblemsMap = stringResourceProblemsMap;
	}

	public Map<String, ColorPalette> getColorPaletteMap() {
		return colorPaletteMap;
	}

	public void setColorPaletteMap(Map<String, ColorPalette> colorPaletteMap) {
		this.colorPaletteMap = colorPaletteMap;
	}

	public Map<String, List<ResourceDefinitionProblem>> getColorPaletteProblemsMap() {
		return colorPaletteProblemsMap;
	}

	public void setColorPaletteProblemsMap(Map<String, List<ResourceDefinitionProblem>> colorPaletteProblemsMap) {
		this.colorPaletteProblemsMap = colorPaletteProblemsMap;
	}

	public String getAccentColor() {
		return accentColor;
	}

	public void setAccentColor(String accentColor) {
		this.accentColor = accentColor;
	}
	
}
