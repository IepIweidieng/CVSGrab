/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */

package net.sourceforge.cvsgrab;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.GlobalOptions;

/**
 * Handles the CVS admin files.
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 9 mars 2004
 */
public class CVSGrabAdminHandler extends StandardAdminHandler {
    
    private CVSGrab _cvsGrab;

    /**
     * Constructor for CVSGrabAdminHandler
     */
    public CVSGrabAdminHandler(CVSGrab cvsGrab) {
        super();
        _cvsGrab = cvsGrab;
    }
    
    /** 
     * {@inheritDoc}
     * @param localDirectory
     * @param repositoryPath
     * @param entry
     * @param globalOptions
     * @throws java.io.IOException
     */
    public void updateAdminData(String localDirectory, String repositoryPath, Entry entry,
            GlobalOptions globalOptions) throws IOException {
        super.updateAdminData(localDirectory, repositoryPath, entry, globalOptions);
        
        final File CVSdir = new File(localDirectory, "CVS"); //NOI18N
        
        // now ensure that the WebRepository files exist
        File repositoryFile = new File(CVSdir, "WebRepository"); //NOI18N
        if (!repositoryFile.exists()) {
            final Properties properties = new Properties();
            final OutputStream w = new BufferedOutputStream(new FileOutputStream(repositoryFile));
            _cvsGrab.getWebOptions().writeProperties(properties);
            properties.store(w, "CVSGrab settings");
            w.close();
        }

    }

}
