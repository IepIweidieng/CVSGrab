/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */
package net.sourceforge.cvsgrab;

import java.util.Properties;

/**
 * Options for the web repository
 *
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 9 mars 2004
 */
public class WebOptions {

    private HttpProxy _httpProxy;
    private WebAuthentification _webAuthentification;
    private String _rootUrl;
    private String _packagePath;
    private String _projectRoot;
    private String _versionTag;
    private String _queryParams;
    private String _webInterfaceId;

    /**
     * Setup the the http proxy and the web authentification settings.
     */
    public void setupConnectionSettings() {
        if (_httpProxy != null) {
            _httpProxy.setup();
        }
        if (_webAuthentification != null) {
            _webAuthentification.setup();
        }
    }

    public void readProperties(Properties properties) {
        setRootUrl(properties.getProperty(CVSGrab.ROOT_URL_OPTION));
        setPackagePath(properties.getProperty(CVSGrab.PACKAGE_PATH_OPTION));
        setProjectRoot(properties.getProperty(CVSGrab.PROJECT_ROOT_OPTION));
        setVersionTag(properties.getProperty(CVSGrab.TAG_OPTION));
        setQueryParams(properties.getProperty(CVSGrab.QUERY_PARAMS_OPTION));
        setWebInterfaceId(properties.getProperty(CVSGrab.WEB_INTERFACE_OPTION));

        if (CVSGrab.getLog().isDebugEnabled()) {
            CVSGrab.getLog().debug("Web option/root url = " + getRootUrl());
            CVSGrab.getLog().debug("Web option/package path = " + getPackagePath());
            CVSGrab.getLog().debug("Web option/project root = " + getProjectRoot());
            CVSGrab.getLog().debug("Web option/version tag = " + getVersionTag());
            CVSGrab.getLog().debug("Web option/query params = " + getQueryParams());
            CVSGrab.getLog().debug("Web option/web interface = " + getWebInterfaceId());
        }

        if (properties.containsKey(CVSGrab.PROXY_HOST_OPTION)) {
            if (_httpProxy == null) {
                _httpProxy = new HttpProxy();
            }
            _httpProxy.readProperties(properties);
        }
        if (properties.containsKey(CVSGrab.WEB_USER_OPTION)) {
            if (_webAuthentification == null) {
                _webAuthentification = new WebAuthentification();
            }
            _webAuthentification.readProperties(properties);
        }
    }

    public void writeProperties(Properties properties) {
        if (getRootUrl() != null) {
            properties.setProperty(CVSGrab.ROOT_URL_OPTION, getRootUrl());
        }
        if (getPackagePath() != null) {
            properties.setProperty(CVSGrab.PACKAGE_PATH_OPTION, getPackagePath());
        }
        if (getProjectRoot() != null) {
            properties.setProperty(CVSGrab.PROJECT_ROOT_OPTION, getProjectRoot());
        }
        if (getVersionTag() != null) {
            properties.setProperty(CVSGrab.TAG_OPTION, getVersionTag());
        }
        if (getQueryParams() != null) {
            properties.setProperty(CVSGrab.QUERY_PARAMS_OPTION, getQueryParams());
        }
        if (getWebInterfaceId() != null) {
            properties.setProperty(CVSGrab.WEB_INTERFACE_OPTION, getWebInterfaceId());
        }
        if (_httpProxy != null) {
            _httpProxy.writeProperties(properties);
        }
        if (_webAuthentification != null) {
            _webAuthentification.writeProperties(properties);
        }
    }

    /**
     * @return Returns the rootUrl.
     */
    public String getRootUrl() {
        return _rootUrl;
    }

    /**
     * Sets the url for the root of the CVS repository accessible via ViewCVS
     * @param rootUrl The rootUrl to set.
     */
    public void setRootUrl(String rootUrl) {
        if (rootUrl != null) {
            _rootUrl = WebBrowser.forceFinalSlash(rootUrl);
        }
    }

