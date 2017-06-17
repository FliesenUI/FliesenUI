package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.TextUtil;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginVariable;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.SharedReplyInterface;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSource;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutBar;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutCell;
import com.bright_side_it.fliesenui.screendefinition.model.LayoutContainer;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.Timer;

public class JavaScreenReplyCreatorLogic {
	
	public List<String> getMethodSignatures(Project project, ScreenDefinition screenDefinition) throws Exception {
		List<String> signatureLines = process(project, screenDefinition, null, false);
		return toSignatures(signatureLines);
	}
	
	private List<String> toSignatures(List<String> signatureLines) {
		List<String> result = new ArrayList<>();
		for (String i: signatureLines){
			result.add(toSignature(i));
		}
		return result;
	}

	private String toSignature(String line) {
		String result = line.trim();
		result = TextUtil.removePrefixIsExisting(result, "public").trim();
		result = TextUtil.removeSuffixIsExisting(result, "{").trim();
		result += ";";
		return result;
	}

	public void createJava(Project project, ScreenDefinition screenDefinition, File screenPackageDir) throws Exception {
		process(project, screenDefinition, screenPackageDir, true);
	}

	/**
	 * 
	 * @param project
	 * @param screenDefinition
	 * @param screenPackageDir
	 * @return list of method signature lines
	 * @throws Exception
	 */
    public List<String> process(Project project, ScreenDefinition screenDefinition, File screenPackageDir, boolean generateCode) throws Exception {
    	List<String> signatures = new ArrayList<>();
        //    	log("createJava for screen " + screenDefinition.getID());
    	addSignatureLinesFromAbstractReply(signatures);
    	
    	
        StringBuilder result = new StringBuilder();

        String className = GeneratorUtil.getReplyClassName(screenDefinition);

        result.append("package " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + ";\n");
        result.append("\n");
        result.append("import java.util.List;\n");
        result.append("import java.util.Collection;\n");
        result.append("import java.util.TreeSet;\n");
        result.append("\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIAbstractReply;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIUtil;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIString.StringLanguage;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".TextHighlighting;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".CursorPos;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".ContextAssist;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".IDLabelImageAssetList;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".IDLabelList;\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIReplyAction.ReplyActionType;\n");
        
        if (imageAssetsExist(project)) {
            result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIImageAssets.ImageAsset;\n");
        }
        result.append("\n");
        result.append(createDTOImportStatements(project, screenDefinition));
        result.append("\n");
        result.append("public class " + className + " extends FLUIAbstractReply" + generateImplementsStatements(project, screenDefinition) + " {\n");
		result.append("    public " + className + "(boolean recordMode, StringLanguage currentLanguage) {\n");
		result.append("        super(recordMode, currentLanguage);\n");
		result.append("    }\n");
		result.append("\n");
        result.append("    protected String getJSON() {\n");
        result.append("        return gson.toJson(replyDTO);\n");
        result.append("    }\n");
        result.append("\n");
        result.append(createVariableSetterMethods(project, screenDefinition, signatures));
        result.append(createDTOSetterMethods(screenDefinition, signatures));
        result.append(createCodeEditorSetterMethods(screenDefinition, signatures));
        result.append(createSelectBoxMethods(screenDefinition, signatures));
        result.append(createTableMethods(screenDefinition, signatures));
        result.append(createOpenScreenMethods(project, signatures));
        result.append("}");

        if (generateCode){
        	File destFile = new File(screenPackageDir, className + GeneratorConstants.JAVA_FILE_ENDING);
        	destFile.getParentFile().mkdirs();
        	result = GeneratorUtil.addJavaGeneratedCommend(result);
        	FileUtil.writeStringToFile(destFile, result.toString());
        }
        
        return signatures;
    }

    private String generateImplementsStatements(Project project, ScreenDefinition screenDefinition) {
    	StringBuilder result = new StringBuilder();
    	for (SharedReplyInterface sharedReplyInterface: BaseUtil.toEmptyMapIfNull(project.getProjectDefinition().getSharedReplyInterfaces()).values()){
    		if (sharedReplyInterface.getScreenIDs().contains(screenDefinition.getID())){
    			if (result.length() == 0){
    				result.append(" implements ");
    			} else {
    				result.append(", ");
    			}
    			result.append(GeneratorUtil.getSharedReplyInterfaceName(sharedReplyInterface));
    		}
    	}
		return result.toString();
	}

	private void addSignatureLinesFromAbstractReply(List<String> signatures) {
    	signatures.add("public void setInfoDialog(String title, String text) {");
    	signatures.add("public void setErrorDialog(String title, String text) {");
    	signatures.add("public void setInfoToast(String text) {");
    	signatures.add("public void setLanguage(StringLanguage language) {");
    	signatures.add("public StringLanguage getCurrentLanguage() {");
    	signatures.add("public void openURL(String url, boolean openInNewWindow) {");
    	signatures.add("public void downloadFile(String fileStreamID){");
    	signatures.add("public void showInputDialog(String referenceID, String title, String textContent, String label, String initialValueText, String okText, String cancelText) {");
    	signatures.add("public void showConfirmDialog(String referenceID, String title, String textContent, String okText, String cancelText) {");
		signatures.add("public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, IDLabelImageAssetList items, Collection<String> selectedIDs){");
		signatures.add("public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, IDLabelList items, Collection<String> selectedIDs){");
		signatures.add("public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, String okText, String cancelText, IDLabelImageAssetList items, Collection<String> selectedIDs){");
		signatures.add("public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, String okText, String cancelText, IDLabelList items, Collection<String> selectedIDs){");
	}

	private StringBuilder createOpenScreenMethods(Project project, List<String> signatures) {
        StringBuilder result = new StringBuilder();
        for (ScreenDefinition i : project.getScreenDefinitionsMap().values()) {
            String parameterString = "";
            if (i.getParameterDTOID() != null) {
                DTODeclaration declaration = i.getDTODeclarations().get(i.getParameterDTOID());
                DTODefinition definition = project.getDTODefinitionsMap().get(declaration.getType());
                parameterString = GeneratorUtil.getDTOClassName(definition) + " " + i.getParameterDTOID();
            }
            String methodName = "openScreen" + BaseUtil.idToFirstCharUpperCase(i.getID());
            appendAndAdd(result, signatures, "    public void " + methodName + "(" + parameterString + ") {\n");
            if (i.getParameterDTOID() != null) {
                result.append("        replyDTO.setOpenParameter(" + i.getParameterDTOID() + ");\n");
            }
            result.append("        replyDTO.setScreenToOpen(\"" + i.getID() + "\");\n");
            result.append("        if (recordMode){\n");
            if (parameterString.isEmpty()){
            	result.append("            addRecordedAction(ReplyActionType.OPEN_SCREEN, \"" + methodName + "(\");\n");
            } else {
            	result.append("            addRecordedAction(ReplyActionType.OPEN_SCREEN, \"" + methodName + "(\", gson.toJson(" + i.getParameterDTOID() + "), getClassName(" + i.getParameterDTOID() + "));\n");
            }
            result.append("        }\n");
            result.append("    }\n");
            result.append("\n");
        }
        result.append("\n");
        return result;
    }


    private boolean imageAssetsExist(Project project) {
        return !BaseUtil.toEmptyMapIfNull(project.getImageAssetDefinitionsMap()).isEmpty();
    }

    private StringBuilder createDTOImportStatements(Project project, ScreenDefinition screenDefinition) {
        StringBuilder result = new StringBuilder();
        for (String i : GeneratorUtil.getRequiredDTOClassNames(project, screenDefinition)) {
            result.append("import " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + "." + i + ";\n");
        }
        return result;
    }

    private void log(String message) {
        System.out.println("JavaScreenReplyCreatorLogic> " + message);
    }

    private StringBuilder createDTOSetterMethods(ScreenDefinition screenDefinition, List<String> signatures) {
        StringBuilder result = new StringBuilder();
        if (screenDefinition.getDTODeclarations() == null) {
            return result;
        }
        for (DTODeclaration i : screenDefinition.getDTODeclarations().values()) {
            result.append(createDTOSetter(i, screenDefinition, signatures));
        }
        result.append("\n");
        return result;
    }

    private StringBuilder createDTOSetter(DTODeclaration dtoDeclaration, ScreenDefinition screenDefinition, List<String> signatures) {
        StringBuilder result = new StringBuilder();
        String dtoClassName = GeneratorUtil.getDTOClassName(dtoDeclaration, screenDefinition);
        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(dtoDeclaration.getID() + "DTO", "set");
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(" + dtoClassName + " dto) {\n");
        result.append("        replyDTO.getDTOsToSet().add(\"" + dtoDeclaration.getID() + "\");\n");
        result.append("        if (dto == null){\n");
        result.append("            replyDTO.getDTOValues().remove(\"" + dtoDeclaration.getID() + "\");\n");
        result.append("        } else {\n");
        result.append("            replyDTO.getDTOValues().put(\"" + dtoDeclaration.getID() + "\", dto);\n");
        result.append("        }\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(ReplyActionType.SET_DTO, \"" + javaSetterMethodName + "(\", gson.toJson(dto), getClassName(dto));\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");

        return result;
    }

    private StringBuilder createVariableSetterMethods(Project project, ScreenDefinition screenDefinition, List<String> signatures) throws Exception {
        StringBuilder result = new StringBuilder();

        for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(screenDefinition)) {
            if (BaseUtil.in(widget.getType(), BasicWidgetType.BUTTON, BasicWidgetType.IMAGE_BUTTON, BasicWidgetType.LABEL, BasicWidgetType.TEXT_AREA,
                    BasicWidgetType.TEXT_FIELD, BasicWidgetType.CHECKBOX, BasicWidgetType.SWITCH, BasicWidgetType.HTML_VIEW)) {
            	if (widget.getID() != null){
            		createTextSetter(result, screenDefinition, widget, signatures);
            	}
            }
            if (widget.getType() == BasicWidgetType.MARKDOWN_VIEW){
            	createMarkdownViewTextSetter(result, screenDefinition, widget, signatures);
            }
            
            if (widget.getType() == BasicWidgetType.PROGRESS_BAR) {
                createProgressSetter(result, screenDefinition, widget, signatures);
                createInditerminateSetter(result, screenDefinition, widget, signatures);
            }
            if (BaseUtil.in(widget.getType(), BasicWidgetType.CHECKBOX, BasicWidgetType.SWITCH)) {
                createSelectedSetter(result, screenDefinition, widget, signatures);
            }
            createVisibleSetter(result, screenDefinition, widget.getID(), signatures);
            if (BaseUtil.in(widget.getType(), BasicWidgetType.MARKDOWN_VIEW, BasicWidgetType.TEXT_AREA, BasicWidgetType.TEXT_FIELD)) {
            	createBackgroundColorSetter(result, screenDefinition, widget, signatures);
            }
        }

        for (LayoutCell i : BaseUtil.getAllLayoutCells(screenDefinition)) {
            createVisibleSetter(result, screenDefinition, i.getID(), signatures);
            if (i.getID() != null){
            	if (i.getHeadlineText() != null){
            		createHeadlineTextSetter(result, screenDefinition, i, signatures);
            	}
            	if (i.getSubheadText() != null){
            		createSubheadTextSetter(result, screenDefinition, i, signatures);
            	}
            }
        }

        for (LayoutBar i : BaseUtil.getAllLayoutBars(screenDefinition)) {
        	createVisibleSetter(result, screenDefinition, i.getID(), signatures);
        }
        
        for (LayoutContainer i : BaseUtil.getAllLayoutContainers(screenDefinition)) {
        	createVisibleSetter(result, screenDefinition, i.getID(), signatures);
        }
        
        for (PluginInstance pluginInstance : BaseUtil.getAllPluginInstances(screenDefinition)) {
            PluginDefinition pluginDefinition = project.getPluginDefinitionsMap().get(pluginInstance.getPluginType());
            for (PluginVariable pluginVariable : BaseUtil.toEmptyCollectionIfNull(pluginDefinition.getVariables().values())) {
                createPluginVariableSetter(result, screenDefinition, pluginInstance, pluginVariable, signatures);
            }
        }

        for (BasicWidget basicWidget : BaseUtil.getAllBasicWidgets(screenDefinition)) {
            createImageSourceSetter(result, screenDefinition, basicWidget, signatures);
        }
        
        for (Timer i: BaseUtil.getAllTimers(screenDefinition)){
        	createTimerActiveSetter(result, screenDefinition, i, signatures);
        }

        result.append("\n");
        return result;
    }


    private void createImageSourceSetter(StringBuilder result, ScreenDefinition screenDefinition, BasicWidget basicWidget, List<String> signatures) {
        ImageSource imageSource = basicWidget.getImageSource();
        if (imageSource == null) {
            return;
        }

        String javaScriptVariableName = GeneratorUtil.getJSImageSourceVariableName(screenDefinition, basicWidget);
        if (imageSource.getImageAssetID() != null) {
        	String methodName = "set" + BaseUtil.idToFirstCharUpperCase(basicWidget.getID()) + "ImageAsset";
        	appendAndAdd(result, signatures, "    public void " + methodName + "(ImageAsset imageAsset) {\n");
            result.append("        replyDTO.getVariablesToSet().add(\"" + javaScriptVariableName + "\");\n");
            result.append("        replyDTO.getVariableValues().put(\"" + javaScriptVariableName + "\", imageAsset.getFilename());\n");
            result.append("        if (recordMode){\n");
            result.append("            addRecordedAction(ReplyActionType.SET_IMAGE_ASSET, \"" + methodName + "(\", imageAsset);\n");
            result.append("        }\n");
            result.append("    }\n");
            result.append("\n");
        } else if (imageSource.getImageStreamID() != null) {
        	String methodName = "set" + BaseUtil.idToFirstCharUpperCase(basicWidget.getID()) + "ImageStreamID";
        	appendAndAdd(result, signatures, "    public void " + methodName + "(String imageStreamID) {\n");
            result.append("        replyDTO.getVariablesToSet().add(\"" + javaScriptVariableName + "\");\n");
            result.append("        replyDTO.getVariableValues().put(\"" + javaScriptVariableName + "\", imageStreamID);\n");
            result.append("        if (recordMode){\n");
            result.append("            addRecordedAction(\"" + methodName + "(\" + escapeString(imageStreamID) + \");\");\n");
            result.append("        }\n");
            result.append("    }\n");
            result.append("\n");
        } else if (imageSource.getImageURL() != null) {
        	String methodName = "set" + BaseUtil.idToFirstCharUpperCase(basicWidget.getID()) + "ImageURL";
        	appendAndAdd(result, signatures, "    public void " + methodName + "(String imageURL) {\n");
            result.append("        replyDTO.getVariablesToSet().add(\"" + javaScriptVariableName + "\");\n");
            result.append("        replyDTO.getVariableValues().put(\"" + javaScriptVariableName + "\", imageURL);\n");
            result.append("        if (recordMode){\n");
            result.append("            addRecordedAction(\"" + methodName + "(\" + escapeString(imageURL) + \");\");\n");
            result.append("        }\n");
            result.append("    }\n");
            result.append("\n");
        }
    }
    
    private StringBuilder createTableMethods(ScreenDefinition screenDefinition, List<String> signatures) {
    	StringBuilder result = new StringBuilder();
    	for (TableWidget i: BaseUtil.getAllTableWidgets(screenDefinition)){
    		if (i.isRowCheckboxes()){
        		String methodName = "set" + BaseUtil.idToFirstCharUpperCase(i.getID()) + "CheckedRowIDs";
        		appendAndAdd(result, signatures, "    public void " + methodName + "(Collection<String> checkedIDs) {\n");
        		String variableName = GeneratorUtil.createJSTableRowCheckedIDVariableName(screenDefinition, i);
    			result.append("        if (checkedIDs == null){\n");
    			result.append("            replyDTO.getTableCheckedRowIDs().put(\"" + variableName + "\", new TreeSet<>());\n");
    			result.append("        } else {\n");
    			result.append("            replyDTO.getTableCheckedRowIDs().put(\"" + variableName + "\", new TreeSet<>(checkedIDs));\n");
    			result.append("        }\n");
    			result.append("        if (recordMode){\n");
    			result.append("            addRecordedAction(ReplyActionType.SET_TABLE_CHECKED_ROW_IDS, \"" + methodName + "(\", checkedIDs);\n");
    			result.append("        }\n");
        		result.append("    }\n");
    		}
    	}
    	return result;
    }

    private StringBuilder createSelectBoxMethods(ScreenDefinition screenDefinition, List<String> signatures) {
    	StringBuilder result = new StringBuilder();
    	for (SelectBox i: BaseUtil.getAllSelectBoxes(screenDefinition)){
    		String methodName = "set" + BaseUtil.idToFirstCharUpperCase(i.getID()) + "SelectedID";
    		appendAndAdd(result, signatures, "    public void " + methodName + "(String selectedID) {\n");
    		result.append("        replyDTO.getSelectBoxSelectedIDs().put(\"" + GeneratorUtil.getJSSelectBoxSelectedItemVariableName(screenDefinition, i) + "\", selectedID);\n");
            result.append("        if (recordMode){\n");
            result.append("            addRecordedAction(\"" + methodName + "(\" + escapeString(selectedID) + \");\");\n");
            result.append("        }\n");
    		result.append("    }\n");
    	}
    	return result;
    }

    private StringBuilder createCodeEditorSetterMethods(ScreenDefinition screenDefinition, List<String> signatures) {
        StringBuilder result = new StringBuilder();
        for (CodeEditorWidget widget : BaseUtil.getAllCodeEditorWidgets(screenDefinition)) {
            createCodeEditorTextSetter(result, screenDefinition, widget, signatures);
            createCodeEditorHighlightingSetter(result, screenDefinition, widget, signatures);
            createCodeEditorCursorPosSetter(result, screenDefinition, widget, signatures);
            createCodeEditorContextAssistSetter(result, screenDefinition, widget, signatures);
        }
        result.append("\n");
        return result;
    }

    private void createCodeEditorCursorPosSetter(StringBuilder result, ScreenDefinition screenDefinition, CodeEditorWidget widget, List<String> signatures) {
        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "CursorPos", "set");
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(int line, int posInLine) {\n");
        result.append("        CursorPos cursorPos = new CursorPos();\n");
        result.append("        cursorPos.setLine(line);\n");
        result.append("        cursorPos.setLine(line);\n");
        result.append("        cursorPos.setPosInLine(posInLine);\n");
        result.append("        replyDTO.getCursorPosValues().put(\"" + GeneratorUtil.createCodeWidgetVariableName(screenDefinition, widget) + "\", cursorPos);\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + line + \", \" + posInLine + \");\");\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createCodeEditorHighlightingSetter(StringBuilder result, ScreenDefinition screenDefinition, CodeEditorWidget widget, List<String> signatures) {
        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "Highlightings", "set");
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(List<TextHighlighting> highlightings) {\n");
        result.append("        if (highlightings == null) {\n");
        result.append("            replyDTO.getTextHighlighting().remove(\"" + GeneratorUtil.createCodeWidgetVariableName(screenDefinition, widget) + "\");\n");
        result.append("        } else {\n");
        result.append("            replyDTO.getTextHighlighting().put(\"" + GeneratorUtil.createCodeWidgetVariableName(screenDefinition, widget) + "\", highlightings);\n");
        result.append("        }\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(ReplyActionType.SET_HIGHLIGHTINGS, \"" + javaSetterMethodName + "(\", highlightings);\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createCodeEditorContextAssistSetter(StringBuilder result, ScreenDefinition screenDefinition, CodeEditorWidget widget, List<String> signatures) {
        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "ContextAssist", "set");
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(ContextAssist contextAssist) {\n");
        result.append("        if (contextAssist == null) {\n");
        result.append("            replyDTO.getContextAssists().remove(\"" + GeneratorUtil.createCodeWidgetVariableName(screenDefinition, widget) + "\");\n");
        result.append("        } else {\n");
        result.append("            replyDTO.getContextAssists().put(\"" + GeneratorUtil.createCodeWidgetVariableName(screenDefinition, widget) + "\", contextAssist);\n");
        result.append("        }\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(ReplyActionType.SET_CONTEXT_ASSIST, \"" + javaSetterMethodName + "(\", contextAssist);\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createCodeEditorTextSetter(StringBuilder result, ScreenDefinition screenDefinition, CodeEditorWidget widget, List<String> signatures) {
        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "Text", "set");
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(String text) {\n");
        result.append("        replyDTO.getObjectsToSetValue().add(\"" + GeneratorUtil.createCodeWidgetVariableName(screenDefinition, widget) + "\");\n");
        result.append("        if (text == null) {\n");
        result.append("            replyDTO.getObjectSetValueValues().remove(\"" + GeneratorUtil.createCodeWidgetVariableName(screenDefinition, widget) + "\");\n");
        result.append("        } else {\n");
        result.append("            replyDTO.getObjectSetValueValues().put(\"" + GeneratorUtil.createCodeWidgetVariableName(screenDefinition, widget) + "\", text);\n");
        result.append("        }\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + escapeString(text) + \");\");\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createTextSetter(StringBuilder result, ScreenDefinition screenDefinition, BasicWidget widget, List<String> signatures) throws Exception {
        if (widget.getID() == null) {
            throw new Exception("widget has no ID. Type: " + widget.getType());
        }

        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "Text", "set");
        String propertyName = GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget);
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(String text) {\n");
        result.append("        replyDTO.getVariablesToSet().add(\"" + propertyName + "\");\n");
        result.append("        if (text == null) {\n");
        result.append("            replyDTO.getVariableValues().remove(\"" + propertyName + "\");\n");
        result.append("        } else {\n");
        result.append("            replyDTO.getVariableValues().put(\"" + propertyName + "\", text);\n");
        result.append("        }\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + escapeString(text) + \");\");\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }
    
    private void createHeadlineTextSetter(StringBuilder result, ScreenDefinition screenDefinition, LayoutCell cell, List<String> signatures) throws Exception {
    	String javaSetterMethodName = BaseUtil.buildIDWithPrefix(cell.getID() + "HeadlineText", "set");
    	String propertyName = GeneratorUtil.getJSWidgetHeadlineTextVariableName(screenDefinition, cell);
    	appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(String text) {\n");
    	result.append("        replyDTO.getVariablesToSet().add(\"" + propertyName + "\");\n");
    	result.append("        if (text == null) {\n");
    	result.append("            replyDTO.getVariableValues().remove(\"" + propertyName + "\");\n");
    	result.append("        } else {\n");
    	result.append("            replyDTO.getVariableValues().put(\"" + propertyName + "\", text);\n");
    	result.append("        }\n");
    	result.append("        if (recordMode){\n");
    	result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + escapeString(text) + \");\");\n");
    	result.append("        }\n");
    	result.append("    }\n");
    	result.append("\n");
    }
    
    private void createSubheadTextSetter(StringBuilder result, ScreenDefinition screenDefinition, LayoutCell cell, List<String> signatures) throws Exception {
    	String javaSetterMethodName = BaseUtil.buildIDWithPrefix(cell.getID() + "SubheadText", "set");
    	String propertyName = GeneratorUtil.getJSWidgetSubheadTextVariableName(screenDefinition, cell);
    	appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(String text) {\n");
    	result.append("        replyDTO.getVariablesToSet().add(\"" + propertyName + "\");\n");
    	result.append("        if (text == null) {\n");
    	result.append("            replyDTO.getVariableValues().remove(\"" + propertyName + "\");\n");
    	result.append("        } else {\n");
    	result.append("            replyDTO.getVariableValues().put(\"" + propertyName + "\", text);\n");
    	result.append("        }\n");
    	result.append("        if (recordMode){\n");
    	result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + escapeString(text) + \");\");\n");
    	result.append("        }\n");
    	result.append("    }\n");
    	result.append("\n");
    }
    
    private void createMarkdownViewTextSetter(StringBuilder result, ScreenDefinition screenDefinition, BasicWidget widget, List<String> signatures) throws Exception {
    	if (widget.getID() == null) {
    		throw new Exception("widget has no ID. Type: " + widget.getType());
    	}
    	String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "Text", "set");
        String propertyName = GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget);
    	appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(String text) {\n");
        result.append("        replyDTO.getVariablesToSet().add(\"" + propertyName + "\");\n");
    	result.append("        if (text == null) {\n");
    	result.append("            replyDTO.getMarkdownViewTexts().remove(\"" + widget.getID() + "\");\n");
        result.append("            replyDTO.getVariableValues().remove(\"" + propertyName + "\");\n");
    	result.append("        } else {\n");
    	result.append("            replyDTO.getMarkdownViewTexts().put(\"" + widget.getID() + "\", text);\n");
        result.append("            replyDTO.getVariableValues().put(\"" + propertyName + "\", text);\n");
    	result.append("        }\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + escapeString(text) + \");\");\n");
        result.append("        }\n");
    	result.append("    }\n");
    	result.append("\n");
    }
    
    private void appendAndAdd(StringBuilder stringBuilder, List<String> list, String text){
    	stringBuilder.append(text);
    	list.add(text);
    }

    private void createSelectedSetter(StringBuilder result, ScreenDefinition screenDefinition, BasicWidget widget, List<String> signatures) throws Exception {
        if (widget.getID() == null) {
            throw new Exception("widget has no ID. Type: " + widget.getType());
        }

        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "Selected", "set");
        String propertyName = GeneratorUtil.getJSWidgetSelectedVariableName(screenDefinition, widget);
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(boolean selected) {\n");
        result.append("        replyDTO.getVariablesToSet().add(\"" + propertyName + "\");\n");
        result.append("        replyDTO.getVariableValues().put(\"" + propertyName + "\", selected);\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + selected + \");\");\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createBackgroundColorSetter(StringBuilder result, ScreenDefinition screenDefinition, BasicWidget widget, List<String> signatures) throws Exception {
    	if (widget.getID() == null) {
    		throw new Exception("widget has no ID. Type: " + widget.getType());
    	}
    	
    	String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "BackgroundColor", "set");
    	String propertyName = GeneratorUtil.getJSWidgetBackgroundColorVariableName(screenDefinition, widget);
    	result.append("/** @param color background color in format '#aabbcc' or null for transparent/default */\n");
    	appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(String color) {\n");
    	result.append("        replyDTO.getVariablesToSet().add(\"" + propertyName + "\");\n");
    	result.append("        replyDTO.getVariableValues().put(\"" + propertyName + "\", color == null ? \"\" : \"background: \" + color);\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + escapeString(color) + \");\");\n");
        result.append("        }\n");
    	result.append("    }\n");
    	result.append("\n");
    }
    
    private void createPluginVariableSetter(StringBuilder result, ScreenDefinition screenDefinition, PluginInstance pluginInstance, PluginVariable pluginVariable, List<String> signatures)
            throws Exception {
        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(pluginInstance.getID() + "Varaible" + BaseUtil.idToFirstCharUpperCase(pluginVariable.getID()),
                "setPlugin");
        String javaClassString = GeneratorUtil.toJavaClassString(pluginVariable.getType());
        String propertyName = GeneratorUtil.getJSPluginVariableName(screenDefinition, pluginInstance.getID(), pluginVariable.getID());
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(" + javaClassString + " value) {\n");
        result.append("        replyDTO.getVariablesToSet().add(\"" + propertyName + "\");\n");

        if (BaseUtil.isPrimitiveDataType(pluginVariable.getType())) {
            result.append("        replyDTO.getVariableValues().put(\"" + propertyName + "\", value);\n");
        } else {
            result.append("        if (value == null) {\n");
            result.append("            replyDTO.getVariableValues().remove(\"" + propertyName + "\");\n");
            result.append("        } else {\n");
            result.append("            replyDTO.getVariableValues().put(\"" + propertyName + "\", value);\n");
            result.append("        }\n");
        }
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + value + \");\");\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createInditerminateSetter(StringBuilder result, ScreenDefinition screenDefinition, BasicWidget widget, List<String> signatures) {
        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "Indeterminate", "set");
        String propertyName = GeneratorUtil.getJSWidgetProgressBarModeVariableName(screenDefinition, widget);
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(boolean indeterminate) {\n");
        result.append("        replyDTO.getVariablesToSet().add(\"" + propertyName + "\");\n");
        result.append("        if (indeterminate) {\n");
        result.append("            replyDTO.getVariableValues().put(\"" + propertyName + "\", \"query\");\n");
        result.append("        } else {\n");
        result.append("            replyDTO.getVariableValues().put(\"" + propertyName + "\", \"determinate\");\n");
        result.append("        }\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + indeterminate + \");\");\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createVisibleSetter(StringBuilder result, ScreenDefinition screenDefinition, String id, List<String> signatures) {
        if (id == null) {
            return;
        }
        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(id + "Visible", "set");
        String propertyName = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, id);
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(boolean visible) {\n");
        result.append("        replyDTO.getVariablesToSet().add(\"" + propertyName + "\");\n");
        result.append("        replyDTO.getVariableValues().put(\"" + propertyName + "\", visible);\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + visible + \");\");\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createProgressSetter(StringBuilder result, ScreenDefinition screenDefinition, BasicWidget widget, List<String> signatures) {
        String javaSetterMethodName = BaseUtil.buildIDWithPrefix(widget.getID() + "Progress", "set");
        String progressPropertyName = GeneratorUtil.getJSWidgetProgressBarProgressVariableName(screenDefinition, widget);
        String propertyIndeterminateName = GeneratorUtil.getJSWidgetProgressBarModeVariableName(screenDefinition, widget);
        appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(double value) {\n");
        result.append("        replyDTO.getVariablesToSet().add(\"" + progressPropertyName + "\");\n");
        result.append("        replyDTO.getVariableValues().put(\"" + progressPropertyName + "\", \"\" + value);\n");
        result.append("        replyDTO.getVariablesToSet().add(\"" + propertyIndeterminateName + "\");\n");
        result.append("        replyDTO.getVariableValues().put(\"" + propertyIndeterminateName + "\", \"determinate\");\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + value + \");\");\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    }

    private void createTimerActiveSetter(StringBuilder result, ScreenDefinition screenDefinition, Timer timer, List<String> signatures) {
    	String javaSetterMethodName = BaseUtil.buildIDWithPrefix(timer.getID() + "Active", "set");
    	String progressPropertyName = GeneratorUtil.getJSTimerActiveVariableName(screenDefinition, timer);
    	appendAndAdd(result, signatures, "    public void " + javaSetterMethodName + "(boolean active) {\n");
    	result.append("        replyDTO.getVariablesToSet().add(\"" + progressPropertyName + "\");\n");
    	result.append("        replyDTO.getVariableValues().put(\"" + progressPropertyName + "\", active);\n");
        result.append("        if (recordMode){\n");
        result.append("            addRecordedAction(\"" + javaSetterMethodName + "(\" + active + \");\");\n");
        result.append("        }\n");
    	result.append("    }\n");
    	result.append("\n");
    }
    
}
