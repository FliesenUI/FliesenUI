package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;

public class JSReplyFunctionCreatorLogic {
	
    public StringBuilder createProcessReplyFunction(ScreenDefinition screenDefinition, String screenIDPrefix) throws Exception {
        StringBuilder result = new StringBuilder();
        result.append(screenIDPrefix + "processReply = function(jsonString){\n");
        result.append("    var reply = JSON.parse(jsonString);\n");
		result.append("    if (typeof reply.languageToSet != \"undefined\") {\n");
		result.append("        currentLanguage = reply.languageToSet;\n");
		result.append("    }\n");
		result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
        result.append("    for (i = 0, len = reply.dtosToSet.length; i < len; i++) {\n");
        result.append("        var dtoValue = reply.dtoValues[reply.dtosToSet[i]];\n");
        result.append("        scope[reply.dtosToSet[i]] = dtoValue;\n");
        if (screenDefinition.getDTODeclarations() != null) {
            for (DTODeclaration dtoDeclaration : screenDefinition.getDTODeclarations().values()) {
                result.append(createDTOToFieldStatementsForProcessReply(dtoDeclaration.getID(), screenDefinition));
            }
        }
        result.append(createLogicToResetCheckTableRowsIfTheTableDTOWasUpdated(screenDefinition));
        
        
        result.append("    }\n");
        //: the next part also ready the DTOs and uses the variable names, but it is an iteration after the DTOs itself have been set
        //: this is required to set the selected value of a select box, since first the items (id and label) need to be set by a DTO
        //: and then another DTO may contain the ID of the selected item which can then be found in the select box items
        result.append("    for (i = 0, len = reply.dtosToSet.length; i < len; i++) {\n");
        result.append("        var dtoValue = reply.dtoValues[reply.dtosToSet[i]];\n");
        if (screenDefinition.getDTODeclarations() != null) {
            for (DTODeclaration dtoDeclaration : screenDefinition.getDTODeclarations().values()) {
            	result.append(createDTOToFieldStatementsForProcessReplyAfterDTOsHaveBeenSet(dtoDeclaration.getID(), screenDefinition));
            }
        }
        result.append("    }\n");
        
        result.append("    for (i = 0, len = reply.objectsToSetValue.length; i < len; i++) {\n");
        result.append("        var valueToSet = reply.objectSetValueValues[reply.objectsToSetValue[i]];\n");
        result.append("        this[reply.objectsToSetValue[i]].setValue(valueToSet);\n");
        result.append("    }\n");
        result.append("    for (i = 0, len = reply.variablesToSet.length; i < len; i++) { \n");
        result.append("        scope[reply.variablesToSet[i]] = reply.variableValues[reply.variablesToSet[i]];\n");
        for (BasicWidget i: BaseUtil.getAllBasicWidgets(screenDefinition)){
        	if (i.getType() == BasicWidgetType.TEXT_AREA){
        		if ((i.getScrollToBottom() != null) && (i.getScrollToBottom().booleanValue())){
        			result.append("        if (reply.variablesToSet[i] == '" + GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, i) + "'){\n");
        			result.append("            var textarea = document.getElementById('" + GeneratorUtil.getJSWidgetHTMLID(screenDefinition, i) + "');\n");
        			result.append("            textarea.scrollTop = textarea.scrollHeight;\n");
        			result.append("        }\n");
        		}
        	}
        }
        