    /**
     * @return Returns the packagePath.
     */
    public String getPackagePath() {
        return _packagePath;
    }

    /**
     * Sets the path of the package/module to update from CVS
     * @param packagePath The packagePath to set.
     */
    public void setPackagePath(String packagePath) {
        if (packagePath != null) {
            _packagePath = WebBrowser.forceFinalSlash(packagePath);
        }
    }

    /**
     * @return The project root, used by CVS with multiple repositories
     */
    public String getProjectRoot() {
        return _projectRoot;
    }

    public void setProjectRoot(String projectRoot) {
        if (projectRoot != null) {
        	_projectRoot = projectRoot;
        }
    }

    /**
     * @return Returns the versionTag.
     */
    public String getVersionTag() {
        return _versionTag;
    }

    /**
     * Sets the name of the tagged version of the files to retrieve, or null
     * @param versionTag The versionTag to set.
     */
    public void setVersionTag(String versionTag) {
    	if (versionTag != null) {
            _versionTag = versionTag;
    	}
    }

    /**
     * @return Returns the queryParams.
     */
    public String getQueryParams() {
        return _queryParams;
    }

    /**
     * @param queryParams The queryParams to set.
     */
    public void setQueryParams(String queryParams) {
    	if (queryParams != null) {
    		_queryParams = queryParams;
    	}
    }

    /**
     * Gets the webInterfaceId.
     * @return the webInterfaceId.
     */
    public String getWebInterfaceId() {
        return _webInterfaceId;
    }

    /**
     * Sets the webInterfaceId.
     * @param webInterfaceId The webInterfaceId to set.
     */
    public void setWebInterfaceId(String webInterfaceId) {
    	if (webInterfaceId != null) {
    		_webInterfaceId = webInterfaceId;
    	}
    }

    /**
     * Gets the httpProxy.
     * @return the httpProxy.
     */
    public HttpProxy getHttpProxy() {
        return _httpProxy;
    }

    /**
     * Sets the httpProxy.
     * @param httpProxy The httpProxy to set.
     */
    public void setHttpProxy(HttpProxy httpProxy) {
        this._httpProxy = httpProxy;
    }

    /**
     * Gets the webAuthentification.
     * @return the webAuthentification.
     */
    public WebAuthentification getWebAuthentification() {
        return _webAuthentification;
    }

    /**
     * Sets the webAuthentification.
     * @param webAuthentification The webAuthentification to set.
     */
    public void setWebAuthentification(WebAuthentification webAuthentification) {
        this._webAuthentification = webAuthentification;
    }

    /**
     * Clears the location parameters
     */
	public void clearLocation() {
	    _rootUrl = null;
	    _packagePath = null;
	    _projectRoot = null;
	    _versionTag = null;
	    _queryParams = null;
	    _webInterfaceId = null;
	}

    /**
     * Nested element for configuring the proxy for http connections
     *
     * @author lclaude
     * @cvsgrab.created May 14, 2002
     */
    public class HttpProxy {
        private String _host = null;
        private int _port = 0;
        private String _ntDomain = null;
        private String _username = null;
        private String _password = null;

        /**
         * Setup the proxy
         */
        public void setup() {
            if (_host != null && _port == 0) {
                throw new IllegalArgumentException("port argument is not specified in the proxy");
            }
            WebBrowser.getInstance().useProxy(_host, _port, _ntDomain, _username, _password);
        }

        /**
         * @param properties
         */
        public void writeProperties(Properties properties) {
            if (getHost() != null) {
                properties.setProperty(CVSGrab.PROXY_HOST_OPTION, getHost());
            }
            if (getPort() != 0) {
                properties.setProperty(CVSGrab.PROXY_PORT_OPTION, String.valueOf(getPort()));
            }
            if (getNtdomain() != null) {
                properties.setProperty(CVSGrab.PROXY_NTDOMAIN_OPTION, getNtdomain());
            }
            if (getUsername() != null) {
                properties.setProperty(CVSGrab.PROXY_USER_OPTION, getUsername());
            }
            if (getPassword() != null) {
                properties.setProperty(CVSGrab.PROXY_PASSWORD_OPTION, getPassword());
            }
        }

