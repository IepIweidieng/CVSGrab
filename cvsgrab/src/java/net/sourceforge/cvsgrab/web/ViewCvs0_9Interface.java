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
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.WebBrowser;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;


/**
 * Support for ViewCvs 0.9 interfaces to a cvs repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public class ViewCvs0_9Interface extends ViewCvsInterface {

    private String _root;
    
    /**
     * Constructor for ViewCvs0_9Interface
     */
    public ViewCvs0_9Interface() {
        super();
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage The web page
     * @throws MarkerNotFoundException if the version marker for the web interface was not found
     * @throws InvalidVersionException if the version detected is incompatible with the version supported by this web interface.
     */
    public void detect(CVSGrab grabber, Document htmlPage) throws MarkerNotFoundException, InvalidVersionException {
        super.detect(grabber, htmlPage);
        
        _root = grabber.getProjectRoot();
        if (_root == null) {
            JXPathContext context = JXPathContext.newContext(htmlPage);
            context.setLenient(true);
            String href = (String) context.getValue("//A/@href[contains(., 'cvsroot=')]");
            if (href != null) {
                _root = href.substring(href.indexOf("cvsroot=") + 8);
                if (_root.indexOf('#') > 0) {
                    _root = _root.substring(0, _root.indexOf('#'));
                }
                if (_root.indexOf('&') > 0) {
                    _root = _root.substring(0, _root.indexOf('&'));
                }
            }
        }
    }

    protected String getVersionMarker() {
        return "ViewCVS 0.9";
    }

    /**
     * @return the base url to use when trying to auto-detect this type of web interface
     */
    public String getBaseUrl(CVSGrab grabber) {
        String url = WebBrowser.forceFinalSlash(grabber.getRootUrl());
        url += grabber.getPackagePath();
        url = WebBrowser.addQueryParam(url, grabber.getQueryParams());
        if (grabber.getProjectRoot() != null) {
            url = WebBrowser.addQueryParam(url, "cvsroot", grabber.getProjectRoot());
        }
        return url;
    }
    
    /**
     * @return the alternate base url to use when trying to auto-detect this type of web interface
     */
    public String getAltBaseUrl(CVSGrab grabber) {
        String url = WebBrowser.forceFinalSlash(grabber.getRootUrl());
        url = WebBrowser.addQueryParam(url, grabber.getQueryParams());
        return url;
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
                url = WebBrowser.addQueryParam(url, "cvsroot", quote(_root));
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
                url = WebBrowser.addQueryParam(url, "cvsroot", quote(_root));
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

}
