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
 * @created on 3 févr. 2004
 */
public class MarkerNotFoundException extends DetectException {

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
