/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.CVSGrab;

import org.w3c.dom.Document;


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
     * @param htmlPage
     * @throws Exception
     */
    public void detect(CVSGrab grabber, Document htmlPage) throws Exception {
        String rootUrl = grabber.getRootUrl();
        if (rootUrl.indexOf("sourceforge.net") > 0 || rootUrl.indexOf("sf.net") > 0) {
            checkRootUrl(grabber.getRootUrl());
            setType("ViewCVS 0.8 on Sourceforge");
        } else {
            super.detect(grabber, htmlPage);
        }
    }
    
    protected String getVersionMarker() {
        return "ViewCVS 0.8";
    }

}
