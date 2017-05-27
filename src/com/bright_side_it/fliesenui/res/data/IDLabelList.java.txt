package generated.fliesenui.core;

import java.util.ArrayList;
import java.util.List;

public class IDLabelList {
	private List<IDLabel> items = new ArrayList<IDLabel>();

	public List<IDLabel> getItems() {
		return items;
	}

	public void setItems(List<IDLabel> items) {
		this.items = items;
	}

	public void addItem(IDLabel item){
		items.add(item);
	}

	public void addItem(String id, String label){
		IDLabel item = new IDLabel();
		item.setID(id);
		item.setLabel(label);
		addItem(item);
	}
}
