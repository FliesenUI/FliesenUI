package com.bright_side_it.fliesenui.generator.service;

import static com.bright_side_it.fliesenui.base.util.BaseUtil.in;

import java.io.File;
import java.util.Set;
import java.util.SortedMap;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.logic.JSCodeEditorCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JSDTOPreviewInitCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JSListChooserDialogCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JSOpenScreenFunctionsCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JSReplyFunctionCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JavaScreenDialogCreatorLogic;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginEvent;
import com.bright_side_it.fliesenui.project.logic.DefinitionResourceLogic;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectDefinition;
import com.bright_side_it.fliesenui.project.model.ProjectResource;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceFormat;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceType;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.EventHandler;
import com.bright_side_it.fliesenui.screendefinition.model.EventHandler.EventType;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener.EventListenType;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSource;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSourceContainer;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem.TableWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.Timer;

public class JSGeneratorService {
	public void generateJS(Project project, Set<ProjectResource> upToDateResources, File dir) throws Exception {
		DefinitionResourceLogic logic = new DefinitionResourceLogic();
		for (ScreenDefinition i : project.getScreenDefinitionsMap().values()) {
			if (!upToDateResources.contains(logic.create(ResourceType.SCREEN, ResourceFormat.XML, i.getID()))) {
				log("Screen " + i.getID() + " has changed and JS needs to be created");
				generateJSFileBeforeContent(i, project, dir);
				generateJSFileAfterContent(i, project.getProjectDefinition(), dir);
			} else {
				log("Screen " + i.getID() + " has NOT changed and JS DOES NOT need to be created");
			}
		}
	}

	private void log(String message) {
		System.out.println("JavascriptGeneratorService> " + message);
	}

	private void generateJSFileBeforeContent(ScreenDefinition screenDefinition, Project project, File dir) throws Exception {
		StringBuilder result = new StringBuilder();
		JSDTOPreviewInitCreatorLogic dtoPreviewInitCreatorLogic = new JSDTOPreviewInitCreatorLogic();

		String screenIDPrefix = GeneratorUtil.createScreenIDPrefix(screenDefinition);

		SortedMap<String, CellItem> widgetMap = GeneratorUtil.readWidgetIDMap(screenDefinition);

		result.append("\n");
		result.append("var " + screenIDPrefix + "controllerReady = false;\n");
		result.append("var " + screenIDPrefix + "logDebugBuffer = \"\";\n");
		result.append("var " + screenIDPrefix + "parameterDTO;\n");
		for (TableWidget tableWidget: BaseUtil.getAllTableWidgets(screenDefinition)){
			result.append("var " + GeneratorUtil.getJSTableFilterTopItemIndexVariableName(screenDefinition, tableWidget) + " = null;\n");
			result.append("var " + GeneratorUtil.getJSTableFilterFilteredIDsVariableName(screenDefinition, tableWidget) + " = null;\n");
		}
		result.append("\n");
		result.append(createTableFilterTextKeyDownMethods(screenDefinition));
		result.append(createTableFilters(screenDefinition));
		result.append("app.controller(\"" + GeneratorUtil.getAngularControllerName(screenDefinition) + "\", function($scope, $mdToast, $mdDialog, $http) {\n");
		result.append("    " + screenIDPrefix + "setInitialValues();\n");
		result.append("    $scope.http = $http;\n");
		result.append(createTableSelectedItemsObjects(screenDefinition, widgetMap, screenIDPrefix));
		
		// result.append(createInitialValuesCode(screenDefinition, project));
		result.append("\n");
		result.append(new JavaScreenDialogCreatorLogic().createShowMessageFunction(screenDefinition));
		result.append(new JavaScreenDialogCreatorLogic().createInputDialogFunction(screenIDPrefix));
		result.append(new JavaScreenDialogCreatorLogic().createConfirmDialogFunction(screenIDPrefix));
		result.append(new JSListChooserDialogCreatorLogic().createListChooserDialogMethods(screenDefinition, screenIDPrefix));
		result.append("\n");
		result.append(createAllButtonsClickMethods(screenDefinition, widgetMap, screenIDPrefix));
		result.append(createAllChangedMethods(screenDefinition, widgetMap, screenIDPrefix));
		result.append(createAllTableWidgetButtonsClickMethods(screenDefinition, widgetMap, screenIDPrefix));
		result.append(createTableRowClickMethods(screenDefinition, widgetMap, screenIDPrefix));
		result.append(createTableRowSelectedBoxClickMethods(screenDefinition, widgetMap, screenIDPrefix));
		result.append(createTableGetCheckedRowsMethods(screenDefinition, widgetMap, screenIDPrefix));
		result.append(createAllPluginEventMethods(project, screenDefinition, widgetMap, screenIDPrefix));
		result.append(createReadParameterDTOCode(screenDefinition, screenIDPrefix));
		result.append(createSelecBoxSelectedIDGetterAndSetterMethods(screenDefinition, screenIDPrefix));
		result.append(createSelectBoxChangeMethods(screenDefinition, widgetMap, screenIDPrefix));
		result.append(createOnLoadMethod(screenIDPrefix));
		result.append("}, 0);\n");
		result.append(dtoPreviewInitCreatorLogic.createCallInitPreviewCode(screenDefinition));
		result.append("\n");
		result.append("    $scope.imageAssetIDToName = " + GeneratorUtil.getJSCreateImageAssetIDToNameMapFunctionName(screenDefinition) + "();");
		result.append("\n");
		result.append(createFirstTimerCalls(screenDefinition, screenIDPrefix));
		result.append("    " + screenIDPrefix + "controllerReady = true;\n");
		result.append("\n");
		result.append("});\n");
		result.append("\n");
		result.append(createInitialValuesFunction(project, screenDefinition, screenIDPrefix));
		result.append(createImageAssetIDToNameMapFunction(project, screenDefinition));
		result.append(createLogDebugFunction(screenIDPrefix));
		result.append(createFromURLParameterFunction(screenIDPrefix));
		result.append(createExecuteOnLoadRequestFunction(screenDefinition, screenIDPrefix));
		result.append(createExecuteOnLoadWhenControllerIsReadyFunction(screenIDPrefix));
		result.append(createCreateRequestObjectFunction(screenDefinition, screenIDPrefix));
		result.append(createExecuteRequestFunction(screenDefinition, screenIDPrefix));
		result.append(createGettersAndSettersCode(screenDefinition));
		result.append(createSetMarkdownViewTextMethods(screenDefinition, screenIDPrefix));
		result.append(createDTOGettersAndSettersCode(screenDefinition));
		result.append(dtoPreviewInitCreatorLogic.createInitPreviewCodeForAllDTOTypes(project, screenDefinition));
		result.append(dtoPreviewInitCreatorLogic.createInitPreviewCodeForDeclaredDTOs(project, screenDefinition));
		result.append(createTimerFunctions(screenDefinition, widgetMap, screenIDPrefix));
		result.append(createFindItemWithIDFunction(screenDefinition, screenIDPrefix));
		result.append(createFileUploadFunctions(screenDefinition, widgetMap, screenIDPrefix));
		result.append(new JSOpenScreenFunctionsCreatorLogic().createOpenScreenMethod(screenDefinition, screenIDPrefix));
		result.append(new JSOpenScreenFunctionsCreatorLogic().createOpenScreenMultiPageApp(screenDefinition, screenIDPrefix));
		result.append(createOnBackPressedFunction(screenDefinition, widgetMap, screenIDPrefix));
		result.append(createUpdateViewsFunction(screenDefinition, widgetMap, screenIDPrefix));
		result.append(new JSReplyFunctionCreatorLogic().createProcessReplyFunction(screenDefinition, screenIDPrefix));

		File fileName = new File(dir, GeneratorUtil.getJSPart1Filename(screenDefinition));
		FileUtil.writeStringToFile(fileName, result.toString());

	}

