package com.bright_side_it.fliesenui.generator.logic;

import static com.bright_side_it.fliesenui.base.util.BaseUtil.in;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.generator.model.ReplyToCallTranslationParameter;
import com.bright_side_it.fliesenui.generator.model.ReplyToCallTranslationParameter.DataType;
import com.bright_side_it.fliesenui.generator.model.RequestToCallTranslation;
import com.bright_side_it.fliesenui.generator.model.RequestToCallTranslation.SpecialMethodType;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginEvent;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget.BasicWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener;
import com.bright_side_it.fliesenui.screendefinition.model.EventListener.EventListenType;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter.WidgetProperty;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameterContainer;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetColumn;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem.TableWidgetType;
import com.bright_side_it.fliesenui.screendefinition.model.Timer;

public class RequestToCallTranslationBuilder {
    public List<RequestToCallTranslation> buildTranslations(Project project, ScreenDefinition screenDefinition) throws Exception {
        List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();

        SortedMap<String, CellItem> widgetMap = GeneratorUtil.readWidgetIDMap(screenDefinition);

        result.addAll(createButtonClickTranslations(project, screenDefinition, widgetMap));
        result.addAll(createOnChangedTranslations(project, screenDefinition, widgetMap));
        result.addAll(createAllTimerTranslations(project, screenDefinition, widgetMap));
        result.addAll(createAllTableButtonClickedTranslations(project, screenDefinition, widgetMap));
        result.addAll(createAllTableRowClickedTranslations(project, screenDefinition, widgetMap));
        result.addAll(createAllSelectBoxTranslations(project, screenDefinition, widgetMap));
//        result.addAll(createAllCodeEditorTranslations(screenDefinition, widgetMap));
        result.addAll(createPluginEventTranslations(project, screenDefinition, widgetMap));
        result.add(createOnLoadedTranslation(project, screenDefinition));
        result.addAll(createFileUploadTranslations(project, screenDefinition, widgetMap));
        result.addAll(createAllEventListenerTranslations(project, screenDefinition, widgetMap));


        result.add(createInputDialogResultTranslation(screenDefinition, widgetMap));
        result.add(createConfirmDialogResultTranslation(screenDefinition, widgetMap));
        result.add(createListChooserResultTranslation(screenDefinition, widgetMap));
        return result;
    }

    private RequestToCallTranslation createInputDialogResultTranslation(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap) {
        RequestToCallTranslation result = new RequestToCallTranslation();
        result.setActionName(GeneratorConstants.REQUEST_ACTION_ON_INNPUT_DIALOG_RESULT);
        result.setMethodName(GeneratorConstants.JAVA_METHOD_NAME_ON_INNPUT_DIALOG_RESULT);
        result.setSpecialMethodType(SpecialMethodType.STRING_INPUT_DIALOG);
//        List<ReplyToCallTranslationParameter> parameter = new ArrayList<ReplyToCallTranslationParameter>();
//        result.setParameter(parameter);
//        parameter.add(createParameter("referenceID", DataType.STRING));
//        parameter.add(createParameter("result", DataType.STRING));
        return result;
    }

    private RequestToCallTranslation createConfirmDialogResultTranslation(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap) {
        RequestToCallTranslation result = new RequestToCallTranslation();
        result.setActionName(GeneratorConstants.REQUEST_ACTION_ON_CONFIRM_DIALOG_RESULT);
        result.setMethodName(GeneratorConstants.JAVA_METHOD_NAME_ON_CONFIRM_DIALOG_RESULT);
        result.setSpecialMethodType(SpecialMethodType.CONFIRM_DIALOG);
//        List<ReplyToCallTranslationParameter> parameter = new ArrayList<ReplyToCallTranslationParameter>();
//        result.setParameter(parameter);
//        parameter.add(createParameter("referenceID", DataType.STRING));
//        parameter.add(createParameter("result", DataType.BOOLEAN_NOT_NULL));
        return result;
    }

    private RequestToCallTranslation createListChooserResultTranslation(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap) {
    	RequestToCallTranslation result = new RequestToCallTranslation();
    	result.setActionName(GeneratorConstants.REQUEST_ACTION_ON_LIST_CHOOSER_RESULT);
    	result.setMethodName(GeneratorConstants.JAVA_METHOD_NAME_ON_LIST_CHOOSER_RESULT);
    	result.setSpecialMethodType(SpecialMethodType.LIST_CHOOSER);
//    	List<ReplyToCallTranslationParameter> parameter = new ArrayList<ReplyToCallTranslationParameter>();
//    	result.setParameter(parameter);
//    	parameter.add(createParameter("referenceID", DataType.STRING));
//    	parameter.add(createParameter("selectedIDs", DataType.LIST_OF_STRING));
    	return result;
    }
    
