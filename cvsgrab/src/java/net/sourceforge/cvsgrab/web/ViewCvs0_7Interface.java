/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.WebBrowser;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.jxpath.JXPathContext;


/**
 * Support for ViewCvs 0.7 interfaces to a cvs repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 11 oct. 2003
 */
public class ViewCvs0_7Interface extends ViewCvsInterface {

    /**
     * Constructor for ViewCvs0_7Interface
     */
    public ViewCvs0_7Interface(CVSGrab grabber) {
        super(grabber);
        
        setFilesXpath("//TR[TD//A/IMG/@alt = '[FILE_ICON]']");
        setFileNameXpath("TD[1]/A/@name");
        setDirectoriesXpath("//TR[TD//A/IMG/@alt = '[DIR_ICON]'][TD/A/@name != 'Attic']");
        setDirectoryXpath("TD[1]/A/@name");
        setCheckoutPath("~checkout~/");
    }

    /**
     * @param rootUrl
     * @param file
     * @return the url to use to access the content
     */
    public String getDownloadUrl(String rootUrl, RemoteFile file) {
        try {
            // http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/org.eclipse.ant.core/about.html?rev=1.20
            String url = WebBrowser.forceFinalSlash(rootUrl);
            String dir = file.getDirectory().getDirectoryPath();
            url += getCheckoutPath();
            url += WebBrowser.forceFinalSlash(quote(dir));
            url += quote(file.getName());
            url = WebBrowser.addQueryParam(url, "rev", quote(file.getVersion()));
            url = WebBrowser.addQueryParam(url, getQueryParams());
            return url;
        } catch (URIException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot create URI");
        }
    }

    protected String getVersionMarker() {
        return "ViewCVS 0.7";
    }

    protected void adjustFile(RemoteFile file, JXPathContext nodeContext) {
        super.adjustFile(file, nodeContext);
        String href = (String) nodeContext.getValue("TD/A/@href");
        if (href.startsWith("Attic/")) {
            file.setInAttic(true);
        } 
    }

}
