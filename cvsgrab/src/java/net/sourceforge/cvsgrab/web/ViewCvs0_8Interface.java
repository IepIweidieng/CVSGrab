/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;



/**
 * Support for ViewCvs 0.8 interfaces to a cvs repository
 *
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public class ViewCvs0_8Interface extends ViewCvsInterface {

    /**
     * Constructor for ViewCvs0_8Interface
     */
    public ViewCvs0_8Interface() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public boolean presetMatch(String rootUrl, String packagePath) {
        if (rootUrl.indexOf("sourceforge.net") > 0 || rootUrl.indexOf("sf.net") > 0) {
            setType("ViewCVS 0.8 on Sourceforge");
            return true;
        }
        return false;
    }

    protected String getVersionMarker() {
        return "ViewCVS 0.8";
    }

}
