package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.SharedReplyInterface;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethod;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethod.CallbackType;
import com.bright_side_it.fliesenui.screendefinition.model.CallbackMethodParameter;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JavaDialogOptionsCreatorLogic {

	public void createJava(Project project, ScreenDefinition screenDefinition, File screenPackageDir) throws Exception {
		Map<CallbackType, CallbackMethod> methodsOfType = new TreeMap<>();
		for (CallbackMethod i: BaseUtil.toEmptyCollectionIfNull(screenDefinition.getCallbackMethods())){
			methodsOfType.put(i.getType(), i);
		}
	
		for (CallbackType i: CallbackType.values()){
			createJava(project, screenDefinition, screenPackageDir, i);
		}
	}
	
	private void createJava(Project project, ScreenDefinition screenDefinition, File screenPackageDir, CallbackType callbackType) throws Exception {
		String className = GeneratorUtil.getDialogOptionsClassName(screenDefinition, callbackType);
		File destFile = new File(screenPackageDir, className + GeneratorConstants.JAVA_FILE_ENDING);

		String replyClassName = GeneratorUtil.getReplyClassName(screenDefinition);
		
		StringBuilder result = new StringBuilder();
		result.append("package " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + ";\n");
		result.append("\n");
		result.append("import java.util.Collection;\n");
		result.append("import java.util.Map;\n");
		result.append("import java.util.TreeMap;\n");
		result.append("import java.util.List;\n");
		
		result.append("import com.google.gson.Gson;\n");
		result.append("import com.google.gson.GsonBuilder;\n");
		
		result.append("\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".CallbackMethodCall;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + "." + getParametersClassName(callbackType) + ";\n");
        result.append("import " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + replyClassName + ";\n");
        
        result.append(createDTOImportStatements(project, screenDefinition));
		result.append("\n");		
		result.append("\n");
		
		result.append("public class " + className + "{\n");
		result.append("     private " + replyClassName + " reply;\n");
		result.append("     private " + getParametersClassName(callbackType) + " dialogParameters;\n");
		result.append("     protected Gson gson = new GsonBuilder().create();\n");
		result.append("\n");
		result.append("     protected " + className + "(" + replyClassName + " reply, " + getParametersClassName(callbackType) + " dialogParameters){\n");
		result.append("         this.reply = reply;\n");
		result.append("         this.dialogParameters = dialogParameters;\n");
		result.append("     }\n");
		
		StringBuilder methodsText = new StringBuilder();
		int index = 0;
		for (CallbackMethod i: BaseUtil.toEmptyCollectionIfNull(screenDefinition.getCallbackMethods())){
			if (i.getType() == callbackType){
				methodsText.append(createWithCallbackMethod(i, index));
			}
			index ++;
		}

		if (methodsText.length() == 0){
			methodsText = createDefaultMethodText();
		}
		
		result.append(methodsText);

        result.append("}");

        destFile.getParentFile().mkdirs();
        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
	}

    public static StringBuilder createDTOImportStatements(Project project, ScreenDefinition screenDefinition) {
        StringBuilder result = new StringBuilder();
        for (String i : project.getDTODefinitionsMap().keySet()) {
            result.append("import " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + "." + GeneratorUtil.getDTOClassName(i) + ";\n");
        }
        return result;
    }


	private StringBuilder createWithCallbackMethod(CallbackMethod method, int index) throws Exception {
		StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append("    public void withCallback" + BaseUtil.idToFirstCharUpperCase(method.getName()) + "(");
        boolean first = true;
        for (CallbackMethodParameter i: BaseUtil.toEmptyCollectionIfNull(method.getParameters())){
        	if (first){
        		first = false;
        	} else {
        		result.append(", ");
        	}
        	result.append(GeneratorUtil.getJavaTypeName(i) + " " + i.getName());
        }
        result.append("){\n");
        result.append("        CallbackMethodCall methodCall = new CallbackMethodCall();\n");
        result.append("        methodCall.setCallbackMethodIndex(" + index + ");\n");
        result.append("        Map<String, Object> params = new TreeMap<String, Object>();\n");
        for (CallbackMethodParameter i: BaseUtil.toEmptyCollectionIfNull(method.getParameters())){
        	String valueText = i.getName();
        	if (i.getDTOClassName() != null){
        		valueText = "gson.toJson(" + i.getName() + ")";
        		
        	}
    		result.append("        params.put(\"" + i.getName() + "\", " + valueText + ");\n");
        }
        result.append("        methodCall.setParameterValues(params);\n");
        result.append("        if (dialogParameters == null){\n");
        result.append("            return;\n");
        result.append("        }\n");
        result.append("        dialogParameters.setCallbackData(gson.toJson(methodCall));\n");
        result.append("    }\n");
        result.append("\n");
		return result;
	}
	
	
//	private String createGetParametersMethodName(CallbackType type) throws Exception {
//		switch (type) {
//		case CONFIRM:
//			return "getConfirmDialogParameters";
//		case LIST_CHOOSER:
//			return "getListChooserParameters";
//		case STRING_INPUT:
//			return "getInputDialogParameters";
//		default:
//			throw new Exception("Unknown type:" + type);
//		}
//	}

	private String getParametersClassName(CallbackType type) throws Exception {
		switch (type) {
		case CONFIRM:
			return "ConfirmDialogParameters";
		case LIST_CHOOSER:
			return "ListChooserParameters";
		case STRING_INPUT:
			return "InputDialogParameters";
		default:
			throw new Exception("Unknown type:" + type);
		}
	}
	

	private StringBuilder createDefaultMethodText() {
		StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append("    public void noCallbackMethodsDefinedInScreenXML(){\n");
        result.append("    }\n");
        result.append("\n");
		return result;
	}


	public void createClasses(Project project, SharedReplyInterface sharedReplyInterface, File screenPackageDir, Map<String, List<String>> screenToReplySignaturesMap) throws Exception{
		StringBuilder result = new StringBuilder();
		
		String interfaceName = GeneratorUtil.getSharedReplyInterfaceName(sharedReplyInterface);
		File destFile = new File(screenPackageDir, interfaceName + GeneratorConstants.JAVA_FILE_ENDING);
		result.append("package " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + ";\n");
		result.append("\n");
		result.append("import java.util.Collection;");
		result.append("\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIString.StringLanguage;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".TextHighlighting;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".CursorPos;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".ContextAssist;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".IDLabelImageAssetList;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".IDLabelList;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIImageAssets.ImageAsset;\n");

		result.append("\n");
		result.append(createDTOImportStatements(project, sharedReplyInterface));
		result.append("\n");
		result.append("public interface " + interfaceName + "{\n");
		result.append(createMethodSignatures(sharedReplyInterface, screenToReplySignaturesMap));
        result.append("}");

        destFile.getParentFile().mkdirs();
        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
	}

	private StringBuilder createDTOImportStatements(Project project, SharedReplyInterface sharedReplyInterface) {
		StringBuilder result = new StringBuilder();
		
		Set<String> dtoClassNames = null;
		for (String screenID: sharedReplyInterface.getScreenIDs()){
			Set<String> dtoClassNamesForScreen = GeneratorUtil.getRequiredDTOClassNames(project, project.getScreenDefinitionsMap().get(screenID));
			
			if (dtoClassNames == null){
				dtoClassNames = dtoClassNamesForScreen;
			} else {
				dtoClassNames.retainAll(dtoClassNamesForScreen);
			}
		}

		for (String i: dtoClassNames){
			result.append("import " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + "." + i + ";\n");
		}
		
		return result;
	}

	private StringBuilder createMethodSignatures(SharedReplyInterface sharedReplyInterface, Map<String, List<String>> screenToReplySignaturesMap) {
		StringBuilder result = new StringBuilder();
		
		Set<String> signaturesThatOccurInAllScreens = null;
		for (String i: sharedReplyInterface.getScreenIDs()){
			if (signaturesThatOccurInAllScreens == null){
				signaturesThatOccurInAllScreens = new TreeSet<>(screenToReplySignaturesMap.get(i));
			} else {
				signaturesThatOccurInAllScreens.retainAll(screenToReplySignaturesMap.get(i));
			}
		}
		
		for (String i: signaturesThatOccurInAllScreens){
			result.append("    " + i + "\n");
		}
		
		return result;
	}

}
