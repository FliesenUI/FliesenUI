package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.generator.logic.TableWidgetStyleHTMLGeneratorLogic.Style;
import com.bright_side_it.fliesenui.generator.model.HTMLTag;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSource;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.TableWidget;

public class TableWidgetHTMLGeneratorLogic {
    public void generateHTML(HTMLTag parentTag, Project project, ScreenDefinition screenDefinition, TableWidget widget, BrowserType browserType) throws Exception {
        switch (widget.getStyle()) {
        case NORMAL:
        	new TableWidgetStyleHTMLGeneratorLogic(Style.NORMAL).generateHTML(project, parentTag, screenDefinition, widget, browserType);
//            new TableWidgetStyleNormalHTMLGeneratorLogic().generateHTML(project, parentTag, screenDefinition, widget);
            break;
        case SMALL:
            new TableWidgetStyleHTMLGeneratorLogic(Style.SMALL).generateHTML(project, parentTag, screenDefinition, widget, browserType);
            break;
        default:
            throw new Exception("Unknown mode: " + widget.getStyle());
        }
    }

    public static HTMLTag createTableImageSourceTag(Project project, HTMLTag parentTag, ImageSource imageSource, BrowserType browserType) throws Exception {
        HTMLTagLogic tagLogic = new HTMLTagLogic();
        HTMLTag result = null;

        result = tagLogic.addTag(parentTag, "img", null, "ng-src", createTableImageSourceSRCPlaceholder(project, imageSource, browserType));
        addImageSizesIfProvided(result, imageSource);
        return result;
    }


    private static String createTableImageSourceSRCPlaceholder(Project project, ImageSource imageSource, BrowserType browserType) throws Exception {
        if (imageSource.getImageAssetIDDTOField() != null) {
            return "img/{{imageAssetIDToName[i." + imageSource.getImageAssetIDDTOField() + "]}}";
        } else if (imageSource.getImageAssetID() != null) {
            ImageAssetDefinition imageAsset = project.getImageAssetDefinitionsMap().get(imageSource.getImageAssetID());
            return "img/" + imageAsset.getFilename();
        } else if (imageSource.getImageStreamIDDTOField() != null) {
            return GeneratorUtil.getImageStreamPrefix(browserType) + "{{i." + imageSource.getImageStreamIDDTOField() + "}}";
        } else if (imageSource.getImageStreamID() != null) {
            return GeneratorUtil.getImageStreamPrefix(browserType) + imageSource.getImageStreamID();
        } else if (imageSource.getImageURLDTOField() != null) {
            return "{{i." + imageSource.getImageURLDTOField() + "}}";
        } else if (imageSource.getImageURL() != null) {
            return imageSource.getImageURL();
        } else {
            throw new Exception("Unknown image source");
        }
    }

    private static void addImageSizesIfProvided(HTMLTag tag, ImageSource imageSource) {
        if (imageSource.getWidth() != null) {
            tag.getAttributes().put("width", "" + imageSource.getWidth() + "px");
        }
        if (imageSource.getHeight() != null) {
            tag.getAttributes().put("height", "" + imageSource.getHeight() + "px");
        }
    }
}
