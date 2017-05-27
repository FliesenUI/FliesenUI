package com.bright_side_it.fliesenui.res.dao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

public class ResourceDAO {

    public enum Resource {
		SCREEN_HTML, FLUI_CSS, ANGULAR_ANIMATE_JS, ANGULAR_AREA_JS, ANGULAR_MATERIAL_CSS, ANGULAR_MATERIAL_JS, ANGULAR_MESSAGES_JS, ANGULAR_JS, FLUI_APPLICATION_JAVA, FLUI_SCREEN_JAVA, FLUI_SCRIPT_EXCEPTION_JAVA, FLUI_WEB_VIEW_JAVA, FLUI_MESSAGE_JAVA, FLUI_REPLY_DTO_JAVA, FLUI_REPLY_ABSTRACT_REPLY_JAVA, CODEMIRROR_XML_JS, CODEMIRROR_JS, CODEMIRROR_CSS, TEXT_HIGHLIGHTING_JAVA, CURSOR_POS_JAVA, FLUI_UTIL_JAVA, CODEMIRROR_ACTIVE_LINE_JS, CODEMIRROR_MATCHBRACKETS_JS, CODEMIRROR_CLOSETAG_JS, CODEMIRROR_MATCHTAGS_JS, CODEMIRROR_XML_FOLD_JS, CODEMIRROR_SHOW_HINT_JS, CODEMIRROR_SHOW_HINT_CSS, CONTEXT_ASSIST_JAVA, CONTEXT_ASSIST_CHOICE_JAVA, CONFIRM_DIALOG_PARAMETERS_JAVA, INPUT_DIALOG_PARAMETERS_JAVA, NEW_SCREEN_TEMPLATE, NEW_DTO_TEMPLATE, NEW_PROJECT_PROJECT_TEMPLATE, NEW_PROJECT_SCREEN_TEMPLATE, NEW_PROJECT_DTO1_TEMPLATE, NEW_PROJECT_DTO2_TEMPLATE, FLUI_REQUEST_JAVA, NEW_PLUGIN_TEMPLATE, FLUI_IMAGE_STREAM_JAVA, FLUI_SCREEN_MANAGER_LISTENER_JAVA, SIMPLE_MANAGER_LISTENER_JAVA, FLUI_WEB_CALL_HANDLER_JAVA, FLUI_WEB_CALL_JAVA, FLUI_FILE_STREAM_JAVA, FLUI_CLIENT_PROPERTIES_DTO_JAVA, FLUI_ANDROID_WEB_VIEW_JAVA, HTTP_MULTIPART_REQUEST_READER_JAVA, SHOWDOWN_JS, SHOWDOWN_LICENSE_TEXT, IMAGE_CHECKBOX_CHECKED, IMAGE_CHECKBOX_UNCHECKED, ID_LABEL_JAVA, ID_LABEL_IMAGE_ASSET_JAVA, ID_LABEL_IMAGE_ASSET_LIST_JAVA, ID_LABEL_LIST_JAVA, LIST_CHOOSER_PARAMETERS_JAVA, LIST_CHOOSER_ITEM_JAVA, KEY_MODIFIER_JAVA, NEW_STRING_RESOURCE, FLUI_ACTION_RECORDING_JAVA, FLUI_SCREEN_REQUEST_JAVA, FLUI_REPLY_ACTION_JAVA, FLUI_TEST_CLASS_WRITER_JAVA, FLUI_TEST_REPLY_WRITER_JAVA, JAR_INPUT_STREAM_URL_CONNECTION_JAVA, FLUI_KEY_EVENT_JAVA, FLUI_UTIL_JS
    };

    private static final String RESOURCE_BASE_DIR = "/com/bright_side_it/fliesenui/res/data/";
    private static final String TEXT_CHARSET = "UTF-8";
    private static final Map<Resource, String> RESOURCE_MAP = createTemplateMap();

    private static final String INTERNAL_JAVA_FILE_ENDING = ".java.txt";
    private static final String EXTERNAL_JAVA_FILE_ENDING = ".java";