        result.append("    }\n");
        result.append("    for (key in reply.selectBoxSelectedIDs) {\n");
        result.append("        scope[key] = " + screenIDPrefix + GeneratorConstants.FIND_ITEM_WITH_ID_FUNCTION_NAME  + "(scope, key, reply.selectBoxSelectedIDs[key])" + ";\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    for (key in reply.markdownViewTexts) {\n");
        for (BasicWidget i: BaseUtil.getAllBasicWidgets(screenDefinition, BasicWidgetType.MARKDOWN_VIEW)){
        	result.append("        if (key == \"" + i.getID() + "\"){\n");
        	result.append("            " + GeneratorUtil.getJSSetMarkdownViewTextMethodName(screenDefinition, i) + "(reply.markdownViewTexts[key]);\n");
        	result.append("        }\n");
        }
        result.append("    }\n");
        result.append("\n");
        result.append("    if (reply.message != null){\n");
        result.append("        scope.showMessage(reply.message);\n");
        result.append("    }\n");
        result.append("    for (textEditorName in reply.textHighlighting){\n");
        result.append("        var lastCursorPos = this[textEditorName].getCursor(); //: clear highlighting\n");
        result.append("        this[textEditorName].setValue(this[textEditorName].getValue()); //: clear highlighting\n");
        result.append("        this[textEditorName].setCursor(lastCursorPos); //: clear highlighting\n");
        result.append("        for (highlightingItemKey in reply.textHighlighting[textEditorName]){\n");
        result.append("            highlightingItem = reply.textHighlighting[textEditorName][highlightingItemKey];\n");
        result.append(
                "            this[textEditorName].markText({line:highlightingItem.startLine,ch:highlightingItem.startPosInLine},{line:highlightingItem.endLine,ch:highlightingItem.endPosInLine},{className:highlightingItem.style})\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("    for (textEditorName in reply.cursorPosValues){\n");
        result.append("        cursorPosItem = reply.cursorPosValues[textEditorName];\n");
        result.append("        this[textEditorName].setCursor(cursorPosItem.line, cursorPosItem.posInLine);\n");
        result.append("        this[textEditorName].focus();\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    for (textEditorName in reply.contextAssists){\n");
        result.append("        contextAssistItem = reply.contextAssists[textEditorName];\n");
        result.append("        console.log(\"context assist: contextAssistItem = \" + JSON.stringify(contextAssistItem));\n");
        result.append("        var hintValue = {}\n");
        result.append("        hintValue.from = {line:contextAssistItem.replaceFrom.line,ch:contextAssistItem.replaceFrom.posInLine};\n");
        result.append("        hintValue.to = {line:contextAssistItem.replaceTo.line,ch:contextAssistItem.replaceTo.posInLine};\n");
        result.append("        hintValue.selectedHint = contextAssistItem.selectedItem;\n");
        result.append("        hintValue.list = [];\n");
        result.append("        var arrayLength = contextAssistItem.choices.length;\n");
        result.append("        for (var i = 0; i < arrayLength; i++) {\n");
        result.append("            var choice = contextAssistItem.choices[i];\n");
        result.append("            hintValue.list[i] = {displayText: choice.label, text: choice.text}\n");
        result.append("        }\n");
        result.append("        console.log(\"context assist: options = \" + JSON.stringify(hintValue));\n");
        result.append("        this[textEditorName].showHint({hint:function(){return hintValue;}});\n");
        result.append("    }\n");
        result.append("\n");
		result.append("    for (key in reply.tableCheckedRowIDs){\n");
		result.append("        scope[key] = new Object();\n");
		result.append("        for (i in reply.tableCheckedRowIDs[key]){\n");
		result.append("            scope[key][reply.tableCheckedRowIDs[key][i]] = true;\n");
		result.append("        }\n");
		result.append("    }\n");
        result.append("\n");
        result.append("    var screenToOpen = reply.screenToOpen;\n");
        result.append("    if (typeof screenToOpen != \"undefined\") {\n");
        result.append("        " + screenIDPrefix + "openScreen(screenToOpen, reply.openParameter);\n");
//        
//        result.append("        if (singlePageApp) {\n");
//        result.append("            setTimeout(function() {openScreenSinglePageApp(screenToOpen, reply.openParameter);}, 0);\n");
//        result.append("        } else {\n");
//        result.append("            " + screenIDPrefix + "openScreenMultiPageApp(screenToOpen, reply.openParameter);\n");
//        result.append("        }\n");
        result.append("    }\n");
        
        result.append("    var urlToOpen = reply.urlToOpen;\n");
        result.append("    if (typeof urlToOpen != \"undefined\") {\n");
        result.append("        if (" + GeneratorConstants.BROWSER_MODE_JS_VARIABLE + " == '" + GeneratorUtil.getBrowserModeJSName(BrowserType.JAVA_FX) + "') {\n");
		result.append("            webView.openURL(urlToOpen, reply.openURLInNewWindow);\n");
		result.append("        } else {\n");
		result.append("            var target = \"_self\";\n");
		result.append("            if (reply.openURLInNewWindow){\n");
		result.append("                target = \"_blank\";\n");
		result.append("            }\n");
		result.append("            window.open(urlToOpen, target);\n");
		result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
        
        result.append("    var downloadFileStreamID = reply.downloadFileStreamID;\n");
        result.append("    if (typeof downloadFileStreamID != \"undefined\") {\n");
        result.append("        if (" + GeneratorConstants.BROWSER_MODE_JS_VARIABLE + " == '" + GeneratorUtil.getBrowserModeJSName(BrowserType.JAVA_FX) + "') {\n");
		result.append("            webView.downloadFile(downloadFileStreamID);\n");
		result.append("        } else {\n");
		result.append("            window.open(\"" + GeneratorConstants.DOWNLOAD_FILE_STREAM_PATH_PREFIX + "\" + downloadFileStreamID, \"_blank\");\n");
		result.append("        }\n");
        result.append("    }\n");

        
        result.append("\n");

        result.append(createDialogMethods());


        //        var hintValue = JSON.parse(jsonObject);
        //        codeEditorCodeEditor.showHint({hint:function(){return hintValue;}});

        result.append("    if (typeof reply.listChooserParameters != \"undefined\") {\n");
		result.append("        scope.showListChooser(reply.listChooserParameters);\n");
		result.append("    }\n");
		//: required in onLoad. Then digest needs to be called. Otherwise the fields that have been set will not be shown. 
		//: However digest may not be called in the other cases where ng-onclick is used and digest is called automatically 
		//: which causes conflicts. Therefore the timer-method is used which may always call digest.
		//: updateViews() is needed for CodeMirror editors. The method refresh() needs to be called after setText. 
		//: It is also needed for text areas with scrollToBottom=true which should be executed after the digest 
        result.append("    setTimeout(function() {scope.$digest();" + screenIDPrefix + "updateViews();}, 0);\n"); 
        result.append("\n");
        result.append("};\n");
        return result;
    }

    private StringBuilder createLogicToResetCheckTableRowsIfTheTableDTOWasUpdated(ScreenDefinition screenDefinition) {
        StringBuilder result = new StringBuilder();
        for (TableWidget i: BaseUtil.getAllTableWidgets(screenDefinition)){
        	if (i.isRowCheckboxes()){
        		result.append("        if (reply.dtosToSet[i] == '" + getTopDTOItem(i.getDTO()) + "'){\n");
        		String variableName = GeneratorUtil.createJSTableRowCheckedIDVariableName(screenDefinition, i);
        		result.append("            scope." + variableName + " = new Object();\n");
        		result.append("        }\n");
        	}
        }
        
		return result;
	}

	private String getTopDTOItem(String dtoName) {
		int pos = dtoName.indexOf(".");
		if (pos < 0){
			return dtoName;
		}
		return dtoName.substring(0, pos);
	}

	private StringBuilder createDialogMethods() {
        StringBuilder result = new StringBuilder();

        result.append("    var inputDialogParameters = reply.inputDialogParameters;\n");
        result.append("    if (typeof inputDialogParameters != \"undefined\") {\n");
        result.append(
                "        scope.showInputDialog(inputDialogParameters.referenceID, inputDialogParameters.callbackData, inputDialogParameters.title, inputDialogParameters.textContent, inputDialogParameters.label, inputDialogParameters.initialValueText, inputDialogParameters.okText, inputDialogParameters.cancelText);\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    var confirmDialogParameters = reply.confirmDialogParameters;\n");
        result.append("    if (typeof confirmDialogParameters != \"undefined\") {\n");
        result.append(
                "        scope.showConfirm(confirmDialogParameters.referenceID, confirmDialogParameters.callbackData, confirmDialogParameters.title, confirmDialogParameters.textContent, confirmDialogParameters.okText, confirmDialogParameters.cancelText);\n");
        result.append("    }\n");
        return result;
    }


    private StringBuilder createDTOToFieldStatementsForProcessReplyAfterDTOsHaveBeenSet(String dtoID, ScreenDefinition screenDefinition) {
        StringBuilder result = new StringBuilder();
        result.append("        if (reply.dtosToSet[i] == '" + dtoID + "'){\n");
        for (SelectBox selectBox: BaseUtil.getAllSelectBoxes(screenDefinition)){
        	if (BaseUtil.isLinkedToDTO(selectBox, dtoID)){
                String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(selectBox.getSelectedIDDTOField());
                result.append("            " + GeneratorUtil.createJSSelectBoxSetSelectedIDMethodName(screenDefinition, selectBox) + "(dtoValue" + "."
                        + fieldNameWithoutDTOName + ");\n");
        	}
        }
        result.append("        }\n");
        return result;
    }

    private StringBuilder createDTOToFieldStatementsForProcessReply(String dtoID, ScreenDefinition screenDefinition) {
        StringBuilder result = new StringBuilder();
        result.append("        if (reply.dtosToSet[i] == '" + dtoID + "'){\n");
        for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(screenDefinition)) {
            if (BaseUtil.isLinkedToDTO(widget, dtoID)) {
            	String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(widget.getTextDTOField());
            	if (widget.getType() == BasicWidgetType.MARKDOWN_VIEW){
            		result.append("            " + GeneratorUtil.getJSSetMarkdownViewTextMethodName(screenDefinition, widget) + "(dtoValue" + "."
            				+ fieldNameWithoutDTOName + ");\n");
            	}
        		result.append("            scope." + GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget) + " = dtoValue" + "."
        				+ fieldNameWithoutDTOName + ";\n");
            }
        }
        result.append("        }\n");
        return result;
    }


}
