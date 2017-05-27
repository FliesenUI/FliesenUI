package generated.fliesenui.core;

import generated.fliesenui.core.FLUIImageAssets.ImageAsset;

import java.util.Collection;

import com.google.gson.Gson;

public class FLUITestReplyWriter {
	private FLUITestWriter testWriter = new FLUITestWriter();
	
	public StringBuilder writeCreateReplyCode(int step, FLUIReplyDTO replyDTO, String replyVariable, String indent) throws Exception{
		StringBuilder result = new StringBuilder();

		if (replyDTO.getRecordedActions() == null){
			throw new Exception("Not in recording mode");
		}
		
		int actionIndex = 1;
		for (FLUIReplyAction i: replyDTO.getRecordedActions()){
			result.append(indent + replyVariable + ".");
			switch (i.getActionType()) {
			case DEFAULT:
				result.append(i.getCode() + "\n");
				break;
			case SET_DTO:
				result.append(i.getCode() + createSetDTOCode(step, actionIndex, i));
				break;
			case OPEN_SCREEN:
				result.append(i.getCode() + createOpenScreenCode(step, actionIndex, i));
				break;
			case SET_CONTEXT_ASSIST:
				result.append(i.getCode() + createContextAssistCode(step, actionIndex, i));
				break;
			case SET_HIGHLIGHTINGS:
				result.append(i.getCode() + createSetHighlightingsCode(step, actionIndex, i));
				break;
			case SET_IMAGE_ASSET:
				result.append(i.getCode() + createSetImageAssetCode(step, actionIndex, i));
				break;
			case SHOW_LIST_CHOOSER_IMGS:
				result.append(i.getCode() + createShowListChooserCode(step, actionIndex, i, true));
				break;
			case SHOW_LIST_CHOOSER_TEXTS:
				result.append(i.getCode() + createShowListChooserCode(step, actionIndex, i, false));
				break;
			default:
				throw new Exception("Unknown type: " + i.getActionType());
			}
			actionIndex ++;
		}
		return result;
	}

	public StringBuilder writeCreateReplyParameterMethods(int step, FLUIReplyDTO replyDTO) throws Exception{
		StringBuilder result = new StringBuilder();

		if (replyDTO.getRecordedActions() == null){
			throw new Exception("Not in recording mode");
		}
		
		int actionIndex = 1;
		for (FLUIReplyAction i: replyDTO.getRecordedActions()){
			switch (i.getActionType()) {
			case DEFAULT:
				break;
			case SET_DTO:
				result.append(createSetDTOParameterMethodCode(step, actionIndex, i));
				break;
			case OPEN_SCREEN:
				result.append(createOpenScreenSetParameterMethodCode(step, actionIndex, i));
				break;
			case SET_CONTEXT_ASSIST:
				result.append(createContextAssistParameterMethodCode(step, actionIndex, i));
				break;
			case SET_HIGHLIGHTINGS:
				break;
			case SET_IMAGE_ASSET:
				break;
			case SHOW_LIST_CHOOSER_IMGS:
				result.append(createListChooserParameterMethodCode(step, actionIndex, i, true));
				break;
			case SHOW_LIST_CHOOSER_TEXTS:
				result.append(createListChooserParameterMethodCode(step, actionIndex, i, false));
				break;
			default:
				throw new Exception("Unknown type: " + i.getActionType());
			}
			actionIndex ++;
		}
		return result;
	}

