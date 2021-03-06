/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */

package net.sourceforge.cvsgrab;

/**
 * Base class for the exceptions thrown by the detect method on the WebInterface class.
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 3 f�vr. 2004
 */
public class DetectException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2152735201329285610L;

	/**
     * Constructor for DetectException
     */
    public DetectException() {
        super();
    }

    /**
     * Constructor for DetectException
     * @param message
     */
    public DetectException(String message) {
        super(message);
    }

    /**
     * Constructor for DetectException
     * @param cause
     */
    public DetectException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor for DetectException
     * @param message
     * @param cause
     */
    public DetectException(String message, Throwable cause) {
        super(message, cause);
    }

}
