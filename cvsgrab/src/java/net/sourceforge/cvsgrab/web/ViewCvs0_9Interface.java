/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;


/**
 * Support for ViewCvs 0.9 interfaces to a cvs repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public class ViewCvs0_9Interface extends ViewCvsInterface {

    /**
     * Constructor for ViewCvs0_9Interface
     */
    public ViewCvs0_9Interface() {
        super();
    }

    protected String getVersionMarker() {
        return "ViewCVS 0.9";
    }

}
