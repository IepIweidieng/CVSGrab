/*
 *  CVSGrab
 *  Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 *  Distributable under BSD license.
 *  See terms of license at gnu.org.
 */

package net.sourceforge.cvsgrab.util;

import java.util.Date;

import org.apache.commons.logging.impl.SimpleLog;

/**
 * Logs messages for CVSGrab.
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 3 févr. 2004
 */
public class CVSGrabLog extends SimpleLog {

    private String prefix;
    
    /**
     * Constructor for CVSGrabLog
     * @param arg0
     */
    public CVSGrabLog(String arg0) {
        super(arg0);
    }

    /** 
     * {@inheritDoc}
     * @param type
     * @param message
     * @param t
     */
    protected void log(int type, Object message, Throwable t) {
        // use a string buffer for better performance
        StringBuffer buf = new StringBuffer();
        
        // append date-time if so configured
        if(showDateTime) {
            buf.append(dateFormatter.format(new Date()));
            buf.append(" ");
        }
        
        // append a readable representation of the log level
        switch(type) {
        case SimpleLog.LOG_LEVEL_TRACE: buf.append("[TRACE] "); break;
        case SimpleLog.LOG_LEVEL_DEBUG: buf.append("[DEBUG] "); break;
        case SimpleLog.LOG_LEVEL_INFO:  break;
        case SimpleLog.LOG_LEVEL_WARN:  buf.append("[WARN] ");  break;
        case SimpleLog.LOG_LEVEL_ERROR: buf.append("[ERROR] "); break;
        case SimpleLog.LOG_LEVEL_FATAL: buf.append("[FATAL] "); break;
        }
        
        // append the name of the log instance if so configured
        if( showShortName) {
            if( prefix==null ) {
                // cut all but the last component of the name for both styles
                prefix = logName.substring( logName.lastIndexOf(".") +1) + " - ";
                prefix = prefix.substring( prefix.lastIndexOf("/") +1) + "-";
            }
            buf.append( prefix );
        } else if(showLogName) {
            buf.append(String.valueOf(logName)).append(" - ");
        }
        
        // append the message
        buf.append(String.valueOf(message));
        
        // append stack trace if not null
        if(t != null) {
            buf.append(" <");
            buf.append(t.toString());
            buf.append(">");
            
            java.io.StringWriter sw= new java.io.StringWriter(1024); 
            java.io.PrintWriter pw= new java.io.PrintWriter(sw); 
            t.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }
        
        // print to System.err
        System.err.println(buf.toString());
    }

}
