/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import org.w3c.dom.Document;


/**
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
     * @param htmlPage
     * @throws Exception
     */
    public void init(String url, Document htmlPage) throws Exception {
        if (url.indexOf("sourceforge.net") > 0 || url.indexOf("sf.net") > 0) {
            checkRootUrl(url);
            setType("ViewCVS 0.8 on Sourceforge");
        } else {
            super.init(url, htmlPage);
        }
    }
    
    protected String getVersionMarker() {
        return "ViewCVS 0.8";
    }

}
