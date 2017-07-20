package mjr.techtest.impl;

import mjr.techtest.SalesService;
import mjr.techtest.SalesService.adjustmentOperation;

public class ModificationRecordItem {

	private SalesService.adjustmentOperation operation;
	private int value;

	public ModificationRecordItem(adjustmentOperation operation, int value) {
		super();
		this.operation = operation;
		this.value = value;
	}

	/**
	 * @return the operation
	 */
	public SalesService.adjustmentOperation getOperation() {
		return operation;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

}
