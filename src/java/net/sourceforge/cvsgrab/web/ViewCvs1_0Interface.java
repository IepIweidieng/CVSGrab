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
import org.w3c.dom.Document;


/**
 * Support for ViewCvs 0.1 interfaces to a cvs repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public class ViewCvs1_0Interface extends ViewCvsInterface {

    private String _root;
    
    /**
     * Constructor for ViewCvs1_0Interface
     */
    public ViewCvs1_0Interface() {
        super();
        setFileVersionXpath("TD/A/B");
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage
     * @throws Exception
     */
    public void detect(CVSGrab grabber, Document htmlPage) throws Exception {
        String rootUrl = grabber.getRootUrl();
        if (rootUrl.indexOf("savannah.gnu.org") > 0) {
            checkRootUrl(grabber.getRootUrl());
            setType("ViewCVS 1.0 on Savannah GNU");
        } else {
            super.detect(grabber, htmlPage);
        }
        JXPathContext context = JXPathContext.newContext(htmlPage);
        String href = (String) context.getValue("//A/@href[contains(., 'root=')]");
        if (href == null) {
            CVSGrab.getLog().info("CVS Root not found, there may be issues if ViewCvs is used with multiple repositories");
        } else {
            _root = href.substring(href.indexOf("root=")+ 5);
            if (_root.indexOf('#') > 0) {
                _root = _root.substring(0, _root.indexOf('#'));
            }
            if (_root.indexOf('&') > 0) {
                _root = _root.substring(0, _root.indexOf('&'));
            }
        }
    }

    /**
     * @param rootUrl
     * @param directoryName
     * @return the url to use to access the contents of the repository
     */
    public String getDirectoryUrl(String rootUrl, String directoryName) {
        try {
            String url = super.getDirectoryUrl(rootUrl, directoryName);
            if (_root != null) {
                url = WebBrowser.addQueryParam(url, "root", quote(_root));
            }
            url = WebBrowser.addQueryParam(url, getQueryParams());
            return url;
        } catch (URIException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot create URI");
        }
    }
    
    /**
     * @param file
     * @return
     */
    public String getDownloadUrl( RemoteFile file) {
        try {
            // http://cvs.apache.org/viewcvs.cgi/*checkout*/jakarta-regexp/KEYS?rev=1.1
            String url = super.getDownloadUrl(file);
            if (_root != null) {
                url = WebBrowser.addQueryParam(url, "root", quote(_root));
            }
            url = WebBrowser.addQueryParam(url, getQueryParams());
            return url;
        } catch (URIException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot create URI");
        }
    }

    /**
     * @return
     */
    public String getRoot() {
        return _root;
    }

    /**
     * @param root
     */
    public void setRoot(String root) {
        _root = root;
    }

    protected String getVersionMarker() {
        return "ViewCVS 1.0";
    }

}
