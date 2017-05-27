package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;

public class JSStringResourceFunctionsCreatorLogic {
	public StringBuilder createStringResourceFunctions(){
		StringBuilder result = new StringBuilder();
		result.append(GeneratorConstants.JS_CURRENT_LANGUAGE_VAR_NAME + " = \"" + BaseConstants.DEFAULT_LANGUAGE_ID + "\";\n");
		result.append("\n");
		result.append(GeneratorConstants.JS_GET_TEXT_FUNCTION_NAME + " = function(stringID){\n");
		result.append("    var result = STRINGS[currentLanguage + \":\" + stringID];\n");
		result.append("    if (result === undefined){\n");
		result.append("        result = STRINGS[\"" + BaseConstants.DEFAULT_LANGUAGE_ID + ":\" + stringID];\n");
		result.append("    }\n");
		result.append("    return result;\n");
		result.append("}\n");
		return result;
	}
}
