/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab;

import java.util.Iterator;
import java.util.Vector;

/**
 * Represents the remote repository available via the ViewCVS web interface.
 *
 * @author Ludovic Claude
 * @created April 16, 2002
 * @version 1.0
 */

public class RemoteRepository {

    private Vector _remoteDirectories = new Vector();
    private Vector _directoriesToProcess = new Vector();

    private String _rootUrl;
    private LocalRepository _localRepository;
    private CvsWebInterface _webInterface;

    /**
     * Constructor for the RemoteRepository object
     *
     * @param rootUrl Description of the Parameter
     * @param localRepository Description of the Parameter
     */
    public RemoteRepository(String rootUrl, LocalRepository localRepository) {
        this._rootUrl = rootUrl;
        this._localRepository = localRepository;
    }

    /**
     * Gets the root url attribute
     *
     * @return The rootUrl value
     */
    public String getRootUrl() {
        return _rootUrl;
    }

    /**
     * Gets the local repository attribute
     *
     * @return The localRepository value
     */
    public LocalRepository getLocalRepository() {
        return _localRepository;
    }
    
    /**
     * Gets the remote directory attribute
     *
     * @param dirPath The directory path, relative to the root url
     * @return The remoteDirectory value
     */
    public RemoteDirectory getRemoteDirectory(String dirPath) {
        for (Iterator i = _remoteDirectories.iterator(); i.hasNext(); ) {
            RemoteDirectory remoteDir = (RemoteDirectory) i.next();
            if (remoteDir.getDirectoryPath().equals(dirPath)) {
                return remoteDir;
            }
        }
        return null;
    }

    /**
     * @return the web interface
     */
    public CvsWebInterface getWebInterface() {
        return _webInterface;    
    }
    
    /**
     * Sets the web interface
     * 
     * @param webInterface the web interface
     */
    public void setWebInterface(CvsWebInterface webInterface) {
        _webInterface = webInterface; 
    }

    /**
     * Registers a directory to process
     *
     * @param remoteDir The remote directory to register
     */
    public void registerDirectoryToProcess(RemoteDirectory remoteDir) {
        if (getRemoteDirectory(remoteDir.getDirectoryPath()) != null) {
            return;
        }
        _remoteDirectories.add(remoteDir);
        _directoriesToProcess.add(remoteDir);
        _localRepository.add(remoteDir);
    }

    /**
     * @return true if there are remote directories that need to be processed 
     */
    public boolean hasDirectoryToProcess() {
        return (_directoriesToProcess.size() > 0);
    }

    /**
     * @return the next directory to process, and remove it from the list of directories to process
     */
    public RemoteDirectory nextDirectoryToProcess() {
        if (!_directoriesToProcess.isEmpty()) {
            RemoteDirectory rDir = (RemoteDirectory) _directoriesToProcess.get(0);
            _directoriesToProcess.remove(0);
            return rDir;
        }
        return null;
    }

    /**
     * @param directoryName The name of the directory
     * @return The full url to use to access the contents of the directory 
     */
    public String getDirectoryUrl(String directoryName) {
        String url = _webInterface.getDirectoryUrl(getRootUrl(), directoryName);
        return url;
    }

    /**
     * @param file
     * @return
     */
    public String getDownloadUrl(RemoteFile file) {
        String url = _webInterface.getDownloadUrl(file); 
        return url;
    }

}