    private static final String RESOURCE_NAME_SCREEN_HTML = "Screen.html";
    private static final String RESOURCE_NAME_FLUI_CSS = "flui.css";
    private static final String RESOURCE_NAME_ANGULAR_ANIMATE_JS = "angular-animate.min.js";
    private static final String RESOURCE_NAME_ANGULAR_AREA_JS = "angular-aria.min.js";
    private static final String RESOURCE_NAME_ANGULAR_MATERIAL_CSS = "angular-material.min.css";
    private static final String RESOURCE_NAME_ANGULAR_MATERIAL_JS = "angular-material.min.js";
    private static final String RESOURCE_NAME_ANGULAR_MESSAGES_JS = "angular-messages.min.js";
    private static final String RESOURCE_NAME_ANGULAR_JS = "angular.min.js";
    private static final String RESOURCE_NAME_CODEMIRROR_CSS = "codemirror.css";
    private static final String RESOURCE_NAME_CODEMIRROR_JS = "codemirror.js";
    private static final String RESOURCE_NAME_FLUI_UTIL_JS = "flui-util.js";
    private static final String RESOURCE_NAME_CODEMIRROR_XML_JS = "xml.js";
    private static final String RESOURCE_NAME_CODEMIRROR_ACTIVE_LINE_JS = "active-line.js";
    private static final String RESOURCE_NAME_CODEMIRROR_MATCHBRACKETS_JS = "matchbrackets.js";
    private static final String RESOURCE_NAME_CODEMIRROR_CLOSETAG_JS = "closetag.js";
    private static final String RESOURCE_NAME_CODEMIRROR_MATCHTAGS_JS = "matchtags.js";
    private static final String RESOURCE_NAME_CODEMIRROR_XML_FOLD_JS = "xml-fold.js";
    private static final String RESOURCE_NAME_CODEMIRROR_SHOW_HINT_JS = "show-hint.js";
    private static final String RESOURCE_NAME_CODEMIRROR_SHOW_HINT_CSS = "show-hint.css";
    private static final String RESOURCE_NAME_SHOWDOWN_JS = "showdown.min.js";
    private static final String RESOURCE_NAME_SHOWDOWN_LICENSE_TXT = "showdown_license.txt";
    private static final String RESOURCE_NAME_FLUI_APPLICATION_JAVA = "FLUIApplication.java.txt";
    private static final String RESOURCE_NAME_FLUI_SCREEN_JAVA = "FLUIScreen.java.txt";
    private static final String RESOURCE_NAME_FLUI_SCRIPT_EXCEPTION_JAVA = "FLUIScriptException.java.txt";
    private static final String RESOURCE_NAME_FLUI_WEB_VIEW_JAVA = "FLUIWebView.java.txt";
    private static final String RESOURCE_NAME_FLUI_ANDROID_WEB_VIEW_JAVA = "FLUIAndroidWebView.java.txt";
    private static final String RESOURCE_NAME_FLUI_MESSAGE_JAVA = "FLUIMessage.java.txt";
    private static final String RESOURCE_NAME_FLUI_REPLY_DTO_JAVA = "FLUIReplyDTO.java.txt";
    private static final String RESOURCE_NAME_FLUI_CLIENT_PROPERTIES_DTO_JAVA = "FLUIClientPropertiesDTO.java.txt";
    private static final String RESOURCE_NAME_FLUI_REPLY_ABSTRACT_REPLY_JAVA = "FLUIAbstractReply.java.txt";
    private static final String RESOURCE_NAME_FLUI_REPLY_ACTION_JAVA = "FLUIReplyAction.java.txt";
    private static final String RESOURCE_NAME_FLUI_REQUEST_JAVA = "FLUIRequest.java.txt";
    private static final String RESOURCE_NAME_FLUI_ACTION_RECORDING_JAVA = "FLUIActionRecording.java.txt";
    private static final String RESOURCE_NAME_FLUI_IMAGE_STREAM_JAVA = "FLUIImageStream.java.txt";
    private static final String RESOURCE_NAME_FLUI_FILE_STREAM_JAVA = "FLUIFileStream.java.txt";
    private static final String RESOURCE_NAME_FLUI_WEB_CALL_HANDLER_JAVA = "FLUIWebCallHandler.java.txt";
    private static final String RESOURCE_NAME_FLUI_WEB_CALL_JAVA = "FLUIWebCall.java.txt";
    private static final String RESOURCE_NAME_FLUI_SCREEN_REQUEST_JAVA = "FLUIScreenRequest.java.txt";
    private static final String RESOURCE_NAME_TEXT_HIGHLIGHTING_JAVA = "TextHighlighting.java.txt";
    private static final String RESOURCE_NAME_FLUI_KEY_EVENT_JAVA = "FLUIKeyEvent.java.txt";
    private static final String RESOURCE_NAME_KEY_MODIFIER_JAVA = "KeyModifier.java.txt";
    private static final String RESOURCE_NAME_CONFIRM_DIALOG_PARAMETERS_JAVA = "ConfirmDialogParameters.java.txt";
    private static final String RESOURCE_NAME_INPUT_DIALOG_PARAMETERS_JAVA = "InputDialogParameters.java.txt";
    private static final String RESOURCE_NAME_CURSOR_POS_JAVA = "CursorPos.java.txt";
    private static final String RESOURCE_NAME_CONTEXT_ASSIST_JAVA = "ContextAssist.java.txt";
    private static final String RESOURCE_NAME_CONTEXT_ASSIST_CHOICE_JAVA = "ContextAssistChoice.java.txt";
    private static final String RESOURCE_NAME_SIMPLE_MANAGER_LISTENER_JAVA = "SimpleManagerListener.java.txt";
    private static final String RESOURCE_NAME_JAR_INPUT_STREAM_URL_CONNECTION_JAVA = "JarImageStreamURLConnection.java.txt";
    private static final String RESOURCE_NAME_FLUI_SCREEN_MANAGER_LISTENER_JAVA = "FLUIScreenManagerListener.java.txt";
    private static final String RESOURCE_NAME_FLUI_UTIL_JAVA = "FLUIUtil.java.txt";
    private static final String RESOURCE_NAME_FLUI_TEST_CLASS_WRITER_JAVA = "FLUITestClassWriter.java.txt";
    private static final String RESOURCE_NAME_FLUI_TEST_REPLY_WRITER_JAVA = "FLUITestReplyWriter.java.txt";
    private static final String RESOURCE_NAME_ID_LABEL_JAVA = "IDLabel.java.txt";
    private static final String RESOURCE_NAME_ID_LABEL_IMAGE_ASSET_JAVA = "IDLabelImageAsset.java.txt";
    private static final String RESOURCE_NAME_ID_LABEL_IMAGE_ASSET_LIST_JAVA = "IDLabelImageAssetList.java.txt";
    private static final String RESOURCE_NAME_ID_LABEL_LIST_JAVA = "IDLabelList.java.txt";
    private static final String RESOURCE_NAME_LIST_CHOOSER_PARAMETERS_JAVA = "ListChooserParameters.java.txt";
    private static final String RESOURCE_NAME_LIST_CHOOSER_ITEM_JAVA = "ListChooserItem.java.txt";
    private static final String RESOURCE_NAME_NEW_SCREEN_TEMPLATE_XML = "NewScreen.xml";
    private static final String RESOURCE_NAME_NEW_DTO_TEMPLATE_XML = "NewDTO.xml";
    private static final String RESOURCE_NAME_NEW_STRING_RESOURCE_TEMPLATE_XML = "strings.xml";
    private static final String RESOURCE_NAME_NEW_PLUGIN_TEMPLATE_XML = "NewPlugin.xml";
    private static final String RESOURCE_NAME_NEW_PROJECT_PROJECT_XML = "NewProjectProject.xml";
    private static final String RESOURCE_NAME_NEW_PROJECT_SCEEN_XML = "NewProjectScreen.xml";
    private static final String RESOURCE_NAME_NEW_PROJECT_DTO1_XML = "NewProjectDTO1.xml";
    private static final String RESOURCE_NAME_NEW_PROJECT_DTO2_XML = "NewProjectDTO2.xml";
    private static final String RESOURCE_NAME_HTTP_MULTIPART_REQUEST_READER_JAVA = "HTTPMultipartRequestReader.java.txt";
    private static final String RESOURCE_NAME_IMAGE_CHECKBOX_CHECKED = "_checkbox_checked.png";
    private static final String RESOURCE_NAME_IMAGE_CHECKBOX_UNCHECKED = "_checkbox_unchecked.png";


