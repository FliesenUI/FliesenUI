package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.SharedReplyInterface;

public class JavaSharedReplyInterfaceCreatorLogic {
	public void createInterface(Project project, SharedReplyInterface sharedReplyInterface, File screenPackageDir, Map<String, List<String>> screenToReplySignaturesMap) throws Exception{
		StringBuilder result = new StringBuilder();
		
		String interfaceName = GeneratorUtil.getSharedReplyInterfaceName(sharedReplyInterface);
		File destFile = new File(screenPackageDir, interfaceName + GeneratorConstants.JAVA_FILE_ENDING);
		result.append("package " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + ";\n");
		result.append("\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".TextHighlighting;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".CursorPos;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".ContextAssist;\n");
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
