/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */
package net.sourceforge.cvsgrab;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import net.sourceforge.cvsgrab.util.PasswordField;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Emulates a web browser
 *
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public class WebBrowser {

    private static WebBrowser _instance = new WebBrowser();

    private HttpClient _client;
    private DOMParser _htmlParser;

    /**
     * @return the singleton instance
     */
    public static WebBrowser getInstance() {
        return _instance;
    }

    public static String forceFinalSlash(String s) {
        if (!s.endsWith("/")) {
            return s + "/";
        }
        return s;
    }

    public static String removeFinalSlash(String s) {
        if (s.endsWith("/")) {
            return s.substring(0, s.length()-1);
        }
        return s;
    }

    public static String addQueryParam(String url, String queryParam) {
        String newUrl = url;
        if (queryParam != null) {
            if (newUrl.indexOf('?') > 0) {
                newUrl += "&";
            } else {
                newUrl += "?";
            }
            newUrl += queryParam;
        }
        return newUrl;
    }

    public static String addQueryParam(String url, String paramName, String paramValue) {
        String newUrl = url;
        if (paramName != null && paramValue != null) {
            if (newUrl.indexOf('?') > 0) {
                newUrl += "&";
            } else {
                newUrl += "?";
            }
            try {
                newUrl += paramName + "=" + URIUtil.encodeQuery(paramValue);
            } catch (URIException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot encode parameter value " + paramValue);
            }
        }
        return newUrl;
    }

    /**
     * Extract the query parameters
     * @param urlQuery The query section of the url. Must be of the form ? (optional) key1=value1&key2=value2
     * @return the parameters extracted as properties
     */
    public static Properties getQueryParams(String urlQuery) {
        Properties p = new Properties();
        StringTokenizer st = new StringTokenizer(urlQuery, "?&;");
        while (st.hasMoreTokens()) {
            String part = st.nextToken();
            String key = part.substring(0, part.indexOf('='));
            String value = part.substring(part.indexOf('=') + 1);
            p.put(key, value);
        }
        return p;
    }

    /**
     * Converts the query items to a single query string
     * @param queryItems The set of (key,value) for the query
     * @return a query string compatible with the format of url queries
     */
    public static String toQueryParams(Properties queryItems) {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = queryItems.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String value = queryItems.getProperty(key);
            sb.append(key);
            sb.append('=');
            sb.append(value);
            if (i.hasNext()) {
                sb.append('&');
            }
        }
        return sb.toString();
    }

    /**
     * Constructor for WebBrowser
     */
    public WebBrowser() {
        super();
        CookiePolicy.setDefaultPolicy(CookiePolicy.COMPATIBILITY);
        _client = new HttpClient();
        _client.setConnectionTimeout(5000);
        _htmlParser = new DOMParser(new HTMLConfiguration());
        try {
            _htmlParser.setProperty("http://cyberneko.org/html/properties/names/elems", "upper");
            _htmlParser.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
            _htmlParser.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
            _htmlParser.setFeature("http://cyberneko.org/html/features/scanner/notify-builtin-refs", true);
            _htmlParser.setFeature("http://xml.org/sax/features/namespaces", false);
        } catch (SAXNotRecognizedException e) {
            e.printStackTrace();
        } catch (SAXNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use a proxy to bypass the firewall
     *
     * @param proxyHost Host of the proxy
     * @param proxyPort Port of the proxy
     * @param proxyNTDomain NT domain for authentification on a MS proxy
     * @param userName Username (if authentification is required), or null
     * @param password Password (if authentification is required), or null
     */
    public void useProxy(String proxyHost, int proxyPort, final String ntDomain, final String userName, String password) {
        CVSGrab.getLog().info("Using proxy " + proxyHost + ":" + proxyPort);
        _client.getHostConfiguration().setProxy(proxyHost, proxyPort);
        if (userName != null) {
            if (password == null ) {
                PasswordField pwdField = new PasswordField();
                try {
                    password = pwdField.getPassword("Enter the password for the proxy: ");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (ntDomain == null) {
                CVSGrab.getLog().info("Login on the proxy with user name " + userName);
                _client.getState().setProxyCredentials(null, proxyHost,
                    new UsernamePasswordCredentials(userName, password));
            } else {
                try {
                    String host = InetAddress.getLocalHost().getHostName();
                    CVSGrab.getLog().info("Login on the NT proxy with user name " + userName
                            + ", host " + host + ", NT domain " + ntDomain);
                    _client.getState().setProxyCredentials(null, proxyHost,
                        new NTCredentials(userName, password, host, ntDomain));
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Use authentification for the web server
     *
     * @param userName The username to use on the web server
     * @param password The password to use on the web server
     */
    public void useWebAuthentification(final String userName, String password) {
        CVSGrab.getLog().info("Login on the web server with user name " + userName + " and password " + password);
        if (password == null ) {
            PasswordField pwdField = new PasswordField();
            try {
                password = pwdField.getPassword("Enter the password for the web server: ");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        _client.getState().setCredentials(null, null,
          new UsernamePasswordCredentials(userName, password));
    }

    /**
     * Allow simultaneous connections on different threads.
     */
    public void useMultithreading() {
        _client = new HttpClient(new MultiThreadedHttpConnectionManager());
    }

    /**
     * Execute a http method
     *
     * @param method The method
     * @param url The url called by the method, only useful for error reporting
     * @return the last http method executed (after following redirects)
     */
    public HttpMethod executeMethod(HttpMethod method, String url) {
        int statusCode = -1;
        int attempt = 0;

        try {
            method.setRequestHeader("User-Agent", "cvsgrab (http://cvsgrab.sourceforge.net)");
            method.setRequestHeader("Cache-Control", "no-cache");
            method.setRequestHeader("Accept-Encoding", "gzip");

            // We will retry up to 3 times.
            while ((statusCode == -1) && (attempt < 3)) {
                try {
                    // execute the method.
                    statusCode = _client.executeMethod(method);
                    CVSGrab.getLog().trace("Executed method " + url + " with status code " + statusCode);
                } catch (HttpRecoverableException e) {
                    CVSGrab.getLog().warn("A recoverable exception occurred, retrying. " + e.getMessage());
                } catch (IOException e) {
                    CVSGrab.getLog().error("Failed to download file " + url);
                    e.printStackTrace();
                    throw new RuntimeException("Failed to download file " + url);
                }
            }

            // Check that we didn't run out of retries.
            if (statusCode == -1) {
                CVSGrab.getLog().error("Failed to recover from exception.");
                throw new RuntimeException("Error when reading " + url);
            }

            if (statusCode >= 400) {
                CVSGrab.getLog().debug("Page not found (error " + statusCode + ")");
                throw new RuntimeException("Error " + statusCode + " when reading " + url);
            }

            // Tests for redirects
            if ((statusCode >= 300) && (statusCode < 400)) {
                Header locationHeader = method.getResponseHeader("location");

                if (locationHeader != null) {
                    String redirectLocation = locationHeader.getValue();

                    method.releaseConnection();
                    CVSGrab.getLog().debug("Redirect to " + redirectLocation);

                    HttpMethod redirectMethod = new GetMethod(redirectLocation);

                    executeMethod(redirectMethod, redirectLocation);

                    return redirectMethod;
                } else {
                    // The response is invalid and did not provide the new location for
                    // the resource.  Report an error or possibly handle the response
                    // like a 404 Not Found error.
                    CVSGrab.getLog().error("Page not found");
                    throw new RuntimeException("Error when reading " + url);
                }
            }
        } catch (RuntimeException e) {
            method.releaseConnection();
            throw e;
        }

        return method;
    }

    /**
     * Gets the response from a method that has been executed
     *
     * @param method The method
     * @param url The url called by the method, only useful for error reporting
     */
    public String getResponse(HttpMethod method, String url) {
        HttpMethod lastMethod = executeMethod(method, url);
        String response = null;
        try {
            // Gzip support by Ralf Stoffels (rstoffels)
            String contentEncoding = null;
            if (lastMethod.getResponseHeader("Content-Encoding") != null) {
                contentEncoding = lastMethod.getResponseHeader("Content-Encoding").getValue();
            }
            if (contentEncoding != null && contentEncoding.toLowerCase().indexOf("gzip") >= 0) {
                try {
                    InputStream inStream = lastMethod.getResponseBodyAsStream();
                    if (inStream != null) {
                        inStream = new GZIPInputStream(lastMethod.getResponseBodyAsStream());
                        if (inStream != null) {
                            response = getResponseContent(lastMethod, inStream);
                        }
                    }
                }
                catch (IOException e) {
                    CVSGrab.getLog().error("I/O failure reading response body", e);
                }
            } else {
                try {
					response = getResponseContent(lastMethod, lastMethod.getResponseBodyAsStream());
				} catch (IOException e) {
                    CVSGrab.getLog().error("I/O failure reading response body", e);
				}
            }
        } finally {
            lastMethod.releaseConnection();
        }
        return response;
    }

	private String getResponseContent(HttpMethod lastMethod, InputStream inStream) throws IOException {
		String response;
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len;
		while ((len = inStream.read(buffer)) > 0) {
		    outstream.write(buffer, 0, len);
		}
		outstream.close();
		response = new String(outstream.toByteArray(),
		        ((HttpMethodBase) lastMethod).getResponseCharSet());
		return response;
	}

    /**
     * Execute the method and gets the response as a xml document.
     *
     * @param method The method
     * @param url The url called by the method, only useful for error reporting
     */
    public Document getDocument(String url) throws Exception {
    	// Safety: some web sites will block if the url tries to open certain paths
    	if (url.endsWith("/browse/")) {
    		if (url.indexOf("netbeans.org") >= 0) {
    			throw new Exception("This url " + url + " doesn't work on Netbeans.org");
    		}
    		if (url.indexOf("dev.java.net") >= 0) {
    			throw new Exception("This url " + url + " doesn't work on dev.java.net");
    		}
    	}
        return getDocument(new GetMethod(url), url);
    }
    
    /**
     * Execute the method and gets the response as a xml document.
     *
     * @param method The method
     * @param url The url called by the method, only useful for error reporting
     */
    public Document getDocument(HttpMethod method, String url) throws Exception {
        String response = getResponse(method, url);
        return getDocumentFromSource(response);
    }

    public Document getDocumentFromSource(String docSource) throws Exception {
        // Hack to kill namespaces in xhtml
        int pos = 0;
        do {
           pos = docSource.indexOf("xmlns", pos);
           if (pos > 0) {
               int eq = docSource.indexOf('=', pos);
               int lt = docSource.indexOf('<', pos);
               int gt = docSource.indexOf('>', pos);
               if (eq > 0 && eq < gt && gt < lt) {
                   docSource = docSource.substring(0, pos) + docSource.substring(gt);
               }
           }
        } while (pos > 0);

        XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(docSource), null);

        _htmlParser.parse(source);

        Document doc = _htmlParser.getDocument();
        return doc;
    }

    public void loadFile(String url, File destFile) throws Exception {
        loadFile(new GetMethod(url), destFile, url);
    }
    
    public void loadFile(HttpMethod method, File destFile, String url) throws Exception {
        HttpMethod lastMethod = executeMethod(method, url);
        String contentEncoding = null;
        if (lastMethod.getResponseHeader("Content-Encoding") != null) {
            contentEncoding = lastMethod.getResponseHeader("Content-Encoding").getValue();
        }
        try {
            FileOutputStream out = null;
            InputStream in = new BufferedInputStream(lastMethod.getResponseBodyAsStream());
            if (contentEncoding != null && contentEncoding.toLowerCase().indexOf("gzip") >= 0) {
                in = new GZIPInputStream(lastMethod.getResponseBodyAsStream());
            }
            try {
                out = new FileOutputStream(destFile);

                byte[] buffer = new byte[8 * 1024];
                int count = 0;
                do {
                    out.write(buffer, 0, count);
                    count = in.read(buffer, 0, buffer.length);
                } while (count != -1);
            } finally {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
        } finally {
            lastMethod.releaseConnection();
        }
    }

}