	private StringBuilder createTableFilters(ScreenDefinition screenDefinition) {
		StringBuilder result = new StringBuilder();
		
		for (TableWidget tableWidget: BaseUtil.getAllTableWidgets(screenDefinition)){
			String filterName = GeneratorUtil.createJSTableFilterMethodName(screenDefinition, tableWidget);
			String topItemIndexName = GeneratorUtil.getJSTableFilterTopItemIndexVariableName(screenDefinition, tableWidget);
			String filteredIDsName = GeneratorUtil.getJSTableFilterFilteredIDsVariableName(screenDefinition, tableWidget);

			result.append("app.filter('" + filterName + "', function () {\n");
			result.append("    return function (dataArray, filterTextRaw) {\n");
			result.append("        if (!dataArray) return;\n");
			result.append("        /* when term is cleared, return full array*/\n");
			result.append("        if (!filterTextRaw) {\n");
			result.append("            " + topItemIndexName + " = null;\n");
			result.append("            " + filteredIDsName + " = null;\n");
			result.append("            return dataArray\n");
			result.append("        }\n");
			result.append("        var filterText = filterTextRaw.trim().toLowerCase();\n");
			result.append("        if (filterText.length == 0){\n");
			result.append("            " + topItemIndexName + " = null;\n");
			result.append("            " + filteredIDsName + " = null;\n");
			result.append("            return dataArray\n");
			result.append("        }\n");
			
			result.append("        var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
			
			
			writeTableColumnNames(result, screenDefinition, tableWidget, "        ");
			int numberOfColumns = BaseUtil.toEmptyCollectionIfNull(tableWidget.getColumns()).size();
			
			result.append("        var searchTermsMap = createSearchTermsMap(filterText, columnNames);\n");
//			result.append("        console.log(\"filter:  = columnNames: \" + JSON.stringify(columnNames));\n");
//			result.append("        console.log(\"filter:  = searchTermsMap: \" + JSON.stringify(searchTermsMap));\n");
			result.append("\n");
			result.append("        var result = dataArray.filter(function(item){\n");

			result.append("            existingItems = new Object();\n");
			for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex ++){
				TableWidgetColumn column = tableWidget.getColumns().get(columnIndex);
				result.append("            existingItems[\"c" + columnIndex + "\"] = ");
				boolean itemsWritten = false;
				boolean first = true;
				for (TableWidgetItem tableItem: BaseUtil.toEmptyCollectionIfNull(column.getTableItems())){
					String textAttribute = tableItem.getTextDTOField();
					if (textAttribute == null){
						textAttribute = tableItem.getTooltipDTOField();
					}
					if (textAttribute != null){
						itemsWritten = true;
						if (first){
							first = false;
						} else{
							result.append(" + \"\\n\"");
						}
						result.append("item." + textAttribute);
					}
				}
				if (!itemsWritten){
					result.append("\"\"");
				}
				result.append(";\n");
			}
			
//			result.append("            console.log(\"filter:  = existingItems: \" + JSON.stringify(existingItems));\n");
			result.append("\n");
			result.append("            if (filterBySearchTerms(existingItems, searchTermsMap)){\n");
//			result.append("                console.log(\"filter: match!\");\n");
			result.append("                return true;\n");
			result.append("            } else {\n");
//			result.append("                console.log(\"filter: no match\");\n");
			result.append("                return false;\n");
			result.append("            }\n");

			
			result.append("        });\n");
			String idPropertyName = tableWidget.getIDDTOField();
			result.append("        if ((result != null) && (result.length != 0)){\n");
			result.append("            " + topItemIndexName + " = findIndex(dataArray, result[0]." + idPropertyName + ", \"" + idPropertyName + "\");\n");
			result.append("            " + filteredIDsName + " = createListFromProperty(result, \"" + idPropertyName + "\");\n");
			result.append("        } else {\n");
			result.append("            " + topItemIndexName + " = null;\n");
			result.append("            " + idPropertyName + " = null;\n");
			result.append("        }\n");
			result.append("        return result;\n");
			result.append("    }\n");
			result.append("});\n");
		}
		
		return result;
	}

