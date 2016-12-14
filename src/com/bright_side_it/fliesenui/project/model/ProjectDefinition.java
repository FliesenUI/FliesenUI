package com.bright_side_it.fliesenui.project.model;

import java.util.List;
import java.util.Map;

import com.bright_side_it.fliesenui.screendefinition.model.NodePath;

public class ProjectDefinition implements ResourceDefinition {
    private String title;
    private boolean darkTheme;
    private String themePrimaryPalette;
    private String themeAccentePalette;
    private String themeBackgroundPalette;
    private String themeWarnPalette;
    private List<ProjectOutput> outputs;
    private int margin;
    private String startScreenID;
    private NodePath nodePath;
    private Map<String, SharedReplyInterface> sharedReplyInterfaces;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public String getThemePrimaryPalette() {
        return themePrimaryPalette;
    }

    public void setThemePrimaryPalette(String themePrimaryPalette) {
        this.themePrimaryPalette = themePrimaryPalette;
    }

    public String getThemeAccentePalette() {
        return themeAccentePalette;
    }

    public void setThemeAccentePalette(String themeAccentePalette) {
        this.themeAccentePalette = themeAccentePalette;
    }

    public String getThemeBackgroundPalette() {
        return themeBackgroundPalette;
    }

    public void setThemeBackgroundPalette(String themeBackgroundPalette) {
        this.themeBackgroundPalette = themeBackgroundPalette;
    }

    public String getThemeWarnPalette() {
        return themeWarnPalette;
    }

    public void setThemeWarnPalette(String themeWarnPalette) {
        this.themeWarnPalette = themeWarnPalette;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

	public String getStartScreenID() {
		return startScreenID;
	}

	public void setStartScreenID(String startScreenID) {
		this.startScreenID = startScreenID;
	}

	public NodePath getNodePath() {
		return nodePath;
	}

	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}

	public List<ProjectOutput> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<ProjectOutput> outputs) {
		this.outputs = outputs;
	}

	public Map<String, SharedReplyInterface> getSharedReplyInterfaces() {
		return sharedReplyInterfaces;
	}

	public void setSharedReplyInterfaces(Map<String, SharedReplyInterface> sharedReplyInterfaces) {
		this.sharedReplyInterfaces = sharedReplyInterfaces;
	}

}
