package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.dto.model.DTOField;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;

public class JavaDTOBuilerCreatorLogic {

    public void createJava(DTODefinition dtoDefinition, File packageDir) throws Exception {
        StringBuilder result = new StringBuilder();
        String className = GeneratorUtil.getDTOBuilderClassName(dtoDefinition);
        File destFile = new File(packageDir, className + GeneratorConstants.JAVA_FILE_ENDING);
        result.append("package " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + ";\n");
        result.append("\n");
        if (containsList(dtoDefinition)) {
            result.append("import java.util.List;\n");
            result.append("\n");
        }

        result.append("public class " + className + " {\n");
        result.append(createConstructMethod(dtoDefinition));
        result.append("}\n");


        destFile.getParentFile().mkdirs();
        FileUtil.writeStringToFile(destFile, result.toString());
    }

    private StringBuilder createConstructMethod(DTODefinition dtoDefinition) throws Exception {
        StringBuilder result = new StringBuilder();
        result.append("    public static " + GeneratorUtil.getDTOClassName(dtoDefinition) + " construct(");
        boolean first = true;
        for (DTOField i : dtoDefinition.getFields().values()) {
        	if (first){
        		first = false;
        	} else {
        		result.append(", ");
        	}
            result.append(getClassNameIncludingOptionalList(i) + " " + i.getID());
        }
        result.append("){\n");
        
        result.append("        " + GeneratorUtil.getDTOClassName(dtoDefinition) + " result = new " + GeneratorUtil.getDTOClassName(dtoDefinition) + "();\n");
        for (DTOField i : dtoDefinition.getFields().values()) {
        	result.append("        result.set" + BaseUtil.idToFirstCharUpperCase(i.getID()) + "(" + i.getID() + ");\n"); 
        }
        
        result.append("        return result;\n");
        result.append("    }\n");
        return result;
    }

    
    private boolean containsList(DTODefinition dtoDefinition) {
        for (DTOField i : dtoDefinition.getFields().values()) {
            if (i.isList()) {
                return true;
            }
        }
        return false;
    }


    private String getClassNameIncludingOptionalList(DTOField field) throws Exception {
        String className = getClassName(field);
        if (field.isList()) {
            return "List<" + Character.toUpperCase(className.charAt(0)) + className.substring(1) + ">"; //: use Boolean and Long instead of boolean, long
        } else {
            return className;
        }
    }

    private String getClassName(DTOField field) throws Exception {
        if (field.getBasicType() != null) {
            if (field.getBasicType() == BasicType.BOOLEAN) {
                return "boolean";
            } else if (field.getBasicType() == BasicType.LONG) {
                return "long";
            } else if (field.getBasicType() == BasicType.STRING) {
                return "String";
            } else {
                throw new Exception("Unknonwn basic field type: " + field.getBasicType());
            }
        }
        return GeneratorUtil.getDTOClassName(field.getDTOType());
    }



    private StringBuilder createGetterAndSetterMethods(DTODefinition dtoDefinition) throws Exception {
        StringBuilder result = new StringBuilder();

        for (DTOField field : dtoDefinition.getFields().values()) {
            createGetter(result, field);
            createSetter(result, field);
        }

        return result;
    }

    private void createSetter(StringBuilder result, DTOField field) throws Exception {
        String methodName = BaseUtil.buildIDWithPrefix(field.getID(), "set");
        result.append("    public void " + methodName + "(" + getClassNameIncludingOptionalList(field) + " " + field.getID() + "){\n");
        result.append("        this." + field.getID() + " = " + field.getID() + ";\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createGetter(StringBuilder result, DTOField field) throws Exception {
        String methodName = BaseUtil.buildIDWithPrefix(field.getID(), "get");
        result.append("    public " + getClassNameIncludingOptionalList(field) + " " + methodName + "(){\n");
        result.append("        return " + field.getID() + ";\n");
        result.append("    }\n");
        result.append("\n");
    }

}
