package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.logic.UnitValueLogic;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer.Orientation;

public class ContainerHTMLGeneratorLogic {
    private HTMLTagLogic tagLogic = new HTMLTagLogic();

    public HTMLTag generateHTML(Project project, ScreenDefinition screenDefinition, LayoutContainer layoutContainer, BrowserType browserType) throws Exception {
        return generateHTML(null, project, screenDefinition, layoutContainer, browserType);
    }

    public HTMLTag generateHTML(HTMLTag parent, Project project, ScreenDefinition screenDefinition, LayoutContainer layoutContainer, BrowserType browserType)
            throws Exception {
        HTMLTag result = null;
        String layoutAlign = "space-between center"; //: see also https://material.angularjs.org/latest/layout/alignment
        if (parent == null) {
            result = tagLogic.createTag("div", null, "layout-wrap", "", "layout-align", layoutAlign);
        } else {
            result = tagLogic.addTag(parent, "div", null, "layout-wrap", "", "layout-align", layoutAlign);
        }
        if ((layoutContainer.getOrientation() == Orientation.COLUMN) || (layoutContainer.getOrientation() == Orientation.ROW)){
        	tagLogic.setAttribute(result, "layout", toString(layoutContainer.getOrientation()));
        }

        if (layoutContainer.getHeight() != null){
            tagLogic.setAttribute(result, "style", "height: " + new UnitValueLogic().toCSSString(layoutContainer.getHeight()));
        }

        
        if (layoutContainer.getID() != null){
            tagLogic.setAttribute(result, "ng-show", GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, layoutContainer.getID()));
        }
        
        LayoutCellHTMLGeneratorLogic cellLogic = new LayoutCellHTMLGeneratorLogic();
        HTMLTag parentTag = result;
        for (LayoutBar bar : layoutContainer.getBars()) {
        	if (bar.getPosition() != null){
        		parentTag = createBorderLayoutPositionTag(result, layoutContainer, bar);
        	}
            for (LayoutCell cell : bar.getCells()) {
                cellLogic.generateHTML(parentTag, project, screenDefinition, bar, cell, browserType);
            }
        }
        return result;
    }

    private HTMLTag createBorderLayoutPositionTag(HTMLTag parentTag, LayoutContainer layoutContainer, LayoutBar bar) throws Exception {
    	String style = "position:fixed; ";
    	String topString = "";
    	String bottomString = "";
    	String leftString = "";
    	String rightString = "";
    	if (layoutContainer.getTopSizeInCM() != null){
    		topString = "top: " + layoutContainer.getTopSizeInCM() + "cm;";
    	}
    	if (layoutContainer.getBottomSizeInCM() != null){
    		bottomString = "bottom: " + layoutContainer.getBottomSizeInCM() + "cm;";
    	}
    	if (layoutContainer.getLeftSizeInCM() != null){
    		leftString = "left: " + layoutContainer.getLeftSizeInCM() + "cm;";
    	}
    	if (layoutContainer.getRightSizeInCM() != null){
    		rightString = "right: " + layoutContainer.getRightSizeInCM() + "cm;";
    	} else {
    		rightString = "right: 0;";
    	}
    	
    	switch (bar.getPosition()) {
		case TOP:
			style += "top:0; left:0; height:" + layoutContainer.getTopSizeInCM() + "cm; width: 100%;";
//			style += "background-color: #aaaaff; ";
			break;
		case BOTTOM:
			style += "bottom:0; left:0; height:" + layoutContainer.getBottomSizeInCM() + "cm; width: 100%;";
//			style += "background-color: #0000ff; ";
			break;
		case LEFT:
			style += "left:0; width:" + layoutContainer.getLeftSizeInCM() + "cm; " +  "overflow: auto;" + topString + bottomString;
//			style += "background-color: #00ff00; ";
			break;
		case RIGHT:
			style += "right:0; width:" + layoutContainer.getRightSizeInCM() + "cm; " +  "overflow: auto;" + topString + bottomString;
//			style += "background-color: #aaffff; ";
			break;
		case CENTER:
			style += "overflow: auto;" + topString + bottomString + leftString + rightString;
//			style += "background-color: #ffaaaa; ";
			break;
		default:
			throw new Exception("Unknown position:" + bar.getPosition());
		}
		return tagLogic.addTag(parentTag, "span", null, "style", style);
	}

	private String toString(Orientation orientation) {
        if (orientation == Orientation.ROW) {
            return "row";
        } else if (orientation == Orientation.COLUMN) {
            return "col";
        }
        throw new RuntimeException("Unknonwn orientation: " + orientation);
    }

}
