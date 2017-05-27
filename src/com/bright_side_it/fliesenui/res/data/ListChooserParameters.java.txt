package generated.fliesenui.core;

import java.util.List;

public class ListChooserParameters {
	private String referenceID;
	private String title;
	private boolean multiSelect;
	private boolean showIcons;
	private String okText;
	private String cancelText;
	private boolean showFilter;
	private List<ListChooserItem> items;
	
	public String getReferenceID() {
		return referenceID;
	}
	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isMultiSelect() {
		return multiSelect;
	}
	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}
	public boolean isShowIcons() {
		return showIcons;
	}
	public void setShowIcons(boolean showIcons) {
		this.showIcons = showIcons;
	}
	public List<ListChooserItem> getItems() {
		return items;
	}
	public void setItems(List<ListChooserItem> items) {
		this.items = items;
	}
	public String getOkText() {
		return okText;
	}
	public void setOkText(String okText) {
		this.okText = okText;
	}
	public String getCancelText() {
		return cancelText;
	}
	public void setCancelText(String cancelText) {
		this.cancelText = cancelText;
	}
	public boolean isShowFilter() {
		return showFilter;
	}
	public void setShowFilter(boolean showFilter) {
		this.showFilter = showFilter;
	}

	
}
