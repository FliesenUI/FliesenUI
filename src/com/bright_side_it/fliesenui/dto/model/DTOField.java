package com.bright_side_it.fliesenui.dto.model;

import com.bright_side_it.fliesenui.base.util.BaseConstants.BasicType;

public class DTOField {

    private String id;
    private boolean list;
    private BasicType basicType;
    private String dtoType;
    private String previewValue;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public BasicType getBasicType() {
        return basicType;
    }

    public void setBasicType(BasicType basicType) {
        this.basicType = basicType;
    }

    public String getDTOType() {
        return dtoType;
    }

    public void setDTOType(String dtoType) {
        this.dtoType = dtoType;
    }

    public String getPreviewValue() {
        return previewValue;
    }

    public void setPreviewValue(String previewValue) {
        this.previewValue = previewValue;
    }



}
