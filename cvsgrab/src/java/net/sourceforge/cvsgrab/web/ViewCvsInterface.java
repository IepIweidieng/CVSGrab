/*
 * Created on 11 oct. 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.sourceforge.cvsgrab.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.cvsgrab.CvsWebInterface;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.WebBrowser;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public abstract class ViewCvsInterface extends CvsWebInterface {

    private String _type;
    private String _filesXpath = "//TABLE//TR[TD/A/IMG/@alt = '(file)']";
    private String _fileNameXpath = "TD[1]/A/@name";
    private String _fileVersionXpath = "TD[2]/A/B";
    private String _directoriesXpath = "//TABLE//TR[TD/A/IMG/@alt = '(dir)'][TD/A/@name != 'Attic']";
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
     * {@inheritDoc}
     * @param htmlPage
     * @throws Exception
     */
    public void init(Document htmlPage) throws Exception {
        JXPathContext context = JXPathContext.newContext(htmlPage);
        Iterator viewCvsTexts = context.iterate("//A[@href]/text()[starts-with(.,'ViewCVS')]");
        _type = null;
        while (viewCvsTexts.hasNext()) {
            String viewCvsVersion = (String) viewCvsTexts.next(); 
            if (viewCvsVersion.startsWith(getVersionMarker())) {
                _type = viewCvsVersion;
                break;
            } 
        }
        if (_type == null) {
            throw new Exception("Invalid version");
        }
    }

    /** 
     * {@inheritDoc}
     * @return
     */
    public String getType() {
        return _type;
    }
    
    /**
     * @param rootUrl
     * @param directoryName
     * @return the url to use to access the contents of the repository
     */
    public String getDirectoryUrl(String rootUrl, String directoryName) {
        String tag = getVersionTag(); 
        String url = WebBrowser.forceFinalSlash(rootUrl);
        url += WebBrowser.forceFinalSlash(directoryName);
        if (tag != null && !tag.equals("")) {
            url += "?only_with_tag=" + tag;
        }
        return url;
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
            String dir = file.getDirectory().getDirectoryName();
            url += getCheckoutPath();
            url += WebBrowser.forceFinalSlash(URIUtil.encodePath(dir));
            if (file.isInAttic()) {
                url += "Attic/";
            }
            url += URIUtil.encodePath(file.getName());
            url += "?rev=" + URIUtil.encodePath(file.getVersion());
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
        boolean inAttic = false;
        String fileName = file.getName();
        if (fileName.startsWith("Attic/")) {
            file.setName(fileName.substring(6));
            file.setInAttic(true);
        } 
    }
    
}
