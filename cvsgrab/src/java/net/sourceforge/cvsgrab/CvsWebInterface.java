/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */
package net.sourceforge.cvsgrab;

import net.sourceforge.cvsgrab.web.Chora_2_0Interface;
import net.sourceforge.cvsgrab.web.CvsWeb1_0Interface;
import net.sourceforge.cvsgrab.web.CvsWeb2_0Interface;
import net.sourceforge.cvsgrab.web.CvsWeb3_0Interface;
import net.sourceforge.cvsgrab.web.FishEye_1_0Interface;
import net.sourceforge.cvsgrab.web.Sourcecast1_0Interface;
import net.sourceforge.cvsgrab.web.Sourcecast2_0Interface;
import net.sourceforge.cvsgrab.web.Sourcecast3_0Interface;
import net.sourceforge.cvsgrab.web.ViewCvs0_7Interface;
import net.sourceforge.cvsgrab.web.ViewCvs0_8Interface;
import net.sourceforge.cvsgrab.web.ViewCvs0_9Interface;
import net.sourceforge.cvsgrab.web.ViewCvs1_0Interface;

import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Abstracts the web interface available for a CVS repository. This allows us to
 * support different web interfaces such as viewcvs, cvsweb, sourcecast and others.
 *
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 6 oct. 2003
 */
public abstract class CvsWebInterface {

    private static CvsWebInterface[] _webInterfaces = new CvsWebInterface[] {
        new ViewCvs0_7Interface(),
        new ViewCvs0_8Interface(),
        new ViewCvs0_9Interface(),
        new ViewCvs1_0Interface(),
        new Sourcecast1_0Interface(),
        new Sourcecast2_0Interface(),
        new Sourcecast3_0Interface(),
        new CvsWeb1_0Interface(),
        new CvsWeb2_0Interface(),
        new CvsWeb3_0Interface(),
        new Chora_2_0Interface(),
        new FishEye_1_0Interface()
    };

    private static Map documents = new HashMap();

    /**
     * Explicitely select a web interface capable of handle the web pages.
     * @param grabber The cvs grabber
     * @param interfaceId The id of the interface
     * @return the selected web interface, or null if the id is not recognized
     * @throws Exception if initialisation of the web interface fails
     */
    public static final CvsWebInterface getInterface(CVSGrab grabber, String interfaceId) throws Exception {
        for (int i = 0; i < _webInterfaces.length; i++) {
            if (_webInterfaces[i].getId().equals(interfaceId)) {
                _webInterfaces[i].init(grabber);
                return _webInterfaces[i];
            }
        }
        return null;
    }

    /**
     * @return an array containing the ids of the registered web interfaces
     */
    public static final String[] getInterfaceIds() {
        String ids[] = new String[_webInterfaces.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = _webInterfaces[i].getId();
        }
        return ids;
    }

