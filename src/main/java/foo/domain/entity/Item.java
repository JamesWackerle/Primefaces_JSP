package foo.domain.entity;

import java.util.HashMap;

public class Item {

	private String item;
	private String itemDescription;

	public static final HashMap<String, String> ITEM_TO_DESC = new HashMap<String, String>();

	public Item(String item) {
		super();
		this.item = item;

		if (ITEM_TO_DESC.get(item) != null) {
			this.itemDescription = ITEM_TO_DESC.get(item);
		} else {
			this.itemDescription = "Item Description not found";
		}

	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public static HashMap<String, String> getItemToDesc() {
		return ITEM_TO_DESC;
	}

}
