/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab;

/**
 * Simple logger interface
 *
 * @author lclaude
 * @created April 29, 2002
 */
public class DefaultLogger implements Logger {

    private boolean verbose;
    private boolean debug;

    /**
     * Sets the debug attribute
     *
     * @param value The new debug value
     */
    public void setDebug(boolean value) {
        debug = value;
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
     * Description of the Method
     *
     * @param msg Description of the Parameter
     */
    public void debug(String msg) {
        if (debug) {
            System.out.println("[debug]" + msg);
        }
    }

    /**
     * Prints a verbose message
     *
     * @param msg The message
     */
    public void verbose(String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }

    /**
     * Prints an info message
     *
     * @param msg The message
     */
    public void info(String msg) {
        System.out.println(msg);
    }

    /**
     * Prints a warning message
     *
     * @param msg The message
     */
    public void warn(String msg) {
        System.out.println("[warning] " + msg);
    }

    /**
     * Prints an error message
     *
     * @param msg The message
     */
    public void error(String msg) {
        System.out.println("[error] " + msg);
    }
}
