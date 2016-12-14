package com.bright_side_it.fliesenui.generator.util;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.TextUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.base.util.BaseConstants.LanguageFlavor;
import com.bright_side_it.fliesenui.dto.model.DTODefinition;
import com.bright_side_it.fliesenui.plugin.model.PluginEvent;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.project.model.ProjectDefinition;
import com.bright_side_it.fliesenui.project.model.SharedReplyInterface;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.CellItem;
import com.bright_side_it.fliesenui.screendefinition.model.CodeEditorWidget;
import com.bright_side_it.fliesenui.screendefinition.model.DTODeclaration;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSourceContainer;
import com.bright_side_it.fliesenui.screendefinition.model.PluginInstance;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.SelectBox;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidgetItem;
import com.bright_side_it.fliesenui.screendefinition.model.Timer;
import com.bright_side_it.fliesenui.screendefinition.model.EventParameter.WidgetProperty;

public class GeneratorUtil {
    public static File getCorePackageDir(File outputBaseDir) {
        return new File(getGeneratedFilesDir(outputBaseDir), "core");
    }

    public static File getWebOutputDir(File outputBaseDir) {
        return new File(getGeneratedFilesDir(outputBaseDir), "web");
    }

    public static File getImageAssetOutputDir(File outputBaseDir) {
        return new File(getWebOutputDir(outputBaseDir), "img");
    }

    public static File getGeneratedFilesDir(File outputBaseDir) {
        File dir = new File(outputBaseDir, "generated");
        return new File(dir, "fliesenui");
    }

    public static String createHTMLFilename(ScreenDefinition screenDefinition, BrowserType browserType) {
        return createHTMLFilename(screenDefinition.getID(), browserType);
    }

    public static String createHTMLFilename(BrowserType browserType) {
        return "_" + getBrowserTypeFilenameSuffix(browserType) + "_app" + GeneratorConstants.HTML_FILE_ENDING;
    }

    public static String createHTMLFilenameWithBrowserTypePlaceholder(ScreenDefinition screenDefinition) {
        return screenDefinition.getID() + "_" + GeneratorConstants.BROWSER_TYPE_FILENAME_PLACEHOLDER + GeneratorConstants.HTML_FILE_ENDING;
    }

    public static String createHTMLFilename(String screenDefinitionID, BrowserType browserType) {
        return screenDefinitionID + "_" + getBrowserTypeFilenameSuffix(browserType) + GeneratorConstants.HTML_FILE_ENDING;
    }

    public static String getBrowserTypeFilenameSuffix(BrowserType browserType) {
        switch (browserType) {
        case JAVA_FX:
            return "jfx";
        case WEB:
            return "web";
        default:
            throw new RuntimeException("Unexpected browser type: " + browserType);
        }
    }

    public static File getScreenPackageDir(File outputBaseDir) {
        return new File(getGeneratedFilesDir(outputBaseDir), "screen");
    }

    public static File getDTOPackageDir(File javaBaseDir) {
        File packageDir = new File(javaBaseDir, "generated");
        packageDir = new File(packageDir, "fliesenui");
        packageDir = new File(packageDir, "dto");
        return packageDir;
    }

    public static String getViewClassName(ScreenDefinition screenDefinition) {
        return BaseUtil.idToFirstCharUpperCase(screenDefinition.getID()) + GeneratorConstants.SCREEN_CLASS_NAME_SUFFIX;
    }

    public static String getSharedReplyInterfaceName(SharedReplyInterface sharedReplyInterface) {
    	return BaseUtil.idToFirstCharUpperCase(sharedReplyInterface.getID()) + GeneratorConstants.SHARED_REPLY_INTERFACE_NAME_SUFFIX;
    }
    
    public static String getReplyClassName(ScreenDefinition screenDefinition) {
        return BaseUtil.idToFirstCharUpperCase(screenDefinition.getID()) + GeneratorConstants.SCREEN_REPLY_CLASS_NAME_SUFFIX;
    }