    private static Map<Resource, String> createTemplateMap() {
        Map<Resource, String> map = new TreeMap<>();
        map.put(Resource.SCREEN_HTML, RESOURCE_NAME_SCREEN_HTML);
        map.put(Resource.FLUI_CSS, RESOURCE_NAME_FLUI_CSS);
        map.put(Resource.ANGULAR_ANIMATE_JS, RESOURCE_NAME_ANGULAR_ANIMATE_JS);
        map.put(Resource.ANGULAR_AREA_JS, RESOURCE_NAME_ANGULAR_AREA_JS);
        map.put(Resource.ANGULAR_MATERIAL_CSS, RESOURCE_NAME_ANGULAR_MATERIAL_CSS);
        map.put(Resource.ANGULAR_MATERIAL_JS, RESOURCE_NAME_ANGULAR_MATERIAL_JS);
        map.put(Resource.ANGULAR_MESSAGES_JS, RESOURCE_NAME_ANGULAR_MESSAGES_JS);
        map.put(Resource.ANGULAR_JS, RESOURCE_NAME_ANGULAR_JS);
        map.put(Resource.CODEMIRROR_CSS, RESOURCE_NAME_CODEMIRROR_CSS);
        map.put(Resource.CODEMIRROR_JS, RESOURCE_NAME_CODEMIRROR_JS);
        map.put(Resource.FLUI_UTIL_JS, RESOURCE_NAME_FLUI_UTIL_JS);
        map.put(Resource.CODEMIRROR_XML_JS, RESOURCE_NAME_CODEMIRROR_XML_JS);
        map.put(Resource.CODEMIRROR_ACTIVE_LINE_JS, RESOURCE_NAME_CODEMIRROR_ACTIVE_LINE_JS);
        map.put(Resource.CODEMIRROR_MATCHBRACKETS_JS, RESOURCE_NAME_CODEMIRROR_MATCHBRACKETS_JS);
        map.put(Resource.CODEMIRROR_CLOSETAG_JS, RESOURCE_NAME_CODEMIRROR_CLOSETAG_JS);
        map.put(Resource.CODEMIRROR_MATCHTAGS_JS, RESOURCE_NAME_CODEMIRROR_MATCHTAGS_JS);
        map.put(Resource.CODEMIRROR_XML_FOLD_JS, RESOURCE_NAME_CODEMIRROR_XML_FOLD_JS);
        map.put(Resource.CODEMIRROR_SHOW_HINT_JS, RESOURCE_NAME_CODEMIRROR_SHOW_HINT_JS);
        map.put(Resource.CODEMIRROR_SHOW_HINT_CSS, RESOURCE_NAME_CODEMIRROR_SHOW_HINT_CSS);
        map.put(Resource.SHOWDOWN_JS, RESOURCE_NAME_SHOWDOWN_JS);
        map.put(Resource.SHOWDOWN_LICENSE_TEXT, RESOURCE_NAME_SHOWDOWN_LICENSE_TXT);
        map.put(Resource.FLUI_APPLICATION_JAVA, RESOURCE_NAME_FLUI_APPLICATION_JAVA);
        map.put(Resource.FLUI_SCREEN_JAVA, RESOURCE_NAME_FLUI_SCREEN_JAVA);
        map.put(Resource.FLUI_SCRIPT_EXCEPTION_JAVA, RESOURCE_NAME_FLUI_SCRIPT_EXCEPTION_JAVA);
        map.put(Resource.FLUI_WEB_VIEW_JAVA, RESOURCE_NAME_FLUI_WEB_VIEW_JAVA);
        map.put(Resource.FLUI_ANDROID_WEB_VIEW_JAVA, RESOURCE_NAME_FLUI_ANDROID_WEB_VIEW_JAVA);
        map.put(Resource.FLUI_MESSAGE_JAVA, RESOURCE_NAME_FLUI_MESSAGE_JAVA);
        map.put(Resource.FLUI_REPLY_DTO_JAVA, RESOURCE_NAME_FLUI_REPLY_DTO_JAVA);
        map.put(Resource.FLUI_CLIENT_PROPERTIES_DTO_JAVA, RESOURCE_NAME_FLUI_CLIENT_PROPERTIES_DTO_JAVA);
        map.put(Resource.FLUI_REPLY_ABSTRACT_REPLY_JAVA, RESOURCE_NAME_FLUI_REPLY_ABSTRACT_REPLY_JAVA);
        map.put(Resource.FLUI_REPLY_ACTION_JAVA, RESOURCE_NAME_FLUI_REPLY_ACTION_JAVA);
        map.put(Resource.FLUI_REQUEST_JAVA, RESOURCE_NAME_FLUI_REQUEST_JAVA);
        map.put(Resource.FLUI_ACTION_RECORDING_JAVA, RESOURCE_NAME_FLUI_ACTION_RECORDING_JAVA);
        map.put(Resource.FLUI_IMAGE_STREAM_JAVA, RESOURCE_NAME_FLUI_IMAGE_STREAM_JAVA);
        map.put(Resource.FLUI_FILE_STREAM_JAVA, RESOURCE_NAME_FLUI_FILE_STREAM_JAVA);
        map.put(Resource.FLUI_WEB_CALL_HANDLER_JAVA, RESOURCE_NAME_FLUI_WEB_CALL_HANDLER_JAVA);
        map.put(Resource.FLUI_WEB_CALL_JAVA, RESOURCE_NAME_FLUI_WEB_CALL_JAVA);
        map.put(Resource.FLUI_SCREEN_REQUEST_JAVA, RESOURCE_NAME_FLUI_SCREEN_REQUEST_JAVA);
        map.put(Resource.TEXT_HIGHLIGHTING_JAVA, RESOURCE_NAME_TEXT_HIGHLIGHTING_JAVA);
        map.put(Resource.KEY_MODIFIER_JAVA, RESOURCE_NAME_KEY_MODIFIER_JAVA);
        map.put(Resource.FLUI_KEY_EVENT_JAVA, RESOURCE_NAME_FLUI_KEY_EVENT_JAVA);
        map.put(Resource.CONFIRM_DIALOG_PARAMETERS_JAVA, RESOURCE_NAME_CONFIRM_DIALOG_PARAMETERS_JAVA);
        map.put(Resource.INPUT_DIALOG_PARAMETERS_JAVA, RESOURCE_NAME_INPUT_DIALOG_PARAMETERS_JAVA);
        map.put(Resource.CURSOR_POS_JAVA, RESOURCE_NAME_CURSOR_POS_JAVA);
        map.put(Resource.FLUI_SCREEN_MANAGER_LISTENER_JAVA, RESOURCE_NAME_FLUI_SCREEN_MANAGER_LISTENER_JAVA);
        map.put(Resource.FLUI_UTIL_JAVA, RESOURCE_NAME_FLUI_UTIL_JAVA);
        map.put(Resource.FLUI_TEST_CLASS_WRITER_JAVA, RESOURCE_NAME_FLUI_TEST_CLASS_WRITER_JAVA);
        map.put(Resource.FLUI_TEST_REPLY_WRITER_JAVA, RESOURCE_NAME_FLUI_TEST_REPLY_WRITER_JAVA);
        map.put(Resource.ID_LABEL_JAVA, RESOURCE_NAME_ID_LABEL_JAVA);
        map.put(Resource.ID_LABEL_IMAGE_ASSET_JAVA, RESOURCE_NAME_ID_LABEL_IMAGE_ASSET_JAVA);
        map.put(Resource.ID_LABEL_IMAGE_ASSET_LIST_JAVA, RESOURCE_NAME_ID_LABEL_IMAGE_ASSET_LIST_JAVA);
        map.put(Resource.ID_LABEL_LIST_JAVA, RESOURCE_NAME_ID_LABEL_LIST_JAVA);
        map.put(Resource.LIST_CHOOSER_PARAMETERS_JAVA, RESOURCE_NAME_LIST_CHOOSER_PARAMETERS_JAVA);
        map.put(Resource.LIST_CHOOSER_ITEM_JAVA, RESOURCE_NAME_LIST_CHOOSER_ITEM_JAVA);
        map.put(Resource.HTTP_MULTIPART_REQUEST_READER_JAVA, RESOURCE_NAME_HTTP_MULTIPART_REQUEST_READER_JAVA);
        map.put(Resource.CONTEXT_ASSIST_JAVA, RESOURCE_NAME_CONTEXT_ASSIST_JAVA);
        map.put(Resource.CONTEXT_ASSIST_CHOICE_JAVA, RESOURCE_NAME_CONTEXT_ASSIST_CHOICE_JAVA);
        map.put(Resource.SIMPLE_MANAGER_LISTENER_JAVA, RESOURCE_NAME_SIMPLE_MANAGER_LISTENER_JAVA);
        map.put(Resource.JAR_INPUT_STREAM_URL_CONNECTION_JAVA, RESOURCE_NAME_JAR_INPUT_STREAM_URL_CONNECTION_JAVA);
        map.put(Resource.NEW_SCREEN_TEMPLATE, RESOURCE_NAME_NEW_SCREEN_TEMPLATE_XML);
        map.put(Resource.NEW_DTO_TEMPLATE, RESOURCE_NAME_NEW_DTO_TEMPLATE_XML);
        map.put(Resource.NEW_STRING_RESOURCE, RESOURCE_NAME_NEW_STRING_RESOURCE_TEMPLATE_XML);
        map.put(Resource.NEW_PLUGIN_TEMPLATE, RESOURCE_NAME_NEW_PLUGIN_TEMPLATE_XML);
        map.put(Resource.NEW_PROJECT_PROJECT_TEMPLATE, RESOURCE_NAME_NEW_PROJECT_PROJECT_XML);
        map.put(Resource.NEW_PROJECT_SCREEN_TEMPLATE, RESOURCE_NAME_NEW_PROJECT_SCEEN_XML);
        map.put(Resource.NEW_PROJECT_DTO1_TEMPLATE, RESOURCE_NAME_NEW_PROJECT_DTO1_XML);
        map.put(Resource.NEW_PROJECT_DTO2_TEMPLATE, RESOURCE_NAME_NEW_PROJECT_DTO2_XML);
        map.put(Resource.IMAGE_CHECKBOX_CHECKED, RESOURCE_NAME_IMAGE_CHECKBOX_CHECKED);
        map.put(Resource.IMAGE_CHECKBOX_UNCHECKED, RESOURCE_NAME_IMAGE_CHECKBOX_UNCHECKED);
        return map;
    }

