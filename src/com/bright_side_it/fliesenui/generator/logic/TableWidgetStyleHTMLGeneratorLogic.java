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
    private static final int ROW_CHECKBOXES_SIZE = 5;
	private static final int SIZE_TO_SAVE = 10;
    public enum Style {SMALL, NORMAL}
    
    public TableWidgetStyleHTMLGeneratorLogic(Style style){
    	this.style = style;
    }

    public void generateHTML(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, BrowserType browserType) throws Exception {
        if (widget.isShowFilter()){
        	createFilter(parentTag, screenDefinition, widget);
        }
        if (widget.isShowColumnHeader()){
        	HTMLTag headerTableTag = tagLogic.addTag(parentTag, "table", null, "style", "width:100%");
        	createHeader(headerTableTag, screenDefinition, widget);
        }

        String style = "width:100%; ";
        if (widget.getContentHeight() != null) {
            style += "overflow-y: scroll; height: " + widget.getContentHeight() + "px;";
        }
        HTMLTag scrollableDiv = tagLogic.addTag(parentTag, "div", null, "style", style);
        HTMLTag contentTableTag = tagLogic.addTag(scrollableDiv, "table", null, "style", "width:100%");
        createRows(project, contentTableTag, screenDefinition, widget, browserType);
    }

    private void createFilter(HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget) {
    	String filterTextVariable = GeneratorUtil.getJSTableFilterTextVariableName(screenDefinition, widget);
    	String filterTextElementID = GeneratorUtil.getJSTableFilterTextElementID(screenDefinition, widget);
    	String infoButtonClickMethodName = GeneratorUtil.getJSTableFilterOnInfoButtonClickMethodName(screenDefinition, widget);
    	String filterKeyDownMethodName = GeneratorUtil.getJSTableFilterKeyDownMethodName(screenDefinition, widget);
    	HTMLTag filterTopTag = tagLogic.addTag(parentTag, "div", null, "flex", "100");
    	HTMLTag filterTableTag = tagLogic.addTag(filterTopTag, "table", null, "style", "width:100%");
    	HTMLTag filterTableRowTag = tagLogic.addTag(filterTableTag, "tr", null);
    	tagLogic.addTag(filterTableRowTag, "td", "Filter:", "style", "width:10%");
    	HTMLTag filterTextTag = tagLogic.addTag(filterTableRowTag, "td", null, "style", "width:83%");
    	tagLogic.addTag(filterTextTag, "input", null, "id", filterTextElementID, "style", "width:90%", "ng-model", filterTextVariable, "onkeydown", filterKeyDownMethodName + "(event)");
    	HTMLTag filterInfoTag = tagLogic.addTag(filterTableRowTag, "td", null, "style", "width:7%");
		tagLogic.addTag(filterInfoTag, "md-button", "?", "class", "fliesenUIButton fliesenUIButtonExtendSmall", "ng-click", infoButtonClickMethodName + "();");		
	}

    private double getColumnWidthFactor(TableWidget widget) {
    	if (widget.isRowCheckboxes()){
    		return 0.95;
    	}
		return 1;
	}
    
	private void createHeader(HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget) throws Exception {
        HTMLTag rowTag = tagLogic.addTag(parentTag, "tr", null, "layout", "row", "layout-wrap", "", "style", "background-color:#3f51b5; color:white");
        double columnWidthFactor = getColumnWidthFactor(widget);
        int tableColumnIndex = 0;
        int savedSize = 0;
        int sizeToSave = SIZE_TO_SAVE;
        if (widget.isRowCheckboxes()){
        	sizeToSave -= ROW_CHECKBOXES_SIZE;
        	tagLogic.addTag(rowTag, "th", "", "style", "width:" + ROW_CHECKBOXES_SIZE + "%;word-wrap: break-word;");
        }
        for (TableWidgetColumn i : widget.getColumns()) {
        	double fullSize = i.getSize() * columnWidthFactor;
        	int size = (int)(fullSize * SIZE_FACTOR_TO_ODD_TABLE_LOOK );
        	if (savedSize >= sizeToSave){
        		size = (int)fullSize;
        	}
        	savedSize += (fullSize - size);
        	String hightStyle = "font-weight:normal;";
        	if (style == Style.NORMAL){
        		hightStyle = "min-height:40px; padding-left:15px; padding-top:15px; font-weight:bold; font-size:1.2em;";
        	}
        	String textVariable = GeneratorUtil.getJSTableColumnTextVariableName(screenDefinition, widget, tableColumnIndex);
        	
            tagLogic.addTag(rowTag, "th", "{{" + textVariable + "}}", "style", "width:" + size + "%;word-wrap: break-word;" + hightStyle);
        	tableColumnIndex ++;
        }
    }


	private void createRows(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, BrowserType browserType) throws Exception {
        String onClick = GeneratorUtil.createJSTableRowClickMethodName(screenDefinition, widget) + "($index, $event)";
        String filterName = GeneratorUtil.createJSTableFilterMethodName(screenDefinition, widget);
        String filterTextVariable = GeneratorUtil.getJSTableFilterTextVariableName(screenDefinition, widget);
        String styleAttribute = "border-color:#999999;border-bottom-style: solid;border-width: 1px; background-color:{{rowBackgroundColor}}";
        String mouseleave = "rowHover=false;rowBackgroundColor='white';";
        String moseover = "rowHover=true;rowBackgroundColor='#f3f3f3';";
        String ngInit = "rowBackgroundColor='white';";

        if (widget.getBackgroundColorDTOField() != null){
        	mouseleave = "rowHover=false;rowBackgroundColor=i." + widget.getBackgroundColorDTOField() + ";";
        	ngInit = "rowBackgroundColor=i." + widget.getBackgroundColorDTOField() + ";";
        }
        
        String repeatText = "i in " + widget.getDTO() + " | " + filterName + ":" + filterTextVariable;
        HTMLTag rowTag = tagLogic.addTag(parentTag, "tr", null, "ng-repeat", repeatText, "ng-mouseleave", mouseleave, "ng-mouseover", moseover, "ng-click",
                onClick, "style", styleAttribute, "layout", "row", "layout-wrap", "", "ng-init", ngInit);

        double columnWidthFactor = getColumnWidthFactor(widget);
        int savedSize = 0;
        int sizeToSave = SIZE_TO_SAVE;
        if (widget.isRowCheckboxes()){
        	sizeToSave -= ROW_CHECKBOXES_SIZE;
        	String clickMethodName = GeneratorUtil.createJSTableRowSelectBoxClickMethodName(screenDefinition, widget);
        	String selectedItemsVariableName = GeneratorUtil.createJSTableRowCheckedIDVariableName(screenDefinition, widget);
        	HTMLTag cellTag = tagLogic.addTag(rowTag, "td", "", "style", "width:" + ROW_CHECKBOXES_SIZE + "%;word-wrap: break-word;", "ng-click", clickMethodName + "($index, $event)");
        	
        	tagLogic.addTag(cellTag, "img", null, "src", "img/_checkbox_checked.png", "width", "" + 20, "style", "white-space: pre-wrap; padding-left:15px; padding-right:15px;", "ng-if", selectedItemsVariableName + "[i." + widget.getIDDTOField() + "]");
        	tagLogic.addTag(cellTag, "img", null, "src", "img/_checkbox_unchecked.png", "width", "" + 20, "style", "white-space: pre-wrap; padding-left:15px; padding-right:15px;", "ng-if", "!" + selectedItemsVariableName + "[i." + widget.getIDDTOField() + "]");
        	
        	
        	
//        	HTMLTag spanTag = tagLogic.addTag(cellTag, "span", null, "style", "white-space: pre-wrap; padding-left:15px; padding-right:15px;", "ng-if", selectedItemsVariableName + "[i." + widget.getIDDTOField() + "]");
//        	tagLogic.addTag(spanTag, "img", null, "src", "img/_checkbox_checked.png", "width", "" + 20);
//        	spanTag = tagLogic.addTag(cellTag, "span", null, "style", "white-space: pre-wrap; padding-left:15px; padding-right:15px;", "ng-if", "!" + selectedItemsVariableName + "[i." + widget.getIDDTOField() + "]");
//        	tagLogic.addTag(spanTag, "img", null, "src", "img/_checkbox_unchecked.png", "width", "" + 20);
        }
        for (TableWidgetColumn column : widget.getColumns()) {
        	double fullSize = column.getSize() * columnWidthFactor;
        	int size = (int)(fullSize * SIZE_FACTOR_TO_ODD_TABLE_LOOK);
        	if (savedSize >= sizeToSave){
        		size = (int)fullSize;
        	}
        	
        	savedSize += (fullSize - size);
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
        tagLogic.setAttribute(tag, "ng-click", GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, widget, tableItem) + "($index, $event)");
        tagLogic.setAttribute(tag, "style", "cursor: pointer;");
        
        if (tableItem.isOnlyShowOnHover()){
        	tagLogic.setAttribute(tag, "ng-show", "rowHover");
        }

        
        addTooltipAttributeIfNeeded(tag, tableItem);
    }

    private void addButtonTableItem(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, TableWidgetItem tableItem, BrowserType browserType)
            throws Exception {
    	String styleClass = "fliesenUITinyButton";
    	if (style == Style.NORMAL){
    		styleClass = "fliesenUINormalSizeTableButton";
    	}
    	HTMLTag tag = null;
    	String textVariable = GeneratorUtil.getJSTableWidgetTextVariableName(screenDefinition, widget, tableItem);
        if (tableItem.getImageSource() == null) {
        	tag = tagLogic.addTag(parentTag, "button", "{{" + textVariable + "}}", "class", styleClass, "ng-click",
                    GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, widget, tableItem) + "($index, $event)");
        } else {
            tag = tagLogic.addTag(parentTag, "div", null, "class", styleClass, "ng-click",
                    GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, widget, tableItem) + "($index, $event)", "style", "cursor: pointer;");
            
            TableWidgetHTMLGeneratorLogic.createTableImageSourceTag(project, tag, tableItem.getImageSource(), browserType);
            tagLogic.addTag(tag, "span", "{{" + textVariable + "}}");
        }
        if (tableItem.isOnlyShowOnHover()){
        	tagLogic.setAttribute(tag, "ng-show", "rowHover");
        }
        addTooltipAttributeIfNeeded(tag, tableItem);
    }

}
