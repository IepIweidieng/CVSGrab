/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab;

import org.apache.commons.logging.Log;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Ant task for CVSGrab.
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 */

public class CVSGrabTask extends Task {

    private CVSGrab _grabber = new CVSGrab();
    private boolean _verbose = true;
    private HttpProxy _proxy = null;
    private WebAuthentification _web = null;
    private int _connections;

    /**
     * Constructor for the CVSGrabTask object
     */
    public CVSGrabTask() {
        super();
    }

    /**
     * Sets the root url attribute
     *
     * @param value The new rootUrl value
     */
    public void setRootUrl(String value) {
        _grabber.setRootUrl(value);
    }

    /**
     * Sets the package path attribute
     *
     * @param value The new package value
     */
    public void setPackagePath(String value) {
        _grabber.setPackagePath(value);
    }

    /**
     * Sets the dest dir attribute
     *
     * @param value The new destDir value
     */
    public void setDestDir(String value) {
        _grabber.setDestDir(value);
    }

    /**
     * Sets the package dir attribute
     *
     * @param value The new package value
     */
    public void setPackageDir(String value) {
        _grabber.setPackageDir(value);
    }

    /**
     * Sets the cvs root attribute
     *
     * @param value The new cvsRoot value
     */
    public void setCvsRoot(String value) {
        _grabber.setCvsRoot(value);
    }

    /**
     * Sets the tag attribute
     *
     * @param value The new tag value
     */
    public void setTag(String value) {
        _grabber.setVersionTag(value);
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
        _grabber.setPruneEmptyDirs(value);
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
        if (_grabber.getRootUrl() == null) {
            throw new BuildException("rootUrl argument is not specified");
        }
        if (_grabber.getDestDir() == null) {
            throw new BuildException("destDir argument is not specified");
        }
        if (_grabber.getPackagePath() == null) {
            throw new BuildException("packagePath argument is not specified");
        }

        AntLogger log = new AntLogger(project);
        log.setVerbose(_verbose);
        CVSGrab.setLog(log);
        if (_proxy != null) {
            _proxy.setup(_grabber);
        }
        if (_web != null) {
            _web.setup(_grabber);
        }
        if (_connections > 0) {
            ThreadPool.init(_connections);
        }
        _grabber.grabCVSRepository();
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
    class AntLogger implements Log {
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
         * {@inheritDoc}
         *
         * @param msg The message
         */
        public void debug(Object msg) {
            // use VERBOSE and not DEBUG, because Ant debug messages are really too much...
            antProject.log(msg.toString(), Project.MSG_VERBOSE);
        }

        /**
         * {@inheritDoc}
         *
         * @param msg The message
         */
        public void verbose(Object msg) {
            if (verbose) {
                antProject.log(msg.toString(), Project.MSG_INFO);
            } else {
                antProject.log(msg.toString(), Project.MSG_VERBOSE);
            }
        }

        /**
         * {@inheritDoc}
         *
         * @param msg The message
         */
        public void info(Object msg) {
            antProject.log(msg.toString(), Project.MSG_INFO);
        }

        /**
         * {@inheritDoc}
         *
         * @param msg The message
         */
        public void warn(Object msg) {
            antProject.log(msg.toString(), Project.MSG_WARN);
        }

        /**
         * {@inheritDoc}
         *
         * @param msg The message
         */
        public void error(Object msg) {
            antProject.log(msg.toString(), Project.MSG_ERR);
        }

        /** 
         * {@inheritDoc}
         * @return
         */
        public boolean isDebugEnabled() {
            return true;
        }

        /** 
         * {@inheritDoc}
         * @return
         */
        public boolean isErrorEnabled() {
            return true;
        }

        /** 
         * {@inheritDoc}
         * @return
         */
        public boolean isFatalEnabled() {
            return true;
        }

        /** 
         * {@inheritDoc}
         * @return
         */
        public boolean isInfoEnabled() {
            return true;
        }

        /** 
         * {@inheritDoc}
         * @return
         */
        public boolean isTraceEnabled() {
            return false;
        }

        /** 
         * {@inheritDoc}
         * @return
         */
        public boolean isWarnEnabled() {
            return true;
        }

        /** 
         * {@inheritDoc}
         * @param arg0
         */
        public void trace(Object arg0) {
            // do nothing
        }

        /** 
         * {@inheritDoc}
         * @param arg0
         * @param arg1
         */
        public void trace(Object arg0, Throwable arg1) {
            // do nothing
        }

        /** 
         * {@inheritDoc}
         * @param arg0
         * @param arg1
         */
        public void debug(Object arg0, Throwable arg1) {
            // do nothing
        }

        /** 
         * {@inheritDoc}
         * @param arg0
         * @param arg1
         */
        public void info(Object arg0, Throwable arg1) {
            // do nothing
        }

        /** 
         * {@inheritDoc}
         * @param arg0
         * @param arg1
         */
        public void warn(Object arg0, Throwable arg1) {
            // do nothing
        }

        /** 
         * {@inheritDoc}
         * @param arg0
         * @param arg1
         */
        public void error(Object arg0, Throwable arg1) {
            // do nothing
        }

        /** 
         * {@inheritDoc}
         * @param arg0
         */
        public void fatal(Object arg0) {
            // do nothing
        }

        /** 
         * {@inheritDoc}
         * @param arg0
         * @param arg1
         */
        public void fatal(Object arg0, Throwable arg1) {
            // do nothing
        }
    }
}
