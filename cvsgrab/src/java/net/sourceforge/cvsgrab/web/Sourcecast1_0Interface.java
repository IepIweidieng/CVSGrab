/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */

package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.CVSGrab;

import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;

/**
 * Support for SourceCast 2.0 interfaces to a cvs repository. <p>
 * 
 * SourceCast 1.x is based apparently on CvsWeb 2.0
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 27 déc. 2003
 */
public class Sourcecast1_0Interface extends CvsWeb2_0Interface {

    /**
     * Constructor for Sourcecast1_0Interface
     * 
     */
    public Sourcecast1_0Interface() {
        super();
        setFilesXpath("//TR[TD/A/A/IMG/@alt = '[TXT]']");
        setDirectoriesXpath("//TR[TD/A/A/IMG/@alt = '[DIR]'][TD/A/@name != 'Attic']");
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage
     * @throws Exception
     */
    public void detect(CVSGrab grabber, Document htmlPage) throws Exception {
        checkRootUrl(grabber.getRootUrl());
        
        JXPathContext context = JXPathContext.newContext(htmlPage);
        // Check that this is Sourcecast
        String version = (String) context.getValue("//META[@name = 'SOURCECAST-VERSION']/@content");
        if (!version.startsWith("1.")) {
            throw new Exception("Invalid version " + version);
        }
        setType("SourceCast " + version);
    }
    
}
