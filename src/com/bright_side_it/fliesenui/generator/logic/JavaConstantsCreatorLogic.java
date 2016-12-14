package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.TextUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JavaConstantsCreatorLogic {

	public void createJava(Project project, File packageDir) throws Exception {
		createImageAssetsClass(project, packageDir);
//		createScreensClass(project, packageDir);
	}
	
	private void createImageAssetsClass(Project project, File packageDir) throws Exception {
		StringBuilder result = new StringBuilder();

        String className = GeneratorUtil.getImageAssetsClassName();

        File destFile = new File(packageDir, className + GeneratorConstants.JAVA_FILE_ENDING);

        result.append("package " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ";\n");
        result.append("\n");

        result.append("public class " + className + " {\n");
        result.append(createImageAssetsEnum(project));
        result.append("\n");
        result.append("}\n");


        destFile.getParentFile().mkdirs();
        FileUtil.writeStringToFile(destFile, result.toString());
	}

	private void createScreensClass(Project project, File packageDir) throws Exception {
		StringBuilder result = new StringBuilder();
		
		String className = GeneratorUtil.getScreensClassName();
		
		File destFile = new File(packageDir, className + GeneratorConstants.JAVA_FILE_ENDING);
		
		result.append("package " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ";\n");
		result.append("\n");
		
		result.append("public class " + className + " {\n");
		result.append(createScreensEnum(project));
		result.append("\n");
		result.append("}\n");
		
		
		destFile.getParentFile().mkdirs();
		FileUtil.writeStringToFile(destFile, result.toString());
	}
	
	private StringBuilder createImageAssetsEnum(Project project) {
        StringBuilder result = new StringBuilder();
        
        if (BaseUtil.toEmptyMapIfNull(project.getImageAssetDefinitionsMap()).isEmpty()){
        	return result;
        }
        
        result.append("\n");
        result.append("public enum " + GeneratorConstants.IMAGE_ASSET_ENUM_NAME + " {\n");
        boolean first = true;
        for (ImageAssetDefinition i: BaseUtil.toEmptyMapIfNull(project.getImageAssetDefinitionsMap()).values()){
        	String separator = "\n      , ";
        	if (first){
        		separator = "        ";
        		first = false;
        	}
        	result.append(separator + TextUtil.toUppercaseAndUnderscore(i.getID()) + "(\"" + i.getID() + "\", \"" + i.getFilename() + "\")");
        }
        result.append(";\n");
        result.append("\n");
        result.append("        private String id;\n");
        result.append("        private String filename;\n");
        result.append("\n");
        result.append("        " + GeneratorConstants.IMAGE_ASSET_ENUM_NAME + "(String id, String filename) {\n");
        result.append("            this.id = id;\n");
        result.append("            this.filename = filename;\n");
        result.append("        }\n");
        result.append("\n");
        result.append("        public String getID() {\n");
        result.append("            return id;\n");
        result.append("        }\n");
        result.append("\n");
        result.append("        public String getFilename() {\n");
        result.append("            return filename;\n");
        result.append("        }\n");
        result.append("    }\n");
		return result;
	}
	
	private StringBuilder createScreensEnum(Project project) {
		StringBuilder result = new StringBuilder();
		
		if (BaseUtil.toEmptyMapIfNull(project.getScreenDefinitionsMap()).values().isEmpty()){
			return result;
		}
		
		result.append("\n");
		result.append("public enum " + GeneratorConstants.SCREEN_ENUM_NAME + " {\n");
		boolean first = true;
		for (ScreenDefinition i: project.getScreenDefinitionsMap().values()){
			String separator = "\n      , ";
			if (first){
				separator = "        ";
				first = false;
			}
			result.append(separator + TextUtil.toUppercaseAndUnderscore(i.getID()) + "(\"" + i.getID() + "\")");
		}
		result.append(";\n");
		result.append("\n");
		result.append("        private String id;\n");
		result.append("\n");
		result.append("        " + GeneratorConstants.SCREEN_ENUM_NAME + "(String id) {\n");
		result.append("            this.id = id;\n");
		result.append("        }\n");
		result.append("\n");
		result.append("        public String getID() {\n");
		result.append("            return id;\n");
		result.append("        }\n");
		result.append("\n");
		result.append("    }\n");
		return result;
	}

}
