/*
 * Insito J2EE - Copyright 2003-2004 Finance Active
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
 * Support for Chora 2.0 interfaces to a cvs repository
 *
 * @author lclaude
 * @date 30 mars 2004
 */
public class Chora_2_0Interface extends CvsWebInterface {

    private String _type;
    private String _filesXpath = "//TR[TD/A/IMG/@alt = 'File']";
    private String _fileNameXpath = "TD[1]/A/@href";
    private String _fileVersionXpath = "TD[2]/B/A";
    private String _directoriesXpath = "//TR[TD/A/IMG/@alt = 'Directory']";
    private String _directoryXpath = "TD[1]/A/@href";
    private String _browsePath = "cvs.php";
    private String _checkoutPath = "co.php";
    
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
    
    protected String getBrowsePath() {
    	return _browsePath;
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
     * {@inheritDoc}
     */
    public void init(CVSGrab grabber) throws Exception {
    }

    /** 
     * {@inheritDoc}
     */
    public void detect(CVSGrab grabber, Document htmlPage) throws MarkerNotFoundException, InvalidVersionException {
        String rootUrl = WebBrowser.removeFinalSlash(grabber.getRootUrl());
		if (!rootUrl.endsWith(getBrowsePath())) {
        	throw new MarkerNotFoundException("Root url should end with " + getBrowsePath());
        }
        JXPathContext context = JXPathContext.newContext(htmlPage);
        _type = (String) context.getValue("//IMG[contains(@src,'chora.gif')]");

        if (_type == null) {
            throw new MarkerNotFoundException("Expected marker 'chora.gif', found none");
        }
        
        // Version cannot be found exactly 
        _type = "Chora 2.x";
        
        if (grabber.getVersionTag() != null) {
        	throw new InvalidVersionException("Chora 2.0 doesn't support version tags");
        }
    }

    /** 
     * {@inheritDoc}
     */
    public String getId() {
        String className = getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1);
        className = className.substring(0, className.indexOf("Interface"));
        return className;
    }

    /** 
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public String getDirectoryUrl(String rootUrl, String directoryName) {
        try {
            String url = WebBrowser.forceFinalSlash(rootUrl);
            url += WebBrowser.forceFinalSlash(quote(directoryName));
            url = WebBrowser.addQueryParam(url, getQueryParams());
            return url;
        } catch (URIException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot create URI");
        }
    }

    /** 
     * {@inheritDoc}
     */
    public RemoteFile[] getFiles(Document htmlPage) {
        JXPathContext context = JXPathContext.newContext(htmlPage);
        List files = new ArrayList();
        Iterator i = context.iteratePointers(getFilesXpath());
        while (i.hasNext()) {
            Pointer pointer = (Pointer) i.next();
            JXPathContext nodeContext = context.getRelativeContext(pointer);
            String fileName = (String) nodeContext.getValue(getFileNameXpath());
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            String version = (String) nodeContext.getValue(getFileVersionXpath());
            RemoteFile file = new RemoteFile(fileName, version);
            //adjustFile(file, nodeContext);
            files.add(file);
        }
        return (RemoteFile[]) files.toArray(new RemoteFile[files.size()]);
    }

    /** 
     * {@inheritDoc}
     */
    public String[] getDirectories(Document htmlPage) {
        JXPathContext context = JXPathContext.newContext(htmlPage);
        List directories = new ArrayList();
        Iterator i = context.iteratePointers(getDirectoriesXpath());
        while (i.hasNext()) {
            Pointer pointer = (Pointer) i.next();
            JXPathContext nodeContext = context.getRelativeContext(pointer);
            String dir = (String) nodeContext.getValue(getDirectoryXpath());
            dir = WebBrowser.removeFinalSlash(dir);
            dir = dir.substring(dir.lastIndexOf('/') + 1);
            directories.add(dir);
        }
        return (String[]) directories.toArray(new String[directories.size()]);
    }

    /** 
     * {@inheritDoc}
     */
    public String getDownloadUrl(RemoteFile file) {
        try {
            // http://cvs.php.net/co.php/smarty/INSTALL?r=1.12&p=1
            String url = WebBrowser.forceFinalSlash(file.getDirectory().getRemoteRepository().getRootUrl());
            url = url.substring(0, url.length() - getBrowsePath().length() - 1);
            url = WebBrowser.forceFinalSlash(url + getCheckoutPath());
            String dir = file.getDirectory().getDirectoryPath();
            url += WebBrowser.forceFinalSlash(quote(dir));
            //if (file.isInAttic()) {
            //    url += "Attic/";
            //}
            url += quote(file.getName());
            url = WebBrowser.addQueryParam(url, "r", file.getVersion());
            url = WebBrowser.addQueryParam(url, "p", "1");
            url = WebBrowser.addQueryParam(url, getQueryParams());
            return url;
        } catch (URIException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot create URI");
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
