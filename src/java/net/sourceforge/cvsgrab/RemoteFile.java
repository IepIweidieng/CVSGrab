/*
 *  CVSGrab
 *  Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 *  Distributable under BSD license.
 *  See terms of license at gnu.org.
 */
 
package net.sourceforge.cvsgrab;

import java.io.File;

import net.sourceforge.cvsgrab.util.*;

import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Represent a file stored in the remote repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 12 oct. 2003
 */
public class RemoteFile {
    
    private RemoteDirectory _directory;
    private String _name;
    private String _version; 
    private boolean _inAttic = false;

    /**
     * Constructor for VersionedFile
     */
    public RemoteFile(String name, String version) {
        super();
        _name = name;
        _version = version;        
    }

    /**
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return _version;
    }

    /**
     * @return the directory
     */
    public RemoteDirectory getDirectory() {
        return _directory;
    }

    /**
     * @return true if the file is stored in the Attic (after delection or recovery from deletion)
     */
    public boolean isInAttic() {
        return _inAttic;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * @param inAttic
     */
    public void setInAttic(boolean inAttic) {
        _inAttic = inAttic;
    }

    /**
     * Sets the directory
     * @param directory
     */
    public void setDirectory(RemoteDirectory directory) {
        _directory = directory;
    }

    /**
     * @param repository
     */
    public void grab(LocalRepository repository) {
        // Remove all http params, keep only the file name
        _directory.registerRemoteFile(this);
        RemoteRepository remoteRepository = _directory.getRemoteRepository();
        LocalRepository localRepository = remoteRepository.getLocalRepository();
        boolean needUpdate = localRepository.needUpdate(this);
        if (!needUpdate) {
            return;
        }
        if (ThreadPool.getInstance() != null) {
            ThreadPool.getInstance().doTask(new Runnable() {
                public void run() {
                    upload();
                }
            });
        } else {
            upload();
        }
    }

    private void upload() {
        RemoteRepository remoteRepository = _directory.getRemoteRepository();
        LocalRepository localRepository = remoteRepository.getLocalRepository();
        try {
            // Make the destination dirs
            File localDir = localRepository.getLocalDir(_directory);
            localDir.mkdirs();
            File destFile = new File(localDir, _name);
            DefaultLogger.getInstance().verbose("Updating " + destFile);
            String url = remoteRepository.getDownloadUrl(this);
            
            // Download the file
            WebBrowser.getInstance().loadFile(new GetMethod(url), destFile);

            localRepository.updateFileVersion(this);

        } catch (Exception ex) {
            localRepository.unregisterFile(this);
            _directory.unregisterRemoteFile(this);
            DefaultLogger.getInstance().error("IO Error: " + ex);
            ex.printStackTrace();
        }
    }

}
