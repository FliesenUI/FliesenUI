package com.bright_side_it.fliesenui.generator.model;

import java.util.List;

public class RequestToCallTranslation {
    private String actionName;
    private List<ReplyToCallTranslationParameter> parameter;
    private String methodName;
    private boolean fileUploadMethod;
    
    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public List<ReplyToCallTranslationParameter> getParameter() {
        return parameter;
    }

    public void setParameter(List<ReplyToCallTranslationParameter> parameter) {
        this.parameter = parameter;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

	public boolean isFileUploadMethod() {
		return fileUploadMethod;
	}

	public void setFileUploadMethod(boolean fileUploadMethod) {
		this.fileUploadMethod = fileUploadMethod;
	}

}
