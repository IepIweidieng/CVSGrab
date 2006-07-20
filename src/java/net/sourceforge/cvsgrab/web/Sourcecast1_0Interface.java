/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
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
 * SourceCast 1.x is based apparently on CvsWeb 2.0
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 27 dec. 2003
 */
public class Sourcecast1_0Interface extends CvsWeb2_0Interface {

    /**
     * Constructor for Sourcecast1_0Interface
     */
    public Sourcecast1_0Interface(CVSGrab grabber) {
        super(grabber);
        
        setFilesXpath("//TR[TD/A/A/IMG/@alt = '[TXT]']");
        setDirectoriesXpath("//TR[TD/A/A/IMG/@alt = '[DIR]'][TD/A/@name != 'Attic']");
        setWebInterfaceType("browse");
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage The web page
     * @throws MarkerNotFoundException if the version marker for the web interface was not found
     * @throws InvalidVersionException if the version detected is incompatible with the version supported by this web interface.
     */
    public void detect(Document htmlPage) throws MarkerNotFoundException, InvalidVersionException {
        JXPathContext context = JXPathContext.newContext(htmlPage);
        context.setLenient(true);
        
        // Check that this is Sourcecast
        String version = (String) context.getValue("//META[@name = 'SOURCECAST-VERSION']/@content");
        if (version == null) {
            throw new MarkerNotFoundException();
        }
        if (!version.startsWith("1.")) {
            throw new InvalidVersionException("Invalid version " + version);
        }
        setType("SourceCast " + version);
    }
    
}
