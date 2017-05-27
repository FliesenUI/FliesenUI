package generated.fliesenui.core;

import generated.fliesenui.core.FLUIString.StringLanguage;

import java.io.File;

public class FLUITestClassWriter {
	private FLUITestWriter testWriter = new FLUITestWriter();
	private FLUITestReplyWriter testReplyWriter = new FLUITestReplyWriter();
	
	public void writeTestClass(File file, String className, String packageName, FLUIActionRecording recording) throws Exception{
		String string = getTestClassString(className, packageName, recording);
		FLUIUtil.writeFile(file, string);
	}
	
	public String getTestClassString(String className, String packageName, FLUIActionRecording recording) throws Exception{
		StringBuilder result = new StringBuilder();
        result.append("package " + packageName + ";\n\n");
        result.append("import org.junit.Test;\n");
        result.append("import static org.junit.Assert.*;\n");
        result.append("import com.google.gson.Gson;\n");
        result.append("import com.google.gson.GsonBuilder;\n");
        result.append("\n");
        result.append("import java.util.ArrayList;\n");
        result.append("import java.util.Arrays;\n");
        result.append("import java.util.List;\n");
        result.append("\n");
        result.append("import generated.fliesenui.core.FLUIReplyDTO;\n");
        result.append("import generated.fliesenui.core.FLUIString.StringLanguage;\n");
        result.append("import generated.fliesenui.core.FLUIAbstractReply;\n");
        result.append("import generated.fliesenui.core.FLUIReplyAction;\n");
        result.append("import generated.fliesenui.core.FLUIClientPropertiesDTO;\n");
        result.append("import generated.fliesenui.core.FLUIScreenManager;\n");
        result.append("import generated.fliesenui.core.IDLabelList;\n");
        result.append("import generated.fliesenui.core.IDLabelImageAssetList;\n");
        result.append(testWriter.generateImports());
        result.append("\n");
        result.append("/* TODO: Add imports for presenters */\n");
        result.append("\n");
        result.append("public class " + className + "{\n");
        result.append("\n");
        result.append("    private FLUIScreenManager screenManager = FLUIScreenManager.createSimpleInstance();\n");
        result.append("    private Gson gson = new GsonBuilder().setPrettyPrinting().create();\n");
        
        result.append("\n");
        result.append(writeSetupPresentersMethod());
        result.append(writeMainTestMethod(recording));
        result.append(writeTestStepMethods(recording));
        result.append(writeTestStepDTOMethods(recording));
        result.append(writeTestStepReplyCodeMethods(recording));
        
        result.append("}\n");
        return result.toString();
	}

	private StringBuilder writeTestStepReplyCodeMethods(FLUIActionRecording recording) throws Exception {
		StringBuilder result = new StringBuilder();
		for (int step = 1; step <= recording.getRequests().size(); step ++){
			FLUIReplyDTO replyDTO = recording.getReplies().get(step - 1);
			result.append(testReplyWriter.writeCreateReplyParameterMethods(step, replyDTO));
		}
		return result;
	}

	private StringBuilder writeSetupPresentersMethod() {
		StringBuilder result = new StringBuilder();
		result.append("    public void setupPresenters(){\n");
		result.append(testWriter.generateSetPresenterStubs());
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder writeMainTestMethod(FLUIActionRecording recording) {
		StringBuilder result = new StringBuilder();
		result.append("    @Test\n");
		result.append("    public void testMain(){\n");
		result.append("        setupPresenters();\n");
		for (int step = 1; step <= recording.getRequests().size(); step ++){
			FLUIRequest request = recording.getRequests().get(step - 1);
			result.append("        " + getTestStepMethodName(step) + "(); //: " + request.getScreenID() + ": " + request.getAction() + "\n");
		}
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder writeTestStepDTOMethods(FLUIActionRecording recording) throws Exception {
		StringBuilder result = new StringBuilder();
		for (int step = 1; step <= recording.getRequests().size(); step ++){
			FLUIRequest requestData = recording.getRequests().get(step - 1);
			result.append(testWriter.writeCreateDTOMethods(step, requestData));
		}
		return result;
	}

	private StringBuilder writeTestStepMethods(FLUIActionRecording recording) throws Exception {
		StringBuilder result = new StringBuilder();
		for (int step = 1; step <= recording.getRequests().size(); step ++){
			result.append(writeTestStepMethod(step, recording.getRequests().get(step - 1), recording.getReplies().get(step - 1)));
		}
		return result;
	}

	private String getTestStepMethodName(int step){
		return "testStep" + String.format("%03d", step);
	}
	
	private StringBuilder writeTestStepMethod(int step, FLUIRequest request, FLUIReplyDTO reply) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("    private void " + getTestStepMethodName(step) + "(){\n");
		result.append("        " + testWriter.writeRequest(step, request) + "\n");
		result.append("        FLUIReplyDTO realResult = screenManager.onScreenRequest(request, null, null, true);\n");
		result.append("        String realResultJSON = gson.toJson(realResult);\n");
		result.append("\n");
		String replyClassName = toReplyClassName(request.getScreenID());
		String stringLanguageString = getStringLanguageString(request.getCurrentLanguage());
		result.append("        " + replyClassName + " expectedReply = new " + replyClassName + "(false, " + stringLanguageString + ");\n");
		result.append(testReplyWriter.writeCreateReplyCode(step, reply, "expectedReply", "        "));
		result.append("\n");
		result.append("        expectedReply.getReplyDTO().setRecordedActions(new ArrayList<FLUIReplyAction>()); //: ignore recorded actions\n");
		result.append("        String expectedResultJSON = gson.toJson(expectedReply.getReplyDTO());\n");
		result.append("        assertEquals(expectedResultJSON, realResultJSON);\n");
		result.append("\n");
		
		//: old compare by JSON OBJECTS {
		//FLUIReplyDTO replyWithoutRecordedActions = new Gson().fromJson(new Gson().toJson(reply), FLUIReplyDTO.class);
		//replyWithoutRecordedActions.setRecordedActions(new ArrayList<FLUIReplyAction>());
		//result.append("        String expectedResultGeneratedFromJSONString = \"" + new Gson().toJson(replyWithoutRecordedActions).replace("\\", "\\\\").replace("\"", "\\\"") + "\";\n");
		//result.append("        assertEquals(expectedResultGeneratedFromJSONString, realResultJSON);\n");
		//: old compare by JSON OBJECTS }
		
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private String getStringLanguageString(String code) {
		StringLanguage currentLanguage = null;
		try {
			currentLanguage = StringLanguage.valueOf(code);
		} catch (Exception ignored) {
		}
		if (currentLanguage == null){
			return "null";
		} else {
			return "StringLanguage." + currentLanguage;
		}
	}

	private String toReplyClassName(String screenID) {
		return Character.toUpperCase(screenID.charAt(0)) + screenID.substring(1) + "Reply";
	}

}
