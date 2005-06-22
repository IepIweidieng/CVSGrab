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
import net.sourceforge.cvsgrab.WebBrowser;

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
    public ViewCvs0_9Interface(CVSGrab grabber) {
        super(grabber);
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
            getGrabber().getWebOptions().setProjectRoot(_root);
        }
    }

    protected String getVersionMarker() {
        return "ViewCVS 0.9";
    }

    /**
     * @return the alternate base url to use when trying to auto-detect this type of web interface
     */
    public String getAltBaseUrl() {
        String url = WebBrowser.forceFinalSlash(getGrabber().getRootUrl());
        url = WebBrowser.addQueryParam(getCvsrootParam(), getProjectRoot());
        url = WebBrowser.addQueryParam(url, getGrabber().getQueryParams());
        return url;
    }

    /**
     * @return
     */
    public String getProjectRoot() {
    	if (_root == null) {
            _root = getGrabber().getProjectRoot();
    	}
        return _root;
    }

    /**
     * @param root
     */
    public void setProjectRoot(String root) {
        _root = root;
    }

}
