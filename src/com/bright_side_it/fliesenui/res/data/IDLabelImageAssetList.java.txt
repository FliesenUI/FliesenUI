package generated.fliesenui.core;

import java.util.ArrayList;
import java.util.List;

import generated.fliesenui.core.FLUIImageAssets.ImageAsset;

public class IDLabelImageAssetList {
	private List<IDLabelImageAsset> items = new ArrayList<IDLabelImageAsset>();

	public List<IDLabelImageAsset> getItems() {
		return items;
	}

	public void setItems(List<IDLabelImageAsset> items) {
		this.items = items;
	}

	public void addItem(IDLabelImageAsset item){
		items.add(item);
	}

	public void addItem(String id, String label, String imageAssetID){
		IDLabelImageAsset item = new IDLabelImageAsset();
		item.setID(id);
		item.setLabel(label);
		item.setImageAssetID(imageAssetID);
		addItem(item);
	}

	public void addItem(String id, String label, ImageAsset imageAsset){
		String imageAssetID = null;
		if (imageAsset != null){
			imageAssetID = imageAsset.getID();
		}
		addItem(id, label, imageAssetID);
	}
}
