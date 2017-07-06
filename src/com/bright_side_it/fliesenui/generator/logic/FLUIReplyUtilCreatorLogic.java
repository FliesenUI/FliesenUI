package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.Collection;

import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class FLUIReplyUtilCreatorLogic {

    public void generate(Project project, File javaBaseDir) throws Exception {
        StringBuilder result = new StringBuilder();
        File packageDir = GeneratorUtil.getCorePackageDir(javaBaseDir);
        File destFile = new File(packageDir, "FLUIReplyUtil.java");

        result.append("package " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ";\n\n");
        Collection<ScreenDefinition> screenDefinitions = project.getScreenDefinitionsMap().values();
        result.append(createImportStatements(screenDefinitions));
        result.append("\n");
        result.append("public class FLUIReplyUtil {\n");
        result.append("\n");
        
        result.append(createMethods(screenDefinitions
        		, "showInputDialog(FLUIAbstractReplyContainer reply, String referenceID, String title, String textContent, String label, String initialValueText, String okText, String cancelText)"
        		, "showInputDialog(referenceID, title, textContent, label, initialValueText, okText, cancelText)"));
        result.append(createMethods(screenDefinitions
        		, "showConfirmDialog(FLUIAbstractReplyContainer reply, String referenceID, String title, String textContent, String okText, String cancelText)"
        		, "showConfirmDialog(referenceID, title, textContent, okText, cancelText)"));
        result.append(createMethods(screenDefinitions
        		, "showListChooser(FLUIAbstractReplyContainer reply, String referenceID, boolean multiSelect, boolean showFilter, String title, IDLabelImageAssetList items, Collection<String> selectedIDs)"
        		, "showListChooser(referenceID, multiSelect, showFilter, title, items, selectedIDs)"));
        result.append(createMethods(screenDefinitions
        		, "showListChooser(FLUIAbstractReplyContainer reply, String referenceID, boolean multiSelect, boolean showFilter, String title, IDLabelList items, Collection<String> selectedIDs)"
        		, "showListChooser(referenceID, multiSelect, showFilter, title, items, selectedIDs)"));
        result.append(createMethods(screenDefinitions
        		, "showListChooser(FLUIAbstractReplyContainer reply, String referenceID, boolean multiSelect, boolean showFilter, String title, String okText, String cancelText, IDLabelImageAssetList items, Collection<String> selectedIDs)"
        		, "showListChooser(referenceID, multiSelect, showFilter, title, okText, cancelText, items, selectedIDs)"));
        result.append(createMethods(screenDefinitions
        		, "showListChooser(FLUIAbstractReplyContainer reply, String referenceID, boolean multiSelect, boolean showFilter, String title, String okText, String cancelText, IDLabelList items, Collection<String> selectedIDs)"
        		, "showListChooser(referenceID, multiSelect, showFilter, title, okText, cancelText, items, selectedIDs)"));

        result.append("}");

        destFile.getParentFile().mkdirs();

        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
        destFile.setWritable(false);
    }

//    private StringBuilder createCreateScreenMethods(Collection<ScreenDefinition> screenDefinitions) {
//        StringBuilder result = new StringBuilder();
//        for (ScreenDefinition i : screenDefinitions) {
//            String className = GeneratorUtil.getViewClassName(i);
//            String classListnerName = GeneratorUtil.getViewListenerClassName(i);
//            result.append("    public " + className + " create" + className + "(" + classListnerName + " listener) {\n");
//            result.append("        return new " + className + "(this, listener);\n");
//            result.append("    }\n");
//        }
//        return result;
//    }

    private StringBuilder createShowInputDialogMethods(Collection<ScreenDefinition> screenDefinitions) {
    	StringBuilder result = new StringBuilder();
    	result.append("    public static void showInputDialog(FLUIAbstractReply reply, String referenceID, String title, String textContent, String label, String initialValueText, String okText, String cancelText) {\n");
    	result.append("        switch (reply.getClass().getName()){\n");
    	for (ScreenDefinition i : screenDefinitions) {
    		String replyClassName = GeneratorUtil.getReplyClassName(i);
    		result.append("        case \"" + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + replyClassName + "\":\n");
    		result.append("            ((" + replyClassName + ")reply).showInputDialog(referenceID, title, textContent, label, initialValueText, okText, cancelText);\n");
    		result.append("            break;\n");
    	}
    	result.append("        }\n");
    	result.append("    }\n");
    	return result;
	}

    private StringBuilder createMethods(Collection<ScreenDefinition> screenDefinitions, String methodNameDeclaration, String medhodCall) {
    	StringBuilder result = new StringBuilder();
    	result.append("    public static void " + methodNameDeclaration + "{\n");
    	result.append("        switch (reply.getClass().getName()){\n");
    	for (ScreenDefinition i : screenDefinitions) {
    		String replyClassName = GeneratorUtil.getReplyClassName(i);
    		result.append("        case \"" + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + replyClassName + "\":\n");
    		result.append("            ((" + replyClassName + ")reply)." + medhodCall + ";\n");
    		result.append("            break;\n");
    	}
    	result.append("        }\n");
    	result.append("    }\n");
    	return result;
	}

	//    private StringBuilder createOpenScreenMethod(Collection<ScreenDefinition> screenDefinitions) {
//        StringBuilder result = new StringBuilder();
//        result.append("    public void openScreen(FLUIScreen screen) {\n");
//        for (ScreenDefinition i : screenDefinitions) {
//            String className = GeneratorUtil.getViewClassName(i);
//            result.append("        if (screen instanceof " + className + ") {\n");
//            String htmlFilename = GeneratorUtil.createHTMLFilename(i, BrowserType.JAVA_FX);
//            result.append("            openScreen(screen.getID(), \"" + htmlFilename + "\", null);\n");
//            result.append("        }\n");
//        }
//        result.append("    }\n");
//
//        return result;
//    }
//
    private StringBuilder createImportStatements(Collection<ScreenDefinition> screenDefinitions) {
        StringBuilder result = new StringBuilder();
        for (ScreenDefinition i : screenDefinitions) {
            result.append("import " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + GeneratorUtil.getReplyClassName(i) + ";\n");
        }

        result.append("import java.util.Collection;\n");
        
        result.append("\n");
        return result;
    }

}
