package com.bright_side_it.fliesenui.generator.logic;

import static com.bright_side_it.fliesenui.base.util.BaseUtil.in;

import java.io.File;
import java.util.List;
import java.util.SortedMap;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.model.ReplyToCallTranslationParameter;
import com.bright_side_it.fliesenui.generator.model.RequestToCallTranslation;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

/**
 * 
 * The screen requests are only used for automated testing, because otherwise just a FLUIRequest object with a map is used
 * However for automated testing a wrapper class for each screen is created that shows problems with variable names etc.
 * at compile time.
 */
public class JavaScreenRequestCreatorLogic {
    public void createJava(Project project, ScreenDefinition screenDefinition, File screenPackageDir) throws Exception {
        StringBuilder result = new StringBuilder();

        String className = GeneratorUtil.getRequestClassName(screenDefinition);
        File destFile = new File(screenPackageDir, className + GeneratorConstants.JAVA_FILE_ENDING);
        result.append("package " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + ";\n");
        result.append("\n");
        result.append("import java.util.Map;\n");
        result.append("import java.util.TreeMap;\n");
        result.append("import com.google.gson.Gson;\n");
        result.append("import java.io.InputStream;\n");
        result.append("\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIScreen;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIRequest;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIScreenRequest;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIAbstractReply;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIString.StringLanguage;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + "." + GeneratorConstants.CLIENT_PROPERTIES_DTO_CLASS_NAME + ";\n");
        result.append("\n");
        result.append("public class " + className + " implements FLUIScreenRequest{\n");
        result.append("\n");
        result.append("    private FLUIRequest request = new FLUIRequest();\n");
        result.append("\n");
        result.append("    private " + className + "(){\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public FLUIRequest getRequest() {\n");
        result.append("        return request;\n");
        result.append("    }\n");
        result.append("\n");
        result.append(createCreateRequestMethod(className, screenDefinition));
//        result.append(createCreateOnLoadRequestMethod(className, screenDefinition));
//        List<RequestToCallTranslation> transalations = new RequestToCallTranslationBuilder().buildTranslations(project, screenDefinition);
//        for (RequestToCallTranslation i: transalations){
//        	log(screenDefinition.getID() + ": translation: " + toString(i));
//        }

        result.append(createCreateMethods(project, screenDefinition, className));
        
        
        
//        SortedMap<String, CellItem> widgetMap = GeneratorUtil.readWidgetIDMap(screenDefinition);
//        result.append(createAllButtonsClickMethods(screenDefinition, widgetMap, className));
        
        result.append("}");

        destFile.getParentFile().mkdirs();
        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
    }

	private StringBuilder createCreateMethods(Project project, ScreenDefinition screenDefinition, String className) throws Exception {
		StringBuilder result = new StringBuilder();
        List<RequestToCallTranslation> transalations = new RequestToCallTranslationBuilder().buildTranslations(project, screenDefinition);
        for (RequestToCallTranslation i: transalations){
        	result.append(createCreateMethodsFromTranslation(screenDefinition, i, className));
        }
		return result;
	}

	private StringBuilder createCreateMethodsFromTranslation(ScreenDefinition screenDefinition, RequestToCallTranslation translation, String className) throws Exception {
		StringBuilder result = new StringBuilder();
		String methodName = "create" + BaseUtil.idToFirstCharUpperCase(translation.getMethodName()) + "Request";
		StringBuilder parameterString = new StringBuilder();
		for (ReplyToCallTranslationParameter i: BaseUtil.toEmptyCollectionIfNull(translation.getParameter())){
			parameterString.append(", ");
			if (i.getDTOClassName() != null){
				if (GeneratorConstants.CLIENT_PROPERTIES_DTO_CLASS_NAME != i.getDTOClassName()){
					parameterString.append("generated.fliesenui.dto.");
				}
				parameterString.append(i.getDTOClassName());
			} else {
				parameterString.append(GeneratorUtil.toJavaTypeName(i.getDataType()));
			}
			parameterString.append(" " + i.getKey());
		}
		
		result.append("    public static " + className + " " + methodName + "(String language" + parameterString.toString() + "){\n");
		result.append("        " + className + " resultInstance = new " + className + "(\"" + translation.getActionName() + "\", language);\n");
		for (ReplyToCallTranslationParameter i: BaseUtil.toEmptyCollectionIfNull(translation.getParameter())){
			String convertPrefix = "";
			String convertSuffix = "";
			if (i.getDTOClassName() != null){
				convertPrefix = "new Gson().toJson(";
				convertSuffix = ")";
			}
			result.append("        resultInstance.request.getParameters().put(\"" + i.getKey() + "\", " + convertPrefix + i.getKey() + convertSuffix + ");\n");
			
		}
		result.append("        return resultInstance;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private void log(String message) {
		System.out.println("JavaScreenRequestCreatorLogic> " + message);
	}

	private String toString(RequestToCallTranslation translation) {
		StringBuilder result = new StringBuilder();
		result.append("Translation: action: '" + translation.getActionName() + "', method: '" + translation.getMethodName() + "'. params:");
		if (translation.getParameter() == null){
			return result.toString();
		}
		for (ReplyToCallTranslationParameter i: translation.getParameter()){
			result.append("\n    - key = '" + i.getKey() + "', dtoClass = '" + i.getDTOClassName() + "', data type: " + i.getDataType());
		}
		
		return result.toString();
	}

	private StringBuilder createAllButtonsClickMethods(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap, String className) throws Exception {
		StringBuilder result = new StringBuilder();
		for (CellItem widget : BaseUtil.getAllBasicWidgets(screenDefinition)) {
			if (widget instanceof BasicWidget) {
				BasicWidget basicWidget = (BasicWidget) widget;
				if (in(basicWidget.getType(), BasicWidgetType.BUTTON, BasicWidgetType.IMAGE_BUTTON)) {
					result.append(createButtonClickMethod(screenDefinition, basicWidget, widgetMap, className));
				}
			}
		}
		return result;
	}
	
	private StringBuilder createButtonClickMethod(ScreenDefinition screenDefinition, BasicWidget widget, SortedMap<String, CellItem> widgetMap, String className)
			throws Exception {
		StringBuilder result = new StringBuilder();
		
		String methodName = "create" + BaseUtil.idToFirstCharUpperCase(GeneratorUtil.getClickedListenerMethodName(widget)) + "Request";
		result.append("    public static " + className + " " + methodName + "(String language");
//		ööö create general method that created parameter list
		result.append("        " + className + " result = new " + className + "(\"" + GeneratorUtil.createJSButtonClickMethodName(widget) + "\", language);\n");
		result.append("        result.request.getParameters().put(\"" + GeneratorConstants.CLIENT_PROPERTIES_PARAMETER_NAME  + "\", clientProperties);\n");
//		result.append(createRequestObjectEventParametersMap(screenDefinition, widget, widgetMap, "        "));
		result.append("        return result;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}

//	private StringBuilder createCreateOnLoadRequestMethod(String className, ScreenDefinition screenDefinition) {
//		StringBuilder result = new StringBuilder();
//		
//		result.append("    public static " + className + " createOnLoadRequest(String language, FLUIClientPropertiesDTO clientProperties");
//		if (screenDefinition.getParameterDTOID() != null) {
//			String parameterName = screenDefinition.getParameterDTOID();
//			String dtoType = screenDefinition.getDTODeclarations().get(screenDefinition.getParameterDTOID()).getType();
//			String dtoClassName = GeneratorUtil.getDTOClassName(dtoType);
//			result.append(", " + dtoClassName + " " + parameterName);
//		}
//		result.append("){\n");
//		result.append("        " + className + " result = new " + className + "(\"" + GeneratorConstants.REQUEST_ACTION_ON_LOADED + "\", language);\n");
//		result.append("        result.request.getParameters().put(\"" + GeneratorConstants.CLIENT_PROPERTIES_PARAMETER_NAME  + "\", clientProperties);\n");
//		if (screenDefinition.getParameterDTOID() != null) {
//			result.append("        result.request.getParameters().put(\"" + screenDefinition.getParameterDTOID() + "\", " + screenDefinition.getParameterDTOID() + ");\n");
//		}
//		result.append("        return result;\n");
//		result.append("    }\n");
//		result.append("\n");
//		
//		return result;
//	}


	private StringBuilder createCreateRequestMethod(String className, ScreenDefinition screenDefinition) {
		StringBuilder result = new StringBuilder();
        result.append("    private " + className + "(String actionName, String language){\n");
        result.append("        request = new FLUIRequest();\n");
        result.append("        request.setAction(actionName);\n");
        result.append("        request.setCurrentLanguage(language);\n");
        result.append("        request.setScreenID(\"" + screenDefinition.getID() + "\");\n");
        result.append("        request.setParameters(new TreeMap<String, Object>());\n");
        result.append("    }\n");
        result.append("\n");
		return result;
	}

//	public static StringBuilder createRequestObjectEventParametersMap(ScreenDefinition screenDefinition, EventParameterContainer eventParameterContainer,
//			SortedMap<String, CellItem> widgetMap, String prefix) throws Exception {
//		StringBuilder result = new StringBuilder();
//
//		if (eventParameterContainer.getEventParameters() == null) {
//			return result;
//		}
//
//		for (EventParameter i : eventParameterContainer.getEventParameters()) {
//			result.append(prefix + "result.request.getParameters().put(\"");
//
//			if (i.getDTOID() != null) {
//				result.append(i.getDTOID());
//				result.append("\", ");
//				result.append(i.getDTOID() + ");\n");
//			} else if (i.getPluginVariableName() != null) {
//				String variableName = GeneratorUtil.getJSPluginVariableName(screenDefinition, i.getPluginInstanceID(), i.getPluginVariableName());
//				result.append(variableName);
//				result.append("\", ");
//				result.append(variableName + ");\n");
//			} else {
//				CellItem referencedWidget = widgetMap.get(i.getWidgetID());
//				if (referencedWidget == null) {
//					throw new Exception("Unknown widget: '" + i.getWidgetID() + "'");
//				}
//				String variable = i.getWidgetID() + GeneratorUtil.widgetPropertyToSuffix(i.getWidgetProperty());
//				result.append(variable);
//				result.append("\", ");
//				result.append(variable);
//				result.append(";\n");
//			}
//		}
//		
//		ööö create new general method: getVariableName and use it in this method and in the parameterList method
//		
//		return result;
//	}

	
}