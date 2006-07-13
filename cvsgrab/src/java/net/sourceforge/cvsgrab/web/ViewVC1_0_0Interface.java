/*
 * CVSGrab
 * Author: Shinobu Kawai (shinobukawai@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.CVSGrab;


/**
 * Support for ViewVC 1.0.0 interfaces to a cvs repository
 * 
 * @author <a href="shinobukawai@users.sourceforge.net">Shinobu Kawai</a>
 * @version $Revision$ $Date$
 * @created June 8, 2006
 */
public class ViewVC1_0_0Interface extends ViewCvs1_0Interface {

    /**
     * Constructor for ViewCvs1_0Interface
     */
    public ViewVC1_0_0Interface(CVSGrab grabber) {
        super(grabber);
        setFileVersionXpath("TD/A/STRONG");
    }

}
