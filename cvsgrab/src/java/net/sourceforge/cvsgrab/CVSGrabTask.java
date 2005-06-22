/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
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
    private int _connections;
    private WebOptions _webOptions;

    /**
     * Constructor for the CVSGrabTask object
     */
    public CVSGrabTask() {
        super();
        _webOptions = _grabber.getWebOptions();
    }

    /**
     * Sets the url attribute
     *
     * @param value The new rootUrl value
     */
    public void setUrl(String value) {
        _grabber.setUrl(value);
    }

    /**
     * Sets the root url attribute
     *
     * @param value The new rootUrl value
     */
    public void setRootUrl(String value) {
        _webOptions.setRootUrl(value);
    }

    /**
     * Sets the package path attribute
     *
     * @param value The new package value
     */
    public void setPackagePath(String value) {
        _webOptions.setPackagePath(value);
    }
    
    public void setProjectRoot(String value) {
        _webOptions.setProjectRoot(value);
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
        _webOptions.setVersionTag(value);
    }

    public void setWebInterface(String value) {
        _webOptions.setWebInterfaceId(value);
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
     * Sets the clean update 
     * 
     * @param value The new cleanUpdate value
     */
    public void setCleanUpdate(boolean value) {
        _grabber.setCleanUpdate(value);
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
    public WebOptions.HttpProxy createProxy() {
        WebOptions.HttpProxy proxy = _webOptions.new HttpProxy();
        _webOptions.setHttpProxy(proxy);
        return proxy;
    }

    /**
     * Create the nested element for configuring the web authentification
     *
     * @return the nested element for the web
     */
    public WebOptions.WebAuthentification createWeb() {
        WebOptions.WebAuthentification auth = _webOptions.new WebAuthentification();
        _webOptions.setWebAuthentification(auth);
        return auth;
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

        AntLogger log = new AntLogger(getProject());
        log.setVerbose(_verbose);
        CVSGrab.setLog(log);
        _webOptions.setupConnectionSettings();

        if (_connections > 1) {
            ThreadPool.init(_connections);
            WebBrowser.getInstance().useMultithreading();
        }
        _grabber.grabCVSRepository();
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