    public static String getViewListenerClassName(ScreenDefinition screenDefinition) {
        return BaseUtil.idToFirstCharUpperCase(screenDefinition.getID()) + GeneratorConstants.SCREEN_LISTENER_CLASS_NAME_SUFFIX;
    }

    public static String getDTOClassName(DTODefinition dtoDefinition) {
        return getDTOClassName(dtoDefinition.getID());
    }

    public static String getDTOBuilderClassName(DTODefinition dtoDefinition) {
    	return getDTOBuilderClassName(dtoDefinition.getID());
    }
    
    public static String getDTOClassName(String dtoID) {
        return BaseUtil.idToFirstCharUpperCase(dtoID) + GeneratorConstants.DTO_CLASS_NAME_SUFFIX;
    }

    public static String getDTOBuilderClassName(String dtoID) {
    	return BaseUtil.idToFirstCharUpperCase(dtoID) + GeneratorConstants.DTO_BUILDER_CLASS_NAME_SUFFIX;
    }
    
    public static List<BasicWidget> getAllBasicWidgets(ScreenDefinition screenDefinition) {
        return BaseUtil.getAllBasicWidgets(screenDefinition);
    }

    public static String getClickedListenerMethodName(BasicWidget widget) {
        return BaseUtil.buildIDWithPrefix(widget.getID() + "Clicked", "on");
    }

    public static String getOnTimerListenerMethodName(Timer timer) {
    	return TextUtil.addSuffixIfMissing(BaseUtil.buildIDWithPrefix(timer.getID() + "", "on"), "Timer");
    }
    
    public static String getContextAssistListenerMethodName(CodeEditorWidget widget) {
        return BaseUtil.buildIDWithPrefix(widget.getID() + "ContextAssist", "on");
    }

    public static String getSaveListenerMethodName(CodeEditorWidget widget) {
        return BaseUtil.buildIDWithPrefix(widget.getID() + "Save", "on");
    }

    public static StringBuilder addJavaGeneratedCommend(StringBuilder text) {
        String comment = "/*Generated! Do not modify!*/ ";
        return new StringBuilder(comment + text.toString().replace("\r", "").replace("\n", "\n" + comment));
    }

    public static String getFieldNameWithoutDTO(String dtoField) {
        int pos = dtoField.indexOf(".");
        return dtoField.substring(pos + 1);
    }

