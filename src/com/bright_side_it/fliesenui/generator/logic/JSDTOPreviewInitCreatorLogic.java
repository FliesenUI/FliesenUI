package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTOField;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JSDTOPreviewInitCreatorLogic {

    private static final int NUMBER_OF_LIST_ITEMS = 10;

    public String createCallInitPreviewCode(ScreenDefinition screenDefinition) {
        String result = "";
        StringBuilder code = new StringBuilder();
        code.append("if (typeof previewWindow != \"undefined\") {\n");
        //        code.append("    console.log('" + screenDefinition.getID() + ":it has a preview object!');\n");
        code.append("    previewWindow.initForPreview();\n");
        code.append("} else {\n");
        //        code.append("    console.log('" + screenDefinition.getID() + ":it has NO preview object!');\n");
        code.append("}\n");
        result = "    setTimeout(function() {\n" + code + "\n";
        result = result.replace("\n", "\n        ");
        result += "}, 0);\n";
        return result;
    }

    public StringBuilder createInitPreviewCodeForAllDTOTypes(Project project, ScreenDefinition screenDefinition) throws Exception {
        StringBuilder result = new StringBuilder();
        for (DTODefinition i : project.getDTODefinitionsMap().values()) {
            result.append(createInitPreviewCodeForDTOType(screenDefinition, i));
        }
        result.append("\n");
        return result;
    }

    private StringBuilder createInitPreviewCodeForDTOType(ScreenDefinition screenDefinition, DTODefinition dtoDefinition) throws Exception {
        StringBuilder result = new StringBuilder();
        String methodName = GeneratorUtil.createJSInitDTOTypeForPreviewMethodName(screenDefinition, dtoDefinition.getID());
        result.append(methodName + " = function(index){\n");
        result.append("    var result = new Object();\n");
        for (DTOField field : dtoDefinition.getFields().values()) {
            if (field.isList()) {
                result.append("    result." + field.getID() + " = [];\n");
                for (int index = 0; index < NUMBER_OF_LIST_ITEMS; index++) {
                    result.append("    result." + field.getID() + "[" + index + "] = " + createDTOPreviewValueExpression(screenDefinition, field, index) + ";\n");
                }
            } else {
                result.append("    result." + field.getID() + " = " + createDTOPreviewValueExpression(screenDefinition, field, null) + ";\n");
            }
        }
        result.append("    return result;\n");
        result.append("};\n");
        return result;
    }


    private String createDTOPreviewValueExpression(ScreenDefinition screenDefinition, DTOField field, Integer index) throws Exception {
        if (field.getBasicType() != null) {
            if (field.getBasicType() == BasicType.STRING) {
                String previewString = field.getPreviewValue();
                if (previewString == null) {
                    previewString = "(preview string " + field.getID() + ")";
                }
                return "\"" + previewString + "\" + \"(\" + index + \")\"";
            } else if (field.getBasicType() == BasicType.LONG) {
                String previewString = null;
                if (field.getPreviewValue() != null) {
                    try {
                        previewString = "" + Long.parseLong(field.getPreviewValue());
                    } catch (Exception e) {
                        throw new Exception("Could not read number from preview value '" + field.getPreviewValue() + "'");
                    }
                }
                if (previewString == null) {
                    previewString = "1000";
                }
                return previewString + " + index";
            } else if (field.getBasicType() == BasicType.BOOLEAN) {
                String previewString = null;
                if (field.getPreviewValue() != null) {
                    try {
                        previewString = "" + Boolean.parseBoolean(field.getPreviewValue());
                    } catch (Exception e) {
                        throw new Exception("Could not read boolean from preview value '" + field.getPreviewValue() + "'");
                    }
                }
                if (previewString == null) {
                    previewString = "true";
                }
                return previewString;
            } else {
                throw new Exception("Unknown basic type: " + field.getBasicType());
            }
        } else if (field.getDTOType() != null) {
            return GeneratorUtil.createJSInitDTOTypeForPreviewMethodName(screenDefinition, field.getDTOType()) + "(" + index + ")";
        } else {
            throw new Exception("neither basic type nor DTO type are set");
        }
    }


    public StringBuilder createInitPreviewCodeForDeclaredDTOs(Project project, ScreenDefinition screenDefinition) throws Exception {
        StringBuilder result = new StringBuilder();
        result.append(screenDefinition.getID() + "$initForPreview = function(){\n");
        result.append("    console.log('" + screenDefinition.getID() + ":executing initForPreview in JS!');\n");
        result.append("    var scope = angular.element(document.getElementById('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')).scope();\n");

        if (screenDefinition.getDTODeclarations() != null) {
            if (screenDefinition.getDTODeclarations().values() != null) {
                for (DTODeclaration dtoDeclaration : screenDefinition.getDTODeclarations().values()) {
                    String methodName = GeneratorUtil.createJSInitDTOTypeForPreviewMethodName(screenDefinition, dtoDeclaration.getType());
                    result.append("    scope." + dtoDeclaration.getID() + " = " + methodName + "(0);\n");
                }
            }
        }

        result.append("    setTimeout(function() {scope.$digest();}, 0);\n");
        result.append("}\n");
        result.append("\n");
        result.append("\n");
        return result;
    }





}
