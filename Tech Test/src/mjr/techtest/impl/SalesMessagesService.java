/**
 * Implementation of the interface for the service that records and manages sale messages.
 */
package mjr.techtest.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mjr.techtest.Sale;
import mjr.techtest.SalesException;
import mjr.techtest.SalesPausedException;
import mjr.techtest.SalesService;

/**
 * @author mrowe
 *
 */
public class SalesMessagesService implements SalesService {
	
	public enum State {RUNNING, LOGGING, PAUSED};
	
	private List<Sale> receivedSales = new ArrayList<Sale>();
	private ModificationRecord saleModifications = new ModificationRecord();
	
	private State currState;
	
	private int logInterval = 10;
	private int pauseInterval = 50;

	public SalesMessagesService() {
		// Initialise the service in the running state.
		this.currState = State.RUNNING;
	}

	public State getCurrState() {
		return currState;
	}


	/* (non-Javadoc)
	 * @see mjr.techtest.SalesNotifications#recordSale(mjr.techtest.Sale)
	 */
	@Override
	public void recordSale(Sale newSale) throws SalesException{
		Sale actualNewSale = new Sale(newSale.getProductType(), newSale.getValue());
		addSale(actualNewSale);
		processSales();
	}

	/* (non-Javadoc)
	 * @see mjr.techtest.SalesNotifications#recordSale(mjr.techtest.Sale, int)
	 */
	@Override
	public void recordSale(Sale newSale, int volume) throws SalesException {
		for (int i = 0; i < volume; i++) {
			recordSale(newSale);
		}

	}

	/* (non-Javadoc)
	 * @see mjr.techtest.SalesNotifications#adjustSale(mjr.techtest.Sale, mjr.techtest.SalesNotifications.adjustmentOperation, float)
	 * 
	 * Applies the specified operation to all stored items of a particular type.
	 */
	@Override
	public void adjustSale(Sale newSale, adjustmentOperation adjustment, int adjustmentValue) {
		
		String typeToAdjust = newSale.getProductType();

		for (Sale currSale : receivedSales) {
			if (currSale.getProductType().equals(typeToAdjust)) {

				if (adjustmentOperation.ADD == adjustment) {
					currSale.setValue(currSale.getValue() + adjustmentValue);
				}
				else if (adjustmentOperation.SUBTRACT == adjustment) {
					currSale.setValue(currSale.getValue() - adjustmentValue);
				}
				else if (adjustmentOperation.MULTIPLY == adjustment) {
					currSale.setValue(currSale.getValue() * adjustmentValue);
				}
				
			}
		}
		
		// Record the modification made
		saleModifications.recordModification(typeToAdjust, new ModificationRecordItem(adjustment, adjustmentValue));
	}
	
	
	/*
	 * Add the sale to the currently stored record of sales that have been made.
	 * This will fail if the service is not in a state suitable for accepting
	 * messages.
	 */
	private void addSale(Sale newSale) throws SalesPausedException {
		if (State.PAUSED != currState) {
			currState = State.RUNNING;
			receivedSales.add(newSale);
		}
		else {
			throw new SalesPausedException("Application has processed 50 sales");
		}
	}
	

	/*
	 * Processes the current record of sales, logging if necessary (which occurs once
	 * every 10 sales) or suspending the service's ability to process sales (which
	 * occurs once 50 sales have been recorded). 
	 */
	private void processSales() {
		if (0 == receivedSales.size() % this.logInterval) {
			currState = State.LOGGING;
			List<SalesReportItem> report = generateReport();
			printReport(report);
		}
		if (this.pauseInterval == receivedSales.size()) {
			currState = State.PAUSED;
			logModifications();
		}
	}
	
	
	/**
	 * Generate a report of all sale items currently received.
	 * Report will contain a list of ReportItems, with each ReportItem matching one type of sale
	 * object.  Each report item will contain:
	 * - The type of the item.
	 * - The number of that type of item sold.
	 * - The total cost of the items sold.
	 */
	public List<SalesReportItem> generateReport() {
		List<SalesReportItem> report;
		
		Map<String, SalesReportItem> discoveredTypes = new HashMap<String, SalesReportItem>();
		
		for (Sale currSale : receivedSales) {
			String currType = currSale.getProductType();
			
			SalesReportItem currReportItem = discoveredTypes.get(currType);
			if (null == currReportItem) {
				currReportItem = new SalesReportItem(currType, 1, currSale.getValue());
				discoveredTypes.put(currType, currReportItem);
			}
			else {
				currReportItem.setNumItems(currReportItem.getNumItems() + 1);
				currReportItem.setTotalValue(currReportItem.getTotalValue() + currSale.getValue());
			}
		}
		
		report = new ArrayList<SalesReportItem>(discoveredTypes.values());
		
		return report;
	}
	
	
	/**
	 * Returns the set of all modifications made to sale item types.
	 * 
	 * @return The set of all modifications made to sale item types.
	 */
	public ModificationRecord getSaleModifications() {
		return saleModifications;
	}
	

	/*
	 * Output the report to the console.
	 */
	private void printReport(List<SalesReportItem> report) {
		System.out.println("Current sales report:");
		System.out.println("Item Type\tQuantity Sold\tTotal Sales Value");
		System.out.println("---------\t-------------\t-----------------");
		for (SalesReportItem item : report) {
			
			String decimalValue = "£" + (item.getTotalValue() / 100) + "." + (item.getTotalValue() % 100); 
			
			System.out.println("- " + item.getItemType() + "\t\t" + item.getNumItems() + "\t\t" + decimalValue);
		}
		System.out.println("End report.\n");
	}
	
	
	/*
	 * Log the changes made to any item types to the console.
	 */
	private void logModifications() {
		Set<String> modifiedTypes = saleModifications.getModifiedTypes();
		
		System.out.println("Recorded Modifications");
		System.out.println("----------------------");
		
		for (String currType : modifiedTypes) {
			System.out.println("- " + currType + " :");
			
			for (ModificationRecordItem currItem : saleModifications.getModificationsForType(currType)) {
				System.out.println("\t" + currItem.getOperation());
			}
		}
		System.out.println("\nMessage processing now paused.\n");
	}
	
}