    private RequestToCallTranslation createTranslation(String actionName, String methodName) {
        RequestToCallTranslation result = new RequestToCallTranslation();
        result.setActionName(actionName);
        result.setMethodName(methodName);
        return result;
    }

    private List<RequestToCallTranslation> createButtonClickTranslations(Project project, ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap)
            throws Exception {
        List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();
    	for (BasicWidget basicWidget : BaseUtil.getAllBasicWidgets(screenDefinition)) {
            if (in(basicWidget.getType(), BasicWidgetType.BUTTON, BasicWidgetType.IMAGE_BUTTON)) {
                RequestToCallTranslation resultItem = new RequestToCallTranslation();
                resultItem.setActionName(GeneratorUtil.createJSButtonClickMethodName(basicWidget));
                resultItem.setMethodName(GeneratorUtil.getClickedListenerMethodName(basicWidget));
                resultItem.setParameter(createParameters(project, screenDefinition, basicWidget, widgetMap));
                result.add(resultItem);
            }
        }
        return result;
    }

    private List<RequestToCallTranslation> createAllEventListenerTranslations(Project project, ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap)
    		throws Exception {
    	List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();
    	for (EventListener i: BaseUtil.getAllEventListenersOfContainer(screenDefinition, EventListenType.BACK_ACTION)){
			RequestToCallTranslation resultItem = new RequestToCallTranslation();
			resultItem.setActionName(GeneratorUtil.createJavaBackActionMethodName());
			resultItem.setMethodName(GeneratorUtil.createJavaBackActionMethodName());
			resultItem.setParameter(createParameters(project, screenDefinition, i, widgetMap));
			result.add(resultItem);
    	}
    	
    	for (CodeEditorWidget codeEditor : BaseUtil.getAllCodeEditorWidgets(screenDefinition)){
        	List<EventListener> eventListeners = BaseUtil.getAllEventListenersOfContainer(codeEditor, EventListenType.KEY_PRESS, EventListenType.KEY_DOWN);
        	if (!eventListeners.isEmpty()) {
        		RequestToCallTranslation resultItem = new RequestToCallTranslation();
        		resultItem.setKeyEventMethod(true);
        		resultItem.setActionName(GeneratorUtil.createJavaKeyEventActionMethodName(codeEditor));
        		resultItem.setMethodName(GeneratorUtil.createJavaKeyEventActionMethodName(codeEditor));
                List<ReplyToCallTranslationParameter> parameter = new ArrayList<ReplyToCallTranslationParameter>();
//                parameter.add(createParameter(GeneratorConstants.KEY_MODIFIER_PARAMETER_NAME, DataType.KEY_MODIFIER));
//                parameter.add(createParameter(GeneratorConstants.KEY_CHAR_PARAMETER_NAME, DataType.CHARACTER));
//                parameter.add(createParameter(GeneratorConstants.KEY_CODE_PARAMETER_NAME, DataType.INT));                
//                parameter.add(createParameter(GeneratorConstants.EDITOR_TEXT_PARAMETER_NAME, DataType.STRING));
//                parameter.add(createParameter(GeneratorConstants.LINE_PARAMETER_NAME, DataType.INT));
//                parameter.add(createParameter(GeneratorConstants.POS_IN_LINE_PARAMETER_NAME, DataType.INT));
        		parameter.addAll(createParameters(project, screenDefinition, eventListeners.get(0), widgetMap));
        		resultItem.setParameter(parameter);
        		result.add(resultItem);
        	}
    	}
    	return result;
    }
    
    private List<RequestToCallTranslation> createFileUploadTranslations(Project project, ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap)
    		throws Exception {
    	List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();
    	for (BasicWidget basicWidget : BaseUtil.getAllBasicWidgets(screenDefinition)) {
			if (in(basicWidget.getType(), BasicWidgetType.FILE_UPLOAD)) {
				RequestToCallTranslation resultItem = new RequestToCallTranslation();
				resultItem.setActionName(GeneratorUtil.createJavaFileUploadMethodName(basicWidget));
				resultItem.setMethodName(GeneratorUtil.createJavaFileUploadMethodName(basicWidget));
				resultItem.setParameter(createParameters(project, screenDefinition, basicWidget, widgetMap));
				resultItem.setFileUploadMethod(true);
				result.add(resultItem);

				resultItem = new RequestToCallTranslation();
				resultItem.setActionName(GeneratorUtil.createJavaFileUploadFinishedMethodName(basicWidget));
				resultItem.setMethodName(GeneratorUtil.createJavaFileUploadFinishedMethodName(basicWidget));
				resultItem.setParameter(createParameters(project, screenDefinition, basicWidget, widgetMap));
				result.add(resultItem);
			}
    	}
    	return result;
    }
    
