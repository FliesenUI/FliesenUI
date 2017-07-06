package generated.fliesenui.core;

import generated.fliesenui.core.FLUIReplyAction.ReplyActionType;
import generated.fliesenui.core.FLUIString.StringLanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class FLUIAbstractReply implements FLUIAbstractReplyContainer{
    protected FLUIReplyDTO replyDTO = createInitialReplyDTO();
    protected Gson gson = new GsonBuilder().create();
    public static final String DEFAULT_OK_TEXT = "OK";
    public static final String DEFAULT_CANCEL_TEXT = "Cancel";
    protected boolean recordMode = false;
    
    public FLUIAbstractReply(boolean recordMode, StringLanguage currentLanguage){
    	this.recordMode = recordMode;
    	replyDTO.setCurrentLanguage(currentLanguage);
    }
    
    protected FLUIReplyDTO createInitialReplyDTO() {
        FLUIReplyDTO result = new FLUIReplyDTO();
        result.setDTOsToSet(new TreeSet<String>());
        result.setVariablesToSet(new TreeSet<String>());
        result.setVariableValues(new TreeMap<String, Object>());
        result.setDTOValues(new TreeMap<String, Object>());
        result.setObjectsToSetValue(new TreeSet<String>());
        result.setObjectSetValueValues(new TreeMap<String, Object>());
        result.setSelectBoxSelectedIDs(new TreeMap<String, String>());
        result.setTableCheckedRowIDs(new TreeMap<String, Set<String>>());
        result.setMarkdownViewTexts(new TreeMap<String, String>());
        result.setCursorPosValues(new TreeMap<String, CursorPos>());
        result.setTextHighlighting(new TreeMap<String, List<TextHighlighting>>());
        result.setContextAssists(new TreeMap<String, ContextAssist>());
        result.setRecordedActions(new ArrayList<FLUIReplyAction>());
        return result;
    }

    protected String escapeString(String string) {
    	if (string == null){
    		return "null";
    	}
		return "\"" + string.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}
    
    protected String getClassName(Object object){
    	if (object == null){
    		return null;
    	}
    	return object.getClass().getSimpleName();
    }

    protected void addRecordedAction(ReplyActionType actionType, String code, Object ... parameters) {
		FLUIReplyAction action = new FLUIReplyAction();
		action.setActionType(actionType);
		action.setCode(code);
		if (parameters != null){
			action.setParameters(Arrays.asList(parameters));
		}
		replyDTO.getRecordedActions().add(action);
	}
    
    protected void addRecordedAction(String code, Object ... parameters) {
		addRecordedAction(ReplyActionType.DEFAULT, code, parameters);
	}
    
    public void setInfoDialog(String title, String text) {
        FLUIMessage message = new FLUIMessage();
        message.setTitle(title);
        message.setText(text);
        message.setTypeID(FLUIMessage.TYPE_ID_INFO_DIALOG);
        replyDTO.setMessage(message);
        if (recordMode){
        	addRecordedAction("setInfoDialog(" + escapeString(title) + ", " + escapeString(text) + ");");
        }
    }

	public void setErrorDialog(String title, String text) {
        FLUIMessage message = new FLUIMessage();
        message.setTitle(title);
        message.setText(text);
        message.setTypeID(FLUIMessage.TYPE_ID_ERROR_DIALOG);
        replyDTO.setMessage(message);
        if (recordMode){
        	addRecordedAction("setErrorDialog(" + escapeString(title) + ", " + escapeString(text) + ");");
        }
    }

    public void setInfoToast(String text) {
        FLUIMessage message = new FLUIMessage();
        message.setText(text);
        message.setTypeID(FLUIMessage.TYPE_ID_INFO_TOAST);
        replyDTO.setMessage(message);
        if (recordMode){
        	addRecordedAction("setInfoToast(" + escapeString(text) + ");");
        }
    }

    public void openURL(String url, boolean openInNewWindow) {
    	replyDTO.setURLToOpen(url);
    	replyDTO.setOpenURLInNewWindow(openInNewWindow);
        if (recordMode){
        	addRecordedAction("openURL(" + escapeString(url) + ", " + openInNewWindow + ");");
        }
    }
    
    public void downloadFile(String fileStreamID){
    	replyDTO.setDownloadFileStreamID(fileStreamID);
        if (recordMode){
        	addRecordedAction("downloadFile(" + escapeString(fileStreamID) + ");");
        }
    }

/*    
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

        if (recordMode){
        	addRecordedAction("showInputDialog(" + escapeString(referenceID) + ", " + escapeString(title) + ", " + escapeString(textContent) 
        			+ ", " + escapeString(label) + ", " + escapeString(initialValueText) + ", " + escapeString(okText) 
        			+ ", " + escapeString(cancelText)+ ");");
        }
    }

    public void showConfirmDialog(String referenceID, String title, String textContent, String okText, String cancelText) {
        ConfirmDialogParameters confirmDialogParameters = new ConfirmDialogParameters();
        confirmDialogParameters.setReferenceID(referenceID);
        confirmDialogParameters.setTitle(title);
        confirmDialogParameters.setTextContent(textContent);
        confirmDialogParameters.setOkText(okText);
        confirmDialogParameters.setCancelText(cancelText);
        replyDTO.setConfirmDialogParameters(confirmDialogParameters);
        if (recordMode){
        	addRecordedAction("showConfirmDialog(" + escapeString(referenceID) + ", " + escapeString(title) + ", " + escapeString(textContent) 
        			+ ", " + escapeString(okText) + ", " + escapeString(cancelText)+ ");");
        }
    }
    
//    public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, String okText, String cancelText, IDLabelImageAssetList items){
//    	showListChooser(referenceID, multiSelect, showFilter, title, okText, cancelText, items, null);
//    }
//    
//    public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, String okText, String cancelText, IDLabelList items){
//    	showListChooser(referenceID, multiSelect, showFilter, title, okText, cancelText, items, null);
//    }
    
//    public void showListChooser(String referenceID, boolean multiSelect, String title, IDLabelImageAssetList items){
//    	showListChooser(referenceID, multiSelect, true, title, DEFAULT_OK_TEXT, DEFAULT_CANCEL_TEXT, items, null);
//    }
//    
//    public void showListChooser(String referenceID, boolean multiSelect, String title, IDLabelList items){
//    	showListChooser(referenceID, multiSelect, true, title, DEFAULT_OK_TEXT, DEFAULT_CANCEL_TEXT, items, null);
//    }
    
    public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, IDLabelImageAssetList items, Collection<String> selectedIDs){
    	showListChooser(referenceID, multiSelect, showFilter, title, DEFAULT_OK_TEXT, DEFAULT_CANCEL_TEXT, items, selectedIDs);
    }
    
    public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, IDLabelList items, Collection<String> selectedIDs){
    	showListChooser(referenceID, multiSelect, showFilter, title, DEFAULT_OK_TEXT, DEFAULT_CANCEL_TEXT, items, selectedIDs);
    }
    
    public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, String okText, String cancelText, IDLabelImageAssetList items, Collection<String> selectedIDs){
    	ListChooserParameters parameters = createListChooserParameters(referenceID, multiSelect, showFilter, title, okText, cancelText);
    	Set<String> selectedIDsSet = new HashSet<String>();
    	if (selectedIDs != null){
    		selectedIDsSet = new HashSet<String>(selectedIDs);
    	}
    	List<ListChooserItem> chooserItems = new ArrayList<ListChooserItem>();
    	for (IDLabelImageAsset i: items.getItems()){
    		chooserItems.add(createItem(i.getID(), i.getLabel(), i.getImageAssetID(), selectedIDsSet.contains(i.getID())));
    	}
    	parameters.setShowIcons(true);
    	parameters.setItems(chooserItems);
    	replyDTO.setListChooserParameters(parameters);
    	
        if (recordMode){
        	addRecordedAction(ReplyActionType.SHOW_LIST_CHOOSER_IMGS, "showListChooser(" + escapeString(referenceID) + ", " + multiSelect + ", " + showFilter + ", " + escapeString(title) 
        			+ ", " + escapeString(okText) + ", " + escapeString(cancelText) + ", ", gson.toJson(items), selectedIDs);
        }
    	
    }

    public void showListChooser(String referenceID, boolean multiSelect, boolean showFilter, String title, String okText, String cancelText, IDLabelList items, Collection<String> selectedIDs){
    	ListChooserParameters parameters = createListChooserParameters(referenceID, multiSelect, showFilter, title, okText, cancelText);
    	Set<String> selectedIDsSet = new HashSet<String>();
    	if (selectedIDs != null){
    		selectedIDsSet = new HashSet<String>(selectedIDs);
    	}
    	List<ListChooserItem> chooserItems = new ArrayList<ListChooserItem>();
    	for (IDLabel i: items.getItems()){
    		chooserItems.add(createItem(i.getID(), i.getLabel(), null, selectedIDsSet.contains(i.getID())));
    	}
    	parameters.setShowIcons(false);
    	parameters.setItems(chooserItems);
    	replyDTO.setListChooserParameters(parameters);
        if (recordMode){
        	addRecordedAction(ReplyActionType.SHOW_LIST_CHOOSER_TEXTS, "showListChooser(" + escapeString(referenceID) + ", " + multiSelect + ", " + showFilter + ", " + escapeString(title) 
        			+ ", " + escapeString(okText) + ", " + escapeString(cancelText) + ", ", gson.toJson(items), selectedIDs);
        }
    }

	private ListChooserParameters createListChooserParameters(String referenceID, boolean multiSelect, boolean showFilter, String title, String okText, String cancelText) {
		ListChooserParameters parameters = new ListChooserParameters();
    	parameters.setReferenceID(referenceID);
    	parameters.setMultiSelect(multiSelect);
    	parameters.setShowFilter(showFilter);
    	parameters.setTitle(title);
    	parameters.setOkText(okText);
    	parameters.setCancelText(cancelText);
    	return parameters;
	}

	private ListChooserItem createItem(String id, String label, String imageAssetID, boolean selected) {
		ListChooserItem result = new ListChooserItem();
		result.setID(id);
		result.setLabel(label);
		result.setImageAssetID(imageAssetID);
		result.setSelected(selected);
		return result;
	}
*/	
	public void setLanguage(StringLanguage language){
		replyDTO.setLanguageToSet("" + language);
        if (recordMode){
        	String value = null;
        	if (language != null){
        		value = "StringLanguage." + language;
        	}
        	addRecordedAction("setLanguage(" + value + ");");
        }
	}
	
	public StringLanguage getCurrentLanguage() {
		return replyDTO.getCurrentLanguage();
	}

	public boolean isRecordMode() {
		return recordMode;
	}

	public FLUIReplyDTO getReplyDTO() {
		return replyDTO;
	}
	
	public FLUIAbstractReply getAbstractReply(){
	    return this;
	}	
}
