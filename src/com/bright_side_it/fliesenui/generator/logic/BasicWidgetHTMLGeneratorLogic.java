package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.screendefinition.model.BasicWidget;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSource;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSourceContainer;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class BasicWidgetHTMLGeneratorLogic {
    private HTMLTagLogic tagLogic = new HTMLTagLogic();

    public void generateHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
        switch (widget.getType()) {
        case BUTTON:
            generateButtonHTML(parentTag, screenDefinition, widget, browserType);
            break;
        case IMAGE_BUTTON:
            generateImageButtonHTML(parentTag, screenDefinition, widget, browserType);
            break;
        case LABEL:
            generateLabelHTML(parentTag, screenDefinition, widget, browserType);
            break;
        case MARKDOWN_VIEW:
        	generateMarkdownViewHTML(parentTag, screenDefinition, widget, browserType);
        	break;
        case IMAGE:
            generateImageHTML(parentTag, screenDefinition, widget, browserType);
            break;
        case TEXT_FIELD:
            generateTextFieldHTML(parentTag, screenDefinition, widget);
            break;
        case TEXT_AREA:
            generateTextAreaHTML(parentTag, screenDefinition, widget);
            break;
        case CHECKBOX:
            generateCheckboxHTML(parentTag, screenDefinition, widget);
            break;
        case SWITCH:
            generateSwitchHTML(parentTag, screenDefinition, widget);
            break;
        case PROGRESS_BAR:
            generateProgressBarHTML(parentTag, screenDefinition, widget);
            break;
        case SPACE:
            generateSpaceHTML(parentTag, screenDefinition, widget);
            break;
        case FILE_UPLOAD:
        	generateFileUploadHTML(parentTag, screenDefinition, widget, browserType);
        	break;
        default:
            throw new RuntimeException("Unknown type: " + widget.getType());
        }
    }

    private void generateTextAreaHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget) {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        String styleString = "resize:none; width:99%;";
        if (widget.getHeight() != null){
        	styleString += " height:" + widget.getHeight() + "px;";
        }
        styleString += "  background: {{" + GeneratorUtil.getJSWidgetBackgroundColorVariableName(screenDefinition, widget) + "}}";
        HTMLTag resultTag = null;
        if (widget.getLabelText() != null) {
            HTMLTag inputContainer = tagLogic.addTag(parentTag, "md-input-container", null, "class", "md-block");
            tagLogic.addTag(inputContainer, "label", createLabelVarPlaceholder(screenDefinition, widget));
            resultTag = tagLogic.addTag(inputContainer, "textarea", "", "ng-model", GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget), 
                    "ng-show", showVariable, "style", styleString);
        } else {
        	resultTag = tagLogic.addTag(parentTag, "textarea", "", "ng-model", GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget), 
                    "ng-show", showVariable, "style", styleString);
        }
        
        if ((widget.isReadOnly() != null) && (widget.isReadOnly().booleanValue())){
        	tagLogic.setAttribute(resultTag, "ng-readonly", "" + widget.isReadOnly().booleanValue());
        } else {
        	if (Boolean.TRUE.equals(widget.getSelectOnFocus())){
        		tagLogic.setAttribute(resultTag, "md-select-on-focus", "");
        	}
        }
        tagLogic.setAttribute(resultTag, "id", GeneratorUtil.getJSWidgetHTMLID(screenDefinition, widget));
    }

    private void generateCheckboxHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget) {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        String selectedVariable = GeneratorUtil.getJSWidgetSelectedVariableName(screenDefinition, widget);
        String textVariable = GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget);
        String onChangeString = GeneratorUtil.createJSOnChangedMethodName(screenDefinition, widget) + "(" + selectedVariable + ");";
        tagLogic.addTag(parentTag, "md-checkbox", "{{" + textVariable + "}}", "ng-model", selectedVariable, "ng-show", showVariable, "ng-change", onChangeString);
    }

    private void generateSwitchHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget) {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        String selectedVariable = GeneratorUtil.getJSWidgetSelectedVariableName(screenDefinition, widget);
        String textVariable = GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget);
        String onChangeString = GeneratorUtil.createJSOnChangedMethodName(screenDefinition, widget) + "(" + selectedVariable + ");";
        tagLogic.addTag(parentTag, "md-switch", "{{" + textVariable + "}}", "ng-model", selectedVariable, "ng-show", showVariable, "ng-change", onChangeString);
    }

    private void generateProgressBarHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget) {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        tagLogic.addTag(parentTag, "md-progress-linear", null, "md-mode",
                "{{" + GeneratorUtil.getJSWidgetProgressBarModeVariableName(screenDefinition, widget) + "}}", "value",
                "{{" + GeneratorUtil.getJSWidgetProgressBarProgressVariableName(screenDefinition, widget) + "}}", "ng-show", showVariable);
    }

    private void generateSpaceHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget) {
        if (widget.getID() != null) {
            String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
            tagLogic.addTag(parentTag, "div", "", "style", "height:" + widget.getHeight() + "px", "ng-show", showVariable);
        } else {
            tagLogic.addTag(parentTag, "div", "", "style", "height:" + widget.getHeight() + "px");
        }
    }

    private String createTextVarPlaceholder(ScreenDefinition screenDefinition, BasicWidget widget) {
        return "{{" + GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget) + "}}";
    }

    private String createLabelVarPlaceholder(ScreenDefinition screenDefinition, BasicWidget widget) {
        return "{{" + GeneratorUtil.getJSWidgetLabelVariableName(screenDefinition, widget) + "}}";
    }

    private void generateButtonHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
        switch (browserType) {
        case JAVA_FX:
            generateButtonHTMLForJavaFX(parentTag, screenDefinition, widget, browserType);
            break;
        case WEB:
            generateButtonHTMLForWeb(parentTag, screenDefinition, widget, browserType);
            break;
        default:
            throw new Exception("Unknown browser type: " + browserType);
        }
    }

    private void generateImageButtonHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
        switch (browserType) {
        case JAVA_FX:
            generateImageButtonHTMLForJavaFX(parentTag, screenDefinition, widget, browserType);
            break;
        case WEB:
            generateImageButtonHTMLForWeb(parentTag, screenDefinition, widget, browserType);
            break;
        default:
            throw new Exception("Unknown browser type: " + browserType);
        }
    }

    private void generateButtonHTMLForWeb(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        String primaryString = "";
        if (widget.isPrimary()) {
            primaryString = " md-primary";
        }
        String extendStyleString = "";
        if (widget.getStyle() != null) {
            switch (widget.getStyle()) {
            case NORMAL:
                extendStyleString = "";
                break;
            case SMALL:
                extendStyleString = " fliesenUIButtonExtendSmall";
                break;
            case TINY:
                extendStyleString = " fliesenUIButtonExtendTiny";
                break;
            default:
                throw new Exception("Unexpected style: " + widget.getStyle());
            }
        }
        String onclickString = GeneratorUtil.createJSButtonClickMethodName(screenDefinition, widget) + "();";

        if (widget.getImageSource() == null) {
            tagLogic.addTag(parentTag, "md-button", createTextVarPlaceholder(screenDefinition, widget), "class", "md-raised" + extendStyleString + primaryString,
                    "ng-click", onclickString, "ng-show", showVariable);
        } else {
            HTMLTag buttonTag = tagLogic.addTag(parentTag, "md-button", null, "class", "md-raised" + extendStyleString + primaryString, "ng-click", onclickString,
                    "ng-show", showVariable);
            //            if (widget.getImageSource().getFormatType() == ImageFormatType.VECTOR) {
            //                tagLogic.addTag(buttonTag, "md-icon", "", "md-svg-icon", createImageSourcePlaceholder(widget));
            //            } else if (widget.getImageSource().getFormatType() == ImageFormatType.PIXEL) {
            addImageSizesIfProvided(tagLogic.addTag(buttonTag, "img", null, "ng-src", createImageSourcePlaceholder(screenDefinition, widget, browserType)), widget.getImageSource());
            //            }
            tagLogic.addTag(buttonTag, "span", createTextVarPlaceholder(screenDefinition, widget));
        }
    }

    private void generateImageButtonHTMLForWeb(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        String primaryString = "";
        if (widget.isPrimary()) {
            primaryString = " fliesenUIPrimary";
        }
        String onclickString = GeneratorUtil.createJSButtonClickMethodName(screenDefinition, widget) + "();";

        HTMLTag buttonTag = tagLogic.addTag(parentTag, "md-button", null, "class", "md-raised md-icon-button" + primaryString, "ng-click", onclickString, "ng-show",
                showVariable);
        //        if (widget.getImageSource().getFormatType() == ImageFormatType.VECTOR) {
        //            tagLogic.addTag(buttonTag, "md-icon", "", "md-svg-icon", createImageSourcePlaceholder(widget));
        //        } else if (widget.getImageSource().getFormatType() == ImageFormatType.PIXEL) {
        addImageSizesIfProvided(tagLogic.addTag(buttonTag, "img", null, "ng-src", createImageSourcePlaceholder(screenDefinition, widget, browserType)), widget.getImageSource());
        //        }
    }


    private String createImageSourcePlaceholder(ScreenDefinition screenDefinition, ImageSourceContainer imageSourceContainer, BrowserType browserType) throws Exception {
        ImageSource imageSource = imageSourceContainer.getImageSource();
        String variable = "{{" + GeneratorUtil.getJSImageSourceVariableName(screenDefinition, imageSourceContainer) + "}}";

        if (imageSource.getImageAssetID() != null) {
            return "img/" + variable;
        } else if (imageSource.getImageStreamID() != null) {
            return GeneratorUtil.getImageStreamPrefix(browserType) + variable;
        } else if (imageSource.getImageURL() != null) {
            return "" + variable;

        } else if (imageSource.getImageAssetIDDTOField() != null) {
            return "img/{{imageAssetIDToName[" + imageSource.getImageAssetIDDTOField() + "]}}";
        } else if (imageSource.getImageStreamIDDTOField() != null) {
            return GeneratorUtil.getImageStreamPrefix(browserType) + "{{" + imageSource.getImageStreamIDDTOField() + "}}";
        } else if (imageSource.getImageURLDTOField() != null) {
            return "{{" + imageSource.getImageURLDTOField() + "}}";
        } else {
            throw new Exception("image source location type not implemented yet");
        }
    }

    private void generateButtonHTMLForJavaFX(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        String primaryString = "";
        if (widget.isPrimary()) {
            primaryString = " fliesenUIPrimary";
        }
        String extendStyleString = "";
        if (widget.getStyle() != null) {
            switch (widget.getStyle()) {
            case NORMAL:
                extendStyleString = "";
                break;
            case SMALL:
                extendStyleString = " fliesenUIButtonExtendSmall";
                break;
            case TINY:
                extendStyleString = " fliesenUIButtonExtendTiny";
                break;
            default:
                throw new Exception("Unexpected style: " + widget.getStyle());
            }
        }
        String onclickString = GeneratorUtil.createJSButtonClickMethodName(screenDefinition, widget) + "();";
        if (widget.getImageSource() == null) {
            tagLogic.addTag(parentTag, "button", createTextVarPlaceholder(screenDefinition, widget), "class", "fliesenUIButton" + extendStyleString + primaryString,
                    "ng-click", onclickString, "ng-show", showVariable);
        } else {
            HTMLTag buttonTag = tagLogic.addTag(parentTag, "button", null, "class", "fliesenUIButton" + extendStyleString + primaryString, "ng-click", onclickString,
                    "ng-show", showVariable);
            //            if (widget.getImageSource().getFormatType() == ImageFormatType.VECTOR) {
            //                tagLogic.addTag(buttonTag, "md-icon", "", "md-svg-icon", createImageSourcePlaceholder(widget));
            //            } else if (widget.getImageSource().getFormatType() == ImageFormatType.PIXEL) {
            addImageSizesIfProvided(tagLogic.addTag(buttonTag, "img", null, "ng-src", createImageSourcePlaceholder(screenDefinition, widget, browserType)), widget.getImageSource());
            //            }
            tagLogic.addTag(buttonTag, "span", createTextVarPlaceholder(screenDefinition, widget));
        }
    }

    public static void addImageSizesIfProvided(HTMLTag tag, ImageSource imageSource) {
        if (imageSource.getWidth() != null) {
            tag.getAttributes().put("width", "" + imageSource.getWidth() + "px");
        }
        if (imageSource.getHeight() != null) {
            tag.getAttributes().put("height", "" + imageSource.getHeight() + "px");
        }
    }

    private void generateImageButtonHTMLForJavaFX(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        String primaryString = "";
        if (widget.isPrimary()) {
            primaryString = " fliesenUIPrimary";
        }
        String onclickString = GeneratorUtil.createJSButtonClickMethodName(screenDefinition, widget) + "();";

        HTMLTag buttonTag = tagLogic.addTag(parentTag, "button", null, "class", "fliesenUIImageButton" + primaryString, "ng-click", onclickString, "ng-show",
                showVariable);
        //        if (widget.getImageSource().getFormatType() == ImageFormatType.VECTOR) {
        //            tagLogic.addTag(buttonTag, "md-icon", "", "md-svg-icon", createImageSourcePlaceholder(widget));
        //        } else if (widget.getImageSource().getFormatType() == ImageFormatType.PIXEL) {
        addImageSizesIfProvided(tagLogic.addTag(buttonTag, "img", null, "ng-src", createImageSourcePlaceholder(screenDefinition, widget, browserType)), widget.getImageSource());
        //        }
    }



    private void generateLabelHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        String tagName = "span";
        if (widget.getStyle() != null) {
            switch (widget.getStyle()) {
            case NORMAL:
                tagName = "span";
                break;
            case SMALL:
                tagName = "h3";
                break;
            case MEDIUM:
                tagName = "h2";
                break;
            case LARGE:
                tagName = "h1";
                break;
            default:
                throw new Exception("Unknown widget style: " + widget.getStyle());
            }
        }
        
        String style = "white-space: pre-wrap;";

        if (widget.getImageSource() == null) {
            tagLogic.addTag(parentTag, tagName, createTextVarPlaceholder(screenDefinition, widget), "ng-show", showVariable, "style", style);
        } else {
            HTMLTag containerTag = tagLogic.addTag(parentTag, tagName, null, "ng-show", showVariable);
            //            if (widget.getImageSource().getFormatType() == ImageFormatType.VECTOR) {
            //                tagLogic.addTag(containerTag, "md-icon", "", "md-svg-icon", createImageSourcePlaceholder(widget));
            //            } else {
            addImageSizesIfProvided(tagLogic.addTag(containerTag, "img", null, "ng-src", createImageSourcePlaceholder(screenDefinition, widget, browserType)),
                    widget.getImageSource());
            //            }
            tagLogic.addTag(containerTag, "span", createTextVarPlaceholder(screenDefinition, widget), "style", style);
        }
    }
    
    private void generateMarkdownViewHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
    	String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
    	String backgroundColorVariable = GeneratorUtil.getJSWidgetBackgroundColorVariableName(screenDefinition, widget);
		HTMLTag cardContainerTag = tagLogic.addTag(parentTag, "md-card", null, "ng-show", showVariable);
		HTMLTag colorContainerTag = tagLogic.addTag(cardContainerTag, "span", null, "style", "background: {{" + backgroundColorVariable + "}}");
		tagLogic.addTag(colorContainerTag, "span", "", "id", GeneratorUtil.createHTMLMarkdownViewID(screenDefinition, widget));
    }
    
    private void generateFileUploadHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
    	switch (browserType) {
		case JAVA_FX:
			generateFileUploadHTMLForJavaFX(parentTag, screenDefinition, widget, browserType);
			break;
		case WEB:
			generateFileUploadHTMLForWeb(parentTag, screenDefinition, widget, browserType);
			break;
		default:
			throw new Exception("Unexpected browser type: " + browserType);
		}
    }
    
    private void generateFileUploadHTMLForJavaFX(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
    	String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        String onclickString = GeneratorUtil.createJSJavaFXUploadFileMethodName(screenDefinition, widget) + "();";
		tagLogic.addTag(parentTag, "button", createTextVarPlaceholder(screenDefinition, widget), "class", "fliesenUIButton",
                "onclick", onclickString, "ng-show", showVariable);
    }
    
    private void generateFileUploadHTMLForWeb(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
    	String screenIDPrefix = GeneratorUtil.createScreenIDPrefix(screenDefinition);
    	String iframeName = screenIDPrefix + widget.getID() + "_HiddenFrame";
    	String onLoadMethodName = GeneratorUtil.createJSUploadIFrameLoadedMethodName(screenDefinition, widget);
    	String uploadFormID = GeneratorUtil.createHTMLUploadFormID(screenDefinition, widget);
    	String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
    	tagLogic.addTag(parentTag, "iframe", "", "name", iframeName, "class", "hide", "onload", onLoadMethodName + "();");
    	HTMLTag formTag = tagLogic.addTag(parentTag, "form", null, "id", uploadFormID, "action", GeneratorConstants.WEB_FILE_UPLOAD_PATH, "method", "post", "enctype"
    			, "multipart/form-data", "target", iframeName, "ng-show", showVariable);
    	
    	String fileInputFieldID = screenIDPrefix + widget.getID() + "FileInputField";
    	String onChangeMethodName = GeneratorUtil.createJSFileSelectedToUploadMethodName(screenDefinition, widget);
    	tagLogic.addTag(formTag, "input", null, "class", "ng-hide", "id", fileInputFieldID, "type", "file", "name", GeneratorConstants.FILE_UPLOAD_FORM_UPLOADED_FILE_NAME
    			, "onChange", onChangeMethodName + "(document.getElementById('" + fileInputFieldID + "').value);");
    	String fileUploadEventDataFieldID = GeneratorUtil.createHTMLFileUploadEventDataFieldID(screenDefinition, widget);
    	tagLogic.addTag(formTag, "input", null, "class", "ng-hide", "id", fileUploadEventDataFieldID, "type", "text", "value", "", "name", GeneratorConstants.FILE_UPLOAD_FORM_REQUEST_DATA_PARAMETER_NAME);
    	tagLogic.addTag(formTag, "label", widget.getText(), "for", fileInputFieldID, "class", "md-button md-raised");
    }

    	
    private void generateImageHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget, BrowserType browserType) throws Exception {
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        HTMLTag containerTag = tagLogic.addTag(parentTag, "span", null, "ng-show", showVariable);
        //        if ((widget.getImageSource().getFormatType() == ImageFormatType.VECTOR) && (hasStreamImageSource(widget))){
        //            tagLogic.addTag(containerTag, "md-icon", "", "md-svg-icon", createImageSourcePlaceholder(widget));
        //        } else {
        addImageSizesIfProvided(tagLogic.addTag(containerTag, "img", null, "ng-src", createImageSourcePlaceholder(screenDefinition, widget, browserType)), widget.getImageSource());
        //        }
    }

    private void generateTextFieldHTML(HTMLTag parentTag, ScreenDefinition screenDefinition, BasicWidget widget) {
        //: style="width=99%" lets the text field fill all the space. 99% because 100% seems to overlap(!?!) with the next widget
        //: "position: relative;top: 50%;transform: translateY(-50%);-webkit-transform: translateY(-50%);" is needed to center vertically
        //        String styleString = "width:99%;position: relative;top: 50%;transform: translateY(-50%);-webkit-transform: translateY(-50%);";
        String styleString = "width:99%;";
        styleString += " background: {{" + GeneratorUtil.getJSWidgetBackgroundColorVariableName(screenDefinition, widget) + "}}";
        String variableName = GeneratorUtil.getJSWidgetTextVariableName(screenDefinition, widget);
        String showVariable = GeneratorUtil.getJSWidgetVisibleVariableName(screenDefinition, widget);
        HTMLTag resultTag = null;
        if (widget.getLabelText() != null) {
            HTMLTag inputContainer = tagLogic.addTag(parentTag, "md-input-container", null, "class", "md-block");
            tagLogic.addTag(inputContainer, "label", createLabelVarPlaceholder(screenDefinition, widget));
            resultTag = tagLogic.addTag(inputContainer, "input", null, "ng-model", variableName, "style", styleString, "ng-show", showVariable);
        } else {
        	resultTag = tagLogic.addTag(parentTag, "input", null, "ng-model", variableName, "style", styleString, "ng-show", showVariable);
        }
        
        if ((widget.isReadOnly() != null) && (widget.isReadOnly().booleanValue())){
        	tagLogic.setAttribute(resultTag, "ng-readonly", "" + widget.isReadOnly().booleanValue());
        } else {
        	if (Boolean.TRUE.equals(widget.getSelectOnFocus())){
        		tagLogic.setAttribute(resultTag, "md-select-on-focus", "");
        	}
        }

    }

}
