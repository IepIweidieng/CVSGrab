/*
 *  CVSGrab
 *  Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 *  Distributable under BSD license.
 *  See terms of license at gnu.org.
 */

package net.sourceforge.cvsgrab;

import java.util.Vector;

import net.sourceforge.cvsgrab.util.*;

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
    private Vector _remoteFiles = new Vector();
    private RemoteRepository _remoteRepository;
    private String _dirPath;
    private String _localDir;

    /**
     * Constructor for the RemoteDirectory object
     *
     * @param repository The repository
     * @param dirPath The name of the directory
     * @param localDir The local name of the directory
     */
    public RemoteDirectory(RemoteRepository repository, String dirPath, String localDir) {
        _remoteRepository = repository;
        _dirPath = WebBrowser.forceFinalSlash(dirPath);
        _localDir = WebBrowser.forceFinalSlash(localDir);
    }

    /**
     * Constructor for RemoteDirectory
     * @param parentDirectory The parent directory
     * @param name The name of the directory
     */
    public RemoteDirectory(RemoteDirectory parentDirectory, String name) {
        _remoteRepository = parentDirectory.getRemoteRepository();
        _dirPath = WebBrowser.forceFinalSlash(parentDirectory.getDirectoryPath() + name);
        _localDir = WebBrowser.forceFinalSlash(parentDirectory.getLocalDir() + name);
    }

    /**
     * Gets the url attribute
     *
     * @return The url value
     */
    public String getUrl() {
        return _remoteRepository.getDirectoryUrl(_dirPath);
    }

    /**
     * Gets the directory path
     *
     * @return The directory path
     */
    public String getDirectoryPath() {
        return _dirPath;
    }

    /**
     * @return the local name of the directory.
     */
    public String getLocalDir() {
        return _localDir;
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
     * @return the remote repository
     */
    public RemoteRepository getRemoteRepository() {
        return _remoteRepository;
    }

    /**
     * Registers the remote file in this directory
     *
     * @param file The remote file to register
     */
    public void registerRemoteFile(RemoteFile file) {
        _remoteFiles.add(file);
    }

    /**
     * Unregisters the remote file
     *
     * @param file The remote file to unrgister 
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
            _remoteRepository.registerDirectoryToProcess(new RemoteDirectory(this, directories[i]));
        }
    }
    
}
