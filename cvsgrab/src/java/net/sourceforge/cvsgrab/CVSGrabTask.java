/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab;

import org.apache.tools.ant.*;

/**
 * Ant task for CVSGrab.
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 */

public class CVSGrabTask extends Task {

    private String _rootUrl;
    private String _destDir;
    private String _cvsRoot = CVSGrab.DUMMY_ROOT;
    private String _packageName;
    private String _tag = null;
    private boolean _verbose = true;
    private boolean _pruneEmptyDirs = false;
    private int _connections = 0;
    private HttpProxy _proxy = null;
    private WebAuthentification _web = null;

    /**
     * Constructor for the CVSGrabTask object
     */
    public CVSGrabTask() { }

    /**
     * Sets the package attribute
     *
     * @param value The new package value
     */
    public void setPackage(String value) {
        _packageName = value;
    }

    /**
     * Sets the root url attribute
     *
     * @param value The new rootUrl value
     */
    public void setRootUrl(String value) {
        _rootUrl = value;
    }

    /**
     * Sets the dest dir attribute
     *
     * @param value The new destDir value
     */
    public void setDestDir(String value) {
        _destDir = value;
    }

    /**
     * Sets the cvs root attribute
     *
     * @param value The new cvsRoot value
     */
    public void setCvsRoot(String value) {
        _cvsRoot = value;
    }

    /**
     * Sets the tag attribute
     *
     * @param value The new tag value
     */
    public void setTag(String value) {
        _tag = value;
    }

    /**
     * Sets the verbose attribute
     *
     * @param value The new verbose value
     */
    public void setVerbose(boolean value) {
        _verbose = value;
    }

    /**
     * Sets the prune empty dirs
     *
     * @param value The new pruneEmptyDirs value
     */
    public void setPruneEmptyDirs(boolean value) {
        _pruneEmptyDirs = value;
    }

    /**
     * Sets the number of simultaneous connections to open
     * @param connections The connections to set.
     */
    public void setConnections(int connections) {
        _connections = connections;
    }

    /**
     * Create the nested element for configuring the proxy
     *
     * @return the nested element for the proxy
     */
    public HttpProxy createProxy() {
        _proxy = new HttpProxy();
        return _proxy;
    }

    /**
     * Create the nested element for configuring the web authentification
     *
     * @return the nested element for the web
     */
    public WebAuthentification createWeb() {
        _web = new WebAuthentification();
        return _web;
    }

    /**
     * Execute the task
     *
     * @exception BuildException when an error occured in the build
     */
    public void execute() throws BuildException {
        if (_rootUrl == null) {
            throw new BuildException("rootUrl argument is not specified");
        }
        if (_destDir == null) {
            throw new BuildException("destDir argument is not specified");
        }
        if (_packageName == null) {
            throw new BuildException("package argument is not specified");
        }

        Logger log = new AntLogger(project);
        log.setVerbose(_verbose);
        DefaultLogger.setInstance(log);
        CVSGrab grabber = new CVSGrab();
        grabber.setPruneEmptyDirs(_pruneEmptyDirs);
        if (_proxy != null) {
            _proxy.setup(grabber);
        }
        if (_web != null) {
            _web.setup(grabber);
        }
        grabber.grabCVSRepository(_rootUrl, _destDir, _packageName, _tag, _cvsRoot);
    }

    /**
     * Nested element for configuring the proxy for http connections
     *
     * @author lclaude
     * @created May 14, 2002
     */
    public class HttpProxy {
        private String _host = null;
        private int _port = 0;
        private String _ntDomain = null;
        private String _user = null;
        private String _password = null;

        /**
         * Setup the proxy
         *
         * @param grabber Description of the Parameter
         * @throws BuildException Description of the Exception
         */
        public void setup(CVSGrab grabber) throws BuildException {
            if (_host != null && _port == 0) {
                throw new BuildException("port argument is not specified in the proxy");
            }
            WebBrowser.getInstance().useProxy(_host, _port, _ntDomain, _user, _password);
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

    /**
     * Nested element for configuring the web server authentification
     *
     * @author lclaude
     * @created May 14, 2002
     */
    public class WebAuthentification {
        private String _user = null;
        private String _password = null;

        /**
         * Setup the web authentification
         *
         * @param grabber Description of the Parameter
         * @throws BuildException Description of the Exception
         */
        public void setup(CVSGrab grabber) throws BuildException {
            WebBrowser.getInstance().useWebAuthentification(_user, _password);
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

    /**
     * Adapter for the Ant logger
     *
     * @author lclaude
     * @created April 29, 2002
     */
    class AntLogger implements Logger {
        private boolean verbose;
        private Project antProject;

        /**
         * Constructor for the AntLogger object
         *
         * @param project Description of the Parameter
         */
        public AntLogger(Project project) {
            antProject = project;
        }

        /**
         * Sets the verbose attribute
         *
         * @param value The new verbose value
         */
        public void setVerbose(boolean value) {
            this.verbose = value;
        }

        /**
         * Sets the debug attribute
         *
         * @param value The new debug value
         */
        public void setDebug(boolean value) {
            // do nothing, use Ant settings
        }

        /**
         * Description of the Method
         *
         * @param msg Description of the Parameter
         */
        public void debug(String msg) {
            // use VERBOSE and not DEBUG, because Ant debug messages are really too much...
            antProject.log(msg, Project.MSG_VERBOSE);
        }

        /**
         * Description of the Method
         *
         * @param msg Description of the Parameter
         */
        public void verbose(String msg) {
            if (verbose) {
                antProject.log(msg, Project.MSG_INFO);
            } else {
                antProject.log(msg, Project.MSG_VERBOSE);
            }
        }

        /**
         * Description of the Method
         *
         * @param msg Description of the Parameter
         */
        public void info(String msg) {
            antProject.log(msg, Project.MSG_INFO);
        }

        /**
         * Description of the Method
         *
         * @param msg Description of the Parameter
         */
        public void warn(String msg) {
            antProject.log(msg, Project.MSG_WARN);
        }

        /**
         * Description of the Method
         *
         * @param msg Description of the Parameter
         */
        public void error(String msg) {
            antProject.log(msg, Project.MSG_ERR);
        }
    }
}
