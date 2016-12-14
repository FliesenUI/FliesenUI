package generated.fliesenui.core;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class FLUIAbstractReply {
    protected FLUIReplyDTO replyDTO = createInitialReplyDTO();
    protected Gson gson = new GsonBuilder().create();

    protected FLUIReplyDTO createInitialReplyDTO() {
        FLUIReplyDTO result = new FLUIReplyDTO();
        result.setDTOsToSet(new TreeSet<String>());
        result.setVariablesToSet(new TreeSet<String>());
        result.setVariableValues(new TreeMap<String, Object>());
        result.setDTOValues(new TreeMap<String, Object>());
        result.setObjectsToSetValue(new TreeSet<String>());
        result.setObjectSetValueValues(new TreeMap<String, Object>());
        result.setSelectBoxSelectedIDs(new TreeMap<String, String>());
        result.setMarkdownViewTexts(new TreeMap<String, String>());
        result.setCursorPosValues(new TreeMap<String, CursorPos>());
        result.setTextHighlighting(new TreeMap<String, List<TextHighlighting>>());
        result.setContextAssists(new TreeMap<String, ContextAssist>());
        return result;
    }

    public void setInfoDialog(String title, String text) {
        FLUIMessage message = new FLUIMessage();
        message.setTitle(title);
        message.setText(text);
        message.setTypeID(FLUIMessage.TYPE_ID_INFO_DIALOG);
        replyDTO.setMessage(message);
    }

    public void setErrorDialog(String title, String text) {
        FLUIMessage message = new FLUIMessage();
        message.setTitle(title);
        message.setText(text);
        message.setTypeID(FLUIMessage.TYPE_ID_ERROR_DIALOG);
        replyDTO.setMessage(message);
    }

    public void setInfoToast(String text) {
        FLUIMessage message = new FLUIMessage();
        message.setText(text);
        message.setTypeID(FLUIMessage.TYPE_ID_INFO_TOAST);
        replyDTO.setMessage(message);
    }

    public void openURL(String url, boolean openInNewWindow) {
    	replyDTO.setURLToOpen(url);
    	replyDTO.setOpenURLInNewWindow(openInNewWindow);
    }
    
    public void downloadFile(String fileStreamID){
    	replyDTO.setDownloadFileStreamID(fileStreamID);
    }
    
    public void showInputDialog(String referenceID, String title, String textContent, String label, String initialValueText, String okText, String cancelText) {
        InputDialogParameters inputDialogParameters = new InputDialogParameters();
        inputDialogParameters.setReferenceID(referenceID);
        inputDialogParameters.setTitle(title);
        inputDialogParameters.setTextContent(textContent);
        inputDialogParameters.setLabel(label);
        inputDialogParameters.setInitialValueText(initialValueText);
        inputDialogParameters.setOkText(okText);
        inputDialogParameters.setCancelText(cancelText);
        replyDTO.setInputDialogParameters(inputDialogParameters);
    }

    public void showConfirmDialog(String referenceID, String title, String textContent, String okText, String cancelText) {
        ConfirmDialogParameters confirmDialogParameters = new ConfirmDialogParameters();
        confirmDialogParameters.setReferenceID(referenceID);
        confirmDialogParameters.setTitle(title);
        confirmDialogParameters.setTextContent(textContent);
        confirmDialogParameters.setOkText(okText);
        confirmDialogParameters.setCancelText(cancelText);
        replyDTO.setConfirmDialogParameters(confirmDialogParameters);
    }

}
