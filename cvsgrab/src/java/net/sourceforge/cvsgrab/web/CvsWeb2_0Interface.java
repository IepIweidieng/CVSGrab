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
 * Support for CvsWeb 2.0 interfaces to a cvs repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 7 déc. 2003
 */
public class CvsWeb2_0Interface extends ViewCvsInterface {

    /**
     * Constructor for CvsWeb2_0Interface
     */
    public CvsWeb2_0Interface() {
        super();
        setFilesXpath("//TR[TD/A/IMG/@alt = '[TXT]']");
        setDirectoriesXpath("//TR[TD/A/IMG/@alt = '[DIR]'][TD/A/@name != 'Attic']");
        setCheckoutPath("~checkout~/");
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage
     * @throws Exception
     */
    public void detect(CVSGrab grabber, Document htmlPage) throws Exception {
        checkRootUrl(grabber.getRootUrl());
        
        JXPathContext context = JXPathContext.newContext(htmlPage);
        // Check that this is CvsWeb
        String generator = (String) context.getValue("//META[@name = 'generator']/@content");
        
        if (generator.toLowerCase().indexOf("cvsweb") < 0) {
            throw new Exception("Not CvsWeb");
        }
        if (generator.indexOf(" 2.") < 0) {
            throw new Exception("Version not supported of CvsWeb: " + generator);
        }
        
        setType(generator);
    }
        
    /** 
     * {@inheritDoc}
     * @return
     */
    protected String getVersionMarker() {
        return null;
    }

}
