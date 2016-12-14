package com.bright_side_it.fliesenui.screendefinition.logic;

import com.bright_side_it.fliesenui.screendefinition.model.ImageSource;

public class ImageSourceLogic {
    public int countLocationParameters(ImageSource imageSource) {
        int result = 0;
        if (imageSource.getImageAssetID() != null) {
            result++;
        }
        if (imageSource.getImageStreamID() != null) {
            result++;
        }
        if (imageSource.getImageURL() != null) {
            result++;
        }
        if (imageSource.getImageAssetIDDTOField() != null) {
            result++;
        }
        if (imageSource.getImageStreamIDDTOField() != null) {
            result++;
        }
        if (imageSource.getImageURLDTOField() != null) {
            result++;
        }

        return result;
    }
    
    public String getDTOField(ImageSource imageSource){
    	if (imageSource.getImageAssetIDDTOField() != null){
    		return imageSource.getImageAssetIDDTOField();
    	} else if (imageSource.getImageStreamIDDTOField() != null){
    		return imageSource.getImageStreamIDDTOField();
    	} else if (imageSource.getImageURLDTOField() != null){
    		return imageSource.getImageURLDTOField();
    	}
    	return null;
    }
}
