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
 * Support for CvsWeb 3.0 interfaces to a cvs repository
 *
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 7 déc. 2003
 */
public class CvsWeb3_0Interface extends ViewCvsInterface {

    /**
     * Constructor for CvsWeb3_0Interface
     */
    public CvsWeb3_0Interface(CVSGrab grabber) {
        super(grabber);
        
        setFilesXpath("//TR/TD[@class = 'file']");
        setFileNameXpath("A[2]/text()");
        setFileVersionXpath("./following::node()/A/text()");
        setDirectoriesXpath("//TR/TD[@class = 'dir'][A/@name != 'Attic' and A/@href !='../']");
        setDirectoryXpath("A/@name");
        setCheckoutPath("~checkout~/");
        setWebInterfaceType("cvsweb");
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
        // Check that this is CvsWeb
        String generator = (String) context.getValue("//META[@name = 'generator']/@content");

        if (generator == null) {
            generator = (String) context.getValue("//comment()[starts-with(normalize-space(.),'FreeBSD-cvsweb')]");
        }

        if (generator == null || generator.toLowerCase().indexOf("cvsweb") < 0) {
            throw new MarkerNotFoundException("Not CvsWeb, found marker " + generator);
        }
        if (generator.indexOf(" 3.") < 0) {
            throw new InvalidVersionException("Version not supported of CvsWeb: " + generator);
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
