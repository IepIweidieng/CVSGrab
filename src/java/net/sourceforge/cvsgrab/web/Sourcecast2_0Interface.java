/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;

/**
 * Sourcecast uses internally ViewCVS
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 12 oct. 2003
 */
public class Sourcecast2_0Interface extends ViewCvsInterface {

    /**
     * Constructor for Sourcecast2_0Interface
     */
    public Sourcecast2_0Interface() {
        super();
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage
     * @throws Exception
     */
    public void init(String url, Document htmlPage) throws Exception {
        checkRootUrl(url);
        
        JXPathContext context = JXPathContext.newContext(htmlPage);
        // Check that this is Sourcecast
        String keywords = (String) context.getValue("//META[@name = 'keywords']/@content");
        String version = (String) context.getValue("//META[@name = 'version']/@content");
        
        if (keywords.indexOf("SourceCast") < 0) {
            throw new Exception("Not SourceCast");
        }
        if (!version.startsWith("2.")) {
            throw new Exception("Invalid version " + version);
        }
        setType("SourceCast " + version);
    }


    /** 
     * {@inheritDoc}
     * @return
     */
    protected String getVersionMarker() {
        return null;
    }

}