    private List<RequestToCallTranslation> createOnChangedTranslations(Project project, ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap)
    		throws Exception {
    	List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();
    	for (CellItem widget : BaseUtil.getAllBasicWidgets(screenDefinition)) {
    		if (widget instanceof BasicWidget) {
    			BasicWidget basicWidget = (BasicWidget) widget;
    			if (in(basicWidget.getType(), BasicWidgetType.CHECKBOX, BasicWidgetType.SWITCH)) {
    				RequestToCallTranslation resultItem = new RequestToCallTranslation();
    				resultItem.setActionName(GeneratorUtil.createJavaOnChangedMethodName(basicWidget));
    				resultItem.setMethodName(GeneratorUtil.createJavaOnChangedMethodName(basicWidget));
                    List<ReplyToCallTranslationParameter> parameters = createParameters(project, screenDefinition, basicWidget, widgetMap);
                    parameters.add(0, createParameter(GeneratorConstants.SELECTED_ID_PARAMETER_NAME, DataType.BOOLEAN_NOT_NULL));
                    resultItem.setParameter(parameters);
    				result.add(resultItem);
    			}
    		}
    	}
    	return result;
    }
    
    private List<RequestToCallTranslation> createAllTimerTranslations(Project project, ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap)
    		throws Exception {
    	List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();
    	for (Timer timer: BaseUtil.getAllTimers(screenDefinition)) {
			RequestToCallTranslation resultItem = new RequestToCallTranslation();
			resultItem.setActionName(GeneratorUtil.createJavaTimerOccuredMethodName(timer));
			resultItem.setMethodName(GeneratorUtil.getOnTimerListenerMethodName(timer));
			resultItem.setParameter(createParameters(project, screenDefinition, timer, widgetMap));
			result.add(resultItem);
    	}
    	return result;
    }
    
    private List<RequestToCallTranslation> createPluginEventTranslations(Project project, ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap)
            throws Exception {
        List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();
        for (PluginInstance pluginInstance : BaseUtil.getAllPluginInstances(screenDefinition)) {
            PluginDefinition pluginDefinition = project.getPluginDefinitionsMap().get(pluginInstance.getPluginType());
            for (PluginEvent event : BaseUtil.toEmptyMapIfNull(pluginDefinition.getEvents()).values()) {
                RequestToCallTranslation resultItem = new RequestToCallTranslation();
                resultItem.setActionName(GeneratorUtil.createJavaPluginEventMethodName(pluginInstance, event));
                resultItem.setMethodName(GeneratorUtil.createJavaPluginEventMethodName(pluginInstance, event));
                resultItem.setParameter(createParameters(project, screenDefinition, pluginInstance, widgetMap));
                result.add(resultItem);
            }
        }
        return result;

    }

    private RequestToCallTranslation createOnLoadedTranslation(Project project, ScreenDefinition screenDefinition) throws Exception {
        RequestToCallTranslation result = new RequestToCallTranslation();
        result.setActionName(GeneratorConstants.REQUEST_ACTION_ON_LOADED);
        result.setMethodName(GeneratorConstants.JAVA_METHOD_NAME_ON_LOADED);

        List<ReplyToCallTranslationParameter> parameterList = new ArrayList<>();
        result.setParameter(parameterList);
        
        ReplyToCallTranslationParameter parameterItem = new ReplyToCallTranslationParameter();
        parameterList.add(parameterItem);
        parameterItem.setKey(GeneratorConstants.CLIENT_PROPERTIES_PARAMETER_NAME);
        parameterItem.setDTOClassName(GeneratorConstants.CLIENT_PROPERTIES_DTO_CLASS_NAME);
        
        if (screenDefinition.getParameterDTOID() != null) {
            parameterItem = new ReplyToCallTranslationParameter();
            parameterList.add(parameterItem);
            parameterItem.setKey(screenDefinition.getParameterDTOID());
            DTODeclaration dtoDeclaration = screenDefinition.getDTODeclarations().get(screenDefinition.getParameterDTOID());
            parameterItem.setDTOClassName(GeneratorUtil.getDTOClassName(dtoDeclaration.getType()));
        }

        return result;

    }

