/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.WebBrowser;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.jxpath.JXPathContext;


/**
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public class ViewCvs0_7Interface extends ViewCvsInterface {

    /**
     * Constructor for ViewCvs0_7Interface
     */
    public ViewCvs0_7Interface() {
        super();
        setFilesXpath("//TABLE//TR[TD//A/IMG/@alt = '[FILE_ICON]']");
        setFileNameXpath("TD[1]/A/@name");
        setDirectoriesXpath("//TABLE//TR[TD//A/IMG/@alt = '[DIR_ICON]'][TD/A/@name != 'Attic']");
        setDirectoryXpath("TD[1]/A/@name");
        setCheckoutPath("~checkout~/");
    }

    /**
     * @param rootUrl
     * @param file
     * @return
     */
    public String getDownloadUrl(String rootUrl, RemoteFile file) {
        try {
            // http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/org.eclipse.ant.core/about.html?rev=1.20
            String url = WebBrowser.forceFinalSlash(rootUrl);
            String dir = file.getDirectory().getDirectoryName();
            url += "~checkout~/";
            url += WebBrowser.forceFinalSlash(URIUtil.encodePath(dir));
            url += URIUtil.encodePath(file.getName());
            url += "?rev=" + URIUtil.encodePath(file.getVersion());
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
