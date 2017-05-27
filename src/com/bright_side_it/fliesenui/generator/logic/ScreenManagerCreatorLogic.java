package com.bright_side_it.fliesenui.generator.logic;

import java.io.File;
import java.util.Collection;

import com.bright_side_it.fliesenui.base.util.BaseUtil;
import com.bright_side_it.fliesenui.base.util.FileUtil;
import com.bright_side_it.fliesenui.base.util.BaseConstants.BrowserType;
import com.bright_side_it.fliesenui.base.util.BaseConstants.LanguageFlavor;
import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.project.model.Project;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class ScreenManagerCreatorLogic {

    public void generateScreenManager(Project project, File javaBaseDir, LanguageFlavor languageFlavor) throws Exception {
    	Collection<ScreenDefinition> screenDefinitions = project.getScreenDefinitionsMap().values();
        StringBuilder result = new StringBuilder();
        File packageDir = GeneratorUtil.getCorePackageDir(javaBaseDir);
        String className = GeneratorUtil.getScreenManagerClassName(languageFlavor);
        
        File destFile = new File(packageDir, className + ".java");

        result.append("package " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ";\n\n");
        result.append("import com.google.gson.Gson;\n");
        if (languageFlavor == LanguageFlavor.ANDROID){
        	result.append("import android.content.Context;\n");
        	result.append("import android.webkit.JavascriptInterface;\n");
        	result.append("import " + GeneratorConstants.GENERATED_CORE_PACKAGE_NAME + ".FLUIAndroidWebView;\n");
        }
        result.append("import java.util.Map;\n");
        result.append("import java.util.List;\n");
        result.append("import java.util.ArrayList;\n");
        result.append("import java.util.TreeMap;\n");
        result.append("import java.io.ByteArrayOutputStream;\n");
		result.append("import java.io.InputStream;\n");
		result.append("import java.nio.charset.Charset;\n");
		result.append("import java.io.OutputStream;\n");
        result.append("\n");
        result.append(createImportStatements(screenDefinitions));
        result.append("\n");
        result.append("public class " + className + " implements FLUIScreenManagerInterface{\n");
        result.append("\n");
        result.append("    public static final String FILE_UPLOAD_FORM_REQUEST_PARAMETER_NAME = \"" + GeneratorConstants.FILE_UPLOAD_FORM_REQUEST_DATA_PARAMETER_NAME + "\";\n");
        result.append("    public static final String FILE_UPLOAD_FORM_FILE_PARAMETER_NAME = \"" + GeneratorConstants.FILE_UPLOAD_FORM_UPLOADED_FILE_NAME + "\";\n");
        result.append("    private static final String RESOURCE_BASE_DIR = \"" + GeneratorConstants.GENERATED_WEB_PACKAGE_NAME.replace(".", "/") + "\";\n");
        result.append("    private static final String TEXT_CHARSET = \"UTF-8\";\n");
        if (languageFlavor == LanguageFlavor.JAVA){
        	result.append("    private FLUIWebView webView;\n");
        }
        if (languageFlavor == LanguageFlavor.ANDROID){
        	result.append("    private FLUIAndroidWebView webView;\n");
        	result.append("    private Context context;\n");
        }
        result.append("    private boolean singlePageApp = true;\n");
        result.append("    private FLUIScreenManagerListener listener;\n");
        result.append("    private boolean recording = false;\n");
        result.append("    private FLUIActionRecording actionRecording;\n");
        result.append("    private FLUIWebCallHandler fluiWebCallHandler = new FLUIWebCallHandler(this);\n");
        result.append("    private Map<String, FLUIScreen> screenIDToViewMap = new TreeMap<String, FLUIScreen>();\n");
        result.append("\n");
//        result.append(createViewDeclarations(screenDefinitions));
//        result.append("\n");
        result.append("    private " + className + "() {\n");
        result.append("    }\n");
        result.append("\n");

        if (languageFlavor == LanguageFlavor.JAVA){
	        result.append("    public static " + className + " createInstance(boolean singlePageApp, FLUIScreenManagerListener listener) {\n");
	        result.append("        " + className + " result = new " + className + "();\n");
	        result.append("        result.singlePageApp = singlePageApp;\n");
	        result.append("        result.listener = listener;\n");
	        result.append("        return result;\n");
	        result.append("    }\n");
	        result.append("\n");
	        result.append("    public static " + className + " createInstance(FLUIScreenManagerListener listener) {\n");
	        result.append("        " + className + " result = new " + className + "();\n");
	        result.append("        result.singlePageApp = true;\n");
	        result.append("        result.listener = listener;\n");
	        result.append("        return result;\n");
	        result.append("    }\n");
	        result.append("\n");
	        result.append("    public static " + className + " createSimpleInstance() {\n");
	        result.append("        " + className + " result = new " + className + "();\n");
	        result.append("        result.singlePageApp = true;\n");
	        result.append("        result.listener = new SimpleManagerListener();\n");
	        result.append("        return result;\n");
	        result.append("    }\n");
        } else if (languageFlavor == LanguageFlavor.ANDROID){
	        result.append("    public static " + className + " createInstance(Context context, boolean singlePageApp, FLUIScreenManagerListener listener) {\n");
	        result.append("        " + className + " result = new " + className + "();\n");
	        result.append("        result.context = context;\n");
	        result.append("        result.singlePageApp = singlePageApp;\n");
	        result.append("        result.listener = listener;\n");
	        result.append("        return result;\n");
	        result.append("    }\n");
	        result.append("\n");
	        result.append("    public static " + className + " createInstance(Context context, FLUIScreenManagerListener listener) {\n");
	        result.append("        " + className + " result = new " + className + "();\n");
	        result.append("        result.context = context;\n");
	        result.append("        result.singlePageApp = true;\n");
	        result.append("        result.listener = listener;\n");
	        result.append("        return result;\n");
	        result.append("    }\n");
	        result.append("\n");
	        result.append("    public static " + className + " createSimpleInstance(Context context) {\n");
	        result.append("        " + className + " result = new " + className + "();\n");
	        result.append("        result.context = context;\n");
	        result.append("        result.singlePageApp = true;\n");
	        result.append("        result.listener = new SimpleManagerListener();\n");
	        result.append("        return result;\n");
	        result.append("    }\n");
	        result.append("    public void setWebView(FLUIAndroidWebView webView) {\n");
	        result.append("        this.webView = webView;\n");
	        result.append("    }\n");
	        
	        
        } else {
        	throw new Exception("Unknown language flavor: " + languageFlavor);
        }
        result.append("\n");
        result.append("    public void setSinglePageApp(boolean singlePageApp) {\n");
        result.append("        this.singlePageApp = singlePageApp;\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public void startRecording() {\n");
        result.append("        actionRecording = new FLUIActionRecording();\n");
        result.append("        actionRecording.setRequests(new ArrayList<FLUIRequest>());\n");
        result.append("        actionRecording.setReplies(new ArrayList<FLUIReplyDTO>());\n");
        result.append("        recording = true;\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public void stopRecording() {\n");
        result.append("        recording = false;\n");
        result.append("    }\n");
        result.append("\n");
		result.append("    public FLUIActionRecording getActionRecording() {\n");
		result.append("        return actionRecording;\n");
		result.append("    }\n");
        result.append("    public boolean isSinglePageApp() {\n");
        result.append("        return singlePageApp;\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public FLUIImageStream getCustomImageStream(String imageStreamID) {\n");
        result.append("        return listener.getCustomImageStream(imageStreamID);\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public FLUIFileStream getFileStream(String fileStreamID) {\n");
        result.append("        return listener.getFileStream(fileStreamID);\n");
        result.append("    }\n");
        result.append("\n");
        if (languageFlavor == LanguageFlavor.JAVA){
        	result.append("    public void setWebView(FLUIWebView webView) {\n");
        	result.append("        this.webView = webView;\n");
        	result.append("    }\n");
        	result.append("\n");
        }
        result.append("    public FLUIScreenManagerListener getListener() {\n");
        result.append("        return listener;\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public void handleWebCall(FLUIWebCall webCall, String method, String requestPath) throws Exception {\n");
        result.append("        fluiWebCallHandler.handle(webCall, method, requestPath);\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public void handleWebUpload(FLUIWebCall webCall, String requestJSON, String uploadFilename, InputStream uploadFileInputStream) throws Exception {\n");
        result.append("        fluiWebCallHandler.handleUpload(webCall, requestJSON, uploadFilename, uploadFileInputStream);\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public FLUIReplyDTO onScreenRequest(FLUIScreenRequest request, String uploadFileName, InputStream uploadFileInputStream, boolean clearRecordedActions){\n");
		result.append("        String requestJSON = new Gson().toJson(request.getRequest());\n");
		result.append("        String replyJSON = onRequest(requestJSON, uploadFileName, uploadFileInputStream);\n");
		result.append("        FLUIReplyDTO result = new Gson().fromJson(replyJSON, FLUIReplyDTO.class);\n");
		result.append("        if (clearRecordedActions){\n");
		result.append("            result.setRecordedActions(new ArrayList<FLUIReplyAction>());\n");
		result.append("        }\n");
		result.append("        return result;\n");
		result.append("    }\n");
		result.append("\n");

        
        
//        result.append(createOpenScreenMethod(screenDefinitions));
    	result.append(createOpenStartScreenMethod(project, languageFlavor));
        result.append(createGetStartWebPageFilenameMethod(project));
        result.append(createGetStartWebPageAsStringMethod(project));
        result.append(createGetWebPageAsStringMethod(project, languageFlavor));
        result.append(createWriteResourceMethod(languageFlavor));
//        result.append(createCreateScreenMethods(screenDefinitions));
        result.append(createReadAllBytesFromStreamMethod());
        result.append(createPresenterSetters(screenDefinitions));
        result.append(createReplyMethod(screenDefinitions, languageFlavor));
        result.append(createOnRequestMethod(languageFlavor));
        result.append("}");

        destFile.getParentFile().mkdirs();

        result = GeneratorUtil.addJavaGeneratedCommend(result);
        FileUtil.writeStringToFile(destFile, result.toString());
        destFile.setWritable(false);
    }

    private StringBuilder createReadAllBytesFromStreamMethod() {
		StringBuilder result = new StringBuilder();
		result.append("    private void writeAllBytesToStream(InputStream inputStream, OutputStream outputStream) throws Exception {\n");
		result.append("        int readBytes;\n");
		result.append("        byte[] buffer = new byte[4096];\n");
		result.append("        while ((readBytes = inputStream.read(buffer)) > 0) {\n");
		result.append("            outputStream.write(buffer, 0, readBytes);\n");
		result.append("        }\n");
		result.append("    }\n");
		return result;
	}

//	private StringBuilder createCreateScreenMethods(Collection<ScreenDefinition> screenDefinitions) {
//        StringBuilder result = new StringBuilder();
//        for (ScreenDefinition i : screenDefinitions) {
//            String className = GeneratorUtil.getViewClassName(i);
//            String classListnerName = GeneratorUtil.getViewListenerClassName(i);
//            result.append("    public " + className + " create" + className + "(" + classListnerName + " listener) {\n");
//            result.append("        return new " + className + "(webView, listener);\n");
//            result.append("    }\n");
//            result.append("\n");
//        }
//        return result;
//    }

        private StringBuilder createPresenterSetters(Collection<ScreenDefinition> screenDefinitions) {
            StringBuilder result = new StringBuilder();
            for (ScreenDefinition i : screenDefinitions) {
                String className = GeneratorUtil.getViewClassName(i);
                String classListnerName = GeneratorUtil.getViewListenerClassName(i);
                result.append("    public void set" + BaseUtil.idToFirstCharUpperCase(i.getID()) + "Presenter(" + classListnerName + " presenter) {\n");
                result.append("        screenIDToViewMap.put(\"" + i.getID() + "\", new " + className + "(presenter));\n");
//                result.append("        " + getViewMemberName(i) + " = new " + className + "(this, presenter);\n");
                result.append("    }\n");
                result.append("\n");
            }
            return result;
        }

//    private StringBuilder createOpenScreenMethod(Collection<ScreenDefinition> screenDefinitions) {
//        StringBuilder result = new StringBuilder();
//        result.append("    public void openScreen(FLUIScreen screen, Object object) {\n");
//        result.append("        String urlSuffix = null;\n");
//        result.append("        if (object != null){\n");
//        result.append("            try {\n");
//        result.append("                urlSuffix = \"?" + GeneratorConstants.SCREEN_PARAMETER_DTO_GET_NAME + "=\" + FLUIUtil.toURLParameter(object);\n");
//        result.append("            } catch (Exception e){\n");
//        result.append("                listener.onError(e);\n");
//        result.append("            }\n");
//        result.append("        }\n");
//        for (ScreenDefinition i : screenDefinitions) {
//            String className = GeneratorUtil.getViewClassName(i);
//            result.append("        if (screen instanceof " + className + ") {\n");
//            String htmlFilename = GeneratorUtil.createHTMLFilename(i, BrowserType.JAVA_FX);
//            result.append("            webView.openScreen(screen.getID(), \"" + htmlFilename + "\", urlSuffix);\n");
//            result.append("        }\n");
//        }
//        result.append("    }\n");
//        result.append("\n");
//        return result;
//    }

    private StringBuilder createOpenStartScreenMethod(Project project, LanguageFlavor languageFlavor) {
    	StringBuilder result = new StringBuilder();
		result.append("    public void openStartScreen() {\n");
    	result.append("        String urlSuffix = null;\n");
    	result.append("//        if (object != null){\n");
    	result.append("//            try {\n");
    	result.append("//                urlSuffix = \"?" + GeneratorConstants.SCREEN_PARAMETER_DTO_GET_NAME + "=\" + FLUIUtil.toURLParameter(object);\n");
    	result.append("//            } catch (Exception e){\n");
    	result.append("//                listener.onError(e);\n");
    	result.append("//            }\n");
    	result.append("//        }\n");
    	ScreenDefinition screenDefinition = project.getScreenDefinitionsMap().get(project.getProjectDefinition().getStartScreenID());
		result.append("        if (singlePageApp){\n");
		result.append("            webView.openScreen(\"" + project.getProjectDefinition().getStartScreenID() + "\", \"" + GeneratorUtil.createHTMLFilename(BrowserType.JAVA_FX) + "\", urlSuffix, singlePageApp);\n");
		result.append("        } else {\n");
		String htmlFilename = GeneratorUtil.createHTMLFilename(screenDefinition, BrowserType.JAVA_FX);
		result.append("            webView.openScreen(\"" + project.getProjectDefinition().getStartScreenID() + "\", \"" + htmlFilename + "\", urlSuffix, singlePageApp);\n");
		result.append("        }\n");
    	result.append("    }\n");
    	result.append("\n");
    	return result;
    }

    private StringBuilder createGetStartWebPageFilenameMethod(Project project) {
    	StringBuilder result = new StringBuilder();
    	result.append("    private String getStartWebPageFilename() {\n");
    	ScreenDefinition screenDefinition = project.getScreenDefinitionsMap().get(project.getProjectDefinition().getStartScreenID());
    	result.append("        if (singlePageApp){\n");
    	result.append("            return \"" + GeneratorUtil.createHTMLFilename(BrowserType.WEB) + "\";\n");
    	result.append("        } else {\n");
    	result.append("            return \"" + GeneratorUtil.createHTMLFilename(screenDefinition, BrowserType.WEB) + "\";\n");
    	result.append("        }\n");
    	result.append("    }\n");
    	result.append("\n");
    	return result;
    }
    
    private StringBuilder createGetStartWebPageAsStringMethod(Project project) {
    	StringBuilder result = new StringBuilder();
    	result.append("    public String getStartWebPageAsString() throws Exception{\n");
    	result.append("        return getResourceAsString(getStartWebPageFilename());\n");
    	result.append("    }\n");
    	result.append("\n");
    	return result;
    }
    
    private StringBuilder createGetWebPageAsStringMethod(Project project, LanguageFlavor languageFlavor) {
    	StringBuilder result = new StringBuilder();
    	result.append("    public String getResourceAsString(String relativeLocation) throws Exception{\n");
		result.append("        InputStream inputStream = null;\n");
		result.append("        String useRelativeLocation = relativeLocation;\n");
		result.append("        if (!useRelativeLocation.startsWith(\"/\")){\n");
		result.append("            useRelativeLocation = \"/\" + useRelativeLocation;\n");
		result.append("        }\n");
		result.append("        String location = null;\n");
		result.append("        try {\n");
		if (languageFlavor == LanguageFlavor.JAVA){
			result.append("            location = \"/\" + RESOURCE_BASE_DIR + useRelativeLocation;\n");
			result.append("            inputStream = this.getClass().getResourceAsStream(location);\n");
		} else if (languageFlavor == LanguageFlavor.ANDROID){
			result.append("            location = RESOURCE_BASE_DIR + useRelativeLocation;\n");
			result.append("            inputStream = inputStream = context.getAssets().open(location);\n");
		}
		result.append("        } catch (Exception e) {\n");
		result.append("            throw new Exception(\"Could not get internal URI for file with relative location '\" + relativeLocation + \"'. location = '\" + location + \"'\", e);\n");
		result.append("        }\n");
		result.append("\n");
		result.append("        if (inputStream == null){\n");
		result.append("            throw new Exception(\"Could not get input stream URI for file with relative location '\" + relativeLocation + \"'. location = '\" + location + \"'\");\n");
		result.append("        }\n");
		result.append("\n");
		result.append("        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();\n");
		result.append("        try {\n");
		result.append("            writeAllBytesToStream(inputStream, outputStream);\n");
		result.append("        } catch (Exception e) {\n");
		result.append("            throw e;\n");
		result.append("        } finally {\n");
		result.append("            if (inputStream != null) {\n");
		result.append("                inputStream.close();\n");
		result.append("            }\n");
		result.append("        }\n");
		result.append("        \n");
		result.append("        String text = new String(outputStream.toByteArray(), Charset.forName(TEXT_CHARSET));\n");
		result.append("        return text;\n");
		result.append("    }\n");
    	result.append("\n");
    	return result;
    }

    private StringBuilder createWriteResourceMethod(LanguageFlavor languageFlavor) {
    	StringBuilder result = new StringBuilder();
    	result.append("    public void writeResource(String relativeLocation, OutputStream outputStream) throws Exception{\n");
    	result.append("        InputStream inputStream = null;\n");
    	result.append("        String useRelativeLocation = relativeLocation;\n");
    	result.append("        if (!useRelativeLocation.startsWith(\"/\")){\n");
    	result.append("            useRelativeLocation = \"/\" + useRelativeLocation;\n");
    	result.append("        }\n");
    	result.append("        String location = null;\n");
    	result.append("        try {\n");
		if (languageFlavor == LanguageFlavor.JAVA){
			result.append("            location = \"/\" + RESOURCE_BASE_DIR + useRelativeLocation;\n");
			result.append("            inputStream = this.getClass().getResourceAsStream(location);\n");
		} else if (languageFlavor == LanguageFlavor.ANDROID){
			result.append("            location = RESOURCE_BASE_DIR + useRelativeLocation;\n");
			result.append("            inputStream = inputStream = context.getAssets().open(location);\n");
		}
    	result.append("        } catch (Exception e) {\n");
    	result.append("            throw new Exception(\"Could not get internal URI for file with relative location '\" + relativeLocation + \"'. location = '\" + location + \"'\", e);\n");
    	result.append("        }\n");
    	result.append("\n");
    	result.append("        if (inputStream == null){\n");
    	result.append("            throw new Exception(\"Could not get input stream URI for file with relative location '\" + relativeLocation + \"'. location = '\" + location + \"'\");\n");
    	result.append("        }\n");
    	result.append("\n");
    	result.append("        try {\n");
    	result.append("            writeAllBytesToStream(inputStream, outputStream);\n");
    	result.append("        } catch (Exception e) {\n");
    	result.append("            throw e;\n");
    	result.append("        } finally {\n");
    	result.append("            if (inputStream != null) {\n");
    	result.append("                inputStream.close();\n");
    	result.append("            }\n");
    	result.append("        }\n");
    	result.append("        \n");
    	result.append("    }\n");
    	result.append("\n");
    	return result;
    }
    
    
    private StringBuilder createImportStatements(Collection<ScreenDefinition> screenDefinitions) {
        StringBuilder result = new StringBuilder();
        for (ScreenDefinition i : screenDefinitions) {
            result.append("import " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + GeneratorUtil.getViewClassName(i) + ";\n");
            result.append("import " + GeneratorConstants.GENERATED_SCREEN_PACKAGE_NAME + "." + GeneratorUtil.getViewListenerClassName(i) + ";\n");
        }
        return result;
    }
    
//    private StringBuilder createViewDeclarations(Collection<ScreenDefinition> screenDefinitions) {
//        StringBuilder result = new StringBuilder();
//        for (ScreenDefinition i : screenDefinitions) {
//            result.append("    private " + GeneratorUtil.getViewClassName(i) + " " + getViewMemberName(i) + ";\n");
//        }
//        return result;
//    }

//	private String getViewMemberName(ScreenDefinition screenDefinintion) {
//		return BaseUtil.idToFirstCharLowerCase(GeneratorUtil.getViewClassName(screenDefinintion));
//	}

    private StringBuilder createReplyMethod(Collection<ScreenDefinition> screenDefinitions, LanguageFlavor languageFlavor) {
    	StringBuilder result = new StringBuilder();
    	result.append("    private void reply(String screenID, String json) {\n");
    	result.append("        if (webView != null){\n");
        result.append("            webView.executeWithResultString(screenID + \"$processReply(\\\"\" + FLUIUtil.reescapeEscapeCharacters(json) + \"\\\");\");\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
    	return result;
	}

    private StringBuilder createOnRequestMethod(LanguageFlavor languageFlavor) {
    	StringBuilder result = new StringBuilder();
        if (languageFlavor == LanguageFlavor.ANDROID){
        	result.append("    @JavascriptInterface\n");
        }
    	result.append("    public String onRequest(String requestJSON, String uploadFileName, InputStream uploadFileInputStream) {\n");
    	result.append("        String replyJSON = null;\n");
    	result.append("        try{\n");
    	result.append("            FLUIRequest request = new Gson().fromJson(requestJSON, FLUIRequest.class);\n");
		result.append("            if (request == null){\n");
		result.append("                throw new Exception(\"request is null, requestJSON = >>\" + requestJSON + \"<<\");\n");
		result.append("            }\n");
    	result.append("            listener.onRequest(request);\n");
    	result.append("            FLUIScreen view = screenIDToViewMap.get(request.getScreenID());\n");
    	result.append("            FLUIAbstractReply reply;\n");
    	result.append("            if (view != null){\n");
    	result.append("                reply = view.onFLUIRequest(recording, request, uploadFileName, uploadFileInputStream);\n");
    	result.append("                if (reply != null){\n");
    	result.append("                    replyJSON = new Gson().toJson(reply.getReplyDTO());\n");
    	result.append("                    if (recording){;\n");
    	result.append("                        actionRecording.getRequests().add(request);\n");
    	result.append("                        actionRecording.getReplies().add(reply.getReplyDTO());\n");
    	result.append("                    };\n");
    	result.append("                }\n");
//    	result.append("                replyJSON = view.onFLUIRequest(request, uploadFileName, uploadFileInputStream);\n");
    	result.append("            }\n");
    	result.append("            if (replyJSON != null){\n");
    	result.append("                reply(request.getScreenID(), replyJSON);\n");
    	result.append("            } else {\n");
    	result.append("                listener.onWarning(new Exception(\"No presenter defined for screen '\" + request.getScreenID() + \"'\"));\n");
    	result.append("            }\n");
    	result.append("        } catch (Throwable error){\n");
    	result.append("            listener.onError(error);\n");
    	result.append("        }\n");
    	result.append("        return replyJSON;\n");
    	result.append("    }\n");
    	result.append("\n");
    	return result;
    }
    
}
