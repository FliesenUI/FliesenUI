package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.Map;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.stringres.model.StringResource;
import com.bright_side_it.fliesenui.stringres.model.StringResourceItem;

public class StringClassCreatorLogic {
	public void generateStringClass(Project project, File javaBaseDir) throws Exception {
		StringBuilder result = new StringBuilder();
        File packageDir = GeneratorUtil.getCorePackageDir(javaBaseDir);
        String className = GeneratorConstants.FLUI_STRING_CLASS_NAME;
        File destFile = new File(packageDir, className + ".java");

        result.append("package " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ";\n\n");
        result.append("\n");
        result.append("import java.util.Map;\n");
        result.append("import java.util.HashMap;\n");
        result.append("\n");
        result.append("public class " + className + "{\n");
        result.append("    private static final Map<String, String> STRINGS = createStringsMap();\n"); 
        result.append("\n"); 
        result.append(createGetStringMethods()); 
        result.append(createStringLanguageEnum(project)); 
        result.append(createStringIDEnum(project)); 
        result.append(createStringsMapMethod(project)); 
        result.append("}\n");
        
        destFile.getParentFile().mkdirs();
        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
        destFile.setWritable(false);

	}
	
	private StringBuilder createGetStringMethods() {
		StringBuilder result = new StringBuilder();
		result.append("    public static String getString(StringLanguage language, StringID id){\n");
		result.append("        String result = STRINGS.get(language + \":\" + id);\n");
		result.append("        if (result == null){\n");
		result.append("            result = STRINGS.get(\"" + BaseConstants.DEFAULT_LANGUAGE_ID + ":\" + id);\n");
		result.append("        }\n");
		result.append("        return result;\n");
		result.append("    }\n");
		result.append("\n");
		result.append("    /** @param formatArgs are used just like in String.format */\n");
		result.append("    public static String getString(StringLanguage language, StringID id, Object... formatArgs){\n");
		result.append("        String result = getString(language, id);\n");
		result.append("        if (result == null){\n");
		result.append("            return null;\n");
		result.append("        }\n");
		result.append("        return String.format(result, formatArgs);\n");
		result.append("    }\n");
		result.append("\n");
		result.append("    public static String getString(FLUIAbstractReply reply, StringID id){\n");
		result.append("        return getString(reply.getCurrentLanguage(), id);\n");
		result.append("    }\n");
		result.append("\n");
		result.append("    /** @param formatArgs are used just like in String.format */\n");
		result.append("    public static String getString(FLUIAbstractReply reply, StringID id, Object... formatArgs){\n");
		result.append("        return getString(reply.getCurrentLanguage(), id, formatArgs);\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createStringLanguageEnum(Project project) {
		StringBuilder result = new StringBuilder();
		result.append("    public enum StringLanguage{");
		boolean first = true;
		for (String i: project.getStringResourceMap().keySet()){
			if (first){
				first = false;
			} else {
				result.append(", ");
			}
			result.append("" + i);	
		}
		
		result.append("}\n\n");
		return result;
	}
	
	private StringBuilder createStringIDEnum(Project project) {
		StringBuilder result = new StringBuilder();
		result.append("    public enum StringID{\n");
		boolean first = true;
		StringResource defaultLanguageStrings = project.getStringResourceMap().get(BaseConstants.DEFAULT_LANGUAGE_ID);
		if (defaultLanguageStrings != null){
			for (String i: BaseUtil.toEmptyMapIfNull(defaultLanguageStrings.getStrings()).keySet()){
				if (first){
					result.append("          ");
					first = false;
				} else {
					result.append("\n        , ");
				}
				result.append(BaseUtil.toStringEnumID(i));	
			}
		}
		result.append("    }\n\n");
		
		
		return result;
	}

	private StringBuilder createStringsMapMethod(Project project) {
		StringBuilder result = new StringBuilder();
		result.append("    private static Map<String, String> createStringsMap(){\n");
		result.append("        Map<String, String> m = new HashMap<String, String>();\n");
		for (String language: project.getStringResourceMap().keySet()){
			for (Map.Entry<String, StringResourceItem> item: project.getStringResourceMap().get(language).getStrings().entrySet()){
				result.append("        m.put(\""  + language + ":" + BaseUtil.toStringEnumID(item.getKey()) + "\", \"" + item.getValue().getString() + "\");\n");
			}
		}
		result.append("        return m;\n");
		result.append("    }\n\n");
		return result;
	}
}
