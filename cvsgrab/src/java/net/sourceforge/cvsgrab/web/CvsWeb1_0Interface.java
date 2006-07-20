/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
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
 * Support for CvsWeb 1.0 interfaces to a cvs repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 7 dec. 2003
 */
public class CvsWeb1_0Interface extends ViewCvsInterface {

    private String _root;
    
    /**
     * Constructor for CvsWeb1_0Interface
     */
    public CvsWeb1_0Interface(CVSGrab grabber) {
        super(grabber);
        
        setFilesXpath("//TR[TD//IMG/@alt = '[TXT]']");
        setDirectoriesXpath("//TR[TD//IMG/@alt = '[DIR]'][TD/A/@name != 'Attic']");
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
        String generator = (String) context.getValue("//comment()[starts-with(normalize-space(.),'hennerik CVSweb')]");
        
        if (generator == null) {
            throw new MarkerNotFoundException("Not CvsWeb, did not found comment containing 'hennerik CVSweb'");
        }
        
        setType(generator);
        
        _root = getGrabber().getProjectRoot();
        if (_root == null) {
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
        
    /** 
     * {@inheritDoc}
     */
    protected String getVersionMarker() {
        return null;
    }
    
    /**
     * @return the base url to use when trying to auto-detect this type of web interface
     */
    public String getBaseUrl() {
        String url = WebBrowser.forceFinalSlash(getGrabber().getRootUrl());
        url += getGrabber().getPackagePath();
        url = WebBrowser.addQueryParam(url, getGrabber().getQueryParams());
        if (getGrabber().getProjectRoot() != null) {
            url = WebBrowser.addQueryParam(url, "cvsroot", getGrabber().getProjectRoot());
        }
        return url;
    }
    
    /**
     * @return the alternate base url to use when trying to auto-detect this type of web interface
     */
    public String getAltBaseUrl() {
        String url = WebBrowser.forceFinalSlash(getGrabber().getRootUrl());
        url = WebBrowser.addQueryParam(url, getGrabber().getQueryParams());
        return url;
    }

    /**
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
    
    public String getDownloadUrl( RemoteFile file) {
        try {
            // http://cvs.hispalinux.es/cgi-bin/cvsweb/~checkout~/mono/README?rev=1.26&content-type=text/plain&cvsroot=mono
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

    public String getRoot() {
        return _root;
    }

    public void setRoot(String root) {
        _root = root;
    }

}
