/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.InvalidVersionException;
import net.sourceforge.cvsgrab.MarkerNotFoundException;

import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;

/**
 * Support for SourceCast 2.0 interfaces to a cvs repository. <p>
 *  
 * Sourcecast 2.0 uses internally ViewCVS 0.9
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
        setWebInterfaceType("browse");
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage The web page
     * @throws MarkerNotFoundException if the version marker for the web interface was not found
     * @throws InvalidVersionException if the version detected is incompatible with the version supported by this web interface.
     */
    public void detect(CVSGrab grabber, Document htmlPage) throws MarkerNotFoundException, InvalidVersionException {
        JXPathContext context = JXPathContext.newContext(htmlPage);
        context.setLenient(true);
        
        // Check that this is Sourcecast
        String keywords = (String) context.getValue("//META[@name = 'keywords']/@content");
        String version = (String) context.getValue("//META[@name = 'version']/@content");
        
        if (keywords == null || keywords.indexOf("SourceCast") < 0) {
            throw new MarkerNotFoundException("Not SourceCast, meta keywords was " + keywords);
        }
        if (!version.startsWith("2.")) {
            throw new InvalidVersionException("Invalid version " + version);
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
