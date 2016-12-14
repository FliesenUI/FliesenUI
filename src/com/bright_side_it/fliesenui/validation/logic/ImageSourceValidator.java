package com.bright_side_it.fliesenui.validation.logic;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.imageasset.model.ImageAssetDefinition;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.dao.ImageSourceAttributesDAO;
import com.bright_side_it.fliesenui.screendefinition.logic.ImageSourceLogic;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSource;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSourceContainer;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;
import com.bright_side_it.fliesenui.screendefinition.model.ResourceDefinitionProblem.ProblemType;
import com.bright_side_it.fliesenui.validation.util.ValidationUtil;

public class ImageSourceValidator {
    public void validate(Project project, ScreenDefinition screenDefinition, NodePath nodePath, ImageSourceContainer imageSourceContainer) {
        ImageSource imageSource = imageSourceContainer.getImageSource();
        if (imageSource == null) {
            return;
        }

        if (new ImageSourceLogic().countLocationParameters(imageSource) > 1) {
            ValidationUtil.addError(project, screenDefinition, nodePath, null, ProblemType.IMAGE_SOUCE_MULTIPLE_LOCATIONS,
                    "an image source may not contain multiple location attributes");
        }
        if (new ImageSourceLogic().countLocationParameters(imageSource) == 0) {
            ValidationUtil.addError(project, screenDefinition, nodePath, null, ProblemType.IMAGE_SOUCE_MISSING_LOCATION,
                    "an image source needs an image location attribute like '" + ImageSourceAttributesDAO.IMAGE_URL_ATTRIBUTE_NAME + "'");
        }

//        if (imageSource.getFormatType() == null) {
//            ValidationUtil.addError(project, screenDefinition, nodePath, ImageSourceAttributesDAO.IMAGE_FORMAT_ATTRIBUTE_NAME, ProblemType.IMAGE_SOUCE_MISSING_FORMAT,
//                    "missing value for image format");
//        }

        if (imageSource.getImageAssetID() != null) {
            ImageAssetDefinition imageAssetDefinition = BaseUtil.toEmptyMapIfNull(project.getImageAssetDefinitionsMap()).get(imageSource.getImageAssetID());
            if (imageAssetDefinition != null) {
//                if (imageAssetDefinition.getFormatType() != imageSource.getFormatType()) {
//                    ValidationUtil.addError(project, screenDefinition, nodePath, ImageSourceAttributesDAO.IMAGE_FORMAT_ATTRIBUTE_NAME,
//                            ProblemType.IMAGE_SOURCE_IMAGE_RESOURCE_UNKNOWN, "The image asset has format '" + imageAssetDefinition.getFormatType() + "'");
//                }
            } else {
                ValidationUtil.addError(project, screenDefinition, nodePath, ImageSourceAttributesDAO.IMAGE_ASSET_ID_ATTRIBUTE_NAME,
                        ProblemType.IMAGE_SOURCE_IMAGE_RESOURCE_UNKNOWN, "There is no image asset with id '" + imageSource.getImageAssetID() + "'");
            }
        }
        
        if (imageSource.getWidth() != null){
        	if (imageSource.getWidth() <= 0){
                ValidationUtil.addError(project, screenDefinition, nodePath, ImageSourceAttributesDAO.IMAGE_WIDTH_ATTRIBUTE_NAME,
                        ProblemType.IMAGE_SOURCE_WRONG_WIDTH, "The image width must be > 0 '");
        	}
        }

        if (imageSource.getHeight() != null){
        	if (imageSource.getHeight() <= 0){
        		ValidationUtil.addError(project, screenDefinition, nodePath, ImageSourceAttributesDAO.IMAGE_HEIGHT_ATTRIBUTE_NAME,
        				ProblemType.IMAGE_SOURCE_WRONG_HEIGHT, "The image height must be > 0 '");
        	}
        }
    }
}
