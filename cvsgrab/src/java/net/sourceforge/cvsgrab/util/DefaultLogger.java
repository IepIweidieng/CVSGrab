/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.util;

/**
 * Simple logger interface
 *
 * @author lclaude
 * @created April 29, 2002
 */
public class DefaultLogger implements Logger {
    
    private static Logger _instance = new DefaultLogger();
    private boolean _verbose = true;
    private boolean _debug;
    
    /**
     * @return the singleton instance
     */
    public static Logger getInstance() {
        return _instance;
    }
    
    /**
     * Sets the singleton instance
     * @param instance
     */
    public static void setInstance(Logger instance) {
        _instance = instance;
    }
    
    private DefaultLogger() {
    }

    /**
     * Sets the debug attribute
     *
     * @param value The new debug value
     */
    public void setDebug(boolean value) {
        _debug = value;
    }

    /**
     * Sets the verbose attribute
     *
     * @param value The new verbose value
     */
    public void setVerbose(boolean value) {
        this._verbose = value;
    }

    /**
     * Description of the Method
     *
     * @param msg Description of the Parameter
     */
    public void debug(String msg) {
        if (_debug) {
            System.out.println("[debug]" + msg);
        }
    }

    /**
     * Prints a verbose message
     *
     * @param msg The message
     */
    public void verbose(String msg) {
        if (_verbose) {
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
