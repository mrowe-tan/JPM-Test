/**
 * Interface for the service that records sale messages, and applies operations to
 * sales logged from previous messages.
 */
package mjr.techtest;

/**
 * @author mrowe
 *
 */
public interface SalesService {
	
	/**
	 * The available operations that can be applied to sales that have been logged.
	 * Our own notation is used here to restrict the range of operations that can
	 * be applied.
	 */
	public static enum adjustmentOperation {ADD, SUBTRACT, MULTIPLY};	
	
	/**
	 * Record the sale of a single item.
	 * 
	 * @param newSale  The item sold.
	 * @throws SalesException
	 */
	public void recordSale(Sale newSale) throws SalesException;
	
	/**
	 * Record the sale of a number of items.
	 * 
	 * @param newSale  The type of item sold.
	 * @param volume   The number of those items sold.
	 * @throws SalesException
	 */
	public void recordSale(Sale newSale, int volume) throws SalesException;
	
	/**
	 * Apply an operation to modify the value (i.e. cost) of a certain item type, for which
	 * sales have been previously recorded.
	 * 
	 * @param existingSale     The item type whose sale has already been recorded.
	 * @param adjustment       The operation to be applied to items of that type.  (e.g. "ADD".)
	 * @param adjustmentValue  The value for the operation to be applied.  (e.g. "10" if the aim 
	 *                             is to increase the cost of an item by 10.)
	 */
	public void adjustSale(Sale existingSale, adjustmentOperation adjustment, int adjustmentValue);
	

}
