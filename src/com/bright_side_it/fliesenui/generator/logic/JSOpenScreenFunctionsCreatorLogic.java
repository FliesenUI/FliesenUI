package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;

public class JSOpenScreenFunctionsCreatorLogic {
	public StringBuilder createOpenScreenMethod(ScreenDefinition screenDefinition, String screenIDPrefix) {
		StringBuilder result = new StringBuilder();
		result.append("var " + screenIDPrefix + "openScreen = function(screenToOpen, openParameter){\n");
        result.append("    if (singlePageApp) {\n");
        result.append("        setTimeout(function() {openScreenSinglePageApp(screenToOpen, openParameter);}, 0);\n");
        result.append("    } else {\n");
        result.append("        " + screenIDPrefix + "openScreenMultiPageApp(screenToOpen, openParameter);\n");
        result.append("    }\n");
		result.append("}\n");
		return result;
	}

	
	public StringBuilder createOpenScreenMultiPageApp(ScreenDefinition screenDefinition, String screenIDPrefix) {
        StringBuilder result = new StringBuilder();
        result.append("var " + screenIDPrefix + "openScreenMultiPageApp = function(screenToOpen, openParameter){\n");
		result.append("    var screenToOpenURL = screenToOpen + \"_\" + htmlFileSuffix + \".html\";\n");
		result.append("    if (typeof openParameter != \"undefined\") {\n");
		result.append("        var jsonString = JSON.stringify(openParameter);\n");
		result.append("        var base64String = btoa(jsonString);\n");
		result.append(
				"            var urlParameterString = base64String.replace(new RegExp(\"\\\\+\", 'g'), \"-\").replace(new RegExp(\"/\", 'g'), \"_\").replace(new RegExp(\"=\", 'g'), \"~\");\n");
		result.append("            var screenToOpenURL = screenToOpenURL + \"?" + GeneratorConstants.SCREEN_PARAMETER_DTO_GET_NAME + "=\" + urlParameterString;\n");
		result.append("            console.log(\"screenToOpenURL via screenToOpen and openParameter = >>\" + screenToOpenURL + \"<<\");\n");
		result.append("    }\n");
		result.append("    window.open(screenToOpenURL, \"_self\");\n");

		result.append("}\n");
		result.append("\n");
		return result;
    }

    public StringBuilder createOpenScreenSinglePageApp(Project project) {
    	StringBuilder result = new StringBuilder();
    	result.append("var openScreenSinglePageApp = function(screenToOpen, openParameter){\n");
    	result.append("    //: close all screens\n");
    	for (ScreenDefinition i: project.getScreenDefinitionsMap().values()){
			result.append("    scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(i) + "')).scope();\n");
			result.append("    scope.$apply(function(){\n");
			result.append("        scope.visible = false;\n");
			result.append("    });\n");
    	}
    	result.append("\n");
    	result.append("    //: set initial values\n");
    	for (ScreenDefinition i: project.getScreenDefinitionsMap().values()){
    		String screenIDPrefix = GeneratorUtil.createScreenIDPrefix(i);
    		result.append("    if (screenToOpen == '" + i.getID() + "') {\n");
    		result.append("        " + screenIDPrefix + "setInitialValues();\n");
    		result.append("    }\n");
    	}
    	result.append("\n");
    	result.append("    //: set open parameters (if applicable)\n");
		result.append("    if ((typeof openParameter != \"undefined\") && (openParameter != null)){\n");
		for (ScreenDefinition i: project.getScreenDefinitionsMap().values()){
			if (i.getParameterDTOID() != null) {
	    		result.append("        if (screenToOpen == '" + i.getID() + "') {\n");
	    		result.append("            scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(i) + "')).scope();\n");
		        result.append("            if ((typeof openParameter != \"undefined\") && (openParameter != null)) {\n");
		        result.append("                scope." + i.getParameterDTOID() + " = openParameter;\n");
		
		        for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(i)) {
		            if (BaseUtil.isLinkedToDTO(widget, i.getParameterDTOID())) {
		                String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(widget.getTextDTOField());
		                result.append("                scope." + GeneratorUtil.getJSWidgetTextVariableName(i, widget) + " = openParameter"
		                        + "." + fieldNameWithoutDTOName + ";\n");
		            }
		        }

		        for (SelectBox selectBox : BaseUtil.getAllSelectBoxes(i)) {
		        	if (BaseUtil.isLinkedToDTO(selectBox, i.getParameterDTOID())) {
		        		String fieldNameWithoutDTOName = GeneratorUtil.getFieldNameWithoutDTO(selectBox.getSelectedIDDTOField());
		        		result.append("                " + GeneratorUtil.createJSSelectBoxSetSelectedIDMethodName(i, selectBox) + "(openParameter"
		        				+ "." + fieldNameWithoutDTOName + ");\n");
		        	}
		        }

		        result.append("            } else {\n");
		        result.append("                scope." + i.getParameterDTOID() + " = null;\n");
		        result.append("            }\n");
		        result.append("        }\n");
			}
		}
		result.append("    }\n");
		result.append("\n");
		result.append("    //: open screen\n");
		result.append("    setTimeout(function() {\n");
		result.append("        scope.$digest();\n");
		for (ScreenDefinition i: project.getScreenDefinitionsMap().values()){
			String screenIDPrefix = GeneratorUtil.createScreenIDPrefix(i);
			result.append("        if (screenToOpen == '" + i.getID() + "') {\n");
			result.append("            scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(i) + "')).scope();\n");
			result.append("            scope.$apply(function(){\n");
			result.append("                scope.visible = true;\n");
			result.append("            });\n");
			result.append("            currentScreenID = \"" + i.getID() + "\";\n");
			result.append("            " + screenIDPrefix + "executeOnLoadRequest();\n");
			result.append("        }\n");
    	}
		result.append("    }, 0);\n");
		result.append("\n");
		result.append("}\n");

    	return result;
    }

}
