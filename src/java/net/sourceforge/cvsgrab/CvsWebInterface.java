/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab;

import net.sourceforge.cvsgrab.web.Sourcecast2_0Interface;
import net.sourceforge.cvsgrab.web.ViewCvs0_7Interface;
import net.sourceforge.cvsgrab.web.ViewCvs0_8Interface;
import net.sourceforge.cvsgrab.web.ViewCvs0_9Interface;
import net.sourceforge.cvsgrab.web.ViewCvs1_0Interface;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 6 oct. 2003
 */
public abstract class CvsWebInterface {
    
    private static CvsWebInterface[] webInterfaces = new CvsWebInterface[] {
        new ViewCvs0_7Interface(),  
        new ViewCvs0_8Interface(),  
        new ViewCvs0_9Interface(),  
        new ViewCvs1_0Interface(),  
        new Sourcecast2_0Interface()  
    };
    
    /**
     * Find the cvs web interface that could have generated this html page
     * 
     * @param htmlPage The html paeg read from the remote web interface
     * @return the cvs web interface that matches best this page
     */
    public static CvsWebInterface findInterface(Document htmlPage) {
        for (int i = 0; i < webInterfaces.length; i++) {
            try {
                webInterfaces[i].init(htmlPage);
                return webInterfaces[i];
            } catch (Exception ex) {
                // ignore
            }
        }
        return null;
    } 

    private String versionTag;
    
    /**
     * Constructor for CvsWebInterface
     * 
     */
    public CvsWebInterface() {
        super();
    }
    
    /**
     * @return the version tag
     */
    public String getVersionTag() {
        return versionTag;
    }

    /**
     * Sets the version tag 
     * @param versionTag
     */
    public void setVersionTag(String versionTag) {
        this.versionTag = versionTag;
    }

    public abstract void init(Document htmlPage) throws Exception; 
        
    public abstract String getType();

    /**
     * @param rootUrl
     * @param directoryName
     * @return the url to use to access the contents of the repository
     */
    public abstract String getDirectoryUrl(String rootUrl, String directoryName);

    /**
     * @param doc
     * @return
     */
    public abstract RemoteFile[] getFiles(Document doc);
    
    /**
     * @param doc
     * @return
     */
    public abstract String[] getDirectories(Document doc);

    /**
     * @param file
     * @return
     */
    public abstract String getDownloadUrl(RemoteFile file);
}