    public static String createJSButtonClickMethodName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return getScreenJSVariablePrefix(screenDefinition) + "widgetButton" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "Clicked";
    }
    
	public static String createJSSelectBoxSetSelectedIDMethodName(ScreenDefinition screenDefinition, SelectBox selectBox) {
        return getScreenJSVariablePrefix(screenDefinition) + "set" + BaseUtil.idToFirstCharUpperCase(selectBox.getID()) + "SelectedID";
	}
	
	public static String createJSSelectBoxGetSelectedIDMethodName(ScreenDefinition screenDefinition, SelectBox selectBox) {
		return getScreenJSVariablePrefix(screenDefinition) + "get" + BaseUtil.idToFirstCharUpperCase(selectBox.getID()) + "SelectedID";
	}
	
	public static String createJSUploadIFrameLoadedMethodName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return createScreenIDPrefix(screenDefinition) + "uploadIFrame" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "Loaded";
	}

	public static String createJSJavaFXUploadFileMethodName(ScreenDefinition screenDefinition, BasicWidget widget) {
		return createScreenIDPrefix(screenDefinition) + "javaFXUploadFile" + BaseUtil.idToFirstCharUpperCase(widget.getID());
	}
	
	public static String createJSFileSelectedToUploadMethodName(ScreenDefinition screenDefinition, BasicWidget widget) {
		return createScreenIDPrefix(screenDefinition) + "fileSelectedToUpload" + BaseUtil.idToFirstCharUpperCase(widget.getID());
	}
	
	public static String createHTMLUploadFormID(ScreenDefinition screenDefinition, BasicWidget widget) {
		return getScreenJSVariablePrefix(screenDefinition) + "uploadForm" + BaseUtil.idToFirstCharUpperCase(widget.getID());
	}
	
	public static String createHTMLMarkdownViewID(ScreenDefinition screenDefinition, BasicWidget widget) {
		return getScreenJSVariablePrefix(screenDefinition) + "markdownView" + BaseUtil.idToFirstCharUpperCase(widget.getID());
	}
	
	public static String createHTMLFileUploadEventDataFieldID(ScreenDefinition screenDefinition, BasicWidget widget) {
		return getScreenJSVariablePrefix(screenDefinition) + "uploadFormEventData" + BaseUtil.idToFirstCharUpperCase(widget.getID());
	}
	

    public static String createJSOnChangedMethodName(ScreenDefinition screenDefinition, BasicWidget widget) {
    	return getScreenJSVariablePrefix(screenDefinition) + "on" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "Changed";
    }
    
    public static String createJSPluginEventMethodName(ScreenDefinition screenDefinition, PluginInstance pluginInstance, PluginEvent event) {
        return getScreenJSVariablePrefix(screenDefinition) + "plugin" + BaseUtil.idToFirstCharUpperCase(pluginInstance.getID()) + "Event"
                + BaseUtil.idToFirstCharUpperCase(event.getID());
    }

    public static String createJavaPluginEventMethodName(PluginInstance pluginInstance, PluginEvent event) {
        return "onPlugin" + BaseUtil.idToFirstCharUpperCase(pluginInstance.getID()) + "Event" + BaseUtil.idToFirstCharUpperCase(event.getID());
    }

    public static String createJSButtonClickMethodName(BasicWidget widget) {
        return "widgetButton" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "Clicked";
    }

    public static String createJavaFileUploadMethodName(BasicWidget widget) {
    	return "on" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "FileUpload";
    }
    
    public static String createJavaFileUploadFinishedMethodName(BasicWidget widget) {
    	return "on" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "FileUploadFinished";
    }
    
    public static String createJavaOnChangedMethodName(BasicWidget widget) {
    	return "on" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "Changed";
    }

    public static String createJavaSelectBoxChangedMethodName(SelectBox selectBox) {
    	return "on" + TextUtil.addSuffixIfMissing(BaseUtil.idToFirstCharUpperCase(selectBox.getID()), "SelectBox") + "Changed";
    }
    
    public static String createJavaTimerOccuredMethodName(Timer timer) {
    	return "on" + TextUtil.addSuffixIfMissing(BaseUtil.idToFirstCharUpperCase(timer.getID()), "Timer");
    }
    
    public static String createJSTableButtonClickMethodName(ScreenDefinition screenDefinition, TableWidget widget, TableWidgetItem tableItem) {
        try {
            return getScreenJSVariablePrefix(screenDefinition) + "table" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "Button"
                    + BaseUtil.idToFirstCharUpperCase(tableItem.getID()) + "Clicked";
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public static String createJSTableRowClickMethodName(ScreenDefinition screenDefinition, TableWidget widget) {
        return getScreenJSVariablePrefix(screenDefinition) + "table" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "RowClicked";
    }

    public static String createJavaTableButtonClickMethodName(TableWidget widget, TableWidgetItem tableItem) {
        return "on" + TextUtil.addSuffixIfMissing(BaseUtil.idToFirstCharUpperCase(widget.getID()), "Table") + TextUtil.addSuffixIfMissing(BaseUtil.idToFirstCharUpperCase(tableItem.getID()), "Button") + "Clicked";
    }

    public static String createJavaTableRowClickMethodName(TableWidget widget) {
        return "on" + TextUtil.addSuffixIfMissing(BaseUtil.idToFirstCharUpperCase(widget.getID()), "Table") + "RowClicked";
    }

    public static String getJSWidgetProgressBarModeVariableName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return getScreenJSVariablePrefix(screenDefinition) + widget.getID() + "_propertyProgressBarMode";
    }

    public static String getJSWidgetProgressBarProgressVariableName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return getScreenJSVariablePrefix(screenDefinition) + widget.getID() + "_propertyProgressBarProgress";
    }

    public static String getJSTimerActiveVariableName(ScreenDefinition screenDefinition, Timer timer) {
    	return getScreenJSVariablePrefix(screenDefinition) + timer.getID() + "_active";
    }
    
    public static String getJSSelectBoxSelectedItemVariableName(ScreenDefinition screenDefinition, SelectBox selectBox) {
    	return getScreenJSVariablePrefix(screenDefinition) + selectBox.getID() + "_selectedItem";
    }
    
    public static String getJSSelectBoxChangeMethodName(ScreenDefinition screenDefinition, SelectBox selectBox) {
    	return getScreenJSVariablePrefix(screenDefinition) + selectBox.getID() + "_changed";
    }
    
    public static String getJSSetMarkdownViewTextMethodName(ScreenDefinition screenDefinition, BasicWidget basicWidget) {
    	return getScreenJSVariablePrefix(screenDefinition) + "set" + BaseUtil.idToFirstCharUpperCase(basicWidget.getID()) + "Text";
    }
    
    public static String getJSWidgetTextVariableName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return getJSWidgetTextVariableName(screenDefinition, widget.getID());
    }

    public static String getJSWidgetHTMLID(ScreenDefinition screenDefinition, BasicWidget widget) {
    	return getScreenJSVariablePrefix(screenDefinition) + widget.getID();
    }

    public static String getJSWidgetTextVariableName(ScreenDefinition screenDefinition, String widgetID) {
        return getScreenJSVariablePrefix(screenDefinition) + widgetID + "_propertyText";
    }

    public static String getJSWidgetSelectedVariableName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return getJSWidgetSelectedVariableName(screenDefinition, widget.getID());
    }

    public static String getJSWidgetSelectedVariableName(ScreenDefinition screenDefinition, String widgetID) {
        return getScreenJSVariablePrefix(screenDefinition) + widgetID + "_propertySelected";
    }

    public static String getJSPluginVariableName(ScreenDefinition screenDefinition, String pluginInstanceID, String variableID) {
        return getScreenJSVariablePrefix(screenDefinition) + "plugin_" + pluginInstanceID + "_" + variableID;
    }

    public static String getJSImageSourceVariableName(ScreenDefinition screenDefinition, ImageSourceContainer imageSourceContainer) {
        return getScreenJSVariablePrefix(screenDefinition) + imageSourceContainer.getID() + "_propertyImageSource";
    }

    public static String getJSWidgetVisibleVariableName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return getJSWidgetVisibleVariableName(screenDefinition, widget.getID());
    }

    public static String getJSWidgetBackgroundColorVariableName(ScreenDefinition screenDefinition, BasicWidget widget) {
    	return getJSWidgetBackgroundColorVariableName(screenDefinition, widget.getID());
    }
    
    public static String getJSWidgetVisibleVariableName(ScreenDefinition screenDefinition, String widgetID) {
        return getScreenJSVariablePrefix(screenDefinition) + widgetID + "_propertyVisible";
    }

    public static String getJSWidgetBackgroundColorVariableName(ScreenDefinition screenDefinition, String widgetID) {
    	return getScreenJSVariablePrefix(screenDefinition) + widgetID + "_propertyBackgroundColor";
    }
    
    public static String getJSWidgetLabelVariableName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return getScreenJSVariablePrefix(screenDefinition) + widget.getID() + "_propertyLabel";
    }

    public static String createCodeWidgetVariableName(ScreenDefinition screenDefinition, CodeEditorWidget codeEditor) {
        return getScreenJSVariablePrefix(screenDefinition) + "codeEditor" + BaseUtil.idToFirstCharUpperCase(codeEditor.getID());
    }

    public static String getJSPart1Filename(ScreenDefinition screenDefinition) {
        return screenDefinition.getID() + "_part1" + GeneratorConstants.JAVASCRIPT_FILE_ENDING;
    }

    public static String getJSPart2Filename(ScreenDefinition screenDefinition) {
        return screenDefinition.getID() + "_part2" + GeneratorConstants.JAVASCRIPT_FILE_ENDING;
    }

    public static String createJSInitDTOTypeForPreviewMethodName(ScreenDefinition screenDefinition, String dtoID) {
        return screenDefinition.getID() + "$initDTOType" + BaseUtil.idToFirstCharUpperCase(dtoID) + "ForPreview";
    }

    public static String widgetPropertyToSuffix(WidgetProperty widgetProperty) throws Exception {
        switch (widgetProperty) {
        case TEXT:
            return "Text";
        case SELECTED_ID:
        	return "SelectedID";
        case SELECTED:
            return "Selected";
        case LINE:
            return "Line";
        case POS_IN_LINE:
            return "PosInLine";
        default:
            throw new Exception("Unkonwn widget property: " + widgetProperty);
        }
    }

    public static String widgetPropertyToDataType(WidgetProperty widgetProperty) throws Exception {
        switch (widgetProperty) {
        case TEXT:
            return "String";
        case SELECTED_ID:
        	return "String";
        case SELECTED:
            return "boolean";
        case LINE:
            return "int";
        case POS_IN_LINE:
            return "int";
        default:
            throw new Exception("Unkonwn widget property: " + widgetProperty);
        }
    }

    public static SortedMap<String, CellItem> readWidgetIDMap(ScreenDefinition screenDefinition) {
        SortedMap<String, CellItem> result = new TreeMap<>();
        for (BasicWidget i : BaseUtil.getAllBasicWidgets(screenDefinition)) {
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

    public static String toPlaceholder(String string) {
        return "${" + string + "}";
    }

    public static String toJavaClassString(BasicType type) throws Exception {
        switch (type) {
        case BOOLEAN:
            return "boolean";
        case STRING:
            return "String";
        case LONG:
            return "long";
        }
        throw new Exception("Unknown type: " + type);
    }

    public static String getImageAssetsClassName() {
        return "FLUIImageAssets";
    }

    public static String getScreensClassName() {
    	return "FLUIScreens";
    }
    
    public static String getJSCreateImageAssetIDToNameMapFunctionName(ScreenDefinition screenDefinition) {
        return getJSCreateImageAssetIDToNameMapFunctionName(screenDefinition.getID());
    }

    public static String getJSCreateImageAssetIDToNameMapFunctionName(String screenID) {
        return screenID + "$createImageAssetIDToNameMap";
    }

    public static String getJSWidgetTextSetterMethodName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return screenDefinition.getID() + "$set" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "_widgetText";
    }

    public static String getJSWidgetTextGetterMethodName(ScreenDefinition screenDefinition, BasicWidget widget) {
        return screenDefinition.getID() + "$get" + BaseUtil.idToFirstCharUpperCase(widget.getID()) + "_widgetText";
    }

    public static String getJSDTOSetterMethodName(ScreenDefinition screenDefinition, DTODeclaration dtoDeclaration) {
        return getJSDTOSetterMethodName(screenDefinition, dtoDeclaration.getID());
    }

    public static String getJSDTOGetterMethodName(ScreenDefinition screenDefinition, DTODeclaration dtoDeclaration) {
        return getJSDTOGetterMethodName(screenDefinition, dtoDeclaration.getID());
    }

    public static String getJSDTOSetterMethodName(ScreenDefinition screenDefinition, String dtoID) {
        return screenDefinition.getID() + "$set" + BaseUtil.idToFirstCharUpperCase(dtoID) + "_dto";
    }

    public static String getJSDTOGetterMethodName(ScreenDefinition screenDefinition, String dtoID) {
        return screenDefinition.getID() + "$get" + BaseUtil.idToFirstCharUpperCase(dtoID) + "_dto";
    }

    public static CharSequence getAngularControllerName(ScreenDefinition screenDefinition) {
        return screenDefinition.getID() + "_Ctrl";
    }

    public static String getHTMLScreenPanelName(ScreenDefinition screenDefinition) {
        return "screen" + BaseUtil.idToFirstCharUpperCase(screenDefinition.getID()) + "Panel";
    }

    public static String generateHTMLJSText(Project project, BrowserType browserType, boolean singlePageApp) throws Exception {
        ProjectDefinition projectDefinition = project.getProjectDefinition();
        StringBuilder result = new StringBuilder();
        result.append("<script type=\"text/javascript\">\n");
        result.append("            " + GeneratorConstants.BROWSER_MODE_JS_VARIABLE + " = \"" + GeneratorUtil.getBrowserModeJSName(browserType) + "\";\n");
        result.append("            htmlFileSuffix = \"" + GeneratorUtil.getBrowserTypeFilenameSuffix(browserType) + "\";\n");
        result.append("            singlePageApp = " + singlePageApp + ";\n");


        result.append("            var app = angular.module('app', ['ngMaterial']).config(function($mdThemingProvider) {\n");
        result.append("                $mdThemingProvider.theme('default')\n");
        result.append("                    .primaryPalette('" + projectDefinition.getThemePrimaryPalette() + "')\n");
        result.append("                    .accentPalette('" + projectDefinition.getThemeAccentePalette() + "')\n");
        result.append("                    .backgroundPalette('" + projectDefinition.getThemeBackgroundPalette() + "')\n");
        result.append("                    .warnPalette('" + projectDefinition.getThemeWarnPalette() + "')");
        if (projectDefinition.isDarkTheme()) {
            result.append("                    .dark()");
        }
        result.append(";\n");
        result.append("            });\n");


        result.append("        </script>");



        return result.toString();
    }

    public static String getBrowserModeJSName(BrowserType browserType) throws Exception{
    	if (browserType == BrowserType.WEB) {
            return "WebBrowser";
        } else if (browserType == BrowserType.JAVA_FX) {
            return "JavaFX";
        } else {
            throw new Exception("Unknown browser type: " + browserType);
        }		
	}

	private static String getScreenJSVariablePrefix(ScreenDefinition screenDefinition) {
        return screenDefinition.getID() + "_";
    }

	public static String createScreenIDPrefix(ScreenDefinition screenDefinition) {
		return screenDefinition.getID() + "$";
	}

	public static String getImageStreamPrefix(BrowserType browserType) throws Exception{
    	if (browserType == BrowserType.WEB) {
            return "imagestream/";
        } else if (browserType == BrowserType.JAVA_FX) {
            return "imagestream:";
        } else {
            throw new Exception("Unknown browser type: " + browserType);
        }		
	}

	public static String getScreenManagerClassName(LanguageFlavor languageFlavor) throws Exception{
		switch (languageFlavor) {
		case ANDROID:
			return "FLUIScreenManagerAndroid";
		case JAVA:
			return "FLUIScreenManager";
		default:
			throw new Exception("Unknown language flavor: " + languageFlavor);
		}
	}

    public static String getDTOClassName(DTODeclaration dtoDeclaration, ScreenDefinition screenDefinition) {
        DTODeclaration declaration = screenDefinition.getDTODeclarations().get(dtoDeclaration.getID());
        String type = declaration.getType();
        return GeneratorUtil.getDTOClassName(type);
    }

    public static Set<String> getRequiredDTOClassNames(Project project, ScreenDefinition screenDefinition) {
        Set<String> result = new TreeSet<String>();
        if (screenDefinition.getDTODeclarations() != null) {
            for (DTODeclaration i : screenDefinition.getDTODeclarations().values()) {
                result.add(getDTOClassName(i, screenDefinition));
            }
        }

        for (ScreenDefinition i : project.getScreenDefinitionsMap().values()) {
            //        	log("getRequiredDTOClassNames for screen " + screenDefinition.getID() + ": checking screen " + i.getID() + "...");
            if (i.getParameterDTOID() != null) {
                //        		log("getRequiredDTOClassNames for screen " + screenDefinition.getID() + ": it needs a dto");
                String dtoClassName = getDTOClassName(i.getDTODeclarations().get(i.getParameterDTOID()), i);
                //        		log("getRequiredDTOClassNames for screen " + screenDefinition.getID() + ": dtoClassName = " + dtoClassName);
                result.add(dtoClassName);
            }
        }

        return result;
    }

}
