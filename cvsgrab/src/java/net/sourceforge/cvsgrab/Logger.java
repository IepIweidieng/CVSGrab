/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab;

/**
 * Simple logger interface
 *
 * @author lclaude
 * @created April 29, 2002
 */
public interface Logger {

    void setDebug(boolean debug);

    void setVerbose(boolean verbose);

    void debug(String msg);

    void verbose(String msg);

    void info(String msg);

    void warn(String msg);

    void error(String msg);
}
