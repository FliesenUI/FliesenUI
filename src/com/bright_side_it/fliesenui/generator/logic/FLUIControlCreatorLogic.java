package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.Collection;

import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class FLUIControlCreatorLogic {

    public void generateFLUIControl(Collection<ScreenDefinition> screenDefinitions, File javaBaseDir) throws Exception {
        StringBuilder result = new StringBuilder();
        File packageDir = GeneratorUtil.getCorePackageDir(javaBaseDir);
        File destFile = new File(packageDir, "FLUIControl.java");

        result.append("package " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ";\n\n");
        result.append(createImportStatements(screenDefinitions));
        result.append("\n");
        result.append("public class FLUIControl extends FLUIWebView {\n");
        result.append("\n");
        result.append("    public FLUIControl(Window stage, int width, int height, FLUIScreenManager screenManager) {\n");
        result.append("        super(stage, width, height, screenManager);\n");
        result.append("        getScreenManager().setWebView(this);\n");
        result.append("    }\n");
        result.append("\n");
//        result.append(createOpenScreenMethod(screenDefinitions));
//        result.append("\n");
//        result.append(createCreateScreenMethods(screenDefinitions));
//        result.append("\n");
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
//        for (ScreenDefinition i : screenDefinitions) {
//            result.append("import " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + GeneratorUtil.getViewClassName(i) + ";\n");
//            result.append("import " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + GeneratorUtil.getViewListenerClassName(i) + ";\n");
//        }

        result.append("\n");
        result.append("import javafx.stage.Window;\n");

        return result;
    }

}