	public static TextHighlighting creteCustomTextHighlighting(int startLine, int startPosInLine, int endLine, int endPosInLine, String style) {
        TextHighlighting result = new TextHighlighting();
        result.setStartLine(startLine);
        result.setStartPosInLine(startPosInLine);
        result.setEndLine(endLine);
        result.setEndPosInLine(endPosInLine);
        result.setStyle(style);
        return result;
	}
	

//	private StringBuilder createShowListChooserCode(int step, int actionIndex, FLUIReplyAction action) {
//		StringBuilder result = new StringBuilder();
//		Object itemsObject = action.getParameters().get(0);
//		IDLabelList simpleList = null;
//		IDLabelImageAssetList imgList = null;
//		result.append()
//		if (itemsObject instanceof IDLabelList){
//			simpleList = (IDLabelList) itemsObject;
//		} else if (itemsObject instanceof IDLabelImageAssetList){
//			imgList = (IDLabelImageAssetList) itemsObject;
//		} else {
//			throw new Exception("Unknown type: " + itemsObject.getClass().getName());
//		}
//		
//		return result;
//	}
//
	private StringBuilder createSetHighlightingsCode(int step, int actionIndex, FLUIReplyAction action) {
		StringBuilder result = new StringBuilder();
		TextHighlighting highlighting = (TextHighlighting) action.getParameters().get(0);
		result.append("FLUITestReplyWriter.creteCustomTextHighlighting(" + highlighting.getStartLine() + ", "
				+ highlighting.getEndPosInLine() + ", " + highlighting.getEndLine() + ", " + highlighting.getEndPosInLine() + ", \""
				+ highlighting.getStyle() + "\"");
		result.append(");\n");
		return result;
	}

	private StringBuilder createSetImageAssetCode(int step, int actionIndex, FLUIReplyAction action) {
		StringBuilder result = new StringBuilder();
		ImageAsset imageAsset = (ImageAsset) action.getParameters().get(0);
		if (imageAsset == null){
			result.append("null");
		} else {
			result.append("ImageAsset." + imageAsset);
		}
		result.append(");\n");
		return result;
	}

	private String createReplyActionParameterMethodName(int step, int actionIndex){
		return "createReplyParamForStep" + step + "Action" + actionIndex;
	}
	
