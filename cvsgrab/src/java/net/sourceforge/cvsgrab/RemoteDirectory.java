/*
 *  CVSGrab
 *  Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 *  Distributable under LGPL license.
 *  See terms of license at gnu.org.
 */

package net.sourceforge.cvsgrab;

import java.util.Vector;

import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;

/**
 * Represents a directory from the remote CVS server
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 */

public class RemoteDirectory {
    private String _dirName;
    private Vector _remoteFiles = new Vector();
    private RemoteRepository _remoteRepository;

    /**
     * Constructor for the RemoteDirectory object
     *
     * @param dirName The name of the directory
     * @param repository The repository
     */
    public RemoteDirectory(RemoteRepository repository, String dirName) {
        _remoteRepository = repository;
        _dirName = dirName;
    }

    /**
     * Gets the url attribute
     *
     * @return The url value
     */
    public String getUrl() {
        return _remoteRepository.getDirectoryUrl(_dirName);
    }

    /**
     * Gets the directory name
     *
     * @return The directory name
     */
    public String getDirectoryName() {
        return _dirName;
    }

    /**
     * Gets the remote files attribute
     *
     * @return The remoteFiles value
     */
    public RemoteFile[] getRemoteFiles() {
        return (RemoteFile[]) _remoteFiles.toArray(new RemoteFile[_remoteFiles.size()]);
    }

    /**
     * @return
     */
    public RemoteRepository getRemoteRepository() {
        return _remoteRepository;
    }

    /**
     * Description of the Method
     *
     * @param file Description of the Parameter
     */
    public void registerRemoteFile(RemoteFile file) {
        _remoteFiles.add(file);
    }

    /**
     * Description of the Method
     *
     * @param file Description of the Parameter
     */
    public void unregisterRemoteFile(RemoteFile file) {
        _remoteFiles.remove(file);
    }

    /**
     * Load the contents of the web page and upload the set of versioned files and sub directories as needed.
     */
    public void loadContents() throws Exception {
        String url = getUrl();
        DefaultLogger.getInstance().info("Parsing page: " + url);
        Document doc = WebBrowser.getInstance().getDocument(new GetMethod(url));
        RemoteFile[] files = _remoteRepository.getWebInterface().getFiles(doc);
        for (int i = 0; i < files.length; i++) {
            RemoteFile file = files[i];
            file.setDirectory(this);
            file.grab(_remoteRepository.getLocalRepository());
        }
        String[] directories = _remoteRepository.getWebInterface().getDirectories(doc);
        for (int i = 0; i < directories.length; i++) {
            String subDir = WebBrowser.forceFinalSlash(_dirName) + directories[i];
            _remoteRepository.registerDirectoryToProcess(subDir);
        }
    }
    
}
