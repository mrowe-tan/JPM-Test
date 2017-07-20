/**
 * An exception to indicate an error that has occurred during the recording
 * of a sale.
 * 
 * Note that at present this adds no additional capabilities, but is provided
 * for future extension purposes, and to denote the particular origin of a 
 * sales error.
 */

package mjr.techtest;

public class SalesException extends Exception {
	
	static final long serialVersionUID = 3142; 

	public SalesException() {
	}

	public SalesException(String arg0) {
		super(arg0);
	}

	public SalesException(Throwable arg0) {
		super(arg0);
	}

	public SalesException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SalesException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