    public static final String[] getBaseUrls(CVSGrab grabber) {
        Set urls = new HashSet();
        for (int i = 0; i < _webInterfaces.length; i++) {
            CvsWebInterface webInterface = _webInterfaces[i];
            urls.add(webInterface.getBaseUrl(grabber));
            urls.add(webInterface.getAltBaseUrl(grabber));
        }
        urls.remove(null);
        String[] listOfUrls = (String[]) urls.toArray(new String[urls.size()]);
        // Sort with the longuest urls first, this can prevent bugs like #988162
        Arrays.sort(listOfUrls, new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = (String) o1;
                String s2 = (String) o2;
                if (s1.length() > s2.length()) {
                    return -1;
                }
                if (s1.length() < s2.length()) {
                    return 1;
                }
                return 0;
            }

        });
        return listOfUrls;
    }

    /**
     * Find the cvs web interface that could have generated this html page
     *
     * @return the cvs web interface that matches best this page
     */
    public static CvsWebInterface findInterface(CVSGrab grabber) throws Exception {
        checkRootUrl(grabber.getRootUrl());
        List errors = new ArrayList();
        // Fast search on know repositories, here we need only to know the url of the web site
        for (int i = 0; i < _webInterfaces.length; i++) {
            CvsWebInterface webInterface = _webInterfaces[i];
            if (webInterface.presetMatch(grabber.getRootUrl(), grabber.getPackagePath())) {
                return webInterface;
            }
        }
        // Deep search, download the web pages and check for markers in the pages
        for (int i = 0; i < _webInterfaces.length; i++) {
            CvsWebInterface webInterface = _webInterfaces[i];
            if (webInterface.validate(grabber, errors)) {
                return webInterface;
            }
        }
        CVSGrab.getLog().info("Tried to connect to the following urls: ");
        for (Iterator i = documents.keySet().iterator(); i.hasNext(); ) {
            CVSGrab.getLog().info(i.next());
        }
        CVSGrab.getLog().info("Problems found during automatic detection: ");
        for (Iterator i = errors.iterator(); i.hasNext();) {
            String msg = (String) i.next();
            CVSGrab.getLog().info(msg);
        }
        return null;
    }

    /**
     * Guess the connection properties by parsing the full url used when connecting to the web repository
     * @param rootUrl the rool url
     * @return the properties compatible with the naming scheme of WebOptions, or an empty list of properties
     * if nothing is found
     */
    public static Properties getWebProperties(String rootUrl) {
        for (int i = 0; i < _webInterfaces.length; i++) {
            CvsWebInterface webInterface = _webInterfaces[i];
            Properties webProperties = webInterface.guessWebProperties(rootUrl);
            if (!webProperties.isEmpty()) {
                return webProperties;
            }
        }
        return new Properties();
    }

    private static Document loadDocument(String url) {
        if (url == null) {
            throw new IllegalArgumentException("Null url");
        }
        Document doc = (Document) documents.get(url);
        if (doc == null) {
            documents.put(url, null);
            try {
                doc = WebBrowser.getInstance().getDocument(new GetMethod(url));
                documents.put(url, doc);
            } catch (Exception ex) {
                // ignore
                CVSGrab.getLog().debug("Error when loading page " + url, ex);
            }
        }
        return doc;
    }

    private static void checkRootUrl(String url) {
        // Sanity check
        // Get the last part of the root url
        int slash = url.indexOf('/', 8);
        if (slash > 0) {
            String path = url.substring(slash);
            String beforeLastPart = "";
            String lastPart = null;
            StringTokenizer st = new StringTokenizer(path, "/", false);
            while (st.hasMoreTokens()) {
                if (lastPart != null) {
                    beforeLastPart += "/" + lastPart;
                }
                lastPart = st.nextToken();
            }
            if (lastPart != null) {
                lastPart = lastPart.toLowerCase();
                if (beforeLastPart.length() > 0 && lastPart.indexOf("cvs") < 0 && lastPart.indexOf(".") < 0
                        && lastPart.indexOf("source") < 0 && lastPart.indexOf("src") < 0
                        && lastPart.indexOf("browse") < 0) {
                    CVSGrab.getLog().warn("The root url " + url + " doesn't seem valid");
                    String newRootUrl = url.substring(0, slash) + beforeLastPart;
                    CVSGrab.getLog().warn("Try " + newRootUrl + " as the root url instead");
                }
            }
        }
    }

    /**
     * For test purposes
     */
    public static void registerDocument(String url, Document doc) {
        documents.put(url, doc);
    }

    private String _versionTag;
    private String _queryParams;

    /**
     * Constructor for CvsWebInterface
     *
     */
    public CvsWebInterface() {
        super();
    }

    /**
     * Returns true if there is a rule that matches this web interface to the given url
     * @param rootUrl The root url
     * @param packagePath The package path
     */
    public boolean presetMatch(String rootUrl, String packagePath) {
        return false;
    }

    /**
     * Validate that this web interface can be used on the remote repository
     * @param grabber The cvs grabber
     * @param errors A list of errors to fill if any error is found
     * @return true if this interface can work on the remote repository
     */
    public boolean validate(CVSGrab grabber, List errors) {
        // Perform no checks on known repositories. Just hope that those web sites don't change too often ;-)
        if (presetMatch(grabber.getRootUrl(), grabber.getPackagePath())) {
            return true;
        }
        Document doc = null;
        String[] urls = new String[] {getBaseUrl(grabber), getAltBaseUrl(grabber)};
        for (int j = 0; j < urls.length; j++) {
            String url = urls[j];
            if (url == null) {
                continue;
            }
            try {
                CVSGrab.getLog().debug(getId() + ": Loading for validation " + url);
                doc = loadDocument(url);
                if (doc == null) {
                    errors.add(getId() + " tried to match page " + url + " but page doesn't exist");
                    continue;
                }

                detect(grabber, doc);
                return true;

            } catch (DetectException ex) {
                // ignore
                CVSGrab.getLog().debug(getId() + " doesn't match, cause is " + ex.toString());
                errors.add(getId() + " tried to match page " + url + " but found error " + ex.getMessage());
            } catch (RuntimeException ex) {
                // ignore
                CVSGrab.getLog().debug(getId() + " doesn't match, cause is " + ex.toString());
                errors.add(getId() + " tried to match page " + url + " but found error " + ex.getMessage());
            }
        }
        return false;
    }

    /**
     * @return the version tag
     */
    public String getVersionTag() {
        return _versionTag;
    }

    /**
     * Sets the version tag
     * @param versionTag
     */
    public void setVersionTag(String versionTag) {
        this._versionTag = versionTag;
    }

    /**
     * @return the queryParams.
     */
    public String getQueryParams() {
        return _queryParams;
    }

    /**
     * Sets the additional query parameters
     * @param params
     */
    public void setQueryParams(String params) {
        _queryParams = params;
    }

    /**
     * Initialize the web interface
     *
     * @param grabber The cvs grabber
     * @throws Exception if initialisation fails
     */
    public abstract void init(CVSGrab grabber) throws Exception;

    /**
     * Detects if the web page is compatible with this web interface, and if yes initialize it.
     *
     * @param grabber The cvs grabber
     * @param htmlPage The web page
     * @throws MarkerNotFoundException if the version marker for the web interface was not found.
     * @throws InvalidVersionException if the version detected is incompatible with the version supported by this web interface.
     * @throws IncompatibleInterfaceException if the web page is not compatible with this type of web interface
     */
    public abstract void detect(CVSGrab grabber, Document htmlPage) throws MarkerNotFoundException, InvalidVersionException;

    /**
     * @return the id identifying the web interface, and used for initialisation
     */
    public abstract String getId();

    /**
     * @return the type of the web interface as detected from the actual website
     */
    public abstract String getType();

    /**
     * @return the base url to use when trying to auto-detect this type of web interface
     */
    public abstract String getBaseUrl(CVSGrab grabber);

    /**
     * @return an alternate base url to use when trying to auto-detect this type of web interface
     */
    public String getAltBaseUrl(CVSGrab grabber) {
        return null;
    }

    /**
     * @param rootUrl
     * @param directoryName
     * @return the url to use to access the contents of the repository
     */
    public abstract String getDirectoryUrl(String rootUrl, String directoryName);

    /**
     * @param doc
     * @return
     */
    public abstract RemoteFile[] getFiles(Document doc);

    /**
     * @param doc
     * @return
     */
    public abstract String[] getDirectories(Document doc);

    /**
     * @param file
     * @return
     */
    public abstract String getDownloadUrl(RemoteFile file);

    /**
     * Guess the web properties frmo the full url
     * @param fullUrl the full url
     * @return a list of Properties guessed from the full url, or an empty property list if no match was possible
     */
    public abstract Properties guessWebProperties(String fullUrl);

}
