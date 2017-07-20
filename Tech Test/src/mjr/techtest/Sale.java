/**
 * A class to record a product whose sale is to be recorded.
 * A "sale" consists of a type of product that has been sold, and an associated
 * value - i.e. cost for that product.
 * 
 * Note that to prevent possible numerical errors, costs are recorded in pence.
 */
package mjr.techtest;

/**
 * @author mrowe
 *
 */
public class Sale {
	
	String invalidNullType = "Product type must not be null";  //TODO - NLS
	String invalidValue = "Product value must be non-negative.";  // TODO - NLS

	protected String productType;
	protected int value;
	

	/**
	 * Create a record for an product whose sale is to be recorded.
	 * 
	 * @param productType  The type of product sold.  Pre-registration of this type is 
	 *                         not currently required.
	 * @param value        The cost of the product.
	 * @throws SalesException
	 */
	public Sale(String productType, int value) throws SalesException {
		super();
		this.productType = productType;
		this.value = value;
		
		if (null == productType || productType.equals("")) {
			throw new SalesException(invalidNullType);
		}
		if (0 > value) {
			throw new SalesException(invalidValue);
		}
		
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean equals(Sale cf) {
		boolean isEqual = false;
		
		if ((cf.value == this.value) && (cf.productType.equals(this.productType))) {
			isEqual = true;
		}
		
		return isEqual;
	}
	
}
