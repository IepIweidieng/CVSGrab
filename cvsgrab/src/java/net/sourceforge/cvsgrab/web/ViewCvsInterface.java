/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */
package net.sourceforge.cvsgrab.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.CvsWebInterface;
import net.sourceforge.cvsgrab.InvalidVersionException;
import net.sourceforge.cvsgrab.MarkerNotFoundException;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.WebBrowser;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.w3c.dom.Document;

/**
 * Support for ViewCvs-like interfaces to a cvs repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public abstract class ViewCvsInterface extends CvsWebInterface {

    private String _type;
    private String _filesXpath = "//TR[TD/A/IMG/@alt = '(file)']";
    private String _fileNameXpath = "TD[1]/A/@name";
    private String _fileVersionXpath = "TD[A/IMG/@alt != '(graph)'][2]/A/B";
    private String _directoriesXpath = "//TR[TD/A/IMG/@alt = '(dir)'][TD/A/@name != 'Attic']";
    private String _directoryXpath = "TD[1]/A/@name";
    private String _checkoutPath = "*checkout*/";

    /**
     * Constructor for ViewCvsInterface
     * 
     */
    public ViewCvsInterface() {
        super();
    }
    
    /**
     * Initialize the web interface
     * 
     * @param grabber The cvs grabber
     */
    public void init(CVSGrab grabber) throws Exception {
        _type = getId();
    }
    
    /** 
     * {@inheritDoc}
     * @param htmlPage The web page
     * @throws MarkerNotFoundException if the version marker for the web interface was not found
     * @throws InvalidVersionException if the version detected is incompatible with the version supported by this web interface.
     */
    public void detect(CVSGrab grabber, Document htmlPage) throws MarkerNotFoundException, InvalidVersionException {
        
        JXPathContext context = JXPathContext.newContext(htmlPage);
        Iterator viewCvsTexts = context.iterate("//A[@href]/text()[starts-with(.,'ViewCVS')]");
        _type = null;
        String viewCvsVersion = null;
        while (viewCvsTexts.hasNext()) {
            viewCvsVersion = (String) viewCvsTexts.next(); 
            if (viewCvsVersion.startsWith(getVersionMarker())) {
                _type = viewCvsVersion;
                break;
            } 
        }
        if (_type == null) {
            throw new MarkerNotFoundException("Expected marker " + getVersionMarker() + ", found " + viewCvsVersion);
        }
    }

    /** 
     * {@inheritDoc}
     * @return
     */
    public String getId() {
        String className = getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1);
        className = className.substring(0, className.indexOf("Interface"));
        return className;
    }
    
    /** 
     * {@inheritDoc}
     * @return
     */
    public String getType() {
        return _type;
    }
    
    /**
     * @return the base url to use when trying to auto-detect this type of web interface
     */
    public String getBaseUrl(CVSGrab grabber) {
        String url = WebBrowser.forceFinalSlash(grabber.getRootUrl());
        url += grabber.getPackagePath();
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
            String tag = getVersionTag(); 
            String url = WebBrowser.forceFinalSlash(rootUrl);
            url += WebBrowser.forceFinalSlash(quote(directoryName));
            url = WebBrowser.addQueryParam(url, "only_with_tag", tag);
            url = WebBrowser.addQueryParam(url, getQueryParams());
            return url;
           } catch (URIException ex) {
               ex.printStackTrace();
               throw new RuntimeException("Cannot create URI");
           }
    }
    
    /** 
     * {@inheritDoc}
     * @param htmlPage
     * @return
     */
    public RemoteFile[] getFiles(Document htmlPage) {
        JXPathContext context = JXPathContext.newContext(htmlPage);
        List files = new ArrayList();
        Iterator i = context.iteratePointers(getFilesXpath());
        while (i.hasNext()) {
            Pointer pointer = (Pointer) i.next();
            JXPathContext nodeContext = context.getRelativeContext(pointer);
            String fileName = (String) nodeContext.getValue(getFileNameXpath());
            String version = (String) nodeContext.getValue(getFileVersionXpath());
            RemoteFile file = new RemoteFile(fileName, version);
            adjustFile(file, nodeContext);
            files.add(file);
        }
        return (RemoteFile[]) files.toArray(new RemoteFile[files.size()]);
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage
     * @return
     */
    public String[] getDirectories(Document htmlPage) {
        JXPathContext context = JXPathContext.newContext(htmlPage);
        List directories = new ArrayList();
        Iterator i = context.iteratePointers(getDirectoriesXpath());
        while (i.hasNext()) {
            Pointer pointer = (Pointer) i.next();
            JXPathContext nodeContext = context.getRelativeContext(pointer);
            String dir = (String) nodeContext.getValue(getDirectoryXpath());
            directories.add(dir);
        }
        return (String[]) directories.toArray(new String[directories.size()]);
    }
    
    /**
     * @param file
     * @return
     */
    public String getDownloadUrl(RemoteFile file) {
        try {
            // http://cvs.apache.org/viewcvs.cgi/*checkout*/jakarta-regexp/KEYS?rev=1.1
            String url = WebBrowser.forceFinalSlash(file.getDirectory().getRemoteRepository().getRootUrl());
            String dir = file.getDirectory().getDirectoryPath();
            url += getCheckoutPath();
            url += WebBrowser.forceFinalSlash(quote(dir));
            if (file.isInAttic()) {
                url += "Attic/";
            }
            url += quote(file.getName());
            url = WebBrowser.addQueryParam(url, "rev", file.getVersion());
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
    public String getFilesXpath() {
        return _filesXpath;
    }

    /**
     * @return
     */
    public String getFileNameXpath() {
        return _fileNameXpath;
    }

    /**
     * @return
     */
    public String getFileVersionXpath() {
        return _fileVersionXpath;
    }

    /**
     * @return
     */
    public String getDirectoriesXpath() {
        return _directoriesXpath;
    }

    /**
     * @return
     */
    public String getDirectoryXpath() {
        return _directoryXpath;
    }

    /**
     * @return
     */
    protected String getCheckoutPath() {
        return _checkoutPath;
    }

    /**
     * @param checkoutPath
     */
    protected void setCheckoutPath(String checkoutPath) {
        _checkoutPath = checkoutPath;
    }

    /**
     * @param directoryXpath
     */
    public void setDirectoryXpath(String directoryXpath) {
        _directoryXpath = directoryXpath;
    }

    /**
     * @param directoriesXpath
     */
    public void setDirectoriesXpath(String directoriesXpath) {
        _directoriesXpath = directoriesXpath;
    }

    /**
     * @param fileVersionXpath
     */
    public void setFileVersionXpath(String fileVersionXpath) {
        _fileVersionXpath = fileVersionXpath;
    }

    /**
     * @param fileNameXpath
     */
    public void setFileNameXpath(String fileNameXpath) {
        _fileNameXpath = fileNameXpath;
    }

    /**
     * @param filesXpath
     */
    public void setFilesXpath(String filesXpath) {
        _filesXpath = filesXpath;
    }

    /**
     * @param type
     */
    protected void setType(String type) {
        _type = type;
    }

    protected abstract String getVersionMarker();

    protected void adjustFile(RemoteFile file, JXPathContext nodeContext) {
        String fileName = file.getName();
        if (fileName.startsWith("Attic/")) {
            file.setName(fileName.substring(6));
            file.setInAttic(true);
        } 
    }
    
    /**
     * Python-style of URIUtil.encodePath
     * 
     * @param original The string to quote
     * @return the quoted string
     */
    protected String quote(String original) throws URIException {
        return URIUtil.encodePath(original, "ISO-8859-1");
    }
}