    private List<RequestToCallTranslation> createAllTableButtonClickedTranslations(Project project, ScreenDefinition screenDefinition,
            SortedMap<String, CellItem> widgetMap) throws Exception {
        List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();

        for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
            for (TableWidgetColumn tableColumn : tableWidget.getColumns()) {
                for (TableWidgetItem tableItem : tableColumn.getTableItems()) {
                    if (in(tableItem.getType(), TableWidgetType.BUTTON, TableWidgetType.IMAGE_BUTTON)) {
                        RequestToCallTranslation resultItem = new RequestToCallTranslation();
                        String javaMethodName = GeneratorUtil.createJavaTableButtonClickMethodName(tableWidget, tableItem);
                        resultItem.setActionName(javaMethodName);
                        resultItem.setMethodName(javaMethodName);
                        List<ReplyToCallTranslationParameter> parameters = createParameters(project, screenDefinition, tableItem, widgetMap);
                        parameters.add(0, createParameter("rowID", DataType.STRING));
                        resultItem.setParameter(parameters);
                        result.add(resultItem);
                    }
                }
            }
        }
        return result;
    }

    private List<RequestToCallTranslation> createAllTableRowClickedTranslations(Project project, ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap)
            throws Exception {
        List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();

        for (TableWidget tableWidget : BaseUtil.getAllTableWidgets(screenDefinition)) {
            RequestToCallTranslation resultItem = new RequestToCallTranslation();
            String javaMethodName = GeneratorUtil.createJavaTableRowClickMethodName(tableWidget);
            resultItem.setActionName(javaMethodName);
            resultItem.setMethodName(javaMethodName);
            List<ReplyToCallTranslationParameter> parameters = createParameters(project, screenDefinition, tableWidget, widgetMap);
            parameters.add(0, createParameter(GeneratorConstants.TABLE_BUTTON_CLICK_ROW_ID_PARAMETER_NAME, DataType.STRING));
            resultItem.setParameter(parameters);
            result.add(resultItem);
        }

        return result;
    }

    private List<RequestToCallTranslation> createAllSelectBoxTranslations(Project project, ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap)
    		throws Exception {
    	List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();
    	
    	for (SelectBox selectBox : BaseUtil.getAllSelectBoxes(screenDefinition)) {
    		RequestToCallTranslation resultItem = new RequestToCallTranslation();
    		String javaMethodName = GeneratorUtil.createJavaSelectBoxChangedMethodName(selectBox);
    		resultItem.setActionName(javaMethodName);
    		resultItem.setMethodName(javaMethodName);
    		List<ReplyToCallTranslationParameter> parameters = createParameters(project, screenDefinition, selectBox, widgetMap);
    		parameters.add(0, createParameter(GeneratorConstants.SELECT_BOX_ROW_ID_PARAMETER_NAME, DataType.STRING));
    		resultItem.setParameter(parameters);
    		result.add(resultItem);
    	}
    	
    	return result;
    }
    
//    private List<RequestToCallTranslation> createAllCodeEditorTranslations(ScreenDefinition screenDefinition, SortedMap<String, CellItem> widgetMap) {
//        List<RequestToCallTranslation> result = new ArrayList<RequestToCallTranslation>();
//        for (CodeEditorWidget i : BaseUtil.getAllCodeEditorWidgets(screenDefinition)) {
//            result.add(createCodeEditorContextAssistTranslation(i));
//            result.add(createCodeEditorSaveTranslation(i));
//
//        }
//        return result;
//    }

