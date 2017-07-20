/**
 * Tests for the implementation of the SalesService interface.
 */
package mjr.techtest.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mjr.techtest.SalesException;
import mjr.techtest.SalesPausedException;
import mjr.techtest.SalesService;
import mjr.techtest.impl.SalesReportItem;
import mjr.techtest.Sale;
import mjr.techtest.impl.SalesMessagesService;

/**
 * @author mrowe
 *
 */
public class SalesServiceTest {

	private String typeApple = "apple";
	private String typePear = "pear";
	private String typePie = "pie";
	
	private int costApple = 20;
	private int costPear = 30;
	private int costPie = 129;

	private SalesMessagesService testService;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testService = new SalesMessagesService();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testType() throws Exception {
		Sale sale = new Sale(typeApple, costApple);
	}

	@Test
	public void testNullType() throws Exception {
		try {
			Sale sale = new Sale(null, costApple);
		}
		catch (SalesException e) {
			// This is actually a success - we expect the method call to fail.
			return;
		}
		// No exception means the test was not carried out.
		fail("Null type should be rejected");
	}
	

	@Test
	public void testEmptyType() throws Exception {
		try {
			Sale sale = new Sale("", costApple);
		}
		catch (SalesException e) {
			// This is actually a success - we expect the method call to fail.
			return;
		}
		// No exception means the test was not carried out.
		fail("Empty type should be rejected");
	}
	

	@Test
	public void testInvalidValue() throws Exception {
		try {
			Sale sale = new Sale(typeApple, -1);
		}
		catch (SalesException e) {
			// This is actually a success - we expect the method call to fail.
			return;
		}
		// No exception means the test was not carried out.
		fail("Negative value should be rejected");
	}
	
	
	/*
	 * Send a single item, and confirm that the contents of the generated report
	 * is as expected.
	 */
	@Test
	public void testReportContentsSingleItem() throws Exception {
		List<SalesReportItem> report;
		
		report = testService.generateReport();
		assertEquals(0, report.size());
		
		Sale sale = new Sale(typeApple, costApple);
		testService.recordSale(sale);
		report = testService.generateReport();
		assertEquals(1, report.size());
		assertEquals(typeApple, report.get(0).getItemType());
		assertEquals(costApple, report.get(0).getTotalValue(), 0);
		assertEquals(1, report.get(0).getNumItems());
	}
	

	/*
	 * Send a one each of two different items, and confirm that the contents of the 
	 * generated report is as expected.
	 */
	@Test
	public void testReportContentsTwoSingleItems() throws Exception {
		List<SalesReportItem> report;
		
		report = testService.generateReport();
		assertEquals(0, report.size());
		
		Sale sale = new Sale(typeApple, costApple);
		testService.recordSale(sale);
		sale = new Sale(typePie, costPie);
		testService.recordSale(sale);
		report = testService.generateReport();

		assertEquals(2, report.size());
		assertEquals(typeApple, report.get(0).getItemType());
		assertEquals(costApple, report.get(0).getTotalValue(), 0);
		assertEquals(1, report.get(0).getNumItems());
		
		// Note a slight cheat here based on our assumption of the implementation of the 
		// service.  This would need to be changed if a more complex implementation of
		// a report was provided.
		assertEquals(2, report.size());
		assertEquals(typePie, report.get(1).getItemType());
		assertEquals(costPie, report.get(1).getTotalValue(), 0);
		assertEquals(1, report.get(1).getNumItems());
	}
	
	
	/*
	 * Send a number of items, and confirm that the contents of generated reports
	 * contain the correct total for each item.
	 */
	@Test
	public void testReportContentsMultipleItems() throws Exception {
		List<SalesReportItem> report;
		
		Sale sale = new Sale(typeApple, costApple);
		testService.recordSale(sale, 2);
		sale = new Sale(typePie, costPie);
		testService.recordSale(sale, 5);
		sale = new Sale(typePear, costPear);
		testService.recordSale(sale, 8);
		
		report = testService.generateReport();
		assertEquals(3, report.size());
		
		for (SalesReportItem item : report) {
			if (typeApple.equals(item.getItemType())) {
				assertEquals(costApple * 2, item.getTotalValue(), 0);
				assertEquals(2, item.getNumItems());
			}
			else if (typePear.equals(item.getItemType())) {
				assertEquals(costPear * 8, item.getTotalValue(), 0);
				assertEquals(8, item.getNumItems());
			}
			else if (typePie.equals(item.getItemType())) {
				assertEquals(costPie * 5, item.getTotalValue(), 0);
				assertEquals(5, item.getNumItems());
			}
			else {
				fail("Unexpected type: " + item.getItemType());
			}
		}
	}
	
	
	/*
	 * Tests that the status of the service changes after 10 messages are received, and changes
	 * back again after a subsequent message.
	 */
	@Test
	public void testStatusChanges() throws Exception {
		SalesMessagesService.State currState = testService.getCurrState();
		assertEquals(SalesMessagesService.State.RUNNING, currState);
		
		Sale sale = new Sale(typePie, costPie);
		testService.recordSale(sale, 10);
		
		currState = this.testService.getCurrState();
		assertEquals(SalesMessagesService.State.LOGGING, currState);
		
		testService.recordSale(sale);
		currState = this.testService.getCurrState();
		assertEquals(SalesMessagesService.State.RUNNING, currState);
	}
	
	
	/* Tests that the service pauses after 50 messages, and no longer accepts further messages. */
	@Test
	public void testServicePause() throws Exception {
		Sale sale = new Sale(typePie, costPie);
		testService.recordSale(sale, 50);
		
		SalesMessagesService.State currState = testService.getCurrState();
		assertEquals(SalesMessagesService.State.PAUSED, currState);
		
		try {
			testService.recordSale(sale);
			// We would expect an exception to be thrown at this point, so fail if one
			// is not received.
			fail("SalesPausedException expected.");
		}
		catch (SalesPausedException spe) {
			// Do nothing
		}
		catch (Exception e) {
			fail("Incorrect exception type received: " + e.getClass().getName());
		}
		
	}
	

