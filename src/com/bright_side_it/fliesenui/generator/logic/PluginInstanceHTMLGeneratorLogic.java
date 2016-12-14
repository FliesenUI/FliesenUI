package com.bright_side_it.fliesenui.generator.logic;

import java.util.ArrayList;
import java.util.List;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.plugin.model.PluginDefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginEvent;
import com.bright_side_it.fliesenui.plugin.model.PluginVariable;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class PluginInstanceHTMLGeneratorLogic {
    public HTMLTag generateHTML(Project project, ScreenDefinition screenDefinition, PluginInstance pluginInstance, BrowserType browserType) throws Exception {
        return generateHTML(null, project, screenDefinition, pluginInstance, browserType);
    }

    public HTMLTag generateHTML(HTMLTag parent, Project project, ScreenDefinition screenDefinition, PluginInstance pluginInstance, BrowserType browserType)
            throws Exception {
        HTMLTag result = null;
        result = new HTMLTag();
        if (parent != null) {
            List<HTMLTag> subTags = parent.getSubTags();
            if (subTags == null) {
                subTags = new ArrayList<>();
                parent.setSubTags(subTags);
            }
            subTags.add(result);
        }
        result.setCustomText(generatePluginInstanceText(project, screenDefinition, pluginInstance, browserType));
        return result;
    }

    private String generatePluginInstanceText(Project project, ScreenDefinition screenDefinition, PluginInstance pluginInstance, BrowserType browserType) {
        PluginDefinition definition = project.getPluginDefinitionsMap().get(pluginInstance.getPluginType());
        String result = definition.getHtmlCode().get(browserType);

        for (String i : BaseUtil.toEmptyMapIfNull(definition.getParameters()).keySet()) {
            String replaceOld = GeneratorUtil.toPlaceholder(i);
            String replaceNew = pluginInstance.getParameterValues().get(i);
            log("parameter: replacing >>" + replaceOld + "<< with >>" + replaceNew + "<<");
            result = result.replace(replaceOld, replaceNew);
        }

        for (PluginVariable i : BaseUtil.toEmptyMapIfNull(definition.getVariables()).values()) {
            String javascriptVariableName = GeneratorUtil.getJSPluginVariableName(screenDefinition, pluginInstance.getID(), i.getID());
            String placeholderName = GeneratorUtil.toPlaceholder(i.getID());
            log("variables: replacing >>" + placeholderName + "<< with >>" + javascriptVariableName + "<<");
            result = result.replace(placeholderName, javascriptVariableName);
        }

        for (PluginEvent i : BaseUtil.toEmptyMapIfNull(definition.getEvents()).values()) {
            String javascriptMethodCall = GeneratorUtil.createJSPluginEventMethodName(screenDefinition, pluginInstance, i) + "();";
            String placeholderName = GeneratorUtil.toPlaceholder(i.getID());
            log("events: replacing >>" + placeholderName + "<< with >>" + javascriptMethodCall + "<<");
            result = result.replace(placeholderName, javascriptMethodCall);
        }

        return "\n<!----- PLUGIN " + definition.getID() + "  START -->\n" + result + "\n<!----- PLUGIN " + definition.getID() + "  END -->\n";
    }

    private void log(String message) {
        System.out.println("PluginInstanceHTMLGeneratorLogic> " + message);
    }

}
