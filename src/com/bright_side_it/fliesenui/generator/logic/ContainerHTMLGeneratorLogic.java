package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
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
            result = tagLogic.createTag("div", null, "layout", toString(layoutContainer.getOrientation()), "layout-wrap", "", "layout-align", layoutAlign);
        } else {
            result = tagLogic.addTag(parent, "div", null, "layout", toString(layoutContainer.getOrientation()), "layout-wrap", "", "layout-align", layoutAlign);
        }
        if (layoutContainer.getID() != null){
            tagLogic.setAttribute(result, "ng-show", GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, layoutContainer.getID()));
        }
        
        LayoutCellHTMLGeneratorLogic cellLogic = new LayoutCellHTMLGeneratorLogic();
        for (LayoutBar bar : layoutContainer.getBars()) {
            for (LayoutCell cell : bar.getCells()) {
                cellLogic.generateHTML(result, project, screenDefinition, bar, cell, browserType);
            }
        }
        return result;
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
