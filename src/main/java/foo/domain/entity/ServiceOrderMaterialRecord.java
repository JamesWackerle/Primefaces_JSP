package foo.domain.entity;

import java.math.BigDecimal;

public class ServiceOrderMaterialRecord {

	private String orderNumber;
	private int activity;
	private int lineNumber;
	private String item;
	private String itemDescription;
	private String activityLineNumber;
	private String employeeNumber;
	private Item itemObject;
	private BigDecimal quantity;
	private String quantityUnit;

	public ServiceOrderMaterialRecord() {
		super();
		quantity = new BigDecimal(0);
	}

	public ServiceOrderMaterialRecord(String orderNumber, int activity, int lineNumber, String item,
			String itemDescription) {
		super();
		this.orderNumber = orderNumber;
		this.activity = activity;
		this.lineNumber = lineNumber;
		this.item = item;
		this.itemDescription = itemDescription;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getActivity() {
		return activity;
	}

	public void setActivity(int activity) {
		this.activity = activity;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
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

	public String getActivityLineNumber() {
		return activityLineNumber;
	}

	public void setActivityLineNumber(String activityLineNumber) {
		this.activityLineNumber = activityLineNumber;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		if (employeeNumber == null || employeeNumber.equals("")) {
			this.employeeNumber = " ";
		} else {
			this.employeeNumber = employeeNumber;
		}
	}

	public Item getItemObject() {
		return itemObject;
	}

	public void setItemObject(Item itemObject) {
		this.itemObject = itemObject;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(String quantityUnit) {
		this.quantityUnit = quantityUnit;
	}

}
