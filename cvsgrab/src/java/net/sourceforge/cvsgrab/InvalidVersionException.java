/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */

package net.sourceforge.cvsgrab;

/**
 * Exception thrown when the version detected is incompatible with the version
 * supported by the web interface. 
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 3 févr. 2004
 */
public class InvalidVersionException extends DetectException {

    /**
     * Constructor for InvalidVersionException
     * @param message
     */
    public InvalidVersionException(String message) {
        super(message);
    }

}
