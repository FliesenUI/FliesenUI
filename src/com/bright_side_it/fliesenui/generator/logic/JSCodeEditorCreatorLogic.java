package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JSCodeEditorCreatorLogic {
    public StringBuilder createAllCodeEditorsCode(ScreenDefinition screenDefinition) {
        StringBuilder result = new StringBuilder();
        for (CodeEditorWidget i : BaseUtil.getAllCodeEditorWidgets(screenDefinition)) {
            result.append(createCodeEditorCode(screenDefinition, i));
        }
        return result;
    }

    private StringBuilder createCodeEditorCode(ScreenDefinition screenDefinition, CodeEditorWidget codeEditor) {
        StringBuilder result = new StringBuilder();
        String variableName = GeneratorUtil.createCodeWidgetVariableName(screenDefinition, codeEditor);
        String screenIDPrefix = GeneratorUtil.createScreenIDPrefix(screenDefinition);

        result.append("    var " + variableName + " = CodeMirror.fromTextArea(document.getElementById(\"" + screenIDPrefix + codeEditor.getID() + "\"), {\n");
        result.append("        mode: \"text/xml\",\n");
        result.append("        lineNumbers: true,\n");
        result.append("        reindentOnLoad: true,\n");
        result.append("        styleActiveLine: true,\n");
        result.append("        matchBrackets: true,\n");
        result.append("        autoCloseTags: true,\n");
        result.append("        matchTags: true,\n");
        result.append("        indentUnit: 4,\n");
        result.append("        extraKeys: {\n");
        result.append("            \"Ctrl-Space\": function(cm) {\n");
        result.append("                var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.getContextAssistListenerMethodName(codeEditor) + "\");\n");
        result.append("                request.parameters[\"editorText\"] = " + variableName + ".getValue();\n");
        result.append("                request.parameters[\"line\"] = " + variableName + ".getCursor().line;\n");
        result.append("                request.parameters[\"posInLine\"] = " + variableName + ".getCursor().ch;\n");
        result.append("                " + screenIDPrefix + "executeRequest(request);\n");
        result.append("            },\n");
        result.append("            \"Ctrl-S\": function(cm) {\n");
        result.append("                var request = " + screenIDPrefix + "createRequest(\"" + GeneratorUtil.getSaveListenerMethodName(codeEditor) + "\");\n");
        result.append("                request.parameters[\"editorText\"] = " + variableName + ".getValue();\n");
        result.append("                request.parameters[\"line\"] = " + variableName + ".getCursor().line;\n");
        result.append("                request.parameters[\"posInLine\"] = " + variableName + ".getCursor().ch;\n");
        result.append("                " + screenIDPrefix + "executeRequest(request);\n");
        result.append("            }\n");
        result.append("        }\n");
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


        result.append("\n");
        result.append("\n");

        return result;
    }

}
