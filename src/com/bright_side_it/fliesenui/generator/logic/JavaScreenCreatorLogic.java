package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.model.ReplyToCallTranslationParameter;
import com.bright_side_it.fliesenui.generator.model.ReplyToCallTranslationParameter.DataType;
import com.bright_side_it.fliesenui.generator.model.RequestToCallTranslation;
import com.bright_side_it.fliesenui.generator.model.RequestToCallTranslation.SpecialMethodType;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethod;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethod.CallbackType;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethodParameter;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethodParameter.ParameterType;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameterContainer;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JavaScreenCreatorLogic {
	private static final String ON_CONFIRM_DIALOG_RESULT_METHOD_NAME = "onConfirmDialogResult";
	private static final String ON_LIST_CHOOSER_DIALOG_RESULT_METHOD_NAME = "onListChooserDialogResult";
	private static final String ON_STRING_INPUT_DIALOG_RESULT_METHOD_NAME = "onStringInputDialogResult";
	
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
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".CallbackMethodCall;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + "." + GeneratorConstants.CLIENT_PROPERTIES_DTO_CLASS_NAME + ";\n");
        result.append("\n");
        result.append(createDTOImportStatements(project));
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
        result.append(createOnDialogResultMethod(project, screenDefinition, replyClassName, CallbackType.CONFIRM));
        result.append(createOnDialogResultMethod(project, screenDefinition, replyClassName, CallbackType.STRING_INPUT));
        result.append(createOnDialogResultMethod(project, screenDefinition, replyClassName, CallbackType.LIST_CHOOSER));
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

    private StringBuilder createOnDialogResultMethod(Project project, ScreenDefinition screenDefinition, String replyClassName, CallbackType callbackType) throws Exception {
    	StringBuilder result = new StringBuilder();
    	String methodName = getDialogResultMethodName(callbackType);
    	result.append("    private void " + methodName + "(" + replyClassName + " reply, String action, Map<String, Object> parameters) {\n");
		result.append("        String callbackDataString = (String)parameters.get(\"callbackData\");\n");
		result.append("        String referenceID = (String)parameters.get(\"referenceID\");\n");
		switch (callbackType) {
		case CONFIRM:
			result.append("        boolean result = (boolean)parameters.get(\"result\");\n");
			result.append("        if (callbackDataString == null){\n");
			result.append("            listener.onConfirmDialogResult(reply, referenceID, result);\n");
			break;
		case STRING_INPUT:
			result.append("        String result = (String)parameters.get(\"result\");\n");
			result.append("        if (callbackDataString == null){\n");
			result.append("            listener.onInputDialogResult(reply, referenceID, result);\n");
			break;
		case LIST_CHOOSER:
			result.append("        java.util.List<String> result = (java.util.List<String>)parameters.get(\"selectedIDs\");\n");
			result.append("        if (callbackDataString == null){\n");
			result.append("            listener.onListChooserResult(reply, referenceID, result);\n");
			break;
		default:
			throw new Exception("Unexpected callback type: " + callbackType);
		}
		
		result.append("            return;\n");
		result.append("        }\n");
		result.append("        Gson gson = new Gson();\n");
		result.append("        CallbackMethodCall callback = gson.fromJson(callbackDataString, CallbackMethodCall.class);\n");
		result.append("        Map<String, Object> callbackMethodParameters = callback.getParameterValues();\n");

		result.append("        switch (callback.getCallbackMethodIndex()) {\n");
		int callbackMethodIndex = 0;
		for (CallbackMethod callbackMethod: BaseUtil.toEmptyCollectionIfNull(screenDefinition.getCallbackMethods())){
			if (callbackMethod.getType() == callbackType){
				result.append("        case " + callbackMethodIndex + ":\n");
				result.append("            listener." + GeneratorUtil.createCallbackMethodName(callbackMethod.getName(), callbackMethod.getType()) + "(reply, result");
				for (CallbackMethodParameter parameter: BaseUtil.toEmptyCollectionIfNull(callbackMethod.getParameters())){
        			String dtoClassName = null;
        			if (parameter.getDTOClassName() != null){
        				dtoClassName = GeneratorUtil.getDTOClassName(parameter.getDTOClassName());
        			}
					appendParameterTranslation(result, parameter.getName(), dtoClassName, translateDataType(parameter.getType()), "callbackMethodParameters");
				}
				result.append(");\n");
				result.append("            break;\n");
			}
			callbackMethodIndex ++;
		}
		result.append("        }\n");
		result.append("    }\n");
		result.append("    \n");
    	
		return result;
	}

	private String getDialogResultMethodName(CallbackType callbackType) throws Exception{
		switch (callbackType) {
		case CONFIRM:
			return ON_CONFIRM_DIALOG_RESULT_METHOD_NAME;
		case LIST_CHOOSER:
			return ON_LIST_CHOOSER_DIALOG_RESULT_METHOD_NAME;
		case STRING_INPUT:
			return ON_STRING_INPUT_DIALOG_RESULT_METHOD_NAME;
		}
		throw new Exception("Unknown callback type: " + callbackType);
	}
	
	private DataType translateDataType(ParameterType type) throws Exception{
		switch (type) {
		case DTO:
			return null;
		case NON_NULLABE_LONG:
			return DataType.LONG_NOT_NULL;
		case NON_NULLABLE_BOOLEAN:
			return DataType.BOOLEAN_NOT_NULL;
		case NON_NULLABLE_INT:
			return DataType.INT;
		case NULLABLE_LONG:
			return DataType.LONG_OR_NULL;
		case NULLABLE_BOOLEAN:
			return DataType.BOOLEAN_OR_NULL;
		case NULLABLE_INT:
			return DataType.INTEGER;
		case STRING:
			return DataType.STRING;
		case LIST_OF_STRING:
			return DataType.LIST_OF_STRING;
		}
		throw new Exception("Unknown data type: " + type);
	}


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
    		if (translation.getSpecialMethodType() == null){
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
    				appendParameterTranslation(result, parameter.getKey(), parameter.getDTOClassName(), parameter.getDataType(), "parameters");
        		}
        		result.append(");\n");
    		} else {
    			switch (translation.getSpecialMethodType()) {
				case CONFIRM_DIALOG:
					result.append("            onConfirmDialogResult(reply, action, parameters);\n");
					break;
				case STRING_INPUT_DIALOG:
					result.append("            onStringInputDialogResult(reply, action, parameters);\n");
					break;
				case LIST_CHOOSER:
					result.append("            onListChooserDialogResult(reply, action, parameters);\n");
					break;
				default:
					break;
				}
    		}
    	}
    	result.append("        }\n");
    	result.append("        return reply;\n");
    	result.append("    }\n");
    	result.append("\n");
    	return result;
    }


	private void appendParameterTranslation(StringBuilder result, String parameterKey, String dtoClassName, DataType dataType
			, String parametersVariableName) throws Exception {
		result.append(", ");
		if (dtoClassName != null) {
			result.append("gson.fromJson((String)" + parametersVariableName + ".get(\"" + parameterKey + "\"), " + dtoClassName + ".class)");
		} else if (dataType == DataType.KEY_MODIFIER){
			result.append("gson.fromJson((String)" + parametersVariableName + ".get(\"" + parameterKey + "\"), " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".KeyModifier.class)");
		} else if (dataType == DataType.CHARACTER){
			result.append("((String)" + parametersVariableName + ".get(\"" + parameterKey + "\")).charAt(0)");
		} else {
			result.append("(");
			String suffix = "";
			if (dataType == DataType.INT) {
				suffix = ").intValue()";
				result.append("(Double");
			} else if (dataType == DataType.LONG_NOT_NULL) {
				suffix = ").longValue()";
				result.append("(Double");
			} else if (dataType == DataType.STRING) {
				result.append("String");
			} else if (dataType == DataType.BOOLEAN_OR_NULL) {
				result.append("Boolean");
			} else if (dataType == DataType.LONG_OR_NULL) {
				result.append("Long");
			} else if (dataType == DataType.INTEGER) {
				result.append("Integer");
			} else if (dataType == DataType.BOOLEAN_NOT_NULL) {
				result.append("boolean");
			} else if (dataType == DataType.LIST_OF_STRING) {
				result.append("java.util.List<String>");
			} else {
				throw new Exception("Unknown parameter data type: " + dataType);
			}
			result.append(")");
			result.append(parametersVariableName + ".get(\"" + parameterKey + "\")" + suffix);
		}
	}
    
    private boolean containsDTOs(ScreenDefinition screenDefinition) {
        if (screenDefinition.getDTODeclarations() == null) {
            return false;
        }
        return !screenDefinition.getDTODeclarations().isEmpty();
    }

    private StringBuilder createDTOImportStatements(Project project) {
        StringBuilder result = new StringBuilder();
        for (String i : project.getDTODefinitionsMap().keySet()) {
            result.append("import " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + "." + GeneratorUtil.getDTOClassName(i) + ";\n");
        }
        return result;

//        StringBuilder result = new StringBuilder();
//        for (String i : getRequiredDTOClassNames(screenDefinition)) {
//            result.append("import " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + "." + i + ";\n");
//        }
//        return result;
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
