/*
 *  CVSGrab
 *  Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 *  Distributable under LGPL license.
 *  See terms of license at gnu.org.
 */

package net.sourceforge.cvsgrab;

import java.util.*;

/**
 * Represents a directory from the remote CVS server
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 */

public class RemoteDirectory {
    private String url;
    private String uri;
    private String localDir;
    private Vector remoteFiles = new Vector();

    /**
     * Constructor for the RemoteDirectory object
     *
     * @param aUri Description of the Parameter
     * @param repository Description of the Parameter
     */
    public RemoteDirectory(RemoteRepository repository, String aUri) {
        url = repository.getRootUrl() + aUri;
        uri = aUri;
        localDir = repository.getLocalRepository().getRootDirectory() + "/" + uri;
    }

    /**
     * Gets the url attribute
     *
     * @return The url value
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the uri attribute
     *
     * @return The uri value
     */
    public String getUri() {
        return uri;
    }

    /**
     * Gets the local dir attribute
     *
     * @return The localDir value
     */
    public String getLocalDir() {
        return localDir;
    }

    /**
     * Gets the remote files attribute
     *
     * @return The remoteFiles value
     */
    public Vector getRemoteFiles() {
        return remoteFiles;
    }

    /**
     * Description of the Method
     *
     * @param fileName Description of the Parameter
     */
    public void registerRemoteFile(String fileName) {
        remoteFiles.add(fileName);
    }

    /**
     * Description of the Method
     *
     * @param fileName Description of the Parameter
     */
    public void unregisterRemoteFile(String fileName) {
        remoteFiles.remove(fileName);
    }
}