    public String readTemplateAsString(Resource resource) throws Exception {
        String filename = RESOURCE_MAP.get(resource);
        if (filename == null) {
            throw new Exception("Unknonwn template: " + resource);
        }

        InputStream inputStream = null;
        String location = RESOURCE_BASE_DIR + filename;
        try {
            location = RESOURCE_BASE_DIR + filename;
            inputStream = this.getClass().getResourceAsStream(location);
        } catch (Exception e) {
            throw new Exception("Could not internal URI for file with name '" + filename + "'. location = '" + location + "'", e);
        }
        log("readTemplateAsString: location = '" + location + "'");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            writeAllBytesToStream(inputStream, outputStream);
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        String text = new String(outputStream.toByteArray(), Charset.forName(TEXT_CHARSET));
        return text;
    }

    private void log(String message) {
    	System.out.println("ResourceDAO> " + message);
	}

	private void writeAllBytesToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        int readBytes;
        byte[] buffer = new byte[4096];
        while ((readBytes = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, readBytes);
        }
    }

    public void copyResourceToFile(Resource resource, File destFile) throws Exception {
        String filename = RESOURCE_MAP.get(resource);
        if (filename == null) {
            throw new Exception("Unknonwn template: " + resource);
        }

        String location = RESOURCE_BASE_DIR + filename;
        try (InputStream inputStream = this.getClass().getResourceAsStream(location); FileOutputStream outputStream = new FileOutputStream(destFile)) {
            writeAllBytesToStream(inputStream, outputStream);
        } catch (Exception e) {
            throw new Exception("Could not copy from location '" + filename + "'. to file = '" + destFile.getAbsolutePath() + "'", e);
        }
    }

    public void copyResourceToDir(Resource resource, File destDir) throws Exception {
        try {
            String sourceFilename = RESOURCE_MAP.get(resource);
            if (sourceFilename == null) {
                throw new Exception("Unknonwn template: " + resource);
            }

            String destFilename = sourceFilename;
            if (destFilename.endsWith(INTERNAL_JAVA_FILE_ENDING)) {
                destFilename = destFilename.substring(0, destFilename.length() - INTERNAL_JAVA_FILE_ENDING.length()) + EXTERNAL_JAVA_FILE_ENDING;
            }
            File destFile = new File(destDir, destFilename);
            String location = RESOURCE_BASE_DIR + sourceFilename;
            try (InputStream inputStream = this.getClass().getResourceAsStream(location); FileOutputStream outputStream = new FileOutputStream(destFile)) {
                writeAllBytesToStream(inputStream, outputStream);
            } catch (Exception e) {
                throw new Exception("Could not copy from location '" + sourceFilename + "'. to file = '" + destFile.getAbsolutePath() + "'", e);
            }
        } catch (Exception e) {
            throw new Exception("Could not copy resource " + resource + " to directory '" + destDir.getAbsolutePath() + "'", e);
        }
    }

}
