/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under LGPL license.
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

    private String rootUrl;
    private String destDir;
    private String cvsRoot = CVSGrab.DUMMY_ROOT;
    private String packageName;
    private String tag = null;
    private boolean verbose = true;
    private boolean pruneEmptyDirs = false;
    private HttpProxy proxy = null;
    private WebAuthentification web = null;

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
        packageName = value;
    }

    /**
     * Sets the root url attribute
     *
     * @param value The new rootUrl value
     */
    public void setRootUrl(String value) {
        rootUrl = value;
    }

    /**
     * Sets the dest dir attribute
     *
     * @param value The new destDir value
     */
    public void setDestDir(String value) {
        destDir = value;
    }

    /**
     * Sets the cvs root attribute
     *
     * @param value The new cvsRoot value
     */
    public void setCvsRoot(String value) {
        cvsRoot = value;
    }

    /**
     * Sets the tag attribute
     *
     * @param value The new tag value
     */
    public void setTag(String value) {
        tag = value;
    }

    /**
     * Sets the verbose attribute
     *
     * @param value The new verbose value
     */
    public void setVerbose(boolean value) {
        verbose = value;
    }

    /**
     * Sets the prune empty dirs
     *
     * @param value The new pruneEmptyDirs value
     */
    public void setPruneEmptyDirs(boolean value) {
        pruneEmptyDirs = value;
    }

    /**
     * Create the nested element for configuring the proxy
     *
     * @return the nested element for the proxy
     */
    public HttpProxy createProxy() {
        proxy = new HttpProxy();
        return proxy;
    }

    /**
     * Create the nested element for configuring the web authentification
     *
     * @return the nested element for the web
     */
    public WebAuthentification createWeb() {
        web = new WebAuthentification();
        return web;
    }

    /**
     * Execute the task
     *
     * @exception BuildException when an error occured in the build
     */
    public void execute() throws BuildException {
        if (rootUrl == null) {
            throw new BuildException("rootUrl argument is not specified");
        }
        if (destDir == null) {
            throw new BuildException("destDir argument is not specified");
        }
        if (packageName == null) {
            throw new BuildException("package argument is not specified");
        }

        Logger log = new AntLogger(project);
        log.setVerbose(verbose);
        DefaultLogger.setInstance(log);
        CVSGrab grabber = new CVSGrab();
        grabber.setPruneEmptyDirs(pruneEmptyDirs);
        if (proxy != null) {
            proxy.setup(grabber);
        }
        if (web != null) {
            web.setup(grabber);
        }
        grabber.grabCVSRepository(rootUrl, destDir, packageName, tag, cvsRoot);
    }

    /**
     * Nested element for configuring the proxy for http connections
     *
     * @author lclaude
     * @created May 14, 2002
     */
    public class HttpProxy {
        private String host = null;
        private int port = 0;
        private String ntDomain = null;
        private String user = null;
        private String password = null;

        /**
         * Setup the proxy
         *
         * @param grabber Description of the Parameter
         * @throws BuildException Description of the Exception
         */
        public void setup(CVSGrab grabber) throws BuildException {
            if (host != null && port == 0) {
                throw new BuildException("port argument is not specified in the proxy");
            }
            WebBrowser.getInstance().useProxy(host, port, ntDomain, user, password);
        }

        /**
         * Sets the host
         *
         * @param value The new host value
         */
        public void setHost(String value) {
            host = value;
        }

        /**
         * Sets the port
         *
         * @param value The new port value
         */
        public void setPort(int value) {
            port = value;
        }

        /**
         * Sets the nt domain
         *  
         * @param ntDomain The new net domain 
         */
        public void setNtdomain(String ntDomain) {
            this.ntDomain = ntDomain;
        }

        /**
         * Sets the username
         *
         * @param value The new username value
         */
        public void setUsername(String value) {
            user = value;
        }

        /**
         * Sets the password
         *
         * @param value The new password value
         */
        public void setPassword(String value) {
            password = value;
        }

    }

    /**
     * Nested element for configuring the web server authentification
     *
     * @author lclaude
     * @created May 14, 2002
     */
    public class WebAuthentification {
        private String user = null;
        private String password = null;

        /**
         * Setup the web authentification
         *
         * @param grabber Description of the Parameter
         * @throws BuildException Description of the Exception
         */
        public void setup(CVSGrab grabber) throws BuildException {
            WebBrowser.getInstance().useWebAuthentification(user, password);
        }

        /**
         * Sets the username
         *
         * @param value The new username value
         */
        public void setUsername(String value) {
            user = value;
        }

        /**
         * Sets the password
         *
         * @param value The new password value
         */
        public void setPassword(String value) {
            password = value;
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
