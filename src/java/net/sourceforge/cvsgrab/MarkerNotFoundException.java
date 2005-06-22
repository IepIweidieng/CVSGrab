/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */

package net.sourceforge.cvsgrab;

/**
 * Exception throw when the detect ethod on the WebInterface doesn't locate the
 * version marker on the page.
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 3 fevr. 2004
 */
public class MarkerNotFoundException extends DetectException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7312995762444322183L;

	/**
     * Constructor for MarkerNotFoundException
     */
    public MarkerNotFoundException() {
        super();
    }

    /**
     * Constructor for MarkerNotFoundException
     * @param message
     */
    public MarkerNotFoundException(String message) {
        super(message);
    }

}
