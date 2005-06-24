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
 * Support for ViewCvs 1.0 interfaces to a cvs repository
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
    public ViewCvs1_0Interface(CVSGrab grabber) {
        super(grabber);
        setFileVersionXpath("TD/A/B");
        setFilesXpath("//TR[TD/A/IMG/@alt = '(file)' or contains(TD/A/IMG/@src, 'text')]");
        //private String _fileVersionXpath = "TD[A/IMG/@alt != '(graph)'][2]/A/B";
        setDirectoriesXpath("//TR[TD/A/IMG/@alt = '(dir)' or contains(TD/A/IMG/@src, 'dir')][TD/A/@name != 'Attic']");
        
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage The web page
     * @throws MarkerNotFoundException if the version marker for the web interface was not found
     * @throws InvalidVersionException if the version detected is incompatible with the version supported by this web interface.
     */
    public void detect(Document htmlPage) throws MarkerNotFoundException, InvalidVersionException {
        super.detect(htmlPage);
        _root = getGrabber().getProjectRoot();
        
        if (_root == null) {
            JXPathContext context = JXPathContext.newContext(htmlPage);
            context.setLenient(true);
            String href = (String) context.getValue("//A/@href[contains(., 'root=')]");
            if (href == null) {
                CVSGrab.getLog().warn("CVS Root not found, there may be issues if ViewCvs is used with multiple repositories");
                CVSGrab.getLog().warn("Use the parameter -cvsRoot <root> to remove this warning");
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
        
        // Check that CVSGrab is not used on projects hosted on a SVN repository
        if (getGrabber().getRootUrl() != null && getGrabber().getRootUrl().startsWith("http://cvs.apache.org/viewcvs.cgi/") && _root != null && _root.indexOf("SVN") >= 0) {
        	throw new InvalidVersionException("CVSGrab cannot be used on projects hosted in a SVN repository");
        }
    }

    /**
     * @return the base url to use when trying to auto-detect this type of web interface
     */
    public String getBaseUrl() {
        String url = WebBrowser.forceFinalSlash(getGrabber().getRootUrl());
        url += getGrabber().getPackagePath();
        url = WebBrowser.addQueryParam(url, getGrabber().getQueryParams());
        if (getGrabber().getProjectRoot() != null) {
            url = WebBrowser.addQueryParam(url, "root", getGrabber().getProjectRoot());
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

    protected void adjustFile(RemoteFile file, JXPathContext nodeContext) {
        super.adjustFile(file, nodeContext);
        String href = (String) nodeContext.getValue("TD[1]/A/@href");
        if (href.indexOf("/Attic/" + file.getName()) >= 0) {
            file.setInAttic(true);
        }
    }
    
}
