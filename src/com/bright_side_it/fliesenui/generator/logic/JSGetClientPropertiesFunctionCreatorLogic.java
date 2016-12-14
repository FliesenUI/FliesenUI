package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;

public class JSGetClientPropertiesFunctionCreatorLogic {
	public StringBuilder createGetClientPropertiesFunction(BrowserType browserType){
		StringBuilder result = new StringBuilder();
		result.append("var " + GeneratorConstants.JS_GET_CLIENT_PROPETIES_FUNCTION_NAME + " = function(){\n");
		result.append("    var result = new Object();\n");
		result.append("    \n");
		result.append("    try{\n");
		result.append("        result.userAgent = navigator.userAgent;\n");
		result.append("        if (\"WebBrowser\" == browserMode){\n");
		result.append("            result.screenAvailableWidthInPixels = window.screen.availWidth;\n");
		result.append("            result.screenAvailableHeightInPixels = window.screen.availHeight;\n");
		result.append("            result.screenWidthInPixels = window.screen.width;\n");
		result.append("            result.screenHeightInPixels= window.screen.height;\n");
		result.append("        } else {\n");
		result.append("            result.screenAvailableWidthInPixels = webView.getAvailWidth();\n");
		result.append("            result.screenAvailableHeightInPixels = webView.getAvailHeight();\n");
		result.append("            result.screenWidthInPixels = webView.getScreenWidth();\n");
		result.append("            result.screenHeightInPixels= webView.getScreenHeight();\n");
		result.append("        }\n");
		result.append("        result.windowInnerWidthInPixels = window.innerWidth;\n");
		result.append("        result.windowInnerHeightInPixels= window.innerHeight;\n");
		result.append("        \n");
		result.append("        result.pixelHeightPerCM = document.getElementById(\"box1cm\").offsetHeight;\n");
		result.append("        result.pixelWidthPerCM = document.getElementById(\"box1cm\").offsetWidth;\n");
		result.append("        result.pixelHeightPerInch = result.pixelHeightPerCM  * 0.393701;\n");
		result.append("        result.pixelWidthPerInch = result.pixelWidthPerCM * 0.393701;\n");
		result.append("        \n");
		result.append("        result.screenWidthInCM = result.screenWidthInPixels / result.pixelWidthPerCM;\n");
		result.append("        result.screenHeightInCM = result.screenHeightInPixels / result.pixelHeightPerCM;\n");
		result.append("        result.screenWidthInInch = result.screenWidthInCM * 0.393701;\n");
		result.append("        result.screenHeightInInch = result.screenHeightInCM * 0.393701;\n");
		result.append("        \n");
		result.append("        var a = result.screenWidthInInch;\n");
		result.append("        var b = result.screenHeightInInch;\n");
		result.append("        \n");
		result.append("        result.screenDiagonalInInch = Math.sqrt((a * a) + (b * b));\n");
		result.append("        \n");
		result.append("        // console.log(JSON.stringify(result));\n");
		result.append("        \n");
		result.append("    } catch (err){\n");
		result.append("        result.errorMessage = err.message + \";\" + JSON.stringify(err);\n");
		result.append("    }\n");
		result.append("    return JSON.stringify(result);\n");
		result.append("}\n");
		
		return result;
	}
}
