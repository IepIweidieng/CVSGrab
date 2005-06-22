/*
 * Insito J2EE - Copyright 2003-2004 Finance Active
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.InvalidVersionException;
import net.sourceforge.cvsgrab.MarkerNotFoundException;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.WebBrowser;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 * Support for FishEye 0.8, 0.9 and maybe 1.0 interfaces to a cvs repository
 *
 * @author lclaude
 * @date 30 mars 2004
 */
public class FishEye_1_0Interface extends ViewCvsInterface {

    public FishEye_1_0Interface(CVSGrab grabber) {
        super(grabber);
        
        setFilesXpath("//TABLE[@id='fileTable']//TR[@class='ftFileRow' or @class='ftFileRowDeleted']");
        setFileNameXpath("TD/A[@class='ftFileName']/text()");
        setFileVersionXpath("TD/A[@class='ftRev'][@title='view' or @title='history' or @title='download']/text()");
        setDirectoriesXpath("//DIV[@class='dirPane']/UL/LI/SPAN[@class='folder'][A/SPAN/@class='folderPlain']");
        setDirectoryXpath("A/SPAN[@class='folderPlain']/text()");
        setWebInterfaceType("viewrep");
    }

    /**
     * {@inheritDoc}
     */
    public void detect(Document htmlPage) throws MarkerNotFoundException, InvalidVersionException {

        JXPathContext context = JXPathContext.newContext(htmlPage);
        context.setLenient(false);
        context.getValue("//DIV[@id='footer']//A[starts-with(@href,'http://www.cenqua.com/fisheye/')]");
        
        String type = "FishEye"; 

        context.setLenient(true);
        // Search version on FishEye 0.8
        String version = (String) context.getValue("//DIV[@id='footer']/P[A/@href='http://www.cenqua.com/fisheye/']/text()");

        // Search version on FishEye 1.0
        if (version == null) {
        	version = (String) context.getValue("//DIV[@id='footer']/text()[string-length(.) > 0]");
        }
        
        if (version == null) {
            version = "Unknown version";
        }
        setType(type + " " + version);

    }

    /**
     * {@inheritDoc}
     */
    public String getDirectoryUrl(String rootUrl, String directoryName) {
        try {
            String url = WebBrowser.forceFinalSlash(rootUrl);
            if (getVersionTag() != null) {
                url += "~br=" + getVersionTag() + "/";
            }
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
            // http://cvs.codehaus.org/viewrep/~raw,r=1.12.2.1/picocontainer/java/picocontainer/build.xml
            String url = WebBrowser.forceFinalSlash(file.getDirectory().getRemoteRepository().getRootUrl());
            url += "~raw,r=" + file.getVersion();
            url = WebBrowser.forceFinalSlash(url);
            String dir = file.getDirectory().getDirectoryPath();
            url += WebBrowser.forceFinalSlash(quote(dir));
            url += quote(file.getName());
            return url;
        } catch (URIException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot create URI");
        }
    }

    public Properties guessWebProperties(String url) {
        Properties properties = new Properties();
        // Simple heuristic for detecting the type of the web interface
        int keywordPosition = url.toLowerCase().indexOf(getWebInterfaceType());
        if (keywordPosition > 0) {
            String versionTag = null;
            String query = null;
            int rootUrlPosition = url.indexOf('/', keywordPosition) + 1;
            int startPackagePath = rootUrlPosition;
            if ('~' == url.charAt(rootUrlPosition)) {
                startPackagePath = url.indexOf('/', rootUrlPosition) + 1;
                int eqPos = url.indexOf('=', rootUrlPosition);
                if ("br".equals(url.substring(rootUrlPosition + 1, eqPos))) {
                    int endVersionTag = startPackagePath - 1;
                    int commaPos = url.indexOf(',', eqPos);
                    if (commaPos > 0) {
                        endVersionTag = commaPos;
                    }
                    versionTag = url.substring(eqPos + 1, endVersionTag);
                }
            }

            String guessedRootUrl = url.substring(0, rootUrlPosition);
            String guessedPackagePath = url.substring(startPackagePath);

            properties.put(CVSGrab.ROOT_URL_OPTION, guessedRootUrl);
            properties.put(CVSGrab.PACKAGE_PATH_OPTION, guessedPackagePath);
            if (versionTag != null && versionTag.trim().length() > 0) {
                properties.put(CVSGrab.TAG_OPTION, versionTag);
            }
            int queryPos = guessedPackagePath.indexOf('?');
            if (queryPos >= 0) {
                query = guessedPackagePath.substring(queryPos + 1);
                guessedPackagePath = guessedPackagePath.substring(0, queryPos);
            } else {
                query = "hideDeletedFiles=Y";
            }
            properties.put(CVSGrab.ROOT_URL_OPTION, guessedRootUrl);
            properties.put(CVSGrab.PACKAGE_PATH_OPTION, guessedPackagePath);
            if (versionTag != null && versionTag.trim().length() > 0) {
                properties.put(CVSGrab.TAG_OPTION, versionTag);
            }
            if (query != null && query.trim().length() > 0) {
                properties.put(CVSGrab.QUERY_PARAMS_OPTION, query);
            }
        }
        return properties;
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

    /**
     * {@inheritDoc}
     */
    protected String getVersionMarker() {
        return null;
    }

    protected void adjustFile(RemoteFile file, JXPathContext nodeContext) {
        if ("ftFileRowDeleted".equals(nodeContext.getValue("@class"))) {
            file.setInAttic(true);
        }
    }
}