	/* Tests that an addition operation can be applied to sale items. */
	@Test
	public void testAddition() throws Exception {
		int firstChange = 10;
		
		Sale sale = new Sale(typeApple, costApple);
		testService.recordSale(sale);		
		testService.adjustSale(sale, SalesService.adjustmentOperation.ADD, firstChange);
		
		List<SalesReportItem> report = testService.generateReport();
		assertEquals(costApple + firstChange, report.get(0).getTotalValue(), 0);
		
		int secondChange = 19;
		testService.recordSale(sale, 10);
		testService.adjustSale(sale, SalesService.adjustmentOperation.ADD, secondChange);

		report = testService.generateReport();
		assertEquals(typeApple, report.get(0).getItemType());
		
		// Note the need to add a single "firstChange" to the total, based on the fact that the
		// original, modified value recorded will have itself been modified further.
		assertEquals(((costApple + secondChange) * 11) + firstChange, report.get(0).getTotalValue(), 0);		
	}
	

	/* Tests that a subtraction operation can be applied to sale items. */
	@Test
	public void testSubtraction() throws Exception {
		int firstChange = 30;
		
		Sale sale = new Sale(typePie, costPie);
		testService.recordSale(sale);
		testService.adjustSale(sale, SalesService.adjustmentOperation.SUBTRACT, firstChange);

		List<SalesReportItem> report = testService.generateReport();
		assertEquals(costPie - firstChange, report.get(0).getTotalValue(), 0);		
		
		int secondChange = 11;
		testService.recordSale(sale, 10);
		testService.adjustSale(sale, SalesService.adjustmentOperation.SUBTRACT, secondChange);

		report = testService.generateReport();
		// Note the need to remove a single "firstChange" from the total, based on the fact that the
		// original, modified value recorded will have itself been modified further.
		assertEquals(((costPie - secondChange) * 11) - firstChange, report.get(0).getTotalValue(), 0);		
	}
	

	/* Tests that a multiplication operation can be applied to sale items. */
	@Test
	public void testMultplication() throws Exception {
		int firstChange = 2;
		
		Sale sale = new Sale(typeApple, costApple);
		testService.recordSale(sale);		
		testService.adjustSale(sale, SalesService.adjustmentOperation.MULTIPLY, firstChange);
		
		List<SalesReportItem> report = testService.generateReport();
		assertEquals(costApple * firstChange, report.get(0).getTotalValue(), 0);		

		int secondChange = 5;
		testService.recordSale(sale, 10);
		testService.adjustSale(sale, SalesService.adjustmentOperation.MULTIPLY, secondChange);
		
		report = testService.generateReport();
		// Note the need to add an additional multiplication to the total, based on the fact that the
		// original, modified value recorded will have itself been modified further.
		assertEquals(((costApple * secondChange) * 10) + (costApple * firstChange * secondChange), report.get(0).getTotalValue(), 0);		
		
	}
	

	/*
	 * Tests that changes made to item types are recorded, and are stored in the order in which they occurred.
	 */
	@Test
	public void testModificationRecord() throws Exception {
		Sale sale = new Sale(typeApple, costApple);
		int appleChangeOne = 10;
		int appleChangeTwo = 20;
		
		testService.recordSale(sale);
		
		assertEquals(0, testService.getSaleModifications().getModifiedTypes().size());
		
		testService.adjustSale(sale, SalesService.adjustmentOperation.MULTIPLY, appleChangeOne);
		assertEquals(1, testService.getSaleModifications().getModifiedTypes().size());
		assertEquals(1, testService.getSaleModifications().getModificationsForType(typeApple).size());
		assertNull(testService.getSaleModifications().getModificationsForType(typePear));
		
		testService.adjustSale(sale, SalesService.adjustmentOperation.ADD, appleChangeTwo);
		assertEquals(1, testService.getSaleModifications().getModifiedTypes().size());
		assertEquals(2, testService.getSaleModifications().getModificationsForType(typeApple).size());
		assertNull(testService.getSaleModifications().getModificationsForType(typePie));
		assertEquals(SalesService.adjustmentOperation.MULTIPLY, 
				testService.getSaleModifications().getModificationsForType(typeApple).get(0).getOperation());
		assertEquals(SalesService.adjustmentOperation.ADD, 
				testService.getSaleModifications().getModificationsForType(typeApple).get(1).getOperation());
		

		sale = new Sale(typePie, costPie);
		int pieChange = 314;
		testService.recordSale(sale);
		testService.adjustSale(sale, SalesService.adjustmentOperation.SUBTRACT, pieChange);
		assertEquals(2, testService.getSaleModifications().getModifiedTypes().size());
		assertEquals(2, testService.getSaleModifications().getModificationsForType(typeApple).size());
		assertNull(testService.getSaleModifications().getModificationsForType(typePear));
		assertEquals(1, testService.getSaleModifications().getModificationsForType(typePie).size());
		assertEquals(SalesService.adjustmentOperation.SUBTRACT, 
				testService.getSaleModifications().getModificationsForType(typePie).get(0).getOperation());
		
	}


