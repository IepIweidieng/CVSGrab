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
     * @param dirUri Description of the Parameter
     * @return The remoteDirectory value
     */
    public RemoteDirectory getRemoteDirectory(String dirUri) {
        for (Iterator i = _remoteDirectories.iterator(); i.hasNext(); ) {
            RemoteDirectory remoteDir = (RemoteDirectory) i.next();
            if (remoteDir.getDirectoryName().equals(dirUri)) {
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
     * Description of the Method
     *
     * @param dirUri Description of the Parameter
     */
    public void registerDirectoryToProcess(String dirUri) {
        String uri = WebBrowser.forceFinalSlash(dirUri);
        if (getRemoteDirectory(uri) != null) {
            return;
        }
        RemoteDirectory remoteDir = new RemoteDirectory(this, uri);
        _remoteDirectories.add(remoteDir);
        _directoriesToProcess.add(remoteDir);
        _localRepository.add(remoteDir);
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public boolean hasDirectoryToProcess() {
        return (_directoriesToProcess.size() > 0);
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
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
