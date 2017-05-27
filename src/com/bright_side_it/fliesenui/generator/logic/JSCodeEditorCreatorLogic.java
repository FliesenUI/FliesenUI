package com.bright_side_it.fliesenui.generator.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget.CodeEditorWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener.EventListenType;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JSCodeEditorCreatorLogic {
    public StringBuilder createAllCodeEditorsCode(ScreenDefinition screenDefinition) throws Exception {
        StringBuilder result = new StringBuilder();
        for (CodeEditorWidget i : BaseUtil.getAllCodeEditorWidgets(screenDefinition)) {
            result.append(createCodeEditorCode(screenDefinition, i));
        }
        return result;
    }

    private StringBuilder createCodeEditorCode(ScreenDefinition screenDefinition, CodeEditorWidget codeEditor) throws Exception {
        StringBuilder result = new StringBuilder();
        String variableName = GeneratorUtil.createCodeWidgetVariableName(screenDefinition, codeEditor);
        String screenIDPrefix = GeneratorUtil.createScreenIDPrefix(screenDefinition);

        result.append("    var " + variableName + " = CodeMirror.fromTextArea(document.getElementById(\"" + screenIDPrefix + codeEditor.getID() + "\"), {\n");
        if (codeEditor.getType() == CodeEditorWidgetType.CODE_EDITOR){
            result.append("        mode: \"text/xml\",\n");
            result.append("        reindentOnLoad: true,\n");
            result.append("        matchBrackets: true,\n");
            result.append("        autoCloseTags: true,\n");
            result.append("        matchTags: true,\n");
            result.append("        lineNumbers: true,\n");
        } else if (codeEditor.getType() == CodeEditorWidgetType.TEXT_EDITOR){
            result.append("        mode: \"text/text\",\n");
        } else {
        	throw new Exception("Unknown code editor type: " + codeEditor.getType());
        }
        result.append("        styleActiveLine: true,\n");
        result.append("        indentUnit: 4\n");
//        result.append("        extraKeys: {\n");
//        if (codeEditor.getType() == CodeEditorWidgetType.CODE_EDITOR){
//	        result.append("            \"Ctrl-Space\": function(cm) {\n");
//	        result.append("                var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.getContextAssistListenerMethodName(codeEditor) + "\");\n");
//	        result.append("                request.parameters[\"editorText\"] = " + variableName + ".getValue();\n");
//	        result.append("                request.parameters[\"line\"] = " + variableName + ".getCursor().line;\n");
//	        result.append("                request.parameters[\"posInLine\"] = " + variableName + ".getCursor().ch;\n");
//	        result.append("                " + screenIDPrefix + "executeRequest(request);\n");
//	        result.append("            },\n");
//        }
//        result.append("            \"Ctrl-S\": function(cm) {\n");
//        result.append("                var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.getSaveListenerMethodName(codeEditor) + "\");\n");
//        result.append("                request.parameters[\"editorText\"] = " + variableName + ".getValue();\n");
//        result.append("                request.parameters[\"line\"] = " + variableName + ".getCursor().line;\n");
//        result.append("                request.parameters[\"posInLine\"] = " + variableName + ".getCursor().ch;\n");
//        result.append("                " + screenIDPrefix + "executeRequest(request);\n");
//        result.append("            }\n");
//        result.append("        }\n");
        result.append("    });\n");
        result.append("    " + variableName + ".setSize(\"100%\",\"100%\");\n");
//        result.append("    " + variableName + ".on(\"cursorActivity\", function(cm){\n");
//        result.append("        console.log(\"cursorActivity. Cursor: Line = \" + " + variableName + ".getCursor().line + \", char = \" + " + variableName
//                + ".getCursor().ch);\n");
//        result.append("        });\n");
//        result.append("    " + variableName + ".on(\"change\", function(){\n");
//        result.append("        console.log(\"onChange\");\n");
//        result.append("    });\n");
        result.append("\n");
        result.append("\n");

        
        result.append(createKeyListenerCode(screenDefinition, codeEditor, EventListenType.KEY_PRESS));
        result.append(createKeyListenerCode(screenDefinition, codeEditor, EventListenType.KEY_DOWN));

        result.append("\n");
        result.append("\n");

        return result;
    }

	private StringBuilder createKeyListenerCode(ScreenDefinition screenDefinition, CodeEditorWidget codeEditor, EventListenType type) throws Exception {
		StringBuilder result = new StringBuilder();
		String variableName = GeneratorUtil.createCodeWidgetVariableName(screenDefinition, codeEditor);
		String screenIDPrefix = GeneratorUtil.createScreenIDPrefix(screenDefinition);
		List<EventListener> keyPressListeners = BaseUtil.getAllEventListenersOfContainer(codeEditor, type);
		if (keyPressListeners.isEmpty()){
			return result;
		}
		
		String eventName = "";
		if (type == EventListenType.KEY_PRESS){
			eventName = "keypress";
		} else if (type == EventListenType.KEY_DOWN){
			eventName = "keydown";
		} else {
			throw new Exception("Unexpected type: " + type);
		}
		
		SortedMap<String, CellItem> widgetMap = GeneratorUtil.readWidgetIDMap(screenDefinition);
		result.append("    " + variableName + ".on(\"" + eventName + "\", function(cm, event){\n");
		result.append("        keyChar = String.fromCharCode(event.keyCode);\n");
		result.append("        keyCode = event.keyCode;\n");
		
		result.append("        if (typeof keyCode == \"undefined\"){\n");
		result.append("             keyCode = 0;\n");
		result.append("        }\n");
		
        for (EventListener i: keyPressListeners){
        	if (i.getKeyCode() != null){
            	result.append("        if ((keyCode == " + i.getKeyCode() + ")" + createMetaKeyCheck(i) + "){\n");
            	result.append("            console.log(\"" + type + ": matching key code: >>\" + keyCode + \"<<\");\n");
        	} else if (i.getKeyChar() != null){
            	String keyCharString = "" + i.getKeyChar();
            	if (keyCharString.equals("\"")){
            		keyCharString = "\\\"";
            	}
            	result.append("        if ((keyChar == \"" + keyCharString + "\")" + createMetaKeyCheck(i) + "){\n");
            	result.append("            console.log(\"" + type + ": matching char: >>\" + keyChar + \"<<\");\n");
        	} else {
        		throw new Exception("Key event must have either a key code or a key char");
        	}
        	
        	if ((Boolean.TRUE.equals(i.getControl())) || (Boolean.TRUE.equals(i.getAlt()))){
        		result.append("            event.preventDefault();\n");
        	}
        	
            result.append("            var keyModifier = new Object();\n");
            result.append("            keyModifier.eventType = \"" + eventName + "\";\n");
            result.append("            keyModifier.shift = event.shiftKey;\n");
            result.append("            keyModifier.alt = event.altKey;\n");
            result.append("            keyModifier.control = event.ctrlKey;\n");
            result.append("            keyModifier.meta = event.metaKey;\n");
            result.append("            var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.createJavaKeyEventActionMethodName(codeEditor) + "\");\n");
            result.append("            request.parameters[\"" + GeneratorConstants.EDITOR_TEXT_PARAMETER_NAME + "\"] = " + variableName + ".getValue();\n");
            result.append("            request.parameters[\"" + GeneratorConstants.LINE_PARAMETER_NAME + "\"] = " + variableName + ".getCursor().line;\n");
            result.append("            request.parameters[\"" + GeneratorConstants.POS_IN_LINE_PARAMETER_NAME + "\"] = " + variableName + ".getCursor().ch;\n");
            result.append("            request.parameters[\"" + GeneratorConstants.KEY_CHAR_PARAMETER_NAME + "\"] = keyChar;\n");
            result.append("            request.parameters[\"" + GeneratorConstants.KEY_CODE_PARAMETER_NAME + "\"] = keyCode;\n");
            result.append("            request.parameters[\"" + GeneratorConstants.KEY_MODIFIER_PARAMETER_NAME + "\"] = JSON.stringify(keyModifier);\n");
    		result.append(GeneratorUtil.createRequestObjectEventParametersMap(screenDefinition, i, widgetMap, "            "));
            result.append("            " + screenIDPrefix + "executeRequest(request);\n");
        	result.append("        }\n");
        }
        result.append("    });\n");
        
        
//        ööö
//        
//        mainScreen_codeEditorCodeEditor.on("keypress", function(cm, event){
//        	console.log("keypress = " + event + ", JSON = " + JSON.stringify(event));
//        	keyChar = String.fromCharCode(event.keyCode);
//        	console.log("keypress. keyChar = '" + keyChar + "'");
//        });
//
//        
//        ööö
//        
        
        
		return result;
	}

	private String createMetaKeyCheck(EventListener eventListener) {
		List<String> checks = new ArrayList<String>();
		
		if (eventListener.getAlt() != null){
			checks.add("event.altKey == " + eventListener.getAlt());
		}
		if (eventListener.getControl() != null){
			checks.add("event.ctrlKey == " + eventListener.getControl());
		}
		if (eventListener.getShift() != null){
			checks.add("event.shiftKey == " + eventListener.getShift());
		}
		
		if (checks.isEmpty()){
			return "";
		}
		String result = "";
		for (String i: checks){
			result += " && (" + i + ")";
		}
		return result;
	}

}