        /**
         * @param properties
         */
        public void readProperties(Properties properties) {
            setHost(properties.getProperty(CVSGrab.PROXY_HOST_OPTION));
            try {
                setPort(Integer.parseInt(properties.getProperty(CVSGrab.PROXY_PORT_OPTION)));
            } catch (NumberFormatException ex) {
                CVSGrab.getLog().error("Parameter " + CVSGrab.PROXY_PORT_OPTION + " must be a number");
                throw ex;
            }
            setNtdomain(properties.getProperty(CVSGrab.PROXY_NTDOMAIN_OPTION));
            setUsername(properties.getProperty(CVSGrab.PROXY_USER_OPTION));
            setPassword(properties.getProperty(CVSGrab.PROXY_PASSWORD_OPTION));
        }

        /**
         * Gets the host.
         * @return the host.
         */
        public String getHost() {
            return _host;
        }

        /**
         * Gets the ntDomain.
         * @return the ntDomain.
         */
        public String getNtdomain() {
            return _ntDomain;
        }

        /**
         * Gets the password.
         * @return the password.
         */
        public String getPassword() {
            return _password;
        }

        /**
         * Gets the port.
         * @return the port.
         */
        public int getPort() {
            return _port;
        }

        /**
         * Gets the user.
         * @return the user.
         */
        public String getUsername() {
            return _username;
        }

        /**
         * Sets the host
         *
         * @param value The new host value
         */
        public void setHost(String value) {
            _host = value;
        }

        /**
         * Sets the port
         *
         * @param value The new port value
         */
        public void setPort(int value) {
            _port = value;
        }

        /**
         * Sets the nt domain
         *
         * @param ntDomain The new net domain
         */
        public void setNtdomain(String ntDomain) {
            this._ntDomain = ntDomain;
        }

        /**
         * Sets the username
         *
         * @param value The new username value
         */
        public void setUsername(String value) {
            _username = value;
        }

        /**
         * Sets the password
         *
         * @param value The new password value
         */
        public void setPassword(String value) {
            _password = value;
        }

    }

    /**
     * Nested element for configuring the web server authentification
     *
     * @author lclaude
     * @cvsgrab.created May 14, 2002
     */
    public class WebAuthentification {
        private String _user = null;
        private String _password = null;

        /**
         * Setup the web authentification
         *
         * @throws BuildException Description of the Exception
         */
        public void setup() {
            WebBrowser.getInstance().useWebAuthentification(_user, _password);
        }

        /**
         * @param properties
         */
        public void writeProperties(Properties properties) {
            if (getUsername() != null) {
                properties.setProperty(CVSGrab.WEB_USER_OPTION, getUsername());
            }
            if (getPassword() != null) {
                properties.setProperty(CVSGrab.WEB_PASSWORD_OPTION, getPassword());
            }
        }

        /**
         * @param properties
         */
        public void readProperties(Properties properties) {
            setUsername(properties.getProperty(CVSGrab.WEB_USER_OPTION));
            setPassword(properties.getProperty(CVSGrab.WEB_PASSWORD_OPTION));
        }

        /**
         * Gets the password.
         * @return the password.
         */
        public String getPassword() {
            return _password;
        }

        /**
         * Gets the user.
         * @return the user.
         */
        public String getUsername() {
            return _user;
        }

        /**
         * Sets the username
         *
         * @param value The new username value
         */
        public void setUsername(String value) {
            _user = value;
        }

        /**
         * Sets the password
         *
         * @param value The new password value
         */
        public void setPassword(String value) {
            _password = value;
        }

    }

}
