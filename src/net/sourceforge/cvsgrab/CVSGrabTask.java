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
    private String cvsHost = "";
    private String cvsRoot = "";
    private String cvsUser = "anonymous";
    private String packageName;
    private String tag = null;
    private boolean verbose = true;

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
     * Sets the cvs user attribute
     *
     * @param value The new cvsUser value
     */
    public void setCvsUser(String value) {
        cvsUser = value;
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
     * Sets the cvs host attribute
     *
     * @param value The new cvsHost value
     */
    public void setCvsHost(String value) {
        cvsHost = value;
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
        CVSGrab grabber = new CVSGrab();
        grabber.setLog(log);
        grabber.grabCVSRepository(rootUrl, destDir, packageName, tag, cvsUser, cvsHost, cvsRoot);
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
