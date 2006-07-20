/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */

package net.sourceforge.cvsgrab;

/**
 * Exception thrown when the version detected is incompatible with the version
 * supported by the web interface. 
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 3 fevr. 2004
 */
public class InvalidVersionException extends DetectException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6577897713989472823L;

	/**
     * Constructor for InvalidVersionException
     * @param message
     */
    public InvalidVersionException(String message) {
        super(message);
    }

}
