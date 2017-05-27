
package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTOField;
import com.bright_side_it.fliesenui.generator.model.ReplyToCallTranslationParameter;
import com.bright_side_it.fliesenui.generator.model.ReplyToCallTranslationParameter.DataType;
import com.bright_side_it.fliesenui.generator.model.RequestToCallTranslation;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JavaTestWriterCreatorLogic {
    public void createJava(Project project, File javaBaseDir) throws Exception {
    	Collection<ScreenDefinition> screenDefinitions = project.getScreenDefinitionsMap().values();
        StringBuilder result = new StringBuilder();
        File packageDir = GeneratorUtil.getCorePackageDir(javaBaseDir);
        String className = GeneratorConstants.TEST_WRITER_CLASS_NAME;
        
        File destFile = new File(packageDir, className + ".java");

        result.append("package " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ";\n\n");
        result.append("import com.google.gson.Gson;\n");
        result.append("import java.util.Map;\n");
        result.append("import java.util.List;\n");
        result.append("import java.util.ArrayList;\n");
        result.append("import java.util.TreeMap;\n");
        result.append("import java.util.Set;\n");
        result.append("import java.util.HashSet;\n");
        result.append("import java.io.ByteArrayOutputStream;\n");
		result.append("import java.io.InputStream;\n");
		result.append("import java.nio.charset.Charset;\n");
		result.append("import java.io.OutputStream;\n");
        result.append("\n");
        result.append(createImportStatements(BaseUtil.toEmptyMapIfNull(project.getDTODefinitionsMap()).values()));
        result.append("\n");
        result.append("public class " + className + "{\n");
        result.append("    private int dtoIndex = 0;\n");
        result.append("    private int listIndex = 0;\n");
        result.append("    private Gson gson = new Gson();\n");
        result.append("    private Set<String> declaredCreateStepDTOMethods = new HashSet<String>();\n");
        result.append("\n");
        result.append(createWriteRequestMethod(project, screenDefinitions));
        result.append(createCreateDTOMethods(project, screenDefinitions));
        result.append(createCreateSetDTODataCodeMethods(project));
        result.append(createCreateSetDTODataCodeByObjectMethod(project));
        result.append(createDTOInstanceByClassNameMethod(project));
        result.append(createCreateScreenFactoryMethods(project, screenDefinitions));
        result.append(createCreateScreenDTOMethods(project, screenDefinitions));
        result.append(createCreateScreenActionDTOMethods(project, screenDefinitions));
        result.append(createCreateSetDTOFLUIClientPropertiesDTOMethod());
        result.append(createGenerateImportsMethod(project));
        result.append(createGenerateSetPresenterStubsMethod(project));
        result.append(createGetNextDTOIndexMethod());
        result.append(createGetNextListIndexMethod());
        result.append(createQuoteIfNotNullMethod());
        result.append(createToStringListMethod());
        result.append("}");
        destFile.getParentFile().mkdirs();

//        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
        destFile.setWritable(false);
    }
    
	private StringBuilder createGenerateImportsMethod(Project project) {
    	StringBuilder result = new StringBuilder();
    	result.append("    public StringBuilder generateImports(){\n");
    	result.append("        StringBuilder result = new StringBuilder();\n");
    	
    	for (DTODefinition i: BaseUtil.toEmptyMapIfNull(project.getDTODefinitionsMap()).values()){
    		result.append("        result.append(\"import " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + "." + GeneratorUtil.getDTOClassName(i) + ";\\n\");\n");
    	}
    	for (ScreenDefinition i: BaseUtil.toEmptyMapIfNull(project.getScreenDefinitionsMap()).values()){
    		result.append("        result.append(\"import " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + GeneratorUtil.getRequestClassName(i) + ";\\n\");\n");
    		result.append("        result.append(\"import " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + GeneratorUtil.getReplyClassName(i) + ";\\n\");\n");
    	}
    	result.append("         return result;\n");
    	result.append("    }\n");
    	result.append("\n");
		return result;
	}
	
    private StringBuilder createGenerateSetPresenterStubsMethod(Project project) {
    	StringBuilder result = new StringBuilder();
    	result.append("    public StringBuilder generateSetPresenterStubs(){\n");
    	result.append("        StringBuilder result = new StringBuilder();\n");
    	
    	for (ScreenDefinition i: BaseUtil.toEmptyMapIfNull(project.getScreenDefinitionsMap()).values()){
    		String presenterID = BaseUtil.idToFirstCharUpperCase(i.getID());
    		result.append("        result.append(\"        screenManager.set" + presenterID + "Presenter(/* TODO: Add presenter " + presenterID + "*/);\\n\");\n"); 
    	}
    	result.append("         return result;\n");
    	result.append("    }\n");
    	result.append("\n");
		return result;
	}


	private StringBuilder createQuoteIfNotNullMethod() {
    	StringBuilder result = new StringBuilder();
    	result.append("    private String quoteIfNotNull(String string){\n");
    	result.append("        if (string == null){\n");
    	result.append("            return \"null\";\n");
    	result.append("        }\n");
    	result.append("        return \"\\\"\" + string + \"\\\"\";\n");
    	result.append("    }\n");
    	result.append("\n");
		return result;
	}
	
	private StringBuilder createToStringListMethod() {
    	StringBuilder result = new StringBuilder();
    	result.append("    private String toStringList(List<String> list){\n");
    	result.append("        StringBuilder result = new StringBuilder();\n");
    	result.append("        if (list == null){\n");
    	result.append("            return \"null\";\n");
    	result.append("        }\n");
    	result.append("        result.append(\"Arrays.asList(\");\n");
    	result.append("        boolean first = true;\n");
    	result.append("        for (String i: list){\n");
    	result.append("            if (first){\n");
    	result.append("                first = false;\n");
    	result.append("            } else {\n");
    	result.append("                result.append(\", \");\n");
    	result.append("            }\n");
    	result.append("            if (i == null){\n");
    	result.append("                result.append(\"null\");\n");
    	result.append("            } else {\n");
    	result.append("                result.append(\"\\\"\" + i + \"\\\"\");\n");
    	result.append("            }\n");
    	result.append("        }\n");
    	result.append("        result.append(\")\");\n");
    	result.append("        return result.toString();\n");
    	result.append("    }\n");
    	result.append("\n");
		return result;
	}
	

	private StringBuilder createCreateSetDTOFLUIClientPropertiesDTOMethod() {
    	StringBuilder result = new StringBuilder();
    	result.append("    private StringBuilder createSetDTOFLUIClientPropertiesDTO(String indent, String prefix, int loopDepth, String currentDTOName, FLUIClientPropertiesDTO dto){\n");
    	result.append("         StringBuilder result = new StringBuilder();\n");
    	result.append(createFLUIClientPropertiesDTOLine("userAgent", true));
    	result.append(createFLUIClientPropertiesDTOLine("navigatorLanguage", true));
    	result.append(createFLUIClientPropertiesDTOLine("screenAvailableWidthInPixels", false));
    	result.append(createFLUIClientPropertiesDTOLine("screenAvailableHeightInPixels", false));
    	result.append(createFLUIClientPropertiesDTOLine("screenWidthInPixels", false));
    	result.append(createFLUIClientPropertiesDTOLine("screenHeightInPixels", false));
    	result.append(createFLUIClientPropertiesDTOLine("windowInnerWidthInPixels", false));
    	result.append(createFLUIClientPropertiesDTOLine("windowInnerHeightInPixels", false));
    	result.append(createFLUIClientPropertiesDTOLine("pixelHeightPerCM", false));
    	result.append(createFLUIClientPropertiesDTOLine("pixelWidthPerCM", false));
    	result.append(createFLUIClientPropertiesDTOLine("pixelHeightPerInch", false));
    	result.append(createFLUIClientPropertiesDTOLine("pixelWidthPerInch", false));
    	result.append(createFLUIClientPropertiesDTOLine("screenWidthInCM", false));
    	result.append(createFLUIClientPropertiesDTOLine("screenHeightInCM", false));
    	result.append(createFLUIClientPropertiesDTOLine("screenWidthInInch", false));
    	result.append(createFLUIClientPropertiesDTOLine("screenHeightInInch", false));
    	result.append(createFLUIClientPropertiesDTOLine("screenDiagonalInInch", false));
    	result.append(createFLUIClientPropertiesDTOLine("errorMessage", true));
    	result.append("         return result;\n");
    	result.append("    }\n");
    	result.append("\n");
		return result;
	}

	private String createFLUIClientPropertiesDTOLine(String memberName, boolean quotesNeeded) {
//		String quote = "";
//		if (quotesNeeded){
//			quote = "\\\"";
//		}
//		return "         result.append(indent + prefix + \".set" + BaseUtil.idToFirstCharUpperCase(memberName) + "(" + quote + "\" + dto.get" + BaseUtil.idToFirstCharUpperCase(memberName) +"() + \"" + quote + ");\\n\");\n";
		String quoteMethodStart = "";
		String quoteMethodEnd = "";
		if (quotesNeeded){
			quoteMethodStart = "quoteIfNotNull(";
			quoteMethodEnd = ")";
		}
		return "         result.append(indent + prefix + \".set" + BaseUtil.idToFirstCharUpperCase(memberName) + "(\" + " + quoteMethodStart + "dto.get" + BaseUtil.idToFirstCharUpperCase(memberName) +"()" + quoteMethodEnd + " + \");\\n\");\n";
	}
	
	private StringBuilder createGetNextListIndexMethod() {
    	StringBuilder result = new StringBuilder();
    	result.append("    private int getNextListIndex(){\n");
    	result.append("        return ++listIndex;\n");
    	result.append("    }\n");
    	result.append("\n");
		return result;
	}

	private StringBuilder createGetNextDTOIndexMethod() {
    	StringBuilder result = new StringBuilder();
    	result.append("    private int getNextDTOIndex(){\n");
    	result.append("        return ++dtoIndex;\n");
    	result.append("    }\n");
    	result.append("\n");
		return result;
	}

	private StringBuilder createImportStatements(Collection<DTODefinition> dtos) {
        StringBuilder result = new StringBuilder();
        for (DTODefinition i : dtos) {
            result.append("import " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + "." + GeneratorUtil.getDTOClassName(i) + ";\n");
        }
        return result;
    }

	private StringBuilder createCreateSetDTODataCodeMethods(Project project) throws Exception {
		StringBuilder result = new StringBuilder();
		for (DTODefinition i: BaseUtil.toEmptyMapIfNull(project.getDTODefinitionsMap()).values()){
			result.append(createCreateSetDTODataCodeMethod(project, i));
		}
		return result;
	}
	
	private StringBuilder createCreateSetDTODataCodeByObjectMethod(Project project) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    public StringBuilder createSetDTOObjectCode(String indent, String prefix, int loopDepth, String currentDTOName, Object dto, String className){\n");
//		result.append("        String className = dto.getClass().getSimpleName();\n");
		boolean first = true;
		for (DTODefinition i: BaseUtil.toEmptyMapIfNull(project.getDTODefinitionsMap()).values()){
			if (first){
				first = false;
				result.append("        ");
			} else {
				result.append("        } else ");
			}
			String methodName = getCreateSetDTOMethodName(i);
			String dtoClassName = GeneratorUtil.getDTOClassName(i);
			result.append("if (className.equals(\"" + dtoClassName + "\")){\n");
			result.append("            return " + methodName + "(indent, prefix, loopDepth, currentDTOName, (" + dtoClassName + ") dto);\n");
		}
		if (!first){
			result.append("        }\n");
		}
		result.append("        return null;\n");
		result.append("    }\n");
		return result;
	}

	private StringBuilder createDTOInstanceByClassNameMethod(Project project) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    public Object createDTOInstanceByClassName(String className){\n");
		boolean first = true;
		for (DTODefinition i: BaseUtil.toEmptyMapIfNull(project.getDTODefinitionsMap()).values()){
			if (first){
				first = false;
				result.append("        ");
			} else {
				result.append("        } else ");
			}
			String dtoClassName = GeneratorUtil.getDTOClassName(i);
			result.append("if (className.equals(\"" + dtoClassName + "\")){\n");
			result.append("            return new " + dtoClassName + "();\n");
		}
		if (!first){
			result.append("        }\n");
		}
		result.append("        return null;\n");
		result.append("    }\n");
		return result;
	}


	
	private StringBuilder createCreateSetDTODataCodeMethod(Project project, DTODefinition dtoDefinition) throws Exception {
		StringBuilder result = new StringBuilder();
		String methodName = getCreateSetDTOMethodName(dtoDefinition);
		result.append("    public StringBuilder " + methodName + "(String indent, String prefix, int loopDepth, String currentDTOName, " + GeneratorUtil.getDTOClassName(dtoDefinition) + " dto){\n");
		result.append("         StringBuilder result = new StringBuilder();\n");
		result.append("         String dtoName;\n");
		result.append("         String listName;\n");
		for (Entry<String, DTOField> i: dtoDefinition.getFields().entrySet()){
			String keyFirstUpperCase = BaseUtil.idToFirstCharUpperCase(i.getKey());
			
			if (i.getValue().isList()) {
				String listItemClassName;
				result.append("         listName = \"list\" + getNextListIndex();\n");
				if (i.getValue().getDTOType() == null){
					listItemClassName = GeneratorUtil.toJavaClassStringForLists(i.getValue().getBasicType());
				} else {
					listItemClassName = GeneratorUtil.getDTOClassName(i.getValue().getDTOType());
				}
				result.append("         result.append(\"\\n\");\n");
				result.append("         if (dto.get" + keyFirstUpperCase + "() != null){\n");
				
				result.append("             result.append(indent + \"List<" + listItemClassName + "> \" + listName + \" = new ArrayList<" + listItemClassName + ">();\\n\");\n");
				result.append("             result.append(indent + prefix + \".set" + keyFirstUpperCase + "(\" + listName + \");\\n\");\n");
				
				result.append("             for (" + listItemClassName + " i : dto.get" + keyFirstUpperCase + "()){\n");
				if (i.getValue().getDTOType() == null){
					if (i.getValue().getBasicType() == BasicType.STRING){
						result.append("                 result.append(indent + listName + \".add(\\\"\" + i + \"\\\");\\n\");\n");
					} else {
						result.append("                 result.append(indent + listName + \".add(\" + i + \");\\n\");\n");
					}
				} else {
					String subDTOClassName = GeneratorUtil.getDTOClassName(i.getValue().getDTOType());
					String subDTOMethodName = getCreateSetDTOMethodName(project.getDTODefinitionsMap().get(i.getValue().getDTOType()));
					result.append("                 dtoName = \"dto\" + getNextDTOIndex();\n");
					result.append("                 result.append(\"\\n\");");
					result.append("                 result.append(indent + \"" + subDTOClassName + " \" + dtoName + \" = new " + subDTOClassName + "();\\n\");\n");
					result.append("                 result.append(indent + listName + \".add(\" + dtoName + \");\\n\");\n");
					result.append("                 result.append(" + subDTOMethodName + "(indent, dtoName, loopDepth + 1, dtoName, i));\n");
					result.append("                 result.append(\"\\n\");");
				}
				result.append("             }\n");
				result.append("         } else {\n");
				result.append("             result.append(indent + prefix + \".set" + keyFirstUpperCase + "(null);\\n\");\n");
				result.append("         }\n");
				
//				result.append("         //!!!! IMPLEMENT ME!!!!! (List of non-DTOs...)\n");
//				result.append("         result.append(indent + prefix + \".set" + keyFirstUpperCase + "(new ArrayList<" + listItemClassName + ">();\");\n");
//				result.append("         result.append(indent + \"for (" + listItemClassName + " i\" + loopDepth + \" : dto.get" + keyFirstUpperCase + "()){\n");
//				result.append("         result.append(indent + \"    \" + prefix + \".get" + keyFirstUpperCase + "().add(dto.get" + keyFirstUpperCase + "().add(i + \"loopDepth\" + \");\n");
//				result.append("         result.append(indent + \"}\");\n");
//			} else if ((i.getValue().isList()) && (i.getValue().getDTOType() != null)){
//				String listItemClassName = GeneratorUtil.getDTOClassName(i.getValue().getDTOType());
//				String subDTOMethodName = getCreateSetDTOMethodName(dtoDefinition);
//				result.append("         result.append(indent + prefix + \".set" + keyFirstUpperCase + "(new ArrayList<" + listItemClassName + ">();\");\n");
//				result.append("         result.append(indent + \"for (int " + listItemClassName + " i\" + loopDepth + \" = 0; i\" + loopDepth + \" < dto.get" + keyFirstUpperCase + "().size(); i\" + loopDepth + \"++){\\n\");\n");
//				result.append("         result.append(indent + \"    \" + (" + subDTOMethodName + "(indent + \"    \", prefix + \".get" + keyFirstUpperCase + "().add(\", loopDepth + 1, dto.get" + keyFirstUpperCase + "().get(i + \" + loopDepth + \");\n");
//				result.append("         result.append(indent + \"}\");\n");
//				result.append("         //!!!! IMPLEMENT ME!!!!! (List of DTOs...)\n");
			} else if (i.getValue().getDTOType() != null){
				String subDTOMethodName = getCreateSetDTOMethodName(project.getDTODefinitionsMap().get(i.getValue().getDTOType()));
				String subDTOClassName = GeneratorUtil.getDTOClassName(i.getValue().getDTOType());
				result.append("         dtoName = \"dto\" + getNextDTOIndex();\n");
				result.append("         result.append(indent + \"" + subDTOClassName + " \" + dtoName + \" = new " + subDTOClassName + "();\\n\");\n");
				result.append("         result.append(indent + prefix + \".set" + keyFirstUpperCase + "(\" + dtoName + \");\\n\");\n");
				result.append("         result.append(" + subDTOMethodName + "(indent, dtoName, loopDepth + 1, dtoName, dto.get" + keyFirstUpperCase + "()));\n");
//				result.append("         result.append(" + subDTOMethodName + "(indent + \"    \", prefix, loopDepth + 1, dto.get" + keyFirstUpperCase + "());\n");
			} else {
				String valuePrefix = getTextPrefix(i.getValue().getBasicType());
				String valueSuffix = getTextSuffix(i.getValue().getBasicType());
				String valueCast = getTypeCast(i.getValue().getBasicType());
//				result.append("\" + " + prefix + typeCast + "p.get(\"" + i.getKey() + "\")" + suffix + " + \"");
				result.append("         result.append(indent + prefix + \".set" + keyFirstUpperCase + "(\" + " + valuePrefix + valueCast + "dto.get" + keyFirstUpperCase + "()" + valueSuffix + " + \");\\n\");\n");
			}
		}
		result.append("         return result;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private String getCreateSetDTOMethodName(DTODefinition dtoDefinition) {
		return getCreateSetDTOMethodName(GeneratorUtil.getDTOClassName(dtoDefinition));
	}

	private String getCreateSetDTOMethodName(String dtoClassName) {
		return "createSetDTO" + dtoClassName;
	}
	
	private StringBuilder createCreateScreenActionDTOMethods(Project project, Collection<ScreenDefinition> screenDefinitions) throws Exception{
		StringBuilder result = new StringBuilder();
		for (ScreenDefinition i: screenDefinitions){
			result.append(createCreateScreenActionDTOMethods(project, i));
		}
		return result;
	}

	private StringBuilder createCreateScreenActionDTOMethods(Project project, ScreenDefinition screenDefinition) throws Exception{
		StringBuilder result = new StringBuilder();
		List<RequestToCallTranslation> translations = new RequestToCallTranslationBuilder().buildTranslations(project, screenDefinition);
		
		for (RequestToCallTranslation i: translations){
			result.append(createCreateScreenActionDTOMethods(project, screenDefinition, i));
		}
		return result;
	}

	private StringBuilder createCreateScreenActionDTOMethods(Project project, ScreenDefinition screenDefinition, RequestToCallTranslation translation) {
		StringBuilder result = new StringBuilder();

		int dtoIndex = 1;
		for (ReplyToCallTranslationParameter i: BaseUtil.toEmptyCollectionIfNull(translation.getParameter())){
//			result.append(", ");
			if (i.getDTOClassName() != null){
				result.append(createCreateScreenActionDTOMethod(project, screenDefinition, translation, dtoIndex, i));
				dtoIndex ++;
			}
		}
//		result.append(")");
		return result;
	}

	private StringBuilder createCreateScreenActionDTOMethod(Project project, ScreenDefinition screenDefinition,
			RequestToCallTranslation translation, int dtoIndex, ReplyToCallTranslationParameter parameter) {
		StringBuilder result = new StringBuilder();
		String setDTOMethodName = getCreateSetDTOMethodName(parameter.getDTOClassName());
		result.append("    private StringBuilder createScreen" + BaseUtil.idToFirstCharUpperCase(screenDefinition.getID()) + "DTO" + dtoIndex + "MethodForAction" + BaseUtil.idToFirstCharUpperCase(translation.getActionName()) + "(int step, FLUIRequest requestData){\n");
		result.append("        StringBuilder result = new StringBuilder();\n");
//		result.append("        String dtoString = (String)requestData.getParameters().get(\"" + parameter.getKey() + "\");\n");
		result.append("        " + parameter.getDTOClassName() + " dto = gson.fromJson((String)requestData.getParameters().get(\"" + parameter.getKey() + "\"), " + parameter.getDTOClassName() + ".class);\n");
		result.append("        String declaration = \"private " + parameter.getDTOClassName() + " " + createCreateStepDTOMethodName(dtoIndex, parameter.getKey()) + "()\";\n");
		result.append("        if (declaredCreateStepDTOMethods.contains(declaration)){\n"); //: it is possible that the method would be created multiple times if the DTO occurs in multiple actions
		result.append("            return result;\n");
		result.append("        }\n");
		result.append("        declaredCreateStepDTOMethods.add(declaration);\n");
		result.append("        result.append(\"    \" + declaration + \"{\\n\");\n");
		result.append("        result.append(\"        " + parameter.getDTOClassName() + " result = new " + parameter.getDTOClassName() + "();\\n\");\n");
		result.append("        result.append(" + setDTOMethodName + "(\"        \", \"result\", 0, \"result\", dto));\n");
		result.append("        result.append(\"        return result;\\n\");\n");
		result.append("        result.append(\"    }\\n\");\n");
		result.append("        result.append(\"\\n\");\n");
		result.append("        return result;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createCreateScreenDTOMethods(Project project, Collection<ScreenDefinition> screenDefinitions) throws Exception {
		StringBuilder result = new StringBuilder();
		for (ScreenDefinition i: screenDefinitions){
			result.append(createCreateScreenDTOMethods(project, i));
		}
		return result;
	}

	private StringBuilder createCreateScreenDTOMethods(Project project, ScreenDefinition screenDefinition) throws Exception{
		StringBuilder result = new StringBuilder();
		result.append("    private StringBuilder createScreen" + BaseUtil.idToFirstCharUpperCase(screenDefinition.getID()) + "DTOMethods(int step, FLUIRequest requestData) throws Exception{\n");
		result.append("        StringBuilder result = new StringBuilder();\n");
		List<RequestToCallTranslation> translations = new RequestToCallTranslationBuilder().buildTranslations(project, screenDefinition);
		boolean first = true;
        for (RequestToCallTranslation translation: translations){
    		String elseString = "";
    		if (first){
    			first = false;
    		} else elseString = "} else ";
    		result.append("        " + elseString + "if (requestData.getAction().equals(\"" + translation.getActionName() + "\")){\n");

    		int dtoIndex = 1;
    		for (ReplyToCallTranslationParameter param: BaseUtil.toEmptyCollectionIfNull(translation.getParameter())){
    			if (param.getDTOClassName() != null){
					result.append("            result.append(createScreen" + BaseUtil.idToFirstCharUpperCase(screenDefinition.getID()) + "DTO" + dtoIndex + "MethodForAction" + BaseUtil.idToFirstCharUpperCase(translation.getActionName()) + "(step, requestData));\n");
    				dtoIndex ++;
    			}
    		}
        }
        result.append("        } else {\n");
        result.append("            throw new Exception(\"Unexpected action: '\" + requestData.getAction() + \"'\");\n");
        result.append("        }\n");
        result.append("        return result;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createCreateDTOMethods(Project project, Collection<ScreenDefinition> screenDefinitions) {
		StringBuilder result = new StringBuilder();
		result.append("    public StringBuilder writeCreateDTOMethods(int step, FLUIRequest requestData) throws Exception{\n");
		result.append("        StringBuilder result = new StringBuilder();\n");
		boolean first = true;
		for (ScreenDefinition i: screenDefinitions){
			String elseString = "";
			if (first){
				first = false;
			} else{
				elseString = "} else ";
			}
			result.append("        " + elseString + "if (requestData.getScreenID().equals(\"" + i.getID() + "\")){\n");
			result.append("            result.append(createScreen" + BaseUtil.idToFirstCharUpperCase(i.getID()) + "DTOMethods(step, requestData));\n"); 
		}
		result.append("        } else {\n");
		result.append("            throw new Exception(\"Unknown screen id: '\" + requestData.getScreenID() + \"'\");\n");
		result.append("        }\n");
		result.append("        return result;\n");
		result.append("    }\n");
		return result;
	}

	private StringBuilder createCreateScreenFactoryMethods(Project project, Collection<ScreenDefinition> screenDefinitions) throws Exception {
		StringBuilder result = new StringBuilder();
		for (ScreenDefinition i: screenDefinitions){
			result.append(createCreateScreenFactoryMethod(project, i));
		}
		return result;
	}

	private StringBuilder createCreateScreenFactoryMethod(Project project, ScreenDefinition screenDefinition) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    private String createScreen" + BaseUtil.idToFirstCharUpperCase(screenDefinition.getID()) + "FactoryMethod(int step, FLUIRequest requestData) throws Exception{\n");
		result.append("        Map<String, Object> p = requestData.getParameters();\n");
		List<RequestToCallTranslation> translations = new RequestToCallTranslationBuilder().buildTranslations(project, screenDefinition);
		boolean first = true;
        for (RequestToCallTranslation i: translations){
        	String elseString = "";
			if (first){
				first = false;
			} else elseString = "} else ";
        	result.append("        " + elseString + "if (requestData.getAction().equals(\"" + i.getActionName() + "\")){\n");
        	result.append("            return " + createFactoryMethodForTranslation(screenDefinition, i) + "\";\n");
        }
        result.append("        } else {\n");
        result.append("            throw new Exception(\"Unexpected action: '\" + requestData.getAction() + \"'\");\n");
        result.append("        }\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}
	
	private String createCreateStepDTOMethodName(int dtoIndex, String key){
		return "createStep\" + step + \"DTO" + dtoIndex + BaseUtil.idToFirstCharUpperCase(key);
	}
	
	private String getTextPrefix(BasicType basicType) {
		if (basicType == BasicType.STRING){
			return "quoteIfNotNull(";
		}
		return "";
	}

	private String getTextSuffix(BasicType basicType) {
		if (basicType == BasicType.STRING){
			return ")";
		}
		return "";
	}

	private String getTypeCast(BasicType basicType) {
		if (basicType == BasicType.STRING){
			return "(String)";
		}
		return "";
	}

	private String getTextPrefix(DataType dataType){
		if (dataType == DataType.STRING){
			return "quoteIfNotNull(";
		} else if (dataType == DataType.CHARACTER){
			return "\"'\" + ";
		}
		return "";
	}
	
	private String getTextSuffix(DataType dataType){
		if (dataType == DataType.STRING){
			return ")";
		} else if (dataType == DataType.CHARACTER){
			return " + \"'\"";
		}
		return "";
	}

	private String getTypeCast(DataType dataType) {
		if (dataType == DataType.STRING){
			return "(String)";
		} else if (dataType == DataType.CHARACTER){
			return "(Character)";
		}
		return "";
	}
	
	private StringBuilder createFactoryMethodForTranslation(ScreenDefinition screenDefinition, RequestToCallTranslation translation) {
		StringBuilder result = new StringBuilder();
		result.append("\"create" + BaseUtil.idToFirstCharUpperCase(translation.getMethodName()) + "Request(");
		result.append("\" + quoteIfNotNull(requestData.getCurrentLanguage()) + \"");
		int dtoIndex = 1;
		for (ReplyToCallTranslationParameter i: BaseUtil.toEmptyCollectionIfNull(translation.getParameter())){
			result.append(", ");
			if (i.getDTOClassName() != null){
				result.append(createCreateStepDTOMethodName(dtoIndex, i.getKey()) + "()");
				dtoIndex ++;
			} else if (i.getDataType() == DataType.LIST_OF_STRING){
				result.append("\" + " + "toStringList((List<String>)p.get(\"" + i.getKey() + "\"))" + " + \"");

//	            return "createOnListChooserResultRequest(" + quoteIfNotNull(requestData.getCurrentLanguage()) + ", " + quoteIfNotNull((String)p.get("referenceID")) + ", " + p.get("selectedIDs") + ")";

				
			} else {
				String prefix = getTextPrefix(i.getDataType());
				String suffix = getTextSuffix(i.getDataType());
				String typeCast = getTypeCast(i.getDataType());
				result.append("\" + " + prefix + typeCast + "p.get(\"" + i.getKey() + "\")" + suffix + " + \"");
			}
		}
		result.append(")");
		return result;
	}

	private StringBuilder createWriteRequestMethod(Project project, Collection<ScreenDefinition> screenDefinitions) {
		StringBuilder result = new StringBuilder();
		result.append("    public StringBuilder writeRequest(int step, FLUIRequest requestData) throws Exception{\n");
		result.append("        StringBuilder result = new StringBuilder();\n");
		result.append("        String requestClassName = Character.toUpperCase(requestData.getScreenID().charAt(0)) + requestData.getScreenID().substring(1) + \"Request\";\n");
		//result.append("        result.append(requestClassName + \" step\" + step + \"Request = \");\n");
		result.append("        result.append(requestClassName + \" request = \");\n");
		boolean first = true;
		for (ScreenDefinition i: screenDefinitions){
			String elseString = "";
			if (first){
				first = false;
			} else{
				elseString = "} else ";
			}
			result.append("        " + elseString + "if (requestData.getScreenID().equals(\"" + i.getID() + "\")){\n");
//			result.append("            result.append(createScreen" + BaseUtil.idToFirstCharUpperCase(i.getID()) + "FactoryMethod(step, requestData) + \";\");\n");
			result.append("            result.append(requestClassName + \".\" + createScreen" + BaseUtil.idToFirstCharUpperCase(i.getID()) + "FactoryMethod(step, requestData) + \";\");\n");
		}
		result.append("        } else {\n");
		result.append("            throw new Exception(\"Unknown screen id: '\" + requestData.getScreenID() + \"'\");\n");
		result.append("        }\n");
		result.append("        return result;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}
}