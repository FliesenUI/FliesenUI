package com.bright_side_it.fliesenui.validation.logic;

import static com.bright_side_it.fliesenui.base.util.BaseUtil.*;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginVariable;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.EventParameterDAO;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameterContainer;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter.WidgetProperty;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class EventParameterValidationLogic {

    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            validate(project, screenDefinition);
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition) {
        SortedSet<String> dtoNames = readDTOIDs(screenDefinition);
        SortedMap<String, CellItem> widgetIDs = readWidgetIDMap(screenDefinition);

        for (EventParameterContainer parameterContainer : BaseUtil.getAllEventParameterContainers(screenDefinition)) {
            if (parameterContainer.getEventParameters() != null) {
                for (EventParameter parameter : parameterContainer.getEventParameters()) {
                    validate(project, screenDefinition, parameterContainer, parameter, dtoNames, widgetIDs);
                }
            }
        }
    }

    private SortedSet<String> readDTOIDs(ScreenDefinition screenDefinition) {
        SortedSet<String> result = new TreeSet<>();
        if (screenDefinition.getDTODeclarations() != null) {
            result = new TreeSet<>(screenDefinition.getDTODeclarations().keySet());
        }
        return result;
    }

    private SortedMap<String, CellItem> readWidgetIDMap(ScreenDefinition screenDefinition) {
        SortedMap<String, CellItem> result = new TreeMap<>();
        for (BasicWidget i : BaseUtil.getAllBasicWidgets(screenDefinition)) {
            if (i.getID() != null) {
                result.put(i.getID(), i);
            }
        }
        for (TableWidget i : BaseUtil.getAllTableWidgets(screenDefinition)) {
            if (i.getID() != null) {
                result.put(i.getID(), i);
            }
        }
        for (SelectBox i : BaseUtil.getAllSelectBoxes(screenDefinition)) {
        	if (i.getID() != null) {
        		result.put(i.getID(), i);
        	}
        }
        for (CodeEditorWidget i : BaseUtil.getAllCodeEditorWidgets(screenDefinition)) {
            if (i.getID() != null) {
                result.put(i.getID(), i);
            }
        }
        return result;
    }

    private void validate(Project project, ScreenDefinition screenDefinition, EventParameterContainer parameterContainer, EventParameter parameter,
            SortedSet<String> dtoNames, SortedMap<String, CellItem> widgets) {
        if (parameter.getDTOID() != null) {
            if (!dtoNames.contains(parameter.getDTOID())) {
                ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                        ProblemType.EVENT_PARAMETER_UNKNOWN_DTO, "Unknown DTO: '" + parameter.getDTOID() + "'");
            }
        } else if (parameter.getPluginVariableName() != null) {
            PluginInstance pluginInstance = BaseUtil.findPluginInstance(screenDefinition, parameter.getPluginInstanceID());
            if (pluginInstance == null) {
                ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                        ProblemType.EVENT_PARAMETER_UNKNOWN_PLUGIN_INSTANCE, "Unknown plugin instance in screen: '" + parameter.getPluginInstanceID() + "'");
                return;
            }
            PluginDefinition pluginDefinition = project.getPluginDefinitionsMap().get(pluginInstance.getPluginType());
            if (pluginDefinition == null) {
                ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                        ProblemType.EVENT_PARAMETER_UNKNOWN_PLUGIN_DEFINITION, "This plugin type is not defined: '" + pluginInstance.getPluginType() + "'");
                return;
            }
            PluginVariable pluginVariable = pluginDefinition.getVariables().get(parameter.getPluginVariableName());
            if (pluginVariable == null) {
                ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                        ProblemType.EVENT_PARAMETER_UNKNOWN_PLUGIN_VARIABLE, "This variable is not defined for the plugin: '" + parameter.getPluginVariableName() + "'");
                return;
            }
        } else if (parameter.getWidgetID() != null) {
            CellItem widget = widgets.get(parameter.getWidgetID());
            if (widget == null) {
                ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                        ProblemType.EVENT_PARAMETER_UNKNOWN_WIDGET, "Unknown widget id: '" + parameter.getWidgetID() + "'");
            }
            if (parameter.getWidgetProperty() == null) {
                ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                        ProblemType.EVENT_PARAMETER_MISSING_WIDGET_PROPERTY, "No property has been specified for the widget");
            }
            if ((widget != null) && (parameter.getWidgetProperty() != null)) {
                if (widget instanceof BasicWidget) {
                    if (!in(parameter.getWidgetProperty(), WidgetProperty.TEXT, WidgetProperty.SELECTED)) {
                        ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                                ProblemType.EVENT_PARAMETER_WRONG_WIDGET_PROPERTY, "property not supported for this widget: " + parameter.getWidgetProperty());
                    }
                } else if (widget instanceof SelectBox) {
                    if (!in(parameter.getWidgetProperty(), WidgetProperty.SELECTED_ID)) {
                        ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                                ProblemType.EVENT_PARAMETER_WRONG_WIDGET_PROPERTY, "property not supported for this select box: " + parameter.getWidgetProperty());
                    }
                } else if (widget instanceof TableWidget) {
                    if (!in(parameter.getWidgetProperty(), WidgetProperty.CHECKED_ROW_IDS)) {
                        ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                                ProblemType.EVENT_PARAMETER_WRONG_WIDGET_PROPERTY, "property not supported for table widget: " + parameter.getWidgetProperty());
                    }
                    if (!((TableWidget) widget).isRowCheckboxes()){
                    	if (parameter.getWidgetProperty().equals(WidgetProperty.CHECKED_ROW_IDS)){
                            ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                                    ProblemType.EVENT_PARAMETER_WRONG_CHECKED_ROW_IDS_REQUIRED_ROW_CHECKBOXES, "The property which rows are checked is only available if the table widget is set to have row check boxes: " + parameter.getWidgetProperty());
                    	}
                    }
                } else if (widget instanceof CodeEditorWidget) {
                	if (!in(parameter.getWidgetProperty(), WidgetProperty.TEXT, WidgetProperty.LINE, WidgetProperty.POS_IN_LINE)) {
                		ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                				ProblemType.EVENT_PARAMETER_WRONG_WIDGET_PROPERTY, "property not supported for this widget: " + parameter.getWidgetProperty());
                	}
                } else {
                    ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), EventParameterDAO.VALUE_ATTRIBUTE_NAME,
                            ProblemType.EVENT_PARAMETER_WRONG_WIDGET_TYPE_REFERENCED, "unexpected widget type referenced: " + widget.getClass().getSimpleName());
                }
            }
        } else {
            ValidationUtil.addError(project, screenDefinition, parameterContainer.getNodePath(), "", ProblemType.EVENT_PARAMETER_UNKNOWN_NO_DTO_AND_NO_WIDGET,
                    "No DTO ID or widget ID specified");
        }

    }


}
