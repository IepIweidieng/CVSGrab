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
 * Support for CvsWeb 1.0 interfaces to a cvs repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 7 d�c. 2003
 */
public class CvsWeb1_0Interface extends ViewCvsInterface {

    /**
     * Constructor for CvsWeb1_0Interface
     */
    public CvsWeb1_0Interface() {
        super();
        setFilesXpath("//TR[TD/A/IMG/@alt = '[TXT]']");
        setDirectoriesXpath("//TR[TD/A/IMG/@alt = '[DIR]'][TD/A/@name != 'Attic']");
        setCheckoutPath("~checkout~/");
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
        // Check that this is CvsWeb
        String generator = (String) context.getValue("//comment()[starts-with(normalize-space(.),'hennerik CVSweb')]");
        
        if (generator == null) {
            throw new MarkerNotFoundException("Not CvsWeb, found marker " + generator);
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
