package com.bright_side_it.fliesenui.generator.logic;

import static com.bright_side_it.fliesenui.base.util.BaseUtil.in;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.TextUtil;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginEvent;
import com.bright_side_it.fliesenui.plugin.model.PluginVariable;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameterContainer;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;
import com.bright_side_it.fliesenui.screendefinition.model.Timer;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter.WidgetProperty;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem.TableWidgetType;

public class JavaScreenListenerCreatorLogic {

    public void createJava(Project project, ScreenDefinition screenDefinition, File screenPackageDir) throws Exception {
        StringBuilder result = new StringBuilder();

        String className = GeneratorUtil.getViewListenerClassName(screenDefinition);
        String replyClassName = GeneratorUtil.getReplyClassName(screenDefinition);

        File destFile = new File(screenPackageDir, className + GeneratorConstants.JAVA_FILE_ENDING);

        result.append("package " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + ";\n");
        result.append("\n");
        result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + "." + GeneratorConstants.CLIENT_PROPERTIES_DTO_CLASS_NAME + ";\n");
        result.append(createDTOImportStatements(screenDefinition));
        result.append("\n");
        result.append("public interface " + className + "{\n");
        result.append(createOnLoadedMethod(project, screenDefinition, replyClassName));
        result.append("    void onInputDialogResult(" + replyClassName + " reply, String referenceID, String dialogResult);\n");
        result.append("    void onConfirmDialogResult(" + replyClassName + " reply, String referenceID, boolean confirmed);\n");
        result.append(createClickedMethods(project, screenDefinition));
        result.append(createChangedMethods(project, screenDefinition));
        result.append(createTimerMethods(project, screenDefinition));
        result.append(createSelectBoxMethods(project, screenDefinition));
        result.append(createPluginEventMethods(project, screenDefinition));
        result.append(createAllTableButtonClickedMethods(project, screenDefinition, replyClassName));
        result.append(createAllTableRowClickedMethods(project, screenDefinition, replyClassName));
        result.append(createAllCodeEditorMethods(screenDefinition, replyClassName));
        result.append(createFileUploadMethods(project, screenDefinition));
        result.append("}");

        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
    }

    private StringBuilder createOnLoadedMethod(Project project, ScreenDefinition screenDefinition, String replyClassName) {
        StringBuilder result = new StringBuilder();
        result.append("    void onLoaded(" + replyClassName + " reply, "+ GeneratorConstants.CLIENT_PROPERTIES_DTO_CLASS_NAME + " clientProperties");
        if (screenDefinition.getParameterDTOID() != null) {
            DTODeclaration dtoDeclaration = screenDefinition.getDTODeclarations().get(screenDefinition.getParameterDTOID());
            result.append(", " + GeneratorUtil.getDTOClassName(dtoDeclaration.getType()) + " " + screenDefinition.getParameterDTOID());
        }

        result.append(");\n");
        return result;
    }

    private StringBuilder createDTOImportStatements(ScreenDefinition screenDefinition) {
        StringBuilder result = new StringBuilder();
        result.append("import java.io.InputStream;\n");
        for (String i : getRequiredDTOClassNames(screenDefinition)) {
            result.append("import " + GeneratorConstants.GENERATED_DTO_PACKAGE_NAME + "." + i + ";\n");
        }
        return result;
    }

    private Set<String> getRequiredDTOClassNames(ScreenDefinition screenDefinition) {
        Set<String> result = new TreeSet<>();
        for (EventParameterContainer widget : BaseUtil.getAllEventParameterContainers(screenDefinition)) {
            if (widget.getEventParameters() != null) {
                for (EventParameter eventParameter : widget.getEventParameters()) {
                    if (eventParameter.getDTOID() != null) {
                        DTODeclaration declaration = screenDefinition.getDTODeclarations().get(eventParameter.getDTOID());
                        result.add(GeneratorUtil.getDTOClassName(declaration.getType()));
                    }
                }
            }
        }

        for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
            //			List<String> dtoFieldChain = BaseUtil.getDTOFieldChain(tableWidget.getDTO());
            //			log("field DTO field chain of widget " + tableWidget.getID() + ": " + dtoFieldChain);
            DTODeclaration declaration = screenDefinition.getDTODeclarations().get(BaseUtil.getDTOInstanceName(tableWidget.getDTO()));
            result.add(GeneratorUtil.getDTOClassName(declaration.getType()));
        }

        if (screenDefinition.getParameterDTOID() != null) {
            DTODeclaration dtoDeclaration = screenDefinition.getDTODeclarations().get(screenDefinition.getParameterDTOID());
            result.add(GeneratorUtil.getDTOClassName(dtoDeclaration.getType()));
        }


        return result;
    }

    private void log(String message) {
        System.out.println("JavaScreenListenerCreatorLogic> " + message);
    }

    private StringBuilder createAllTableButtonClickedMethods(Project project, ScreenDefinition screenDefinition, String replyClassName) throws Exception {
        StringBuilder result = new StringBuilder();

        for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
            for (TableWidgetColumn tableColumn : tableWidget.getColumns()) {
                for (TableWidgetItem tableItem : tableColumn.getTableItems()) {
                    if (in(tableItem.getType(), TableWidgetType.BUTTON, TableWidgetType.IMAGE_BUTTON)) {
                        result.append(createTableWidgetButtonsClickMethod(project, screenDefinition, tableItem, tableWidget, replyClassName));
                    }
                }
            }
        }

        return result;
    }

    private StringBuilder createAllTableRowClickedMethods(Project project, ScreenDefinition screenDefinition, String replyClassName) throws Exception {
        StringBuilder result = new StringBuilder();

        for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
            result.append(createTableWidgetRowClickMethod(project, screenDefinition, tableWidget, replyClassName));
        }

        return result;
    }

    private StringBuilder createTableWidgetButtonsClickMethod(Project project, ScreenDefinition screenDefinition, TableWidgetItem tableItem, TableWidget tableWidget,
            String replyClassName) throws Exception {
        StringBuilder result = new StringBuilder();
        String javaMethodName = GeneratorUtil.createJavaTableButtonClickMethodName(tableWidget, tableItem);
        result.append(
                "    void " + javaMethodName + "(" + replyClassName + " reply, String rowID" + createEventParameterList(project, screenDefinition, tableItem) + ");\n");
        return result;
    }

    private StringBuilder createTableWidgetRowClickMethod(Project project, ScreenDefinition screenDefinition, TableWidget tableWidget, String replyClassName)
            throws Exception {
        StringBuilder result = new StringBuilder();
        String javaMethodName = GeneratorUtil.createJavaTableRowClickMethodName(tableWidget);
        result.append(
                "    void " + javaMethodName + "(" + replyClassName + " reply, String rowID" + createEventParameterList(project, screenDefinition, tableWidget) + ");\n");
        return result;
    }

    private StringBuilder createClickedMethods(Project project, ScreenDefinition screenDefinition) throws Exception {
        StringBuilder result = new StringBuilder();
        String replyClassName = GeneratorUtil.getReplyClassName(screenDefinition);
        for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(screenDefinition)) {
            if (in(widget.getType(), BasicWidgetType.BUTTON, BasicWidgetType.IMAGE_BUTTON)) {
                result.append("    void " + GeneratorUtil.getClickedListenerMethodName(widget) + "(" + replyClassName + " reply"
                        + createEventParameterList(project, screenDefinition, widget) + ");\n");
            }
        }
        return result;
    }

    private StringBuilder createFileUploadMethods(Project project, ScreenDefinition screenDefinition) throws Exception {
    	StringBuilder result = new StringBuilder();
    	String replyClassName = GeneratorUtil.getReplyClassName(screenDefinition);
    	for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(screenDefinition)) {
    		if (in(widget.getType(), BasicWidgetType.FILE_UPLOAD)) {
    			result.append("    void " + GeneratorUtil.createJavaFileUploadMethodName(widget) + "(String uploadedFileName, InputStream uploadedFileInputStream"
    					+ createEventParameterList(project, screenDefinition, widget) + ");\n");
    			result.append("\n");
    			result.append("    void " + GeneratorUtil.createJavaFileUploadFinishedMethodName(widget) + "(" + replyClassName + " reply"
    					+ createEventParameterList(project, screenDefinition, widget) + ");\n");
    			result.append("\n");
    		}
    	}
    	return result;
    }
    
    private StringBuilder createChangedMethods(Project project, ScreenDefinition screenDefinition) throws Exception {
    	StringBuilder result = new StringBuilder();
    	String replyClassName = GeneratorUtil.getReplyClassName(screenDefinition);
    	for (BasicWidget widget : GeneratorUtil.getAllBasicWidgets(screenDefinition)) {
    		if (in(widget.getType(), BasicWidgetType.CHECKBOX, BasicWidgetType.SWITCH)) {
    			result.append("    void " + GeneratorUtil.createJavaOnChangedMethodName(widget) + "(" + replyClassName + " reply, boolean selected"
    					+ createEventParameterList(project, screenDefinition, widget) + ");\n");
    		}
    	}
    	return result;
    }
    
    private StringBuilder createTimerMethods(Project project, ScreenDefinition screenDefinition) throws Exception {
    	StringBuilder result = new StringBuilder();
    	String replyClassName = GeneratorUtil.getReplyClassName(screenDefinition);
    	for (Timer timer: BaseUtil.getAllTimers(screenDefinition)) {
			result.append("    void " + GeneratorUtil.getOnTimerListenerMethodName(timer) + "(" + replyClassName + " reply"
					+ createEventParameterList(project, screenDefinition, timer) + ");\n");
    	}
    	return result;
    }
    
    private StringBuilder createSelectBoxMethods(Project project, ScreenDefinition screenDefinition) throws Exception {
    	StringBuilder result = new StringBuilder();
    	String replyClassName = GeneratorUtil.getReplyClassName(screenDefinition);
    	for (SelectBox selectBox: BaseUtil.getAllSelectBoxes(screenDefinition)) {
    		result.append("    void " + GeneratorUtil.createJavaSelectBoxChangedMethodName(selectBox) + "(" + replyClassName + " reply, String itemID" + 
    				createEventParameterList(project, screenDefinition, selectBox) + ");\n");
    	}
    	return result;
    }
    
    private StringBuilder createPluginEventMethods(Project project, ScreenDefinition screenDefinition) throws Exception {
        StringBuilder result = new StringBuilder();
        String replyClassName = GeneratorUtil.getReplyClassName(screenDefinition);
        for (PluginInstance pluginInstance : BaseUtil.getAllPluginInstances(screenDefinition)) {
            PluginDefinition pluginDefinition = project.getPluginDefinitionsMap().get(pluginInstance.getPluginType());
            for (PluginEvent event : BaseUtil.toEmptyMapIfNull(pluginDefinition.getEvents()).values()) {
                result.append("    void " + GeneratorUtil.createJavaPluginEventMethodName(pluginInstance, event) + "(" + replyClassName + " reply"
                        + createEventParameterList(project, screenDefinition, pluginInstance) + ");\n");
            }

        }
        return result;
    }

    private StringBuilder createEventParameterList(Project project, ScreenDefinition screenDefinition, EventParameterContainer eventParameterContainer) throws Exception {
        StringBuilder result = new StringBuilder();
        if (eventParameterContainer.getEventParameters() == null) {
            return result;
        }
        for (EventParameter i : eventParameterContainer.getEventParameters()) {
            result.append(", ");

            if (i.getDTOID() != null) {
                DTODeclaration declaration = screenDefinition.getDTODeclarations().get(i.getDTOID());
                String dtoClassName = GeneratorUtil.getDTOClassName(declaration.getType());

                result.append(dtoClassName + " " + i.getDTOID());
            } else if (i.getPluginVariableName() != null) {
                PluginInstance pluginInstance = BaseUtil.findPluginInstance(screenDefinition, i.getPluginInstanceID());
                PluginDefinition pluginDefinition = project.getPluginDefinitionsMap().get(pluginInstance.getPluginType());
                PluginVariable pluginVariable = pluginDefinition.getVariables().get(i.getPluginVariableName());
                result.append(GeneratorUtil.toJavaClassString(pluginVariable.getType()) + " " + i.getPluginInstanceID()
                        + BaseUtil.idToFirstCharUpperCase(i.getPluginVariableName()));
            } else {
                if (i.getWidgetProperty() == WidgetProperty.TEXT) {
                    result.append("String " + TextUtil.addSuffixIfMissing(i.getWidgetID(), "Text"));
                } else if (i.getWidgetProperty() == WidgetProperty.SELECTED_ID) {
                    result.append("String " + i.getWidgetID() + "SelectedID");
                } else if (i.getWidgetProperty() == WidgetProperty.SELECTED) {
                    result.append("boolean " + i.getWidgetID() + "Selected");
                } else if (i.getWidgetProperty() == WidgetProperty.LINE) {
                    result.append("int " + i.getWidgetID() + "Line");
                } else if (i.getWidgetProperty() == WidgetProperty.POS_IN_LINE) {
                    result.append("int " + i.getWidgetID() + "PosInLine");
                } else {
                    throw new Exception("Unkonwn widget property: " + i.getWidgetProperty());
                }
            }

        }
        return result;
    }

    private StringBuilder createAllCodeEditorMethods(ScreenDefinition screenDefinition, String replyClassName) {
        StringBuilder result = new StringBuilder();
        for (CodeEditorWidget i : BaseUtil.getAllCodeEditorWidgets(screenDefinition)) {
            result.append(createCodeEditorMethods(screenDefinition, replyClassName, i));
        }
        return result;
    }

    private StringBuilder createCodeEditorMethods(ScreenDefinition screenDefinition, String replyClassName, CodeEditorWidget codeEditor) {
        StringBuilder result = new StringBuilder();
        String javaMethodName = GeneratorUtil.getContextAssistListenerMethodName(codeEditor);
        result.append("    void " + javaMethodName + "(" + replyClassName + " reply, String editorText, int line, int posInLine);\n");
        result.append("\n");
        javaMethodName = GeneratorUtil.getSaveListenerMethodName(codeEditor);
        result.append("    void " + javaMethodName + "(" + replyClassName + " reply, String editorText);\n");
        return result;
    }



}
