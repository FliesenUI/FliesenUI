package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.PluginInstanceDAO;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class PluginInstanceValidationLogic {
    public void validate(Project project) {
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            for (PluginInstance i : BaseUtil.getAllPluginInstances(screenDefinition)) {
                validate(project, screenDefinition, i);
            }
        }
    }

    private void validate(Project project, ScreenDefinition screenDefinition, PluginInstance pluginInstance) {
        String typeName = pluginInstance.getPluginType();
        PluginDefinition pluginDefinition = project.getPluginDefinitionsMap().get(typeName);
        if (pluginDefinition == null) {
            ValidationUtil.addError(project, screenDefinition, pluginInstance.getNodePath(), PluginInstanceDAO.TYPE_ATTRIBUTE_NAME,
                    ProblemType.PLUGIN_INSTANCE_UNKNOWN_TYPE, "Undefined plugin type: '" + typeName + "'");
        }

        if (pluginInstance.getID() == null) {
            ValidationUtil.addError(project, screenDefinition, pluginInstance.getNodePath(), BaseConstants.ID_ATTRIBUTE_NAME, ProblemType.PLUGIN_INSTANCE_MISSING_ID,
                    "Plugin instances must have an ID");
        }

        for (String i : pluginInstance.getParameterValues().keySet()) {
            if (!pluginDefinition.getParameters().containsKey(i)) {
                ValidationUtil.addError(project, screenDefinition, pluginInstance.getNodePath(), i, ProblemType.PLUGIN_PARAMETER_UNEXPECTED,
                        "Undefined attribute plugin parameter: '" + i + "'");
            }
        }
        for (String i : pluginDefinition.getParameters().keySet()) {
            if (!pluginInstance.getParameterValues().containsKey(i)) {
                ValidationUtil.addError(project, screenDefinition, pluginInstance.getNodePath(), null, ProblemType.PLUGIN_PARAMETER_MISSING,
                        "Missing attribute plugin parameter: '" + i + "'");
            }
        }

    }


}
