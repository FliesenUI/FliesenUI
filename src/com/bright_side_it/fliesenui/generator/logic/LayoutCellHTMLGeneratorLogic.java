package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell.AlignType;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell.CellStyle;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;

public class LayoutCellHTMLGeneratorLogic {
    private HTMLTagLogic tagLogic = new HTMLTagLogic();

    public void generateHTML(HTMLTag parentTag, Project project, ScreenDefinition screenDefinition, LayoutBar bar, LayoutCell cell, BrowserType browserType) throws Exception {
    	
    	HTMLTag result = tagLogic.addTag(parentTag, "div", null, "flex", "" + cell.getSize());
    	HTMLTag contentParentTag = result;
    	if (cell.getCellStyle() == CellStyle.CARD){
    		HTMLTag cardTag = tagLogic.addTag(result, "md-card", null);

    		if ((cell.getHeadlineText() != null) || (cell.getSubheadText() != null)){
    			HTMLTag cardTitle = tagLogic.addTag(cardTag, "md-card-title", null);
    			HTMLTag cardTitleTextTag = tagLogic.addTag(cardTitle, "md-card-title-text", null);
    			if (cell.getHeadlineText() != null){
    				tagLogic.addTag(cardTitleTextTag, "span", "{{" + GeneratorUtil.getJSWidgetHeadlineTextVariableName(screenDefinition, cell) + "}}", "class", "md-headline");
    			}
    			if (cell.getSubheadText() != null){
    				tagLogic.addTag(cardTitleTextTag, "span", "{{" + GeneratorUtil.getJSWidgetSubheadTextVariableName(screenDefinition, cell) + "}}", "class", "md-subhead");
    			}
    		}
    		
    		contentParentTag = tagLogic.addTag(cardTag, "md-card-content", null);
    	} else {
    		if (cell.getHeadlineText() != null){
    			tagLogic.addTag(result, "div", "{{" + GeneratorUtil.getJSWidgetHeadlineTextVariableName(screenDefinition, cell) + "}}", "class", "md-headline");
    		}
    		if (cell.getSubheadText() != null){
    			tagLogic.addTag(result, "div", "{{" + GeneratorUtil.getJSWidgetSubheadTextVariableName(screenDefinition, cell) + "}}", "class", "md-subhead");
    		}
    	}
    	
    	HTMLTag contentTag = tagLogic.addTag(contentParentTag, "span", null);
    	if (cell.getContentAlign() != null){
    		log("cell content align: " + cell.getContentAlign());
    		tagLogic.setAttribute(contentTag, "align", alignValueToHTMLValue(cell.getContentAlign()));
    	}

        if (bar.getID() != null) {
        	//: since the bars are declared by the user, but there are no bars in HTML, hiding a bar means hiding all cells of the bar
            String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, bar.getID());
            tagLogic.setAttribute(result, "ng-show", showVariable);
        }
    	
        if (cell.getID() != null) {
        	//: if a cell is hidden, the space that is used should stay the same. Otherwise cells of the next bar might move up, because there are no bars in HTML
            String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, cell.getID());
            tagLogic.setAttribute(contentTag, "ng-show", showVariable);
        }


        String style = "";
        if (cell.getBackgroundColor() != null) {
            style += "background-color: " + cell.getBackgroundColor() + ";";
        }
        if (cell.getHeight() != null) {
            style += "height: " + cell.getHeight() + "px;";
        }
        if (style.length() > 0) {
            tagLogic.setAttribute(result, "style", style);
        }

        BasicWidgetHTMLGeneratorLogic basicWidgetLogic = new BasicWidgetHTMLGeneratorLogic();
        SelectBoxHTMLGeneratorLogic selectBoxLogic = new SelectBoxHTMLGeneratorLogic();
        TableWidgetHTMLGeneratorLogic tableWidgetLogic = new TableWidgetHTMLGeneratorLogic();
        ContainerHTMLGeneratorLogic containerHTMLGeneratorLogic = new ContainerHTMLGeneratorLogic();
        PluginInstanceHTMLGeneratorLogic pluginInstanceHTMLGeneratorLogic = new PluginInstanceHTMLGeneratorLogic();
        CodeEditorWidgetHTMLGeneratorLogic codeEditorWidgetHTMLGeneratorLogic = new CodeEditorWidgetHTMLGeneratorLogic();

        if (cell.getCellItems() != null) {
            for (CellItem i : cell.getCellItems()) {
                if (i instanceof BasicWidget) {
                    basicWidgetLogic.generateHTML(contentTag, screenDefinition, (BasicWidget) i, browserType);
                } else if (i instanceof SelectBox) {
                	selectBoxLogic.generateHTML(contentTag, screenDefinition, (SelectBox) i, browserType);
                } else if (i instanceof TableWidget) {
                    tableWidgetLogic.generateHTML(contentTag, project, screenDefinition, (TableWidget) i, browserType);
                } else if (i instanceof LayoutContainer) {
                    containerHTMLGeneratorLogic.generateHTML(contentTag, project, screenDefinition, (LayoutContainer) i, browserType);
                } else if (i instanceof PluginInstance) {
                    pluginInstanceHTMLGeneratorLogic.generateHTML(contentTag, project, screenDefinition, (PluginInstance) i, browserType);
                } else if (i instanceof CodeEditorWidget) {
                    codeEditorWidgetHTMLGeneratorLogic.generateHTML(contentTag, screenDefinition, (CodeEditorWidget) i);
                } else {
                    throw new Exception("Unknonwn widget type: " + i.getClass().getSimpleName());
                }
            }
        }
    }

	private String alignValueToHTMLValue(AlignType contentAlign) throws Exception {
		switch (contentAlign) {
		case LEFT:
			return "left";
		case RIGHT:
			return "right";
		case CENTER:
			return "center";
		default:
			throw new Exception("Unknown content align value: " + contentAlign);
		}
	}

	private void log(String message) {
		System.out.println("LayoutCellHTMLGeneratorLogic> " + message);
	}

}
