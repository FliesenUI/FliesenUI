package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.model.ReplyToCallTranslationParameter;
import com.bright_side_it.fliesenui.generator.model.ReplyToCallTranslationParameter.DataType;
import com.bright_side_it.fliesenui.generator.model.RequestToCallTranslation;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameterContainer;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JavaScreenCreatorLogic {
    /** disabled because this control flow does not work with web */
    public void createJava(Project project, ScreenDefinition screenDefinition, File screenPackageDir) throws Exception {
        StringBuilder result = new StringBuilder();

        String className = GeneratorUtil.getViewClassName(screenDefinition);
        String replyClassName = GeneratorUtil.getReplyClassName(screenDefinition);

        File destFile = new File(screenPackageDir, className + GeneratorConstants.JAVA_FILE_ENDING);

        String listenerClassName = GeneratorUtil.getViewListenerClassName(screenDefinition);

        result.append("package " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + ";\n");
        result.append("\n");
        result.append("import java.util.Map;\n");
        result.append("import com.google.gson.Gson;\n");
        result.append("import java.io.InputStream;\n");
//        if (containsDTOs(screenDefinition)) {
//            result.append("import com.google.gson.GsonBuilder;\n");
//        }
        result.append("\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIScreen;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIRequest;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIAbstractReply;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIString.StringLanguage;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIUtil;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + "." + GeneratorConstants.CLIENT_PROPERTIES_DTO_CLASS_NAME + ";\n");
        result.append("\n");
        result.append(createDTOImportStatements(screenDefinition));
        result.append("\n");
        result.append("public class " + className + " implements FLUIScreen {\n");
        result.append("    private " + listenerClassName + " listener;\n");
        result.append("\n");
        result.append("    public " + className + "(" + listenerClassName + " listener) {\n");
        result.append("        this.listener = listener;\n");
        result.append("    }\n");
        result.append("\n");
//        result.append(createOnRequestOldMethodRequestObject(project, screenDefinition, replyClassName));
        result.append(createOnRequestMethodRequestObject(project, screenDefinition, replyClassName));
        result.append("\n");
        result.append("    @Override\n");
        result.append("    public String getID() {\n");
        result.append("        return \"" + screenDefinition.getID() + "\";\n");
        result.append("    }\n");


        result.append("}");

        destFile.getParentFile().mkdirs();
        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
    }

//    private StringBuilder createOnRequestOldMethodRequestObject(Project project, ScreenDefinition screenDefinition, String replyClassName) throws Exception {
//    	StringBuilder result = new StringBuilder();
//    	result.append("    public String onFLUIRequestOld(FLUIRequest request, String uploadedFileName, InputStream uploadedFileInputStream) {\n");
//    	result.append("        Map<String, Object> parameters = request.getParameters();\n");
//    	result.append("        String action = request.getAction();\n");
//    	
//    	result.append("        StringLanguage currentLanguage = null;\n");
//    	result.append("        try{\n");
//		result.append("            currentLanguage = StringLanguage.valueOf(request.getCurrentLanguage());\n");
//		result.append("        } catch (Exception ignored){\n");
//		result.append("        }\n");
//
//    	result.append("        Gson gson = new Gson();\n");
//    	result.append("        " + replyClassName + " reply = new " + replyClassName + "(currentLanguage);\n");
//    	boolean first = true;
//    	for (RequestToCallTranslation translation : new RequestToCallTranslationBuilder().buildTranslations(project, screenDefinition)) {
//    		String prefix = "} else ";
//    		if (first) {
//    			first = false;
//    			prefix = "";
//    		}
//    		result.append("        " + prefix + "if (\"" + translation.getActionName() + "\".equals(action)) {\n");
//    		result.append("            listener." + translation.getMethodName() + "(");
//    		if (translation.isFileUploadMethod()){
//    			result.append("uploadedFileName, uploadedFileInputStream");
//    		} else {
//    			result.append("reply");
//    		}
//    		for (ReplyToCallTranslationParameter parameter : BaseUtil.toEmptyCollectionIfNull(translation.getParameter())) {
//				result.append(", ");
//    			if (parameter.getDTOClassName() != null) {
//    				result.append("gson.fromJson((String)parameters.get(\"" + parameter.getKey() + "\"), " + parameter.getDTOClassName() + ".class)");
//    			} else if (parameter.getDataType() == DataType.KEY_MODIFIER){
//    				result.append("gson.fromJson((String)parameters.get(\"" + parameter.getKey() + "\"), " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".KeyModifier.class)");
//    			} else if (parameter.getDataType() == DataType.CHARACTER){
//    				result.append("((String)parameters.get(\"" + parameter.getKey() + "\")).charAt(0)");
//    			} else {
//    				result.append("(");
//    				String suffix = "";
//    				if (parameter.getDataType() == DataType.INT) {
//    					suffix = ").intValue()";
//    					result.append("(Double");
//    				} else if (parameter.getDataType() == DataType.STRING) {
//    					result.append("String");
//    				} else if (parameter.getDataType() == DataType.BOOLEAN) {
//    					result.append("boolean");
//    				} else if (parameter.getDataType() == DataType.LIST_OF_STRING) {
//    					result.append("java.util.List<String>");
//    				} else {
//    					throw new Exception("Unknown parameter data type: " + parameter.getDataType());
//    				}
//    				result.append(")");
//    				result.append("parameters.get(\"" + parameter.getKey() + "\")" + suffix);
//    			}
//    		}
//    		result.append(");\n");
//    	}
//    	result.append("        }\n");
//    	result.append("        return reply.getJSON();\n");
//    	result.append("    }\n");
//    	return result;
//    }

    private StringBuilder createOnRequestMethodRequestObject(Project project, ScreenDefinition screenDefinition, String replyClassName) throws Exception {
    	StringBuilder result = new StringBuilder();
    	result.append("    public FLUIAbstractReply onFLUIRequest(boolean recordMode, FLUIRequest request, String uploadedFileName, InputStream uploadedFileInputStream) throws Exception{\n");
    	result.append("        Map<String, Object> parameters = request.getParameters();\n");
    	result.append("        String action = request.getAction();\n");
    	
    	result.append("        StringLanguage currentLanguage = null;\n");
    	result.append("        try{\n");
		result.append("            currentLanguage = StringLanguage.valueOf(request.getCurrentLanguage());\n");
		result.append("        } catch (Exception ignored){\n");
		result.append("        }\n");

    	result.append("        Gson gson = new Gson();\n");
    	result.append("        " + replyClassName + " reply = new " + replyClassName + "(recordMode, currentLanguage);\n");
    	boolean first = true;
    	for (RequestToCallTranslation translation : new RequestToCallTranslationBuilder().buildTranslations(project, screenDefinition)) {
    		String prefix = "} else ";
    		if (first) {
    			first = false;
    			prefix = "";
    		}
    		result.append("        " + prefix + "if (\"" + translation.getActionName() + "\".equals(action)) {\n");
    		result.append("            listener." + translation.getMethodName() + "(");
    		if (translation.isFileUploadMethod()){
    			result.append("uploadedFileName, uploadedFileInputStream");
    		} else {
    			result.append("reply");
    		}
    		if (translation.isKeyEventMethod()){
    			result.append(", FLUIUtil.createFLUIKeyEvent(");
    			result.append("gson.fromJson((String)parameters.get(\"" + GeneratorConstants.KEY_MODIFIER_PARAMETER_NAME + "\"), " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".KeyModifier.class)");
				result.append(", ");
				result.append("((String)parameters.get(\"" + GeneratorConstants.KEY_CHAR_PARAMETER_NAME + "\"))");
				result.append(", ");
				result.append("(Double)parameters.get(\"" + GeneratorConstants.KEY_CODE_PARAMETER_NAME + "\")");
				result.append(", ");
				result.append("((String)parameters.get(\"" + GeneratorConstants.EDITOR_TEXT_PARAMETER_NAME + "\"))");
				result.append(", ");
				result.append("((Double)parameters.get(\"" + GeneratorConstants.LINE_PARAMETER_NAME + "\")).intValue()");
				result.append(", ");
				result.append("((Double)parameters.get(\"" + GeneratorConstants.POS_IN_LINE_PARAMETER_NAME + "\")).intValue()");
				result.append(", ");
				result.append("((String)parameters.get(\"" + GeneratorConstants.KEY_EVENT_INFO_PARAMETER_NAME + "\"))");
				result.append(")");
    		}

    		for (ReplyToCallTranslationParameter parameter : BaseUtil.toEmptyCollectionIfNull(translation.getParameter())) {
				result.append(", ");
    			if (parameter.getDTOClassName() != null) {
    				result.append("gson.fromJson((String)parameters.get(\"" + parameter.getKey() + "\"), " + parameter.getDTOClassName() + ".class)");
    			} else if (parameter.getDataType() == DataType.KEY_MODIFIER){
    				result.append("gson.fromJson((String)parameters.get(\"" + parameter.getKey() + "\"), " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".KeyModifier.class)");
    			} else if (parameter.getDataType() == DataType.CHARACTER){
    				result.append("((String)parameters.get(\"" + parameter.getKey() + "\")).charAt(0)");
    			} else {
    				result.append("(");
    				String suffix = "";
    				if (parameter.getDataType() == DataType.INT) {
    					suffix = ").intValue()";
    					result.append("(Double");
    				} else if (parameter.getDataType() == DataType.STRING) {
    					result.append("String");
    				} else if (parameter.getDataType() == DataType.BOOLEAN) {
    					result.append("boolean");
    				} else if (parameter.getDataType() == DataType.LIST_OF_STRING) {
    					result.append("java.util.List<String>");
    				} else {
    					throw new Exception("Unknown parameter data type: " + parameter.getDataType());
    				}
    				result.append(")");
    				result.append("parameters.get(\"" + parameter.getKey() + "\")" + suffix);
    			}
    		}
    		result.append(");\n");
    	}
    	result.append("        }\n");
    	result.append("        return reply;\n");
    	result.append("    }\n");
    	return result;
    }

    
    private boolean containsDTOs(ScreenDefinition screenDefinition) {
        if (screenDefinition.getDTODeclarations() == null) {
            return false;
        }
        return !screenDefinition.getDTODeclarations().isEmpty();
    }

    private StringBuilder createDTOImportStatements(ScreenDefinition screenDefinition) {
        StringBuilder result = new StringBuilder();
        for (String i : getRequiredDTOClassNames(screenDefinition)) {
            result.append("import " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + "." + i + ";\n");
        }
        return result;
    }

    private List<String> getRequiredDTOClassNames(ScreenDefinition screenDefinition) {
        List<String> result = new ArrayList<String>();
        if (screenDefinition.getDTODeclarations() == null) {
            return result;
        }

        for (DTODeclaration i : screenDefinition.getDTODeclarations().values()) {
            result.add(getDTOClassName(i, screenDefinition));
        }

        return result;
    }

    @SuppressWarnings("unused")
    private boolean containsEventParameterDTOs(EventParameterContainer eventParameterContainer) {
        if (eventParameterContainer.getEventParameters() == null) {
            return false;
        }
        for (EventParameter i : eventParameterContainer.getEventParameters()) {
            if (i.getDTOID() != null) {
                return true;
            }
        }
        return false;
    }

    private String getDTOClassName(DTODeclaration dtoDeclaration, ScreenDefinition screenDefinition) {
        DTODeclaration declaration = screenDefinition.getDTODeclarations().get(dtoDeclaration.getID());
        String type = declaration.getType();
        return GeneratorUtil.getDTOClassName(type);
    }


}
