/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */

package net.sourceforge.cvsgrab;

/**
 * Base class for the exceptions thrown by the detect method on the WebInterface class.
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 3 févr. 2004
 */
public class DetectException extends Exception {

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
