package com.bright_side_it.fliesenui.screendefinition.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.XMLUtil;
import com.bright_side_it.fliesenui.project.model.AssistValue;
import com.bright_side_it.fliesenui.screendefinition.logic.ImageSourceLogic;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSource;
import com.bright_side_it.fliesenui.screendefinition.model.ImageSourceContainer;
import com.bright_side_it.fliesenui.screendefinition.model.NodePath;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinitionDAOResult;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefionitionReadException;

public class ImageSourceAttributesDAO {

    public static final String IMAGE_ASSET_ID_ATTRIBUTE_NAME = "imageAssetID";
    public static final String IMAGE_ASSET_ID_DTO_FIELD_ATTRIBUTE_NAME = "imageAssetIDDTOField";
    public static final String IMAGE_STREAM_ID_ATTRIBUTE_NAME = "imageStreamID";
    public static final String IMAGE_STREAM_ID_DTO_FIELD_ATTRIBUTE_NAME = "imageStreamIDDTOField";
    public static final String IMAGE_URL_ATTRIBUTE_NAME = "imageURL";
    public static final String IMAGE_URL_DTO_FIELD_ATTRIBUTE_NAME = "imageURLDTOField";

    public static final String IMAGE_WIDTH_ATTRIBUTE_NAME = "imageWidth";
    public static final String IMAGE_HEIGHT_ATTRIBUTE_NAME = "imageHeight";

    public void readImageSourceFromAttributes(Node node, NodePath nodePath, ScreenDefinitionDAOResult result, ImageSourceContainer imageSourceContainer)
            throws ScreenDefionitionReadException, Exception {

        ImageSource imageSource = new ImageSource();

        imageSource.setImageAssetID(XMLUtil.getStringAttributeOptional(node, IMAGE_ASSET_ID_ATTRIBUTE_NAME, null));
        imageSource.setImageAssetIDDTOField(XMLUtil.getStringAttributeOptional(node, IMAGE_ASSET_ID_DTO_FIELD_ATTRIBUTE_NAME, null));
        imageSource.setImageStreamID(XMLUtil.getStringAttributeOptional(node, IMAGE_STREAM_ID_ATTRIBUTE_NAME, null));
        imageSource.setImageStreamIDDTOField(XMLUtil.getStringAttributeOptional(node, IMAGE_STREAM_ID_DTO_FIELD_ATTRIBUTE_NAME, null));
        imageSource.setImageURL(XMLUtil.getStringAttributeOptional(node, IMAGE_URL_ATTRIBUTE_NAME, null));
        imageSource.setImageURLDAO(XMLUtil.getStringAttributeOptional(node, IMAGE_URL_DTO_FIELD_ATTRIBUTE_NAME, null));
        imageSource.setWidth(XMLUtil.getIntegerAttributeOptional(node, IMAGE_WIDTH_ATTRIBUTE_NAME, null));
        imageSource.setHeight(XMLUtil.getIntegerAttributeOptional(node, IMAGE_HEIGHT_ATTRIBUTE_NAME, null));

//        String typeString = XMLUtil.getStringAttributeOptional(node, IMAGE_FORMAT_ATTRIBUTE_NAME, null);
//        if (typeString != null) {
//            if (typeString.equals(IMAGE_FORMAT_PIXEL_VALUE)) {
//                imageSource.setFormatType(ImageFormatType.PIXEL);
//            } else if (typeString.equals(IMAGE_FORMAT_VECTOR_VALUE)) {
//                imageSource.setFormatType(ImageFormatType.VECTOR);
//            } else {
//                ScreenDefinitionDAO.addError(result, nodePath, IMAGE_FORMAT_ATTRIBUTE_NAME, ProblemType.IMAGE_SOURCE_UNKNOWN_FORMAT, "Unknown format: " + typeString);
//            }
//        }

        if ((new ImageSourceLogic().countLocationParameters(imageSource) != 0) || (imageSource.getWidth() != null) || (imageSource.getHeight() != null)) {
            imageSourceContainer.setImageSource(imageSource);
        }

    }

    public List<AssistValue> getTagAttributes() {
        List<AssistValue> result = new ArrayList<>();
        result.add(BaseUtil.createAssistValue(null, IMAGE_ASSET_ID_ATTRIBUTE_NAME, "ID of the image asset"));
        result.add(BaseUtil.createAssistValue(null, IMAGE_STREAM_ID_ATTRIBUTE_NAME, "ID of the image stream"));
        result.add(BaseUtil.createAssistValue(null, IMAGE_URL_ATTRIBUTE_NAME, "image URL"));
        result.add(BaseUtil.createAssistValue(null, IMAGE_ASSET_ID_DTO_FIELD_ATTRIBUTE_NAME, "Field in DTO that contains the image asset id"));
        result.add(BaseUtil.createAssistValue(null, IMAGE_STREAM_ID_DTO_FIELD_ATTRIBUTE_NAME, "Field in DTO that contains the image stream id"));
        result.add(BaseUtil.createAssistValue(null, IMAGE_URL_DTO_FIELD_ATTRIBUTE_NAME, "Field in DTO that contains the image URL"));
        result.add(BaseUtil.createAssistValue(null, IMAGE_WIDTH_ATTRIBUTE_NAME, "image widght"));
        result.add(BaseUtil.createAssistValue(null, IMAGE_HEIGHT_ATTRIBUTE_NAME, "image height"));
        return result;
    }

}
