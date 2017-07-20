/**
 * An exception that will be thrown if a user attempts to record a sale when
 * the service is in the "paused" state.  This will occur as the result of
 * 50 sales being recorded.
 * 
 */

package mjr.techtest;

/**
 * @author mrowe
 *
 */
public class SalesPausedException extends SalesException {
	
	static final long serialVersionUID = 314165;

	public SalesPausedException() {
	}

	public SalesPausedException(String arg0) {
		super(arg0);
	}

	public SalesPausedException(Throwable arg0) {
		super(arg0);
	}

	public SalesPausedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SalesPausedException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
