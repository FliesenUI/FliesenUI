package com.bright_side_it.fliesenui.generator.model;

public class ReplyToCallTranslationParameter {
    public enum DataType {
        STRING, INT, BOOLEAN_NOT_NULL, LIST_OF_STRING, CHARACTER, KEY_MODIFIER, LONG_NOT_NULL, LONG_OR_NULL, BOOLEAN_OR_NULL, INTEGER
    }

    private String key;
    private DataType dataType;
    private String DTOClassName;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getDTOClassName() {
        return DTOClassName;
    }

    public void setDTOClassName(String dTOClassName) {
        DTOClassName = dTOClassName;
    }



}