    private String escapeString(String string) {
    	if (string == null){
    		return "null";
    	}
		return "\"" + string.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

    private StringBuilder createSetDTOParameterMethodCode(int step, int actionIndex, FLUIReplyAction action) {
    	StringBuilder result = new StringBuilder();
    	String className = (String) action.getParameters().get(1);
    	if (className == null){
    		return result;
    	}
    	String dtoJSON = (String)action.getParameters().get(0);
    	Object dto = new Gson().fromJson(dtoJSON, testWriter.createDTOInstanceByClassName(className).getClass());
    	
    	result.append("    private " + className + " " + createReplyActionParameterMethodName(step, actionIndex) + "(){\n");
    	result.append("        " + className + " dto = new " + className + "();\n");
    	result.append(testWriter.createSetDTOObjectCode("        ", "dto", 0, "", dto, className));
    	result.append("        return dto;\n");
    	result.append("    }\n");
    	result.append("\n");
    	return result;
    }
    
	private StringBuilder createOpenScreenSetParameterMethodCode(int step, int actionIndex, FLUIReplyAction action) {
		StringBuilder result = new StringBuilder();
		if ((action.getParameters() == null) || (action.getParameters().isEmpty())){
			return result;
		}
			
		
		String className = (String) action.getParameters().get(1);
		if (className == null){
			return result;
		}
		String dtoJSON = (String)action.getParameters().get(0);
		Object dto = new Gson().fromJson(dtoJSON, testWriter.createDTOInstanceByClassName(className).getClass());
		result.append("    private " + className + " " + createReplyActionParameterMethodName(step, actionIndex) + "(){\n");
		result.append("        " + className + " dto = new " + className + "();\n");
		result.append(testWriter.createSetDTOObjectCode("        ", "dto", 0, "", dto, className));
		result.append("        return dto;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}
	
	private StringBuilder createListChooserParameterMethodCode(int step, int actionIndex, FLUIReplyAction action, boolean includeImages) {
		StringBuilder result = new StringBuilder();
		result.append("    private ");
		if (includeImages){
			result.append("IDLabelImageAssetList");
		} else {
			result.append("IDLabelList");
		}
		
		result.append(" " + createReplyActionParameterMethodName(step, actionIndex) + "(){\n");

		String dtoJSON = (String)action.getParameters().get(0);
		IDLabelList textItems = null;
		IDLabelImageAssetList imageItems = null;
		if (includeImages){
			imageItems = new Gson().fromJson(dtoJSON, IDLabelImageAssetList.class);
			result.append("        IDLabelImageAssetList items = new IDLabelImageAssetList();\n");
			for (IDLabelImageAsset i: imageItems.getItems()){
				result.append("        items.addItem(" + escapeString(i.getID()) + ", " + escapeString(i.getLabel()) + ", " + escapeString(i.getImageAssetID()) + ");\n");
			}
		} else {
			textItems = new Gson().fromJson(dtoJSON, IDLabelList.class);
			result.append("        IDLabelList items = new IDLabelList();\n");
			for (IDLabel i: textItems.getItems()){
				result.append("        items.addItem(" + escapeString(i.getID()) + ", " + escapeString(i.getLabel()) + ");\n");
			}
		}
		result.append("        return items;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}
	
	private StringBuilder createContextAssistParameterMethodCode(int step, int actionIndex, FLUIReplyAction action) {
		StringBuilder result = new StringBuilder();
		
		String cursorPosJSON = (String)action.getParameters().get(0);
		ContextAssist contextAssist = new Gson().fromJson(cursorPosJSON, ContextAssist.class);
		
		result.append("    private ContextAssist " + createReplyActionParameterMethodName(step, actionIndex) + "(){\n");
		result.append("        ContextAssist result = new ContextAssist();\n");
		result.append("        CursorPos cursorPos = new CursorPos();\n");
		result.append("        cursorPos.setLine(" + contextAssist.getReplaceFrom().getLine() + ";\n");
		result.append("        cursorPos.setPosInLine(" + contextAssist.getReplaceFrom().getPosInLine() + ";\n");
		result.append("        result.setReplaceFrom(cursorPos);\n");
		result.append("        cursorPos = new CursorPos();\n");
		result.append("        cursorPos.setLine(" + contextAssist.getReplaceTo().getLine() + ";\n");
		result.append("        cursorPos.setPosInLine(" + contextAssist.getReplaceTo().getPosInLine() + ";\n");
		result.append("        result.setReplaceTo(cursorPos);\n");
		result.append("        result.setSelectedItem(" + contextAssist.getSelectedItem() + ");\n");
		result.append("        List<ContextAssistChoice> choices = new ArrayList<ContextAssistChoice>();\n");
		for (ContextAssistChoice i: contextAssist.getChoices()){
			result.append("        choices.add(FLUIUtil.createContextAssistChoice(" + escapeString(i.getLabel()) + ", " + escapeString(i.getText()) + "));\n");
		}
		result.append("        result.setChoices(choices);\n");
		result.append("        return result;\n");
		result.append("    }\n");
		result.append("\n");
		return result;
	}

	private StringBuilder createShowListChooserCode(int step, int actionIndex, FLUIReplyAction action, boolean includeImages) {
		StringBuilder result = new StringBuilder();
		result.append(createReplyActionParameterMethodName(step, actionIndex) + "()");
		result.append(", ");
		@SuppressWarnings("unchecked")
		Collection<String> selectedIDs = (Collection<String>) action.getParameters().get(1);
		if (selectedIDs == null){
			result.append("null");
		} else {
			result.append("Arrays.asList(");
			boolean first = true;
			for (String i: selectedIDs){
				if (first){
					first = false;
				} else {
					result.append(", ");
				}
				result.append(escapeString(i));
			}
			result.append(")");
		}
		
		result.append(");\n");
		return result;
	}
	
	private StringBuilder createContextAssistCode(int step, int actionIndex, FLUIReplyAction action) {
		StringBuilder result = new StringBuilder();
		result.append(createReplyActionParameterMethodName(step, actionIndex));
		result.append(");\n");
		return result;
	}

	private StringBuilder createSetDTOCode(int step, int actionIndex, FLUIReplyAction action) {
		StringBuilder result = new StringBuilder();
		result.append(createReplyActionParameterMethodName(step, actionIndex) + "()");
		result.append(");\n");
		return result;
	}
	
	private StringBuilder createOpenScreenCode(int step, int actionIndex, FLUIReplyAction action) {
		StringBuilder result = new StringBuilder();
		if ((action.getParameters() != null) && (!action.getParameters().isEmpty())){
			
			String className = (String) action.getParameters().get(1);
			if (className == null){
				result.append("null");
			} else {
				result.append(createReplyActionParameterMethodName(step, actionIndex) + "()");
			}

			
		}
		result.append(");\n");
		return result;
	}

}
