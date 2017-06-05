package com.bright_side_it.fliesenui.generator.service;

import java.io.File;
import java.util.Set;

import com.bright_side_it.fliesenui.base.util.BaseConstants;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.logic.JSGetClientPropertiesFunctionCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JSOpenScreenFunctionsCreatorLogic;
import com.bright_side_it.fliesenui.generator.logic.JSStringResourceFunctionsCreatorLogic;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.logic.DefinitionResourceLogic;
import com.bright_side_it.fliesenui.project.model.ProjectResource;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceFormat;
import com.bright_side_it.fliesenui.project.model.ProjectResource.ResourceType;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class SinglePageAppHTMLGeneratorService {

    public void generateHTML(Project project, Set<ProjectResource> upToDateResources, File dir) throws Exception {
        for (BrowserType browserType : BaseConstants.BrowserType.values()) {
            if (generationNeeded(project, upToDateResources, browserType)) {
                generateHTML(project, browserType, dir);
            }
        }
    }

    private boolean generationNeeded(Project project, Set<ProjectResource> upToDateResources, BrowserType browserType) {
        DefinitionResourceLogic logic = new DefinitionResourceLogic();
        for (ScreenDefinition screenDefinition : project.getScreenDefinitionsMap().values()) {
            if (!upToDateResources.contains(logic.create(ResourceType.SCREEN, ResourceFormat.XML, screenDefinition.getID()))) {
                return true;
            }
        }
        return false;
    }

    public void generateHTML(Project project, BrowserType browserType, File dir) throws Exception {
        StringBuilder result = new StringBuilder();
        result.append("<!DOCTYPE html>\n");
        result.append("<html lang=\"en\">\n");
        result.append("    <head>\n");
        result.append("        <meta charset=\"utf-8\"/>\n");
        result.append("        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
        result.append("        <link rel=\"stylesheet\" href=\"lib/angular-material.min.css\">\n");
        result.append("        <link rel=\"stylesheet\" href=\"cmlib/codemirror.css\">\n");
        result.append("        <link rel=\"stylesheet\" href=\"cmlib/show-hint.css\">\n");
        result.append("        <link rel=\"stylesheet\" href=\"lib/flui.css\">\n");
        result.append("        " + GeneratorUtil.generateHTMLJSBackButtonLogic(project, browserType, null) + "\n");
        result.append("    </head>\n");
        result.append("    <body ng-app=\"app\">\n");
        result.append("        <script src=\"lib/angular.min.js\"></script>\n");
        result.append("        <script src=\"lib/angular-animate.min.js\"></script>\n");
        result.append("        <script src=\"lib/angular-aria.min.js\"></script>\n");
        result.append("        <script src=\"lib/angular-messages.min.js\"></script>\n");
        result.append("        <script src=\"lib/angular-material.min.js\"></script>\n");
        result.append("        <script src=\"lib/angular-sanitize.min.js\"></script>\n");
        result.append("        <script src=\"lib/showdown.min.js\"></script>\n");
        result.append("        <script src=\"lib/strings.js\"></script>\n");
        result.append("        <script src=\"cmlib/codemirror.js\"></script>\n");
        result.append("        <script src=\"cmlib/xml.js\"></script>\n");
        result.append("        <script src=\"cmlib/active-line.js\"></script>\n");
        result.append("        <script src=\"cmlib/matchbrackets.js\"></script>\n");
        result.append("        <script src=\"cmlib/closetag.js\"></script>\n");
        result.append("        <script src=\"cmlib/matchtags.js\"></script>\n");
        result.append("        <script src=\"cmlib/xml-fold.js\"></script>\n");
        result.append("        <script src=\"cmlib/show-hint.js\"></script>\n");
        result.append("        <script src=\"lib/flui-util.js\"></script>\n");
        result.append("        " + GeneratorUtil.generateHTMLJSText(project, browserType, true) + "\n");
        result.append("        <script type=\"text/javascript\">\n");
        result.append("            " + new JSOpenScreenFunctionsCreatorLogic().createOpenScreenSinglePageApp(project).toString().replace("\n", "\n            ") + "\n");
        result.append("            " + new JSGetClientPropertiesFunctionCreatorLogic().createGetClientPropertiesFunction(browserType).toString().replace("\n", "\n            ") + "\n\n");
        result.append("            " + new JSStringResourceFunctionsCreatorLogic().createStringResourceFunctions().toString().replace("\n", "\n            ") + "\n");
        result.append("        </script>\n");
        result.append("        " + GeneratorConstants.BOX_1_CM_HTML + "\n");
        
        
        
//        result.append("        <div style=\"border-style: solid;border-color: white;border-width:10px;\">\n");
        result.append("        <div>\n");
        for (ScreenDefinition i : project.getScreenDefinitionsMap().values()) {
            //            if ((i.getID().equals("screen1")) || (i.getID().equals("screen2"))) {
            //            if (BaseUtil.in(i.getID(), "screen1", "screen2", "imagesScreen", "namesScreen", "codeEditorScreen", "nestedContainerScreen", "openParamScreen",
            //                    "openParamScreen2", "pluginScreen")) {
            //            if (BaseUtil.in(i.getID(), "screen1", "screen2", "imagesScreen", "namesScreen", "codeEditorScreen", "nestedContainerScreen", "pluginScreen",
            //                    "openParamScreen")) {
//            if (BaseUtil.in(i.getID(), "openParamScreen", "openParamScreen2")) {
        	int margin = project.getProjectDefinition().getMargin();
        	String styleString = "position:absolute;width: 98%;margin-left:" + margin + "px;margin-right:" + margin + "px;";
                result.append("        <div ng-controller=\"" + GeneratorUtil.getAngularControllerName(i) + "\" id=\"" + GeneratorUtil.getHTMLScreenPanelName(i)
                        + "\" style=\"" + styleString + "\" ng-show=\"visible\" ng-cloak=\"\">\n");
                result.append("            <!-- ######################## Beginning: HTML code of Screen '" + i.getID() + "' ################################# -->\n");
                result.append("            <script src=\"" + GeneratorUtil.getJSPart1Filename(i) + "\"></script>\n");
                result.append("            "
                        + new MultiPageAppHTMLGeneratorService().generateHTMLBodyContentText(project, i, browserType, true).toString().replace("\n", "\n        ") + "\n");
                result.append("            <script src=\"" + GeneratorUtil.getJSPart2Filename(i) + "\"></script>\n");
                result.append("            <!-- ######################## End: HTML code of Screen '" + i.getID() + "' ################################# -->\n");
                result.append("        </div>\n");
//            }

        }
        result.append("        </div>\n");

        result.append("        <script type=\"text/javascript\">\n");
		result.append("            angular.element(document).ready(function () {\n");
		result.append("                console.log('page loading completed');\n");
		result.append("                openScreenSinglePageApp(\"" + project.getProjectDefinition().getStartScreenID() + "\", null);\n");
		result.append("            });\n");
        result.append("        </script>\n");

        result.append("    </body>\n");
        result.append("</html>");


        File file = new File(dir, GeneratorUtil.createHTMLFilename(browserType));
        FileUtil.writeStringToFile(file, result.toString());
    }

    //    private void log(String message) {
    //        System.out.println("SinglePageAppHTMLGeneratorService: " + message);
    //    }

}
