package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.Collection;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.LanguageFlavor;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class ScreenManagerInterfaceCreatorLogic {

    public void generateScreenManagerInterface(Project project, File javaBaseDir, LanguageFlavor languageFlavor) throws Exception {
    	StringBuilder result = new StringBuilder();
    	
		result.append("package generated.fliesenui.core;\n");
		result.append("\n");
		result.append("import java.io.InputStream;\n");
		result.append("import java.io.OutputStream;\n");
		result.append(createImportStatements(project.getScreenDefinitionsMap().values()));
		result.append("\n");
		result.append("public interface FLUIScreenManagerInterface {\n");
		result.append("    String onRequest(String requestJSON, String uploadFileName, InputStream uploadFileInputStream);\n");
		result.append("    FLUIScreenManagerListener getListener();\n");
		result.append("    FLUIImageStream getCustomImageStream(String imageStreamID);\n");
		result.append("    FLUIFileStream getFileStream(String fileStreamID);\n");
		result.append("    void writeResource(String relativeLocation, OutputStream outputStream) throws Exception;\n");
		result.append("    String getStartWebPageAsString() throws Exception;\n");
		result.append("    String getResourceAsString(String relativeLocation) throws Exception;\n");
		result.append(createPresenterSetters(project));
		result.append("}\n");
    	
    	
        File packageDir = GeneratorUtil.getCorePackageDir(javaBaseDir);
        File destFile = new File(packageDir, "FLUIScreenManagerInterface.java");
		
        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
        destFile.setWritable(false);
    }

	private StringBuilder createPresenterSetters(Project project) {
		StringBuilder result = new StringBuilder();
        for (ScreenDefinition i : project.getScreenDefinitionsMap().values()) {
            String classListnerName = GeneratorUtil.getViewListenerClassName(i);
            result.append("    void set" + BaseUtil.idToFirstCharUpperCase(i.getID()) + "Presenter(" + classListnerName + " presenter);\n");
        }
        return result;
	}
	
    private StringBuilder createImportStatements(Collection<ScreenDefinition> screenDefinitions) {
        StringBuilder result = new StringBuilder();
        for (ScreenDefinition i : screenDefinitions) {
            result.append("import " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + GeneratorUtil.getViewListenerClassName(i) + ";\n");
        }
        return result;
    }
    


}