	private void writeTableColumnNames(StringBuilder result, ScreenDefinition screenDefinition, TableWidget tableWidget, String indent) {
		int numberOfColumns = BaseUtil.toEmptyCollectionIfNull(tableWidget.getColumns()).size();
		result.append(indent + "var columnNames = [");
		for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex ++){
			if (columnIndex > 0){
				result.append(indent + "                 , ");
			}
			result.append("scope." + GeneratorUtil.getJSTableColumnTextVariableName(screenDefinition, tableWidget, columnIndex));
			if (columnIndex < numberOfColumns - 1){
				result.append("\n");
			}
		}
		result.append("];\n");
	}
	
	private StringBuilder createTableFilterTextKeyDownMethods(ScreenDefinition screenDefinition) {
		StringBuilder result = new StringBuilder();
		
		for (TableWidget tableWidget: BaseUtil.getAllTableWidgets(screenDefinition)){
			result.append(GeneratorUtil.getJSTableFilterKeyDownMethodName(screenDefinition, tableWidget) + " = function (event) {\n");
			String topIndexVariableName = GeneratorUtil.getJSTableFilterTopItemIndexVariableName(screenDefinition, tableWidget);
			result.append("    if (event.keyCode == 13){\n");
			result.append("        var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
			result.append("        if (" + topIndexVariableName + " != null){\n");
//			result.append("            scope." + GeneratorUtil.createJSTableRowClickMethodName(screenDefinition, tableWidget) + "(" + topIndexVariableName + ", event);\n");
			result.append("            scope." + GeneratorUtil.createJSTableRowClickMethodName(screenDefinition, tableWidget) + "(0, event);\n");
			result.append("        }\n");
			result.append("        return;\n");
			result.append("    }\n");
			result.append("\n");  
			result.append("    if ((event.keyCode != 32) || (!event.ctrlKey)){\n");
			result.append("        return;\n");
			result.append("    }\n");
			result.append("    event.preventDefault();\n");
			result.append("    var inputField = document.getElementById(\"" + GeneratorUtil.getJSTableFilterTextElementID(screenDefinition, tableWidget) + "\");\n");
			result.append("    var currentText = inputField.value;\n");
			result.append("    var cursorPos = inputField.selectionStart;\n");
			result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
			writeTableColumnNames(result, screenDefinition, tableWidget, "    ");
			result.append("    try{\n");
			result.append("        var updatedTextAndCursor = updateTextAndCursorToNextColumn(currentText, cursorPos, columnNames);\n");
			result.append("\n");
			result.append("        inputField.value = updatedTextAndCursor.text;\n");
			result.append("        inputField.selectionStart = updatedTextAndCursor.cursorPos;\n");
			result.append("        inputField.selectionEnd = updatedTextAndCursor.cursorPos;\n");
			result.append("\n");
			result.append("        scope[\"" + GeneratorUtil.getJSTableFilterTextVariableName(screenDefinition, tableWidget) + "\"] = updatedTextAndCursor.text;\n");
			result.append("        setTimeout(function() {scope.$digest();}, 0);\n");
			result.append("    } catch (e){\n");
			result.append("        console.log(\"error: \" + e + \", \" + JSON.stringify(e));\n");
			result.append("    }\n");
			result.append("}\n");
		}
		return result;
	}

	private StringBuilder createFileUploadFunctions(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (BasicWidget i: BaseUtil.getAllBasicWidgets(screenDefinition)){
			if (i.getType() == BasicWidgetType.FILE_UPLOAD){
				String fileUploadEventDataFieldID = GeneratorUtil.createHTMLFileUploadEventDataFieldID(screenDefinition, i);
				String uploadFormID = GeneratorUtil.createHTMLUploadFormID(screenDefinition, i);
				result.append("var " + GeneratorUtil.createJSFileSelectedToUploadMethodName(screenDefinition, i) + " = function(selection){\n");
				result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
				result.append("    scope.$apply(function(){\n");
				result.append("        scope.selectedFile = selection;\n");
				result.append("        var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaFileUploadMethodName(i) + "\");\n");
				result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, i, widgetMap, "        "));
				result.append("        document.getElementById('" + fileUploadEventDataFieldID + "').value = JSON.stringify(request);\n");
				result.append("        document.getElementById('" + uploadFormID + "').submit();\n");
				result.append("    });\n");
				result.append("}\n");
				result.append("\n");
				result.append("var " + GeneratorUtil.createJSUploadIFrameLoadedMethodName(screenDefinition, i) + " = function(){\n");
				String firstIFrameLoadVariableName = "firstIFrameLoad" + BaseUtil.idToFirstCharUpperCase(i.getID());
				//: the IFrame is loaded on page load which is not an important event, but when the file upload has the iFrame as a "dummy target" 
				//: (otherwise an empty page would be loaded) so when the IFrame is loaded a second time (or third, ...) it means that the upload is finished
				result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
				result.append("    scope.$apply(function(){\n");
				result.append("        if (scope." + firstIFrameLoadVariableName + " == false){\n");
				result.append("            var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaFileUploadFinishedMethodName(i) + "\");\n");
				result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, i, widgetMap, "        "));
				result.append("            " + screenIDPrefix + "executeRequest(request);\n");
				result.append("        }\n");
				result.append("        scope." + firstIFrameLoadVariableName + " = false;\n");
				result.append("    });\n");
				result.append("}\n");
				result.append("\n");

				
				result.append("var " + GeneratorUtil.createJSJavaFXUploadFileMethodName(screenDefinition, i) + " = function(){\n");
				result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
				result.append("    scope.$apply(function(){\n");
				result.append("        var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaFileUploadMethodName(i) + "\");\n");
				result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, i, widgetMap, "        "));
				result.append("        var uploadRequest = request;\n");
				result.append("        request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaFileUploadFinishedMethodName(i) + "\");\n");
				result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, i, widgetMap, "        "));
				result.append("        var uploadFinishedRequest = request;\n");
				result.append("        webView.fileUpload(JSON.stringify(uploadRequest), JSON.stringify(uploadFinishedRequest));\n");
				result.append("    });\n");
				result.append("}\n");
				result.append("\n");
			}
		}
		return result;
	}

	private StringBuilder createFirstTimerCalls(ScreenDefinition screenDefinition, String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		for (Timer i : BaseUtil.getAllTimers(screenDefinition)) {
			result.append("    " + createTimerMethodName(screenIDPrefix, i) + "();\n");
		}
		return result;
	}

	private StringBuilder createTimerFunctions(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (Timer i : BaseUtil.getAllTimers(screenDefinition)) {
			result.append(createTimerFunction(screenDefinition, widgetMap, screenIDPrefix, i));
		}
		return result;
	}

	private StringBuilder createFindItemWithIDFunction(ScreenDefinition screenDefinition, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("var " + screenIDPrefix + GeneratorConstants.FIND_ITEM_WITH_ID_FUNCTION_NAME + " = function(scope, variableName, id){\n");
		result.append("    var list = null;\n");
		result.append("    var fieldName = null;\n");
		for (SelectBox i: BaseUtil.getAllSelectBoxes(screenDefinition)){
			result.append("    if (variableName == \"" + GeneratorUtil.getJSSelectBoxSelectedItemVariableName(screenDefinition, i) + "\"){\n");
			result.append("        list = scope." + i.getDTO() + ";\n");
			result.append("        fieldName = \"" + i.getIDDTOField() + "\";\n");
			result.append("    }\n");
		}
		result.append("    if (list == null){\n");
		result.append("        return null;\n");
		result.append("    }\n");
		result.append("    var length = list.length;\n");
		result.append("    for (var i = 0; i < length; i++) {\n");
		result.append("        if (list[i][fieldName] == id){\n");
		result.append("            return list[i]; \n");
		result.append("        }\n");
		result.append("    }\n");
		result.append("    return null;\n");
		result.append("}\n");
		return result;
	}

	private String createTimerMethodName(String screenIDPrefix, Timer timer) {
		return screenIDPrefix + "timer" + BaseUtil.idToFirstCharUpperCase(timer.getID());
	}

	private StringBuilder createTimerFunction(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix, Timer timer) throws Exception {
		StringBuilder result = new StringBuilder();
		String methodName = createTimerMethodName(screenIDPrefix, timer);
		result.append("var " + methodName + " = function(){\n");
		result.append("    setTimeout(function() {\n");
		result.append("        var $scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
		result.append("        if ((!singlePageApp) || ($scope.visible)){\n");
		result.append("            if ($scope." + GeneratorUtil.getJSTimerActiveVariableName(screenDefinition, timer) + ") {\n");
		result.append("                var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaTimerOccuredMethodName(timer) + "\");\n");
		result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, timer, widgetMap, "                "));
		result.append("               " + screenIDPrefix + "executeRequest(request);\n");
		result.append("            }\n");
		result.append("        }\n");
		result.append("        " + methodName + "();\n");
		result.append("    }, " + timer.getIntervalInMillis() + ");\n");
		result.append("}\n");
		return result;
	}
	
	private StringBuilder createSelecBoxSelectedIDGetterAndSetterMethods(ScreenDefinition screenDefinition, String screenIDPrefix){
		StringBuilder result = new StringBuilder();
		for (SelectBox selectBox : BaseUtil.getAllSelectBoxes(screenDefinition)) {
			String setterMethod = GeneratorUtil.createJSSelectBoxSetSelectedIDMethodName(screenDefinition, selectBox);
			String selectedItemVariable = GeneratorUtil.getJSSelectBoxSelectedItemVariableName(screenDefinition, selectBox);
			result.append("    " + setterMethod + " = function(selectedID){\n");
			result.append("        var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
			result.append("        scope." + selectedItemVariable + " = " + screenIDPrefix + GeneratorConstants.FIND_ITEM_WITH_ID_FUNCTION_NAME  + "(scope, \"" + selectedItemVariable + "\", selectedID)" + ";\n");
			result.append("    }\n");
			result.append("\n");
			
			String getterMethod = GeneratorUtil.createJSSelectBoxGetSelectedIDMethodName(screenDefinition, selectBox);
			result.append("    " + getterMethod + " = function(){\n");
			result.append("        var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
			result.append("        return (typeof scope." + selectedItemVariable + " == \"undefined\") ? null : scope." + selectedItemVariable + "." + selectBox.getIDDTOField() + ";\n");
			result.append("    }\n");
			result.append("\n");
		}
		return result;
	}

	private StringBuilder createReadParameterDTOCode(ScreenDefinition screenDefinition, String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		if (screenDefinition.getParameterDTOID() == null) {
			return result;
		}

		result.append("    if (!singlePageApp){\n");
		result.append("        var queryDict = {}\n");
		result.append("        location.search.substr(1).split(\"&\").forEach(function(item) {queryDict[item.split(\"=\")[0]] = item.split(\"=\")[1]})\n");
		result.append(
				"    " + screenIDPrefix + "parameterDTO = " + screenIDPrefix + "fromURLParameter(queryDict[\"" + GeneratorConstants.SCREEN_PARAMETER_DTO_GET_NAME + "\"]);\n");
		result.append("        if (typeof " + screenIDPrefix + "parameterDTO == \"undefined\"){\n");
		result.append("            " + screenIDPrefix + "parameterDTO = null;\n");
		result.append("        } else if (" + screenIDPrefix + "parameterDTO != null){\n");

		for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(screenDefinition)) {
			if (BaseUtil.isLinkedToDTO(widget, screenDefinition.getParameterDTOID())) {
				String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(widget.getTextDTOField());
				result.append("            $scope." + GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget) + " = " + screenIDPrefix + "parameterDTO" + "."
						+ fieldNameWithoutDTOName + ";\n");
			}
		}
		for (SelectBox selectBox : BaseUtil.getAllSelectBoxes(screenDefinition)) {
			if (BaseUtil.isLinkedToDTO(selectBox, screenDefinition.getParameterDTOID())) {
				String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(selectBox.getSelectedIDDTOField());
				result.append("            " + GeneratorUtil.createJSSelectBoxSetSelectedIDMethodName(screenDefinition, selectBox) + "(" + screenIDPrefix + "parameterDTO" + "."
						+ fieldNameWithoutDTOName + ");\n");
			}
		}

		result.append("        }\n");
		result.append("        $scope." + screenDefinition.getParameterDTOID() + " = " + screenIDPrefix + "parameterDTO;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createFromURLParameterFunction(String screenIDPrefix) {
		StringBuilder result = new StringBuilder();

		result.append("var " + screenIDPrefix + "fromURLParameter = function(urlParameter){\n");
		result.append("	if (typeof urlParameter != \"undefined\"){\n");
		result.append("		if (urlParameter == null){\n");
		result.append("			return null;\n");
		result.append("		}\n");
		result.append(
				"		var objectBase64String = urlParameter.replace(new RegExp(\"-\", 'g'), \"+\").replace(new RegExp(\"_\", 'g'), \"/\").replace(new RegExp(\"~\", 'g'), \"=\");\n");
		result.append("		var jsonString = atob(objectBase64String);\n");
		result.append("		return JSON.parse(jsonString);\n");
		result.append("	} else {\n");
		result.append("		return null;\n");
		result.append("	}\n");
		result.append("}\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createImageAssetIDToNameMapFunction(Project project, ScreenDefinition screenDefinition) {
		StringBuilder result = new StringBuilder();
		result.append("var " + GeneratorUtil.getJSCreateImageAssetIDToNameMapFunctionName(screenDefinition) + " = function(){\n");
		result.append("    result = new Object();\n");
		for (ImageAssetDefinition i : BaseUtil.toEmptyMapIfNull(project.getImageAssetDefinitionsMap()).values()) {
			result.append("    result[\"" + i.getID() + "\"] = \"" + i.getFilename() + "\";\n");
		}
		result.append("    return result;\n");
		result.append("}\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createLogDebugFunction(String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		result.append("var " + screenIDPrefix + "logDebug = function(message){\n");
		result.append("//	if (" + screenIDPrefix + "logDebugBuffer.length > 0){\n");
		result.append("//		console.log(\"buffered messages:\\n\" + " + screenIDPrefix + "logDebugBuffer + \"\\n}\");\n");
		result.append("//	}\n");
		result.append("//	" + screenIDPrefix + "logDebugBuffer = " + screenIDPrefix + "logDebugBuffer + message + \"\\n\";\n");
		result.append("    console.log(message);\n");
		result.append("}\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createExecuteOnLoadWhenControllerIsReadyFunction(String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		result.append(
				"/** called in JavaFX mode by web view when the screen object has been set. Wait until the Angular JS controller is ready and then call onLoad in the listener*/\n");
		result.append(screenIDPrefix + "executeOnLoadWhenControllerIsReady = function(){\n");
		result.append(
				"    " + screenIDPrefix + "logDebug(\"executeOnLoadWhenControllerIsReady: " + screenIDPrefix + "controllerReady = \" + " + screenIDPrefix + "controllerReady);\n");
		result.append("    if (" + screenIDPrefix + "controllerReady){\n");
		result.append("        " + screenIDPrefix + "logDebug(\"executeOnLoadWhenControllerIsReady: ready\");\n");
		result.append("        " + screenIDPrefix + "executeOnLoadRequest();\n");
		result.append("        " + screenIDPrefix + "logDebug(\"executeOnLoadWhenControllerIsReady: called onLoaded\");\n");
		result.append("    } else {\n");
		result.append("        " + screenIDPrefix + "logDebug(\"executeOnLoadWhenControllerIsReady: waiting\");\n");
		result.append("        setTimeout(" + screenIDPrefix + "executeOnLoadWhenControllerIsReady(), 200);\n");
		result.append("    }\n");
		result.append("}\n");
		return result;
	}

	private StringBuilder createExecuteOnLoadRequestFunction(ScreenDefinition screenDefinition, String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		result.append(screenIDPrefix + "executeOnLoadRequest = function(){\n");
		result.append("    var request = " + screenIDPrefix + "createRequest(\"" + GeneratorConstants.REQUEST_ACTION_ON_LOADED + "\");\n");
		result.append("    request.parameters[\"" + GeneratorConstants.CLIENT_PROPERTIES_PARAMETER_NAME + "\"] = " 
				+ GeneratorConstants.JS_GET_CLIENT_PROPETIES_FUNCTION_NAME + "();\n");
		if (screenDefinition.getParameterDTOID() != null) {
			result.append("    request.parameters[\"" + screenDefinition.getParameterDTOID() + "\"] = "
					+ getDTOGetterMethodName(screenDefinition, screenDefinition.getParameterDTOID()) + "();\n");
		}
		result.append("    " + screenIDPrefix + "executeRequest(request);\n");
		result.append("}\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createOnLoadMethod(String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		result.append("    setTimeout(function() {if ((\"WebBrowser\" == browserMode) && (!singlePageApp)){" + screenIDPrefix + "executeOnLoadRequest();}");
		return result;
	}

	private StringBuilder createSelectBoxChangeMethods(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (SelectBox i : BaseUtil.getAllSelectBoxes(screenDefinition)) {
			result.append("    $scope." + GeneratorUtil.getJSSelectBoxChangeMethodName(screenDefinition, i) + " = function (id){\n");
			result.append("        var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaSelectBoxChangedMethodName(i) + "\");\n");
			result.append("        request.parameters[\"" + GeneratorConstants.SELECT_BOX_ROW_ID_PARAMETER_NAME + "\"] = id;\n");
			result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, i, widgetMap, "        "));
			result.append("        " + screenIDPrefix + "executeRequest(request);\n");
			result.append("    }\n");
		}

		return result;
	}

	private StringBuilder createSetMarkdownViewTextMethods(ScreenDefinition screenDefinition, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (BasicWidget i : BaseUtil.getAllBasicWidgets(screenDefinition, BasicWidgetType.MARKDOWN_VIEW)) {
			result.append(GeneratorUtil.getJSSetMarkdownViewTextMethodName(screenDefinition, i) + " = function (text){\n");
			result.append("    var converter = new showdown.Converter();\n");
			result.append("    converter.setOption('tables', 'true');\n");
			result.append("    converter.setOption('literalMidWordUnderscores', 'true');\n");
			result.append("    var lineBreakText = text.replace(new RegExp(\"\\n\", \"g\"), \"\\n<br/>\");\n");
			result.append("    html = converter.makeHtml(lineBreakText);\n");
			result.append("    target = document.getElementById(\"" + GeneratorUtil.createHTMLMarkdownViewID(screenDefinition, i) + "\");\n");
			result.append("    target.innerHTML = html;\n");
			result.append("};\n");
			result.append("\n");
		}
		return result;
	}
	
	private StringBuilder createUpdateViewsFunction(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		result.append(screenIDPrefix + "updateViews = function(){\n");
		for (CodeEditorWidget i: BaseUtil.getAllCodeEditorWidgets(screenDefinition)){
			String variableName = GeneratorUtil.createCodeWidgetVariableName(screenDefinition, i);
			//: CodeMirror code editors require a call of refresh after set text
			result.append("    setTimeout(function() {" + variableName + ".refresh();}, 0);\n");
		}
		result.append("};\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createOnBackPressedFunction(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
			result.append(screenIDPrefix + "backButtonPressed = function(){\n");
			result.append("    console.log(\"Screen " + screenDefinition.getID() + ": Back pressed.\");\n");
			for (EventListener i: BaseUtil.getAllEventListenersOfContainer(screenDefinition, EventListenType.BACK_ACTION)){
				result.append("    var $scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
				result.append("    var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaBackActionMethodName() + "\");\n");
				result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, i, widgetMap, "    "));
				result.append("    " + screenIDPrefix + "executeRequest(request);\n");
			}
			result.append("};\n");
			result.append("\n");
		return result;
	}
	
	private StringBuilder createCreateRequestObjectFunction(ScreenDefinition screenDefinition, String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		result.append(screenIDPrefix + "createRequest = function(actionName){\n");
		result.append("    request = new Object();\n");
		result.append("    request.action = actionName;\n");
		result.append("    request.currentLanguage = currentLanguage;\n");
		result.append("    request.screenID = \"" + screenDefinition.getID() + "\";\n");
		result.append("    request.parameters = new Object();\n");
		result.append("    return request;\n");
		result.append("};\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createExecuteRequestFunction(ScreenDefinition screenDefinition, String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		result.append(screenIDPrefix + "executeRequest = function(request){\n");
		result.append("    //later: add server call and process request here...\n");

		result.append("    if (browserMode == 'JavaFX'){\n");
		result.append("        screenManager.onRequest(JSON.stringify(request), null, null);\n");
		result.append("    } else {\n");
		result.append("        var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
		result.append("        scope.http.post(\"/request\", request).then(function (response) {\n");
		result.append("            if ((response.data != null) && (typeof response.data != \"undefined\")){\n");
		result.append("                " + screenIDPrefix + "processReply(JSON.stringify(response.data));\n");
		result.append("            }\n");
		result.append("        });\n");
		result.append("    }\n");
		result.append("};\n");
		result.append("\n");
		result.append("\n");
		return result;
	}

	private void generateJSFileAfterContent(ScreenDefinition screenDefinition, ProjectDefinition projectDefinition, File dir) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append(new JSCodeEditorCreatorLogic().createAllCodeEditorsCode(screenDefinition));

		File fileName = new File(dir, screenDefinition.getID() + "_part2" + GeneratorConstants.JAVASCRIPT_FILE_ENDING);
		FileUtil.writeStringToFile(fileName, result.toString());
	}

	private StringBuilder createAllButtonsClickMethods(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (CellItem widget : BaseUtil.getAllBasicWidgets(screenDefinition)) {
			if (widget instanceof BasicWidget) {
				BasicWidget basicWidget = (BasicWidget) widget;
				if (in(basicWidget.getType(), BasicWidgetType.BUTTON, BasicWidgetType.IMAGE_BUTTON)) {
					result.append(createButtonClickMethod(screenDefinition, basicWidget, widgetMap, screenIDPrefix));
				}
			}
		}
		
		for (TableWidget tableWidget: BaseUtil.getAllTableWidgets(screenDefinition)) {
			result.append(createTableFilterInfoButtonClickMethod(screenDefinition, tableWidget, screenIDPrefix));
		}
		
		return result;
	}

	private StringBuilder createAllChangedMethods(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (CellItem widget : BaseUtil.getAllBasicWidgets(screenDefinition)) {
			if (widget instanceof BasicWidget) {
				BasicWidget basicWidget = (BasicWidget) widget;
				if (in(basicWidget.getType(), BasicWidgetType.CHECKBOX, BasicWidgetType.SWITCH)) {
					result.append(createOnChangedMethod(screenDefinition, basicWidget, widgetMap, screenIDPrefix));
				}
			}
		}
		return result;
	}
	
	private StringBuilder createAllPluginEventMethods(Project project, ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix)
			throws Exception {
		StringBuilder result = new StringBuilder();

		for (PluginInstance pluginInstance : BaseUtil.getAllPluginInstances(screenDefinition)) {
			PluginDefinition pluginDefinition = project.getPluginDefinitionsMap().get(pluginInstance.getPluginType());
			for (PluginEvent event : BaseUtil.toEmptyMapIfNull(pluginDefinition.getEvents()).values()) {
				result.append(createPluginEventMethod(screenDefinition, pluginInstance, event, widgetMap, screenIDPrefix));
			}
		}

		return result;
	}

	private StringBuilder createPluginEventMethod(ScreenDefinition screenDefinition, PluginInstance pluginInstance, PluginEvent event, SortedMap<String, CellItem> widgetMap,
			String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    $scope." + GeneratorUtil.createJSPluginEventMethodName(screenDefinition, pluginInstance, event) + " = function () {\n");

		result.append("        var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaPluginEventMethodName(pluginInstance, event) + "\");\n");
		result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, pluginInstance, widgetMap, "        "));
		result.append("        " + screenIDPrefix + "executeRequest(request);\n");
		result.append("    }\n");
		result.append("\n");

		return result;
	}
	private StringBuilder createOnChangedMethod(ScreenDefinition screenDefinition, BasicWidget widget, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    $scope." + GeneratorUtil.createJSOnChangedMethodName(screenDefinition, widget) + " = function (selected) {\n");
		result.append("        var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaOnChangedMethodName(widget) + "\");\n");
		result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, widget, widgetMap, "        "));
		result.append("        request.parameters[\"" + GeneratorConstants.SELECTED_ID_PARAMETER_NAME + "\"] = selected;\n");
		result.append("        " + screenIDPrefix + "executeRequest(request);\n");
		result.append(createEventHandlerCode(screenDefinition, widget, screenIDPrefix));
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createButtonClickMethod(ScreenDefinition screenDefinition, BasicWidget widget, SortedMap<String, CellItem> widgetMap, String screenIDPrefix)
			throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    $scope." + GeneratorUtil.createJSButtonClickMethodName(screenDefinition, widget) + " = function () {\n");
		result.append("        var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJSButtonClickMethodName(widget) + "\");\n");
		result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, widget, widgetMap, "        "));
		result.append("        " + screenIDPrefix + "executeRequest(request);\n");
		result.append(createEventHandlerCode(screenDefinition, widget, screenIDPrefix));
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createTableFilterInfoButtonClickMethod(ScreenDefinition screenDefinition, TableWidget widget, String screenIDPrefix)
			throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    $scope." + GeneratorUtil.getJSTableFilterOnInfoButtonClickMethodName(screenDefinition, widget) + " = function () {\n");
		
		result.append("        var message = new Object();\n");
		result.append("        message.typeID = 101;\n");
		result.append("        message.title = \"Quick Filter Info\";\n");
		result.append("        message.text = \"Type any text to filter. Add preciding '-' to filter out.  Add column name and ':' like 'myColumn:x' to filter in a column.  \";\n");
		result.append("        message.text += \"Use quotes (\\\") for spaces in column name or texts.  Press enter to select(click) first item in table.  \";\n");
		result.append("        message.text += \"Press ctrl+space for auto-complete of column names or next column name.\";\n");
		result.append("\n");
		result.append("        $scope.showMessage(message);\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}
	
	private StringBuilder createEventHandlerCode(ScreenDefinition screenDefinition, BasicWidget widget, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (EventHandler i : BaseUtil.toEmptyCollectionIfNull(widget.getEventHandlers())) {
			if (i.getEventType() == EventType.CLICK) {
				if (i.getURLToOpen() != null) {
					result.append("        if (" + GeneratorConstants.BROWSER_MODE_JS_VARIABLE + " == '" + GeneratorUtil.getBrowserModeJSName(BrowserType.JAVA_FX) + "') {\n");
					result.append("            webView.openURL(\"" + i.getURLToOpen() + "\", " + i.isOpenURLInNewWindow() + ");\n");
					result.append("        } else {\n");
					String target = "_self";
					if (i.isOpenURLInNewWindow()) {
						target = "_blank";
					}
					result.append("            window.open(\"" + i.getURLToOpen() + "\", \"" + target + "\");\n");
					result.append("        }\n");
				} else if (i.getScreenToOpen() != null) {
					String parameter = "null";
					if (i.getOpenScreenParameterDTO() != null) {
						parameter = "JSON.parse(" + getDTOGetterMethodName(screenDefinition, i.getOpenScreenParameterDTO()) + "()" + ")";
					}
					result.append("        " + screenIDPrefix + "openScreen(\"" + i.getScreenToOpen() + "\", " + parameter + ");\n");
				}
			}
		}
		return result;
	}


//	private StringBuilder createEventParametersString(ScreenDefinition screenDefinition, EventParameterContainer eventParameterContainer, SortedMap<String, CellItem> widgetMap,
//			boolean alwaysStartWithComma) throws Exception {
//		StringBuilder result = new StringBuilder();
//		if (eventParameterContainer.getEventParameters() == null) {
//			return result;
//		}
//
//		for (EventParameter i : eventParameterContainer.getEventParameters()) {
//			if ((result.length() > 0) || (alwaysStartWithComma)) {
//				result.append(", ");
//			}
//
//			if (i.getDTOID() != null) {
//				result.append(getDTOGetterMethodName(screenDefinition, i.getDTOID()) + "()");
//			} else if (i.getPluginVariableName() != null) {
//				String javascripVariableName = GeneratorUtil.getJSPluginVariableName(screenDefinition, i.getPluginInstanceID(), i.getPluginVariableName());
//				result.append("$scope." + javascripVariableName);
//			} else {
//				CellItem referencedWidget = widgetMap.get(i.getWidgetID());
//				if (referencedWidget instanceof BasicWidget) {
//					result.append("$scope.");
//					if (i.getWidgetProperty() == WidgetProperty.TEXT) {
//						result.append(GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, i.getWidgetID()));
//					} else if (i.getWidgetProperty() == WidgetProperty.SELECTED) {
//						result.append(GeneratorUtil.getJSWidgetSelectedVariableName(screenDefinition, i.getWidgetID()));
//					} else {
//						throw new Exception("Unkonwn widget property: " + i.getWidgetProperty());
//					}
//				} else if (referencedWidget instanceof SelectBox) {
//					if (i.getWidgetProperty() == WidgetProperty.SELECTED_ID) {
//						SelectBox selectBox = (SelectBox) referencedWidget;
//						String variable = GeneratorUtil.getJSSelectBoxSelectedItemVariableName(screenDefinition, selectBox);
//						result.append("(typeof $scope." + variable + " == \"undefined\") ? null : $scope." + variable + "." + selectBox.getIDDTOField());
//						//
//						//
//						// SelectBox selectBox = (SelectBox)referencedWidget;
//						// result.append(GeneratorUtil.getJSSelectBoxSelectedItemVariableName(screenDefinition,
//						// selectBox) + "." + selectBox.getIDDTOField());
//					} else {
//						throw new Exception("Unkonwn select box property: " + i.getWidgetProperty());
//					}
//				} else if (referencedWidget instanceof CodeEditorWidget) {
//					String codeEditorObjectVariableName = GeneratorUtil.createCodeWidgetVariableName(screenDefinition, (CodeEditorWidget) referencedWidget);
//					if (i.getWidgetProperty() == WidgetProperty.TEXT) {
//						result.append(codeEditorObjectVariableName + ".getValue()");
//					} else if (i.getWidgetProperty() == WidgetProperty.LINE) {
//						result.append(codeEditorObjectVariableName + ".getCursor().line");
//					} else if (i.getWidgetProperty() == WidgetProperty.POS_IN_LINE) {
//						result.append(codeEditorObjectVariableName + ".getCursor().ch");
//					} else {
//						throw new Exception("Unkonwn code editor widget property: " + i.getWidgetProperty());
//					}
//				} else {
//					throw new Exception("Unexpected widget type: " + referencedWidget.getClass().getSimpleName());
//				}
//			}
//
//		}
//		return result;
//	}

	private StringBuilder createAllTableWidgetButtonsClickMethods(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix)
			throws Exception {
		StringBuilder result = new StringBuilder();
		for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
			for (TableWidgetColumn tableColumn : tableWidget.getColumns()) {
				for (TableWidgetItem tableItem : tableColumn.getTableItems()) {
					if (in(tableItem.getType(), TableWidgetType.BUTTON, TableWidgetType.IMAGE_BUTTON)) {
						result.append(createTableWidgetButtonsClickMethod(screenDefinition, tableItem, tableWidget, widgetMap, screenIDPrefix));
					}
				}
			}
		}
		return result;
	}

	private StringBuilder createTableRowClickMethods(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
			result.append(createTableWidgetRowClickMethod(screenDefinition, tableWidget, widgetMap, screenIDPrefix));
		}
		return result;
	}
	
	private StringBuilder createTableGetCheckedRowsMethods(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
			if (tableWidget.isRowCheckboxes()){
				result.append(createTableWidgetGetCheckedAndFilteredIDsMethod(screenDefinition, tableWidget, widgetMap, screenIDPrefix));
			}
		}
		return result;
	}

	private StringBuilder createTableRowSelectedBoxClickMethods(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
			if (tableWidget.isRowCheckboxes()){
				result.append(createTableWidgetRowSelectBoxClickMethod(screenDefinition, tableWidget, widgetMap, screenIDPrefix));
			}
		}
		return result;
	}
	
	private StringBuilder createTableSelectedItemsObjects(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
			if (tableWidget.isRowCheckboxes()){
				String selectedItemsVariableName = GeneratorUtil.createJSTableRowCheckedIDVariableName(screenDefinition, tableWidget);
				result.append("    $scope." + selectedItemsVariableName + " = new Object();\n");
			}
		}
		return result;
	}
	
	private StringBuilder createTableWidgetButtonsClickMethod(ScreenDefinition screenDefinition, TableWidgetItem tableItem, TableWidget tableWidget,
			SortedMap<String, CellItem> widgetMap, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    $scope." + GeneratorUtil.createJSTableButtonClickMethodName(screenDefinition, tableWidget, tableItem) + " = function (index, event) {\n");
		result.append("        event.preventDefault();\n"); // : execute if the "default was not prevented" which means here that a table button has been clicked
		result.append("        var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaTableButtonClickMethodName(tableWidget, tableItem) + "\");\n");
		result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, tableItem, widgetMap, "        "));
		String filteredIDsName = GeneratorUtil.getJSTableFilterFilteredIDsVariableName(screenDefinition, tableWidget);
		result.append("        if (" + filteredIDsName + " != null){\n");
		result.append("            request.parameters[\"" + GeneratorConstants.TABLE_BUTTON_CLICK_ROW_ID_PARAMETER_NAME + "\"] = " + filteredIDsName + "[index];\n");
		result.append("        } else {\n");
		result.append("            request.parameters[\"" + GeneratorConstants.TABLE_BUTTON_CLICK_ROW_ID_PARAMETER_NAME + "\"] = $scope." + tableWidget.getDTO() + "[index]."
				+ tableWidget.getIDDTOField() + ";\n");
		result.append("        }\n");
		result.append("        " + screenIDPrefix + "executeRequest(request);\n");
		result.append("    }\n");
		return result;
	}

	private StringBuilder createTableWidgetRowClickMethod(ScreenDefinition screenDefinition, TableWidget tableWidget, SortedMap<String, CellItem> widgetMap, String screenIDPrefix)
			throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    $scope." + GeneratorUtil.createJSTableRowClickMethodName(screenDefinition, tableWidget) + " = function (index, event) {\n");
		result.append("        if (!event.defaultPrevented) {\n"); // : execute if the "default was not prevented" which means here that a table button has been clicked
		result.append("            var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaTableRowClickMethodName(tableWidget) + "\");\n");
		result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, tableWidget, widgetMap, "            "));
		String filteredIDsName = GeneratorUtil.getJSTableFilterFilteredIDsVariableName(screenDefinition, tableWidget);
		result.append("            if (" + filteredIDsName + " != null){\n");
		result.append("                request.parameters[\"" + GeneratorConstants.TABLE_BUTTON_CLICK_ROW_ID_PARAMETER_NAME + "\"] = " + filteredIDsName + "[index];\n");
		result.append("            } else {\n");
		result.append("                request.parameters[\"" + GeneratorConstants.TABLE_BUTTON_CLICK_ROW_ID_PARAMETER_NAME + "\"] = $scope." + tableWidget.getDTO() + "[index]."
				+ tableWidget.getIDDTOField() + ";\n");
		result.append("            }\n");
		result.append("            " + screenIDPrefix + "executeRequest(request);\n");
		result.append("        }\n");
		result.append("    }\n");
		return result;
	}
	
	private StringBuilder createTableWidgetGetCheckedAndFilteredIDsMethod(ScreenDefinition screenDefinition, TableWidget tableWidget, SortedMap<String, CellItem> widgetMap, String screenIDPrefix)
		throws Exception {

		StringBuilder result = new StringBuilder();
		result.append("    " + GeneratorUtil.createJSTableGetCheckedAndFileredIDsMethodName(screenDefinition, tableWidget) + " = function () {\n");
		result.append("        var result = [];\n");
		result.append("        var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
		String selectedItemsVariableName = GeneratorUtil.createJSTableRowCheckedIDVariableName(screenDefinition, tableWidget);
		result.append("        if (typeof scope." + selectedItemsVariableName + " == \"undefined\"){\n");
		result.append("            return result;\n");
		result.append("        }\n");
		result.append("        if (scope." + selectedItemsVariableName + ".size == 0){\n");
		result.append("            return result;\n");
		result.append("        }\n");
		result.append("        var visibleIDs = null;\n");
		String filteredVariableName = GeneratorUtil.getJSTableFilterFilteredIDsVariableName(screenDefinition, tableWidget);
		result.append("        if (" + filteredVariableName + " != null){\n");
		result.append("            visibleIDs = " + filteredVariableName + ";\n");
		result.append("        } else {\n");
		result.append("            visibleIDs = createListFromProperty(scope." + tableWidget.getDTO() + ", \"" + tableWidget.getIDDTOField() + "\");\n");
		result.append("        }\n");
		result.append("        for (i in visibleIDs){\n");
		result.append("            var itemID = visibleIDs[i];\n");
		result.append("            var selected = scope." + selectedItemsVariableName + "[itemID];\n");
		result.append("            if (selected){\n");
		result.append("                result.push(itemID);\n");
		result.append("            }\n");
		result.append("        }\n");
		result.append("        return result;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createTableWidgetRowSelectBoxClickMethod(ScreenDefinition screenDefinition, TableWidget tableWidget, SortedMap<String, CellItem> widgetMap, String screenIDPrefix)
			throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    $scope." + GeneratorUtil.createJSTableRowSelectBoxClickMethodName(screenDefinition, tableWidget) + " = function (index, event) {\n");
		result.append("        event.preventDefault();\n"); // : execute if the "default was not prevented" which means here that a table button has been clicked
		result.append("        var rowID = null\n");
		String filteredIDsName = GeneratorUtil.getJSTableFilterFilteredIDsVariableName(screenDefinition, tableWidget);
		result.append("        if (" + filteredIDsName + " != null){\n");
		result.append("            rowID = " + filteredIDsName + "[index];\n");
		result.append("        } else {\n");
		result.append("            rowID = $scope." + tableWidget.getDTO() + "[index]." + tableWidget.getIDDTOField() + ";\n");
		result.append("        }\n");
		String selectedItemsVariableName = GeneratorUtil.createJSTableRowCheckedIDVariableName(screenDefinition, tableWidget);
		result.append("        if ($scope." + selectedItemsVariableName + "[rowID] == true){\n");
		result.append("            $scope." + selectedItemsVariableName + "[rowID] = false;\n");
		result.append("        } else {\n");
		result.append("            $scope." + selectedItemsVariableName + "[rowID] = true;\n");
		result.append("        } \n");
		result.append("    }\n");
		return result;
	}
	
	
	
	
	private StringBuilder createDTOGettersAndSettersCode(ScreenDefinition screenDefinition) {
		StringBuilder result = new StringBuilder();

		if (screenDefinition.getDTODeclarations() == null) {
			return result;
		}

		for (DTODeclaration i : screenDefinition.getDTODeclarations().values()) {
			result.append(createDTOSetter(screenDefinition, i));
			result.append(createDTOGetter(screenDefinition, i));
		}
		result.append("\n");
		return result;
	}

	private String getDTOGetterMethodName(ScreenDefinition screenDefinition, String dtoID) {
		return GeneratorUtil.getJSDTOGetterMethodName(screenDefinition, dtoID);
	}

	private StringBuilder createDTOGetter(ScreenDefinition screenDefinition, DTODeclaration dtoDeclaration) {
		StringBuilder result = new StringBuilder();
		String getterMethodName = getDTOGetterMethodName(screenDefinition, dtoDeclaration.getID());

		result.append(getterMethodName + " = function(){\n");
		// result.append(" var result = new Object();\n");
		result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
		result.append("    var result = new Object();\n");
		result.append("    if (scope." + dtoDeclaration.getID() + " != null){\n");
		result.append("        result = JSON.parse(JSON.stringify(scope." + dtoDeclaration.getID() + "));\n"); // :
																												// use
																												// last
																												// sent
																												// DTO
																												// as
																												// base
		result.append("    }\n");
		result.append(createFieldToDTOStatements(dtoDeclaration.getID(), screenDefinition));
		result.append("    return JSON.stringify(result);\n");
		result.append("};\n");
		return result;
	}

	private StringBuilder createFieldToDTOStatements(String dtoID, ScreenDefinition screenDefinition) {
		StringBuilder result = new StringBuilder();
		for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(screenDefinition)) {
			if (BaseUtil.isLinkedToDTO(widget, dtoID)) {
				String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(widget.getTextDTOField());
				result.append("    result." + fieldNameWithoutDTOName + " = scope." + GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget) + ";\n");
			}
		}
		for (SelectBox selectBox : BaseUtil.getAllSelectBoxes(screenDefinition)) {
			if (BaseUtil.isLinkedToDTO(selectBox, dtoID)) {
				String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(selectBox.getSelectedIDDTOField());
				result.append("    result." + fieldNameWithoutDTOName + " = " + GeneratorUtil.createJSSelectBoxGetSelectedIDMethodName(screenDefinition, selectBox) + "();\n");
			}
		}

		return result;
	}

	private StringBuilder createDTOSetter(ScreenDefinition screenDefinition, DTODeclaration dtoDeclaration) {
		StringBuilder result = new StringBuilder();
		// String setterMethodName =
		// BaseUtil.buildIDWithPrefix(dtoDeclaration.getID() +
		// GeneratorConstants.JAVASCRIPT_DTO_SETTER_SUFFIX, "set");
		String setterMethodName = GeneratorUtil.getJSDTOSetterMethodName(screenDefinition, dtoDeclaration);
		result.append(setterMethodName + " = function(jsonString){\n");
		result.append("    var paramObject = JSON.parse(jsonString);\n");
		result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
		result.append("    scope.$apply(function(){\n");
		result.append("        scope." + dtoDeclaration.getID() + " = paramObject;\n");
		result.append(createDTOToFieldStatements(dtoDeclaration.getID(), screenDefinition));
		result.append("    });\n");
		result.append("};\n");
		return result;
	}

	private StringBuilder createDTOToFieldStatements(String dtoID, ScreenDefinition screenDefinition) {
		StringBuilder result = new StringBuilder();
		for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(screenDefinition)) {
			if (BaseUtil.isLinkedToDTO(widget, dtoID)) {
				String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(widget.getTextDTOField());
				result.append("        scope." + GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget) + " = paramObject" + "." + fieldNameWithoutDTOName + ";\n");
			}
		}
		for (SelectBox selectBox : BaseUtil.getAllSelectBoxes(screenDefinition)) {
			if (BaseUtil.isLinkedToDTO(selectBox, dtoID)) {
				String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(selectBox.getSelectedIDDTOField());
				result.append("        " + GeneratorUtil.createJSSelectBoxSetSelectedIDMethodName(screenDefinition, selectBox) + "(paramObject" + "." + fieldNameWithoutDTOName + ");\n"); 
			}
		}

		return result;
	}

	private StringBuilder createGettersAndSettersCode(ScreenDefinition screenDefinition) throws Exception {
		StringBuilder result = new StringBuilder();
		for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(screenDefinition)) {
			if (BaseUtil.in(widget.getType(), BasicWidgetType.TEXT_FIELD, BasicWidgetType.TEXT_AREA, BasicWidgetType.LABEL)) {
				if (widget.getID() != null) {
					// String setterMethodName =
					// BaseUtil.buildIDWithPrefix(widget.getID() +
					// GeneratorConstants.JAVASCRIPT_WIDGET_TEXT_SETTER_SUFFIX,
					// "set");
					String setterMethodName = GeneratorUtil.getJSWidgetTextSetterMethodName(screenDefinition, widget);
					result.append(setterMethodName + " = function(text){\n");
					result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
					result.append("    scope.$apply(function(){\n");
					result.append("        scope." + GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget) + " = text;\n");
					result.append("    });\n");
					result.append("};\n");

					// String getterMethodName =
					// BaseUtil.buildIDWithPrefix(widget.getID() +
					// GeneratorConstants.JAVASCRIPT_WIDGET_TEXT_SETTER_SUFFIX,
					// "get");
					String getterMethodName = GeneratorUtil.getJSWidgetTextGetterMethodName(screenDefinition, widget);
					result.append(getterMethodName + " = function(){\n");
					result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
					result.append("    return scope." + GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget) + ";\n");
					result.append("};\n");
					result.append("\n\n");
				}
			}

		}
		result.append("\n");
		return result;
	}

	private StringBuilder createInitialValuesFunction(Project project, ScreenDefinition screenDefinition, String screenIDPrefix) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("var " + screenIDPrefix + "setInitialValues = function(){\n");
		result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");
		String linePrefix = "    scope.";
		for (BasicWidget widget : BaseUtil.getAllBasicWidgets(screenDefinition)) {
			if (widget.getText() != null) {
				result.append(linePrefix + GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget) + " = " + GeneratorUtil.getJSTextOrResource(widget.getText()) + ";\n");
			}
			if (widget.getLabelText() != null) {
				result.append(linePrefix + GeneratorUtil.getJSWidgetLabelVariableName(screenDefinition, widget) + " = " + GeneratorUtil.getJSTextOrResource(widget.getLabelText()) + ";\n");
			}
			if (widget.getType() == BasicWidgetType.PROGRESS_BAR) {
				result.append(linePrefix + GeneratorUtil.getJSWidgetProgressBarModeVariableName(screenDefinition, widget) + " = \"query\";\n");
			}
			if (BaseUtil.in(widget.getType(), BasicWidgetType.CHECKBOX, BasicWidgetType.SWITCH)){
				result.append(linePrefix + GeneratorUtil.getJSWidgetSelectedVariableName(screenDefinition, widget) + " = false;\n");
			}
			if (BaseUtil.in(widget.getType(), BasicWidgetType.MARKDOWN_VIEW, BasicWidgetType.TEXT_AREA, BasicWidgetType.TEXT_FIELD)){
				result.append(linePrefix + GeneratorUtil.getJSWidgetBackgroundColorVariableName(screenDefinition, widget) + " = \"\";\n");
			}
			
			result.append(linePrefix + GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget) + " = " + widget.isVisible() + ";\n");
		}

		for (ImageSourceContainer i : BaseUtil.getAllImageSourceContainers(screenDefinition)) {
			ImageSource imageSource = i.getImageSource();
			if (imageSource != null) {
				String variable = GeneratorUtil.getJSImageSourceVariableName(screenDefinition, i);
				String value = "";
				if (imageSource.getImageAssetID() != null) {
					ImageAssetDefinition imageAssetDefinitnion = project.getImageAssetDefinitionsMap().get(imageSource.getImageAssetID());
					value = imageAssetDefinitnion.getFilename();
				} else if (imageSource.getImageStreamID() != null) {
					value = imageSource.getImageStreamID();
				} else if (imageSource.getImageURL() != null) {
					value = imageSource.getImageURL();
				} else if ((imageSource.getImageAssetIDDTOField() == null) && (imageSource.getImageStreamIDDTOField() == null) && (imageSource.getImageURLDTOField() == null)) {
					throw new Exception("not implemented, yet");
				}
				result.append(linePrefix + variable + " = \"" + value + "\";\n");

			}
		}

		for (Timer i : BaseUtil.getAllTimers(screenDefinition)) {
			result.append("    scope." + GeneratorUtil.getJSTimerActiveVariableName(screenDefinition, i) + " = " + i.isActive() + ";\n");
		}

		for (LayoutCell i : BaseUtil.getAllLayoutCells(screenDefinition)) {
			if (i.getID() != null) {
				result.append(linePrefix + GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, i.getID()) + " = " + i.isVisible() + ";\n");
			}
		}

		for (LayoutBar i : BaseUtil.getAllLayoutBars(screenDefinition)) {
			if (i.getID() != null) {
				result.append(linePrefix + GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, i.getID()) + " = " + i.isVisible() + ";\n");
			}
		}
		
		for (LayoutContainer i : BaseUtil.getAllLayoutContainers(screenDefinition)) {
			if (i.getID() != null) {
				result.append(linePrefix + GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, i.getID()) + " = " + i.isVisible() + ";\n");
			}
		}
		
		for (TableWidget table: BaseUtil.getAllTableWidgets(screenDefinition)){
			int tableCoumnIndex = 0;
			for (TableWidgetColumn column : table.getColumns()) {
				result.append(linePrefix + GeneratorUtil.getJSTableColumnTextVariableName(screenDefinition, table, tableCoumnIndex) + " = " + GeneratorUtil.getJSTextOrResource(column.getText()) + ";\n");
				tableCoumnIndex ++;
				for (TableWidgetItem tableWidgetItem : column.getTableItems()){
					if (BaseUtil.in(tableWidgetItem.getType(), TableWidgetType.BUTTON, TableWidgetType.IMAGE_BUTTON)){
						result.append(linePrefix + GeneratorUtil.getJSTableWidgetTextVariableName(screenDefinition, table, tableWidgetItem) + " = " + GeneratorUtil.getJSTextOrResource(tableWidgetItem.getText()) + ";\n");					
					}
				}
			}
		}
		
		result.append("}\n");
		result.append("\n");

		return result;
	}

}