//    private RequestToCallTranslation createCodeEditorContextAssistTranslation(CodeEditorWidget codeEditor) {
//        RequestToCallTranslation resultItem = new RequestToCallTranslation();
//        String javaMethodName = GeneratorUtil.getContextAssistListenerMethodName(codeEditor);
//        resultItem.setActionName(javaMethodName);
//        resultItem.setMethodName(javaMethodName);
//        List<ReplyToCallTranslationParameter> parameters = new ArrayList<>();
//        resultItem.setParameter(parameters);
//        parameters.add(createParameter("editorText", DataType.STRING));
//        parameters.add(createParameter("line", DataType.INT));
//        parameters.add(createParameter("posInLine", DataType.INT));
//        return resultItem;
//    }
//
//    private RequestToCallTranslation createCodeEditorSaveTranslation(CodeEditorWidget codeEditor) {
//        RequestToCallTranslation resultItem = new RequestToCallTranslation();
//        String javaMethodName = GeneratorUtil.getSaveListenerMethodName(codeEditor);
//        resultItem.setActionName(javaMethodName);
//        resultItem.setMethodName(javaMethodName);
//        List<ReplyToCallTranslationParameter> parameters = new ArrayList<>();
//        resultItem.setParameter(parameters);
//        parameters.add(createParameter("editorText", DataType.STRING));
//        return resultItem;
//    }

    private ReplyToCallTranslationParameter createParameter(String key, DataType dataType) {
        ReplyToCallTranslationParameter result = new ReplyToCallTranslationParameter();
        result.setKey(key);
        result.setDataType(dataType);
        return result;
    }

    private List<ReplyToCallTranslationParameter> createParameters(Project project, ScreenDefinition screenDefinition, EventParameterContainer eventParameterContainer,
            SortedMap<String, CellItem> widgetMap) throws Exception {
        List<ReplyToCallTranslationParameter> result = new ArrayList<>();

        if (eventParameterContainer.getEventParameters() == null) {
            return result;
        }

        for (EventParameter i : eventParameterContainer.getEventParameters()) {
            ReplyToCallTranslationParameter resultItem = new ReplyToCallTranslationParameter();
            result.add(resultItem);

            if (i.getDTOID() != null) {
                resultItem.setKey(i.getDTOID());
                DTODeclaration dtoDeclaration = screenDefinition.getDTODeclarations().get(i.getDTOID());
                resultItem.setDTOClassName(GeneratorUtil.getDTOClassName(dtoDeclaration.getType()));
            } else if (i.getPluginVariableName() != null) {
                PluginInstance pluginInstance = BaseUtil.findPluginInstance(screenDefinition, i.getPluginInstanceID());
                PluginDefinition pluginDefinition = project.getPluginDefinitionsMap().get(pluginInstance.getPluginType());
                resultItem.setKey(GeneratorUtil.getJSPluginVariableName(screenDefinition, i.getPluginInstanceID(), i.getPluginVariableName()));
                resultItem.setDataType(mapDataType(pluginDefinition.getVariables().get(i.getPluginVariableName()).getType()));
            } else {
                CellItem referencedWidget = widgetMap.get(i.getWidgetID());
                resultItem.setKey(i.getWidgetID() + GeneratorUtil.widgetPropertyToSuffix(i.getWidgetProperty()));
                if (referencedWidget instanceof BasicWidget) {
                    if (i.getWidgetProperty() == WidgetProperty.TEXT) {
                        resultItem.setDataType(DataType.STRING);
                    } else if (i.getWidgetProperty() == WidgetProperty.SELECTED) {
                        resultItem.setDataType(DataType.BOOLEAN_NOT_NULL);
                    } else {
                        throw new Exception("Unkonwn widget property: " + i.getWidgetProperty());
                    }
                } else if (referencedWidget instanceof SelectBox){ 
                	if (i.getWidgetProperty() == WidgetProperty.SELECTED_ID) {
                		resultItem.setDataType(DataType.STRING);
                	} else {
                		throw new Exception("Unkonwn select box property: " + i.getWidgetProperty());
                	}
                } else if (referencedWidget instanceof TableWidget){ 
                	if (i.getWidgetProperty() == WidgetProperty.CHECKED_ROW_IDS) {
                		resultItem.setDataType(DataType.LIST_OF_STRING);
                	} else {
                		throw new Exception("Unkonwn table widget property: " + i.getWidgetProperty());
                	}
                } else if (referencedWidget instanceof CodeEditorWidget) {
                    if (i.getWidgetProperty() == WidgetProperty.TEXT) {
                        resultItem.setDataType(DataType.STRING);
                    } else if (i.getWidgetProperty() == WidgetProperty.LINE) {
                        resultItem.setDataType(DataType.INT);
                    } else if (i.getWidgetProperty() == WidgetProperty.POS_IN_LINE) {
                        resultItem.setDataType(DataType.INT);
                    } else {
                        throw new Exception("Unkonwn code editor widget property: " + i.getWidgetProperty());
                    }
                } else {
                	if (referencedWidget == null){
                		throw new Exception("Unexpected widget type. Referenced widget is null");
                	} else {
                		throw new Exception("Unexpected widget type: " + referencedWidget.getClass().getSimpleName());
                	}
                }
            }
        }
        return result;
    }

    private DataType mapDataType(BasicType type) throws Exception {
        switch (type) {
        case BOOLEAN:
            return DataType.BOOLEAN_NOT_NULL;
        case STRING:
            return DataType.STRING;
        case LONG:
            throw new Exception("No matching basic type to long. There is only int");
        }
        throw new Exception("Unexpected basic type: " + type);
    }


}
