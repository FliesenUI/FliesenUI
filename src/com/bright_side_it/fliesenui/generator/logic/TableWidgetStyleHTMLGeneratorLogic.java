package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;

public class TableWidgetStyleHTMLGeneratorLogic {
    private HTMLTagLogic tagLogic = new HTMLTagLogic();
	private Style style;
    private static final double SIZE_FACTOR_TO_ODD_TABLE_LOOK = 0.9;
    public enum Style {SMALL, NORMAL}
    
    public TableWidgetStyleHTMLGeneratorLogic(Style style){
    	this.style = style;
    }

    public void generateHTML(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, BrowserType browserType) throws Exception {
        HTMLTag headerTableTag = tagLogic.addTag(parentTag, "table", null, "style", "width:100%");
        createHeader(headerTableTag, widget);


        String style = "width:100%; ";
        if (widget.getContentHeight() != null) {
            style += "overflow-y: scroll; height: " + widget.getContentHeight() + "px;";
        }
        HTMLTag scrollableDiv = tagLogic.addTag(parentTag, "div", null, "style", style);
        HTMLTag contentTableTag = tagLogic.addTag(scrollableDiv, "table", null, "style", "width:100%");
        createRows(project, contentTableTag, screenDefinition, widget, browserType);
    }

    private void createHeader(HTMLTag parentTag, TableWidget widget) throws Exception {
        HTMLTag rowTag = tagLogic.addTag(parentTag, "tr", null, "layout", "row", "layout-wrap", "", "style", "background-color:#3f51b5; color:white");
        for (TableWidgetColumn i : widget.getColumns()) {
        	int size = (int)(i.getSize() * SIZE_FACTOR_TO_ODD_TABLE_LOOK);
        	String hightStyle = "font-weight:normal;";
        	if (style == Style.NORMAL){
        		hightStyle = "min-height:40px; padding-left:15px; padding-top:15px; font-weight:bold; font-size:1.2em;";
        	}
            tagLogic.addTag(rowTag, "th", i.getText(), "style", "width:" + size + "%;word-wrap: break-word;" + hightStyle);
        }
    }

    private void createRows(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, BrowserType browserType) throws Exception {
        String onClick = GeneratorUtil.createJSTableRowClickMethodName(screenDefinition, widget) + "($index, $event)";
        String styleAttribute = "border-color:#999999;border-bottom-style: solid;border-width: 1px; background-color:{{rowBackgroundColor}}";
        String mouseleave = "rowHover=false;rowBackgroundColor='white';";
        String moseover = "rowHover=true;rowBackgroundColor='#f3f3f3';";
        String ngInit = "rowBackgroundColor='white';";

        if (widget.getBackgroundColorDTOField() != null){
        	mouseleave = "rowHover=false;rowBackgroundColor=i." + widget.getBackgroundColorDTOField() + ";";
        	ngInit = "rowBackgroundColor=i." + widget.getBackgroundColorDTOField() + ";";
        }
        
        HTMLTag rowTag = tagLogic.addTag(parentTag, "tr", null, "ng-repeat", "i in " + widget.getDTO(), "ng-mouseleave", mouseleave, "ng-mouseover", moseover, "ng-click",
                onClick, "style", styleAttribute, "layout", "row", "layout-wrap", "", "ng-init", ngInit);

        for (TableWidgetColumn column : widget.getColumns()) {
        	int size = (int)(column.getSize() * SIZE_FACTOR_TO_ODD_TABLE_LOOK);
        	String hightStyle = "";
        	if (style == Style.NORMAL){
        		hightStyle = "min-height:60px; padding-left:15px; padding-top:15px;";
        	}

            HTMLTag cellTag = tagLogic.addTag(rowTag, "td", null, "style", "width:" + size + "%;word-wrap: break-word;" + hightStyle);
            for (TableWidgetItem tableItem : column.getTableItems()) {
                addTableItem(project, cellTag, screenDefinition, widget, tableItem, browserType);
            }
        }
    }

    private void addTableItem(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, TableWidgetItem tableItem, BrowserType browserType) throws Exception {
        switch (tableItem.getType()) {
        case BUTTON:
            addButtonTableItem(project, parentTag, screenDefinition, widget, tableItem, browserType);
            break;
        case IMAGE_BUTTON:
            addImageButtonTableItem(project, parentTag, screenDefinition, widget, tableItem, browserType);
            break;
        case LABEL:
            addLabelTableItem(project, parentTag, widget, tableItem, browserType);
            break;
        case IMAGE:
            addImageTableItem(project, parentTag, widget, tableItem, browserType);
            break;
        default:
            throw new Exception("Unknonwn type: " + tableItem.getType());
        }
    }

    private void addLabelTableItem(Project project, HTMLTag parentTag, TableWidget widget, TableWidgetItem tableItem, BrowserType browserType) throws Exception {
        if (tableItem.getImageSource() != null) {
            TableWidgetHTMLGeneratorLogic.createTableImageSourceTag(project, parentTag, tableItem.getImageSource(), browserType);
        }
        HTMLTag tag = tagLogic.addTag(parentTag, "span", "{{ i." + tableItem.getTextDTOField() + " }}", "style", "white-space: pre-wrap;");
        addTooltipAttributeIfNeeded(tag, tableItem);
    }

    private void addImageTableItem(Project project, HTMLTag parentTag, TableWidget widget, TableWidgetItem tableItem, BrowserType browserType) throws Exception {
        HTMLTag tag = TableWidgetHTMLGeneratorLogic.createTableImageSourceTag(project, parentTag, tableItem.getImageSource(), browserType);
        addTooltipAttributeIfNeeded(tag, tableItem);
    }
    
    private void addTooltipAttributeIfNeeded(HTMLTag tag, TableWidgetItem tableItem){
        if (tableItem.getTooltipDTOField() != null){
        	tagLogic.setAttribute(tag, "title", "{{i." + tableItem.getTooltipDTOField() + "}}");
        }
    }

    private void addImageButtonTableItem(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, TableWidgetItem tableItem, BrowserType browserType)
            throws Exception {
        HTMLTag tag = TableWidgetHTMLGeneratorLogic.createTableImageSourceTag(project, parentTag, tableItem.getImageSource(), browserType);
        tagLogic.setAttribute(tag, "ng-show", "rowHover");
        tagLogic.setAttribute(tag, "ng-click", GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, widget, tableItem) + "($index, $event)");
        tagLogic.setAttribute(tag, "style", "cursor: pointer;");
        addTooltipAttributeIfNeeded(tag, tableItem);
    }

    private void addButtonTableItem(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, TableWidgetItem tableItem, BrowserType browserType)
            throws Exception {
    	String styleClass = "fliesenUITinyButton";
    	if (style == Style.NORMAL){
    		styleClass = "fliesenUINormalSizeTableButton";
    	}
    	HTMLTag tag = null;
        if (tableItem.getImageSource() == null) {
        	tag = tagLogic.addTag(parentTag, "button", tableItem.getText(), "class", styleClass, "ng-show", "rowHover", "ng-click",
                    GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, widget, tableItem) + "($index, $event)");
        } else {
            tag = tagLogic.addTag(parentTag, "div", null, "class", styleClass, "ng-show", "rowHover", "ng-click",
                    GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, widget, tableItem) + "($index, $event)", "style", "cursor: pointer;");
            TableWidgetHTMLGeneratorLogic.createTableImageSourceTag(project, tag, tableItem.getImageSource(), browserType);
            tagLogic.addTag(tag, "span", tableItem.getText());
        }
        addTooltipAttributeIfNeeded(tag, tableItem);
    }

}
