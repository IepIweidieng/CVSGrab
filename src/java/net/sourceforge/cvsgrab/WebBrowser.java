/*
 *  CVSGrab
 *  Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 *  Distributable under BSD license.
 *  See terms of license at gnu.org.
 */

package net.sourceforge.cvsgrab;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 11 oct. 2003
 */
public class WebBrowser {
    
    private static WebBrowser _instance = new WebBrowser();
    
    private HttpClient _client;
    private DOMParser _parser;

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
    
    /**
     * Constructor for WebBrowser
     */
    public WebBrowser() {
        super();
        _client = new HttpClient(new MultiThreadedHttpConnectionManager());
        _parser = new DOMParser();
        CookiePolicy.setDefaultPolicy(CookiePolicy.COMPATIBILITY);
        try {
            _parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
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
    public void useProxy(String proxyHost, int proxyPort, final String ntDomain, final String userName, final String password) {
        _client.getHostConfiguration().setProxy(proxyHost, proxyPort);
        if (ntDomain == null) {
            _client.getState().setProxyCredentials(null, proxyHost,
              new UsernamePasswordCredentials(userName, password));
        } else {
            try {
            String host = InetAddress.getLocalHost().getHostName();
            _client.getState().setProxyCredentials(ntDomain, proxyHost,
              new NTCredentials(userName, password, host, ntDomain));
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Use authentification for the web server 
     * 
     * @param userName The username to use on the web server
     * @param password The password to use on the web server
     */
    public void useWebAuthentification(final String userName, final String password) {
        _client.getState().setCredentials(null, null,
          new UsernamePasswordCredentials(userName, password));
    }  
    
    /**
     * Execute a http method
     * 
     * @param method The method
     * @return the last http method executed (after following redirects)
     */
    public HttpMethod executeMethod(HttpMethod method) {
        int statusCode = -1;
        int attempt = 0;
    
        method.setRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0b; Windows 98)");
    
        // We will retry up to 3 times.
        while ((statusCode == -1) && (attempt < 3)) {
            try {
                // execute the method.
                statusCode = _client.executeMethod(method);
                DefaultLogger.getInstance().debug("Executed method " + method.getPath() + " with status code " + statusCode);
            } catch (HttpRecoverableException e) {
                DefaultLogger.getInstance().warn("A recoverable exception occurred, retrying.  " + e.getMessage());
            } catch (IOException e) {
                DefaultLogger.getInstance().error("Failed to download file.");
                e.printStackTrace();
                throw new RuntimeException("Failed to download file.");
            }
        }
    
        // Check that we didn't run out of retries.
        if (statusCode == -1) {
            DefaultLogger.getInstance().error("Failed to recover from exception.");
            throw new RuntimeException("Error when reading " + method);
        }
    
        if (statusCode >= 400) {
            DefaultLogger.getInstance().error("Page not found (error " + statusCode + ")");
            throw new RuntimeException("Error when reading " + method);
        }
    
        // Tests for redirects
        if ((statusCode >= 300) && (statusCode < 400)) {
            Header locationHeader = method.getResponseHeader("location");
    
            if (locationHeader != null) {
                String redirectLocation = locationHeader.getValue();
                
                method.releaseConnection();    
                DefaultLogger.getInstance().error("Redirect to " + redirectLocation);
    
                HttpMethod redirectMethod = new GetMethod(redirectLocation);
    
                executeMethod(redirectMethod);
    
                return redirectMethod;
            } else {
                // The response is invalid and did not provide the new location for
                // the resource.  Report an error or possibly handle the response
                // like a 404 Not Found error.
                DefaultLogger.getInstance().error("Page not found");
                throw new RuntimeException("Error when reading " + method);
            }
        }
    
        return method;
    }

    /**
     * Gets the response from a method that has been executed
     * 
     * @param method
     * @return
     */
    public String getResponse(HttpMethod method) {
        return new String(method.getResponseBody());
    }
    
    /**
     * Execute the method and gets the response as a xml document. 
     * 
     * @param method
     * @return
     */
    public Document getDocument(HttpMethod method) throws Exception {
        HttpMethod lastMethod = executeMethod(method);
        String response = getResponse(lastMethod);
        lastMethod.releaseConnection();
        
        XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(response), null);
    
        _parser.parse(source);
    
        Document doc = _parser.getDocument();
        return doc;        
    }

    public void loadFile(HttpMethod method, File destFile) throws Exception {
        HttpMethod lastMethod = executeMethod(method);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = new BufferedInputStream(lastMethod.getResponseBodyAsStream());
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
    }
}
