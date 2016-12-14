package com.bright_side_it.fliesenui.screendefinition.model;

public class ImageSource {

    private String imageAssetID;
    private String imageStreamID;
    private String imageURL;
    private String imageAssetIDDTOField;
    private String imageStreamIDDTOField;
    private String imageURLDTOField;
    private Integer width;
    private Integer height;

    public String getImageAssetID() {
        return imageAssetID;
    }

    public void setImageAssetID(String imageAssetID) {
        this.imageAssetID = imageAssetID;
    }

    public String getImageStreamID() {
        return imageStreamID;
    }

    public void setImageStreamID(String imageStreamID) {
        this.imageStreamID = imageStreamID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageAssetIDDTOField() {
        return imageAssetIDDTOField;
    }

    public void setImageAssetIDDTOField(String imageAssetIDDAO) {
        this.imageAssetIDDTOField = imageAssetIDDAO;
    }

    public String getImageStreamIDDTOField() {
        return imageStreamIDDTOField;
    }

    public void setImageStreamIDDTOField(String imageStreamIDDAO) {
        this.imageStreamIDDTOField = imageStreamIDDAO;
    }

    public String getImageURLDTOField() {
        return imageURLDTOField;
    }

    public void setImageURLDAO(String imageURLDAO) {
        this.imageURLDTOField = imageURLDAO;
    }

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

}
