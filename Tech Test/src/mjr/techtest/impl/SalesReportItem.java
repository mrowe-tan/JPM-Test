package mjr.techtest.impl;

public class SalesReportItem {

	private String itemType;
	private int numItems;
	private int totalValue;
	
	public SalesReportItem(String itemType, int numItems, int totalValue) {
		this.itemType = itemType;
		this.numItems = numItems;
		this.totalValue = totalValue;
	}

	/**
	 * @return the itemType
	 */
	public String getItemType() {
		return itemType;
	}

	/**
	 * @return the numItems
	 */
	public int getNumItems() {
		return numItems;
	}

	/**
	 * @return the totalValue
	 */
	public int getTotalValue() {
		return totalValue;
	}

	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	/**
	 * @param numItems the numItems to set
	 */
	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}

	/**
	 * @param totalValue the totalValue to set
	 */
	public void setTotalValue(int totalValue) {
		this.totalValue = totalValue;
	}

}