	/*
	 * Tests that changes made to item types are recorded, and are stored in the order in which they occurred.
	 * Included for the purposes of verifying logging.
	 */
	@Test
	public void testModificationRecord2() throws Exception {
		Sale saleApple = new Sale(typeApple, costApple);
		int appleChangeOne = 10;
		int appleChangeTwo = 20;
		int appleChangeThree = 14;
		Sale salePear = new Sale(typePear, costPear);
		int pearChangeOne = 2;
		int pearChangeTwo = 4;
		Sale salePie = new Sale(typePie, costPie);
		int pieChangeOne = 1;
		int pieChangeTwo = 4;
		int pieChangeThree = 9;
		int pieChangeFour = 16;
		int pieChangeFive = 25;
		SalesService.adjustmentOperation appleAdjustmentOne = SalesService.adjustmentOperation.ADD;
		SalesService.adjustmentOperation appleAdjustmentTwo = SalesService.adjustmentOperation.ADD;
		SalesService.adjustmentOperation appleAdjustmentThree = SalesService.adjustmentOperation.SUBTRACT;
		SalesService.adjustmentOperation pearAdjustmentOne = SalesService.adjustmentOperation.ADD;
		SalesService.adjustmentOperation pearAdjustmentTwo = SalesService.adjustmentOperation.MULTIPLY;
		SalesService.adjustmentOperation pieAdjustmentOne = SalesService.adjustmentOperation.SUBTRACT;
		SalesService.adjustmentOperation pieAdjustmentTwo = SalesService.adjustmentOperation.SUBTRACT;
		SalesService.adjustmentOperation pieAdjustmentThree = SalesService.adjustmentOperation.MULTIPLY;
		SalesService.adjustmentOperation pieAdjustmentFour = SalesService.adjustmentOperation.ADD;
		SalesService.adjustmentOperation pieAdjustmentFive = SalesService.adjustmentOperation.SUBTRACT;
		
		testService.recordSale(saleApple, 10);
		testService.recordSale(salePear, 20);
		testService.recordSale(salePie, 19);
		
		testService.adjustSale(saleApple, appleAdjustmentOne, appleChangeOne);
		testService.adjustSale(salePear, appleAdjustmentTwo, pearChangeOne);
		testService.adjustSale(salePie, appleAdjustmentThree, pieChangeOne);
		testService.adjustSale(saleApple, pearAdjustmentOne, appleChangeTwo);
		testService.adjustSale(salePear, pearAdjustmentTwo, pearChangeTwo);
		testService.adjustSale(salePie, pieAdjustmentOne, pieChangeTwo);
		testService.adjustSale(saleApple, pieAdjustmentTwo, appleChangeThree);
		testService.adjustSale(salePie, pieAdjustmentThree, pieChangeThree);
		testService.adjustSale(salePie, pieAdjustmentFour, pieChangeFour);
		testService.adjustSale(salePie, pieAdjustmentFive, pieChangeFive);
		
		assertEquals(3, testService.getSaleModifications().getModifiedTypes().size());
		assertEquals(3, testService.getSaleModifications().getModificationsForType(typeApple).size());
		assertEquals(2, testService.getSaleModifications().getModificationsForType(typePear).size());
		assertEquals(5, testService.getSaleModifications().getModificationsForType(typePie).size());
		assertEquals(appleAdjustmentOne, 
				testService.getSaleModifications().getModificationsForType(typeApple).get(0).getOperation());
		assertEquals(appleChangeOne, 
				testService.getSaleModifications().getModificationsForType(typeApple).get(0).getValue());
		assertEquals(pearAdjustmentTwo, 
				testService.getSaleModifications().getModificationsForType(typePear).get(1).getOperation());
		assertEquals(pearChangeTwo, 
				testService.getSaleModifications().getModificationsForType(typePear).get(1).getValue());
		assertEquals(pieAdjustmentFour, 
				testService.getSaleModifications().getModificationsForType(typePie).get(3).getOperation());
		assertEquals(pieChangeFour, 
				testService.getSaleModifications().getModificationsForType(typePie).get(3).getValue());
		
		testService.recordSale(saleApple);
		assertEquals(SalesMessagesService.State.PAUSED, testService.getCurrState());
		
	}

}
