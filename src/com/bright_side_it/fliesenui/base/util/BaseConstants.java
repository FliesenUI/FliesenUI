package com.bright_side_it.fliesenui.base.util;

public class BaseConstants {
    public enum BrowserType {
        JAVA_FX, WEB
    }
    
    public enum LanguageFlavor {
    	JAVA, ANDROID
    }

    public enum BasicType {
        BOOLEAN, LONG, STRING
    };


    public static final String SCREEN_DIR_NAME = "screen";
    public static final String HISTORY_DIR_NAME = "history";
    public static final String DTO_DIR_NAME = "dto";
    public static final String PLUGIN_DIR_NAME = "plugin";
    public static final String IMAGE_ASSET_DIR_NAME = "image";
    public static final String SCREEN_DEFINITION_FILE_ENDING = ".xml";
    public static final String DTO_DEFINITION_FILE_ENDING = ".xml";
    public static final String PLUGIN_DEFINITION_FILE_ENDING = ".xml";
    public static final String STRING_RESOURCE_HISTORY_FILE_ENDING = ".xml";
    public static final String PROJECT_FILE_NAME = "FliesenUIProject.xml";
    public static final String ID_ATTRIBUTE_NAME = "id";
	public static final String IMAGE_ASSET_FILE_TO_IGNORE = "Thumbs.db";
	public static final String DEFAULT_STRING_RESOURCE_DIR = "string";
	public static final String STRING_RESOURCE_HISTORY_DIR_NAME = "string";
	public static final String STRING_RESOURCE_FILE_NAME = "strings.xml";
	public static final String DEFAULT_LANGUAGE_ID = "DEFAULT";

	public static final String STRING_RESOURCE_PREFIX = "$";
}
