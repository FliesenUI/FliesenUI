package com.bright_side_it.fliesenui.generator.logic;
//package fliesenui.generator.logic;
//
//import fliesenui.generator.model.HTMLTag;
//import fliesenui.generator.util.GeneratorUtil;
//import fliesenui.project.model.Project;
//import fliesenui.screendefinition.model.ScreenDefinition;
//import fliesenui.screendefinition.model.TableWidget;
//import fliesenui.screendefinition.model.TableWidgetColumn;
//import fliesenui.screendefinition.model.TableWidgetItem;
//
//public class TableWidgetStyleNormalHTMLGeneratorLogic {
//    private HTMLTagLogic tagLogic = new HTMLTagLogic();
//
//    public void generateHTML(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget) throws Exception {
//        createHeader(parentTag, widget);
//        createRows(project, parentTag, screenDefinition, widget);
//    }
//
//    private void createHeader(HTMLTag parentTag, TableWidget widget) throws Exception {
//        HTMLTag topTag = tagLogic.addTag(parentTag, "div", null, "layout", "row", "layout-wrap", "");
//        for (TableWidgetColumn i : widget.getColumns()) {
//            HTMLTag divTag = tagLogic.addTag(topTag, "div", null, "flex", "" + i.getSize());
//            HTMLTag toolbarTag = tagLogic.addTag(divTag, "md-toolbar", null);
//            HTMLTag subDivTag = tagLogic.addTag(toolbarTag, "md-toolbar", null, "class", "md-toolbar-tools");
//            tagLogic.addTag(subDivTag, "h2", i.getText());
////            HTMLTag toolbarTag = tagLogic.addTag(topTag, "md-toolbar", null, "flex", "" + i.getSize());
////            HTMLTag subDivTag = tagLogic.addTag(toolbarTag, "md-toolbar", null, "class", "md-toolbar-tools");
////            tagLogic.addTag(subDivTag, "h2", i.getText());
//        }
//    }
//
//    private void createRows(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget) throws Exception {
//        HTMLTag topTag = null;
//        if (widget.getContentHeight() == null) {
//            topTag = tagLogic.addTag(parentTag, "div", null);
//        } else {
//            topTag = tagLogic.addTag(parentTag, "div", null, "style", "overflow-y: scroll; height: " + widget.getContentHeight() + "px;");
//        }
//
//        HTMLTag repeatTag = tagLogic.addTag(topTag, "div", null, "ng-repeat", "i in " + widget.getDTO());
//        String styleString = "border-color:#999999;border-bottom-style: solid;border-width: 1px; height: 80px;background-color:{{rowBackgroundColor}}";
//        String onClick = GeneratorUtil.createJSTableRowClickMethodName(screenDefinition, widget) + "($index, $event)";
//        HTMLTag rowContentTag = tagLogic.addTag(repeatTag, "md-content", null, "layout-padding", "", "layout", "row", "layout-wrap", "", "ng-click", onClick, "style",
//                styleString, "ng-mouseover", "rowHover=true;rowBackgroundColor='#f3f3f3';", "ng-mouseleave", "rowHover=false;rowBackgroundColor='white';");
//        for (TableWidgetColumn column : widget.getColumns()) {
//            HTMLTag cellTag = tagLogic.addTag(rowContentTag, "div", null, "flex", "" + column.getSize());
//            for (TableWidgetItem tableItem : column.getTableItems()) {
//                addTableItem(project, cellTag, screenDefinition, widget, tableItem);
//            }
//        }
//    }
//
//    private void addTableItem(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, TableWidgetItem tableItem) throws Exception {
//        switch (tableItem.getType()) {
//        case BUTTON:
//            addButtonTableItem(project, parentTag, screenDefinition, widget, tableItem);
//            break;
//        case IMAGE_BUTTON:
//            addImageButtonTableItem(project, parentTag, screenDefinition, widget, tableItem);
//            break;
//        case LABEL:
//            addLabelTableItem(project, parentTag, widget, tableItem);
//            break;
//        case IMAGE:
//            addImageTableItem(project, parentTag, widget, tableItem);
//            break;
//        default:
//            throw new Exception("Unknonwn type: " + tableItem.getType());
//        }
//    }
//
//    private void addImageTableItem(Project project, HTMLTag parentTag, TableWidget widget, TableWidgetItem tableItem) throws Exception {
//        TableWidgetHTMLGeneratorLogic.createTableImageSourceTag(project, parentTag, tableItem.getImageSource());
//    }
//
//    private void addImageButtonTableItem(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, TableWidgetItem tableItem)
//            throws Exception {
//        HTMLTag tag = TableWidgetHTMLGeneratorLogic.createTableImageSourceTag(project, parentTag, tableItem.getImageSource());
//        tagLogic.setAttribute(tag, "ng-show", "rowHover");
//        tagLogic.setAttribute(tag, "ng-click", GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, widget, tableItem) + "($index, $event)");
//        tagLogic.setAttribute(tag, "style", "cursor: pointer;");
//    }
//
//    private void addLabelTableItem(Project project, HTMLTag parentTag, TableWidget widget, TableWidgetItem tableItem) throws Exception {
//        if (tableItem.getImageSource() != null) {
//            TableWidgetHTMLGeneratorLogic.createTableImageSourceTag(project, parentTag, tableItem.getImageSource());
//        }
//        tagLogic.addTag(parentTag, "span", "{{ i." + tableItem.getTextDTOField() + " }}", "style", "word-wrap: break-word;");
//    }
//
//
//
//    private void addButtonTableItem(Project project, HTMLTag parentTag, ScreenDefinition screenDefinition, TableWidget widget, TableWidgetItem tableItem)
//            throws Exception {
//        //        tagLogic.addTag(parentTag, "button", tableItem.getText(), "class", "fliesenUIButton", "ng-show", "rowHover", "ng-click",
//        //                GeneratorUtil.createJavascriptTableButtonClickMethodName(widget, tableItem) + "($index)");
//
//        if (tableItem.getImageSource() == null) {
//            tagLogic.addTag(parentTag, "button", tableItem.getText(), "class", "fliesenUIButton", "ng-show", "rowHover", "ng-click",
//                    GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, widget, tableItem) + "($index, $event)");
//        } else {
//            HTMLTag tag = tagLogic.addTag(parentTag, "div", null, "class", "fliesenUIButton", "ng-show", "rowHover", "ng-click",
//                    GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, widget, tableItem) + "($index, $event)", "style", "cursor: pointer;");
//            TableWidgetHTMLGeneratorLogic.createTableImageSourceTag(project, tag, tableItem.getImageSource());
//            tagLogic.addTag(tag, "span", tableItem.getText());
//        }
//
//    }
//
//}
