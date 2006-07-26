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
 * Support for SourceCast 3.0 interfaces to a cvs repository. <p>
 *
 * Sourcecast 3.0 uses internally ViewCVS 0.9
 *
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 12 oct. 2003
 */
public class Sourcecast3_0Interface extends ViewCvsInterface {

    /**
     * Constructor for Sourcecast3_0Interface
     */
    public Sourcecast3_0Interface(CVSGrab grabber) {
        super(grabber);
        
        setFilesXpath("//TABLE[@class = 'filebrowse']//TD/DIV[@class = 'leaf']");
        setFileNameXpath("@id");
        setFileVersionXpath("../following::node()/text()");
        setDirectoriesXpath("//TABLE[@class = 'filebrowse']//TD/DIV[@class = 'leafnode'][@id != 'Attic']");
        setDirectoryXpath("@id");
        setWebInterfaceType("browse");
    }

    /**
     * {@inheritDoc}
     */
    public boolean presetMatch(String rootUrl, String packagePath) {
        if (rootUrl.indexOf("dev.java.net") > 0) {
            setType("SourceCast 3.0 on dev.java.net");
            return true;
        }
        return false;
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

        // Check that this is Sourcecast/CollabNet
        String keywords = (String) context.getValue("//META[@name = 'keywords']/@content");
        String description = (String) context.getValue("//META[@name = 'description']/@content");
        String version = (String) context.getValue("//META[@name = 'version']/@content");
        if (version == null) {
        	version = (String) context.getValue("//META[@name = 'SourceCastVersion']/@content");
        }
        if (version == null) {
            throw new InvalidVersionException("Invalid version " + version);
        }

        boolean sourcecastKeyword = false;
        boolean collabnetKeyword = false;
        boolean poweredByCollabnet = false;
        if (keywords != null) {
            sourcecastKeyword = (keywords.toLowerCase().indexOf("sourcecast") >= 0);
            collabnetKeyword = (keywords.toLowerCase().indexOf("collabnet") >= 0);
        }
        if (description != null) {
            sourcecastKeyword |= (description.toLowerCase().indexOf("sourcecast") >= 0);
            collabnetKeyword |= (description.toLowerCase().indexOf("collabnet") >= 0);
        }
        if (!sourcecastKeyword && !collabnetKeyword && version != null) {
        	// Try to scan the Powered by buttons
        	poweredByCollabnet = context.getValue("//A[contains(@href, 'www.collab.net')][IMG/@id='poweredby']") != null;        	
        }
        if (!sourcecastKeyword && !collabnetKeyword && !poweredByCollabnet) {
            CVSGrab.getLog().warn("Might not be SourceCast/CollabNet, meta keywords was '" + keywords + "', description was '" + description + "'");
        }
        if (version != null && version.startsWith("2.")) {
            // Only 2.6+ generated an html similar to version 3.0
            int minorVersion = Integer.parseInt(version.substring(2, version.indexOf('.', 2)));
            if (minorVersion < 6) {
                throw new InvalidVersionException("Version 2.5 and earlier are not supported");
            }
        } else if (version == null || !version.startsWith("3.")) {
            throw new InvalidVersionException("Invalid version " + version);
        }
        setType("SourceCast " + version);
    }

    /**
     * {@inheritDoc}
     */
    protected String getVersionMarker() {
        return null;
    }

}
