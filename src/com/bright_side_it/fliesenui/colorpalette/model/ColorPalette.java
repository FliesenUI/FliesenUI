package com.bright_side_it.fliesenui.colorpalette.model;

import java.util.Map;

import com.bright_side_it.fliesenui.screendefinition.model.NodePath;

public class ColorPalette {
	public enum Shade {SHADE_50, SHADE_100, SHADE_200, SHADE_300, SHADE_400, SHADE_500
		, SHADE_600, SHADE_700, SHADE_800, SHADE_900, SHADE_A100, SHADE_A200, SHADE_A400
		, SHADE_A700}

	private String id;
	private String extendedPalette;
	private Map<Shade, String> colors;
	private NodePath nodePath;
	
	public Map<Shade, String> getColors() {
		return colors;
	}

	public void setColors(Map<Shade, String> colors) {
		this.colors = colors;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getExtendedPalette() {
		return extendedPalette;
	}

	public void setExtendedPalette(String extendedPalette) {
		this.extendedPalette = extendedPalette;
	}

	public NodePath getNodePath() {
		return nodePath;
	}

	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}
	
	
}
