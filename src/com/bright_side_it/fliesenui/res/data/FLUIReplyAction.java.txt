package generated.fliesenui.core;

import java.util.List;

public class FLUIReplyAction {
	public enum ReplyActionType{DEFAULT, SHOW_LIST_CHOOSER_IMGS, SHOW_LIST_CHOOSER_TEXTS, OPEN_SCREEN, SET_DTO, SET_IMAGE_ASSET, SET_HIGHLIGHTINGS, SET_CONTEXT_ASSIST, SET_TABLE_CHECKED_ROW_IDS}
	
	private ReplyActionType actionType;
	private String code;
	private List<Object> parameters;
	
	public ReplyActionType getActionType() {
		return actionType;
	}
	public void setActionType(ReplyActionType actionType) {
		this.actionType = actionType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<Object> getParameters() {
		return parameters;
	}
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
}
