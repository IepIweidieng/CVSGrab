/*
 * CVSGrab
 * Author: Shinobu Kawai (shinobukawai@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import java.util.Iterator;
import java.util.Properties;

import net.sourceforge.cvsgrab.CVSGrab;
import net.sourceforge.cvsgrab.InvalidVersionException;
import net.sourceforge.cvsgrab.MarkerNotFoundException;
import net.sourceforge.cvsgrab.RemoteFile;
import net.sourceforge.cvsgrab.WebBrowser;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;


/**
 * Support for ViewVC 1.0 interfaces to a cvs repository
 * 
 * @author <a href="mailto:shinobukawai@users.sourceforge.net">Shinobu Kawai</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created June 8, 2006
 */
public class ViewVC1_0Interface extends ViewCvsInterface {

    private String _root;
    /**
     * Constructor for ViewCvs1_0Interface
     */
    public ViewVC1_0Interface(CVSGrab grabber) {
        super(grabber);
        setWebInterfaceType("viewvc");
        setFileVersionXpath("TD/A/STRONG");
        setFilesXpath("//TR[TD/A/IMG/@alt = '(file)' or contains(TD/A/IMG/@src, 'text')]");
        setDirectoriesXpath("//TR[TD/A/IMG/@alt = '(dir)' or contains(TD/A/IMG/@src, 'dir')][TD/A/@name != 'Attic']");
        setTagParam("pathrev");
    }

    /** 
     * {@inheritDoc}
     * @param htmlPage The web page
     * @throws MarkerNotFoundException if the version marker for the web interface was not found
     * @throws InvalidVersionException if the version detected is incompatible with the version supported by this web interface.
     */
    public void detect(Document htmlPage) throws MarkerNotFoundException, InvalidVersionException {
        JXPathContext context = JXPathContext.newContext(htmlPage);
        Iterator viewCvsTexts = context.iterate("//META[@name = 'generator']/@content[starts-with(.,'ViewVC')] | //A[@href]/text()[starts-with(.,'ViewVC')]");
        setType(null);
        String viewCvsVersion = null;
        while (viewCvsTexts.hasNext()) {
            viewCvsVersion = (String) viewCvsTexts.next();
            if (viewCvsVersion.startsWith(getVersionMarker())) {
                setType(viewCvsVersion);
                break;
            }
        }
        if (getType() == null) {
            throw new MarkerNotFoundException("Expected marker " + getVersionMarker() + ", found " + viewCvsVersion);
        }

        if (_root == null) {
            context.setLenient(true);
            String strong = (String) context.getValue("//STRONG");
            if (strong == null) {
                CVSGrab.getLog().info("CVS Root not found, there may be issues if ViewCvs is used with multiple repositories");
                CVSGrab.getLog().info("Use the parameter -projectRoot <root> to remove this warning");
            } else {
                int start = strong.indexOf('[') + 1;
                int end = strong.indexOf(']');
                _root = strong.substring(start, end);
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
            String tag = getVersionTag();
            String url = WebBrowser.forceFinalSlash(rootUrl);
            if (getRoot() != null) {
                url += WebBrowser.forceFinalSlash(quote(getRoot()));
            }
            url += WebBrowser.forceFinalSlash(quote(directoryName));
            url = WebBrowser.addQueryParam(url, getTagParam(), tag);
            url = WebBrowser.addQueryParam(url, getQueryParams());
            return url;
        } catch (URIException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot create URI");
        }
    }

    public String getDownloadUrl(RemoteFile file) {
        try {
            // http://cvs.apache.org/viewcvs.cgi/*checkout*/jakarta-regexp/KEYS?rev=1.1
            String url = WebBrowser.forceFinalSlash(file.getDirectory().getRemoteRepository().getRootUrl());
            String dir = file.getDirectory().getDirectoryPath();
            url += getCheckoutPath();
            if (getRoot() != null) {
                url += WebBrowser.forceFinalSlash(quote(getRoot()));
            }
            url += WebBrowser.forceFinalSlash(quote(dir));
            url += quote(file.getName());
            url = WebBrowser.addQueryParam(url, "revision", file.getVersion());
            url = WebBrowser.addQueryParam(url, getQueryParams());
            return url;
        } catch (URIException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot create URI");
        }
    }

    public Properties guessWebProperties(String url) {
        if (url.toLowerCase().indexOf(".cvs.sourceforge.net") > 0) {
            return guessSourceforgeWebProperties(url);
        }

        return guessGenericWebProperties(url);
    }

    protected Properties guessGenericWebProperties(String url) {
        return super.guessWebProperties(url);
    }

    private Properties guessSourceforgeWebProperties(String url) {
        int endOfHostPosition = url.toLowerCase().indexOf(".cvs.sourceforge.net") + ".cvs.sourceforge.net".length();
        
        int rootUrlPosition = url.indexOf('/', endOfHostPosition) + 1;
        int projectRootEndPosition = url.indexOf('/', rootUrlPosition);
        int nextSlashPos = projectRootEndPosition + 1;

        String guessedRootUrl = url.substring(0, rootUrlPosition);
        String guessedProjectPath = url.substring(rootUrlPosition, projectRootEndPosition);
        String guessedPackagePath = url.substring(nextSlashPos);
        String versionTag = null;
        String query = null;
        int queryPos = guessedPackagePath.indexOf('?');
        if (queryPos >= 0) {
            query = guessedPackagePath.substring(queryPos + 1);
            guessedPackagePath = guessedPackagePath.substring(0, queryPos);
            Properties queryItems = WebBrowser.getQueryParams(query);
            versionTag = (String) queryItems.remove(getTagParam());
            query = WebBrowser.toQueryParams(queryItems);
        }

        Properties properties = new Properties();
        properties.put(CVSGrab.ROOT_URL_OPTION, guessedRootUrl);
        properties.put(CVSGrab.PROJECT_ROOT_OPTION, guessedProjectPath);
        properties.put(CVSGrab.PACKAGE_PATH_OPTION, guessedPackagePath);
        if (versionTag != null && versionTag.trim().length() > 0) {
            properties.put(CVSGrab.TAG_OPTION, versionTag);
        }
        if (query != null && query.trim().length() > 0) {
            properties.put(CVSGrab.QUERY_PARAMS_OPTION, query);
        }
        
        return properties;
    }

    public String getRoot() {
        if (_root == null) {
            setRoot(getGrabber().getProjectRoot());
        }
        return _root;
    }

    /**
     * @param root
     */
    public void setRoot(String root) {
        _root = root;
    }

    protected String getVersionMarker() {
        return "ViewVC 1.0";
    }
}
