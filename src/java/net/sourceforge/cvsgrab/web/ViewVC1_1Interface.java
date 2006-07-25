/*
 * CVSGrab
 * Author: Shinobu Kawai (shinobukawai@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import java.util.Iterator;

import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.InvalidVersionException;
import net.sourceforge.cvsgrab.MarkerNotFoundException;

import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;


/**
 * Support for ViewVC 1.1 interfaces to a cvs repository
 * 
 * @author <a href="mailto:shinobukawai@users.sourceforge.net">Shinobu Kawai</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created July 24, 2006
 */
public class ViewVC1_1Interface extends ViewVC1_0Interface {

    /**
     * Constructor for ViewCvs1_1Interface
     */
    public ViewVC1_1Interface(CVSGrab grabber) {
        super(grabber);
        setCheckoutPath("");
    }
    /** 
     * {@inheritDoc}
     * @param htmlPage The web page
     * @throws MarkerNotFoundException if the version marker for the web interface was not found
     * @throws InvalidVersionException if the version detected is incompatible with the version supported by this web interface.
     */
    public void detect(Document htmlPage) throws MarkerNotFoundException, InvalidVersionException {
        JXPathContext context = JXPathContext.newContext(htmlPage);
        Iterator viewCvsTexts = context.iterate("//META[@name = 'generator']/@content[starts-with(.,'ViewVC')] | //A[@href]/text()[starts-with(.,'ViewVC')]");
        setType(null);
        String viewCvsVersion = null;
        while (viewCvsTexts.hasNext()) {
            viewCvsVersion = (String) viewCvsTexts.next();
            if (viewCvsVersion.startsWith(getVersionMarker())) {
                setType(viewCvsVersion);
                break;
            }
        }
        if (getType() == null) {
            throw new MarkerNotFoundException("Expected marker " + getVersionMarker() + ", found " + viewCvsVersion);
        }
    }

    protected String getVersionMarker() {
        return "ViewVC 1.1";
    }

}
