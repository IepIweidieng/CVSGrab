/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Represents the remove repository available via the ViewCVS web interface.
 *
 * @author Ludovic Claude
 * @created April 16, 2002
 * @version 1.0
 */

public class RemoteRepository {

    private Vector remoteDirectories = new Vector();
    private Vector directoriesToProcess = new Vector();

    private String rootUrl;
    private LocalRepository localRepository;
    private Logger log = new DefaultLogger();

    /**
     * Constructor for the RemoteRepository object
     *
     * @param rootUrl Description of the Parameter
     * @param localRepository Description of the Parameter
     */
    public RemoteRepository(String rootUrl, LocalRepository localRepository) {
        this.rootUrl = rootUrl;
        this.localRepository = localRepository;
    }

    /**
     * Gets the root url attribute
     *
     * @return The rootUrl value
     */
    public String getRootUrl() {
        return rootUrl;
    }

    /**
     * Gets the local repository attribute
     *
     * @return The localRepository value
     */
    public LocalRepository getLocalRepository() {
        return localRepository;
    }

    /**
     * Gets the files in directory attribute
     *
     * @param dirUri Description of the Parameter
     * @return The filesInDirectory value
     */
    public Vector getFilesInDirectory(String dirUri) {
        RemoteDirectory rDir = getRemoteDirectory(dirUri);
        if (rDir != null) {
            return rDir.getRemoteFiles();
        }
        return null;
    }

    /**
     * Gets the remote directory attribute
     *
     * @param dirUri Description of the Parameter
     * @return The remoteDirectory value
     */
    public RemoteDirectory getRemoteDirectory(String dirUri) {
        for (Iterator i = remoteDirectories.iterator(); i.hasNext(); ) {
            RemoteDirectory rDir = (RemoteDirectory) i.next();
            if (rDir.getUri().equals(dirUri)) {
                return rDir;
            }
        }
        return null;
    }

    /**
     * Sets the log attribute
     *
     * @param value The new log value
     */
    public void setLog(Logger value) {
        log = value;
    }

    /**
     * Description of the Method
     *
     * @param dirUri Description of the Parameter
     */
    public void registerDirectoryToProcess(String dirUri) {
        if (getRemoteDirectory(dirUri) != null) {
            return;
        }
        RemoteDirectory rDir = new RemoteDirectory(this, dirUri);
        remoteDirectories.add(rDir);
        directoriesToProcess.add(rDir);
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public boolean hasDirectoryToProcess() {
        return (directoriesToProcess.size() > 0);
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public RemoteDirectory nextDirectoryToProcess() {
        if (!directoriesToProcess.isEmpty()) {
            RemoteDirectory rDir = (RemoteDirectory) directoriesToProcess.get(0);
            directoriesToProcess.remove(0);
            return rDir;
        }
        return null;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public RemoteDirectory currentDirectoryToProcess() {
        if (!directoriesToProcess.isEmpty()) {
            return (RemoteDirectory) directoriesToProcess.get(0);
        }
        return null;
    }

    /**
     * Description of the Method
     *
     * @param remoteDirectory Description of the Parameter
     * @param fullCVSViewUri Description of the Parameter
     */
    public void grabFile(RemoteDirectory remoteDirectory, String fullCVSViewUri) {
        String uri = fullCVSViewUri;
        // Remove content_type param
        uri = uri.substring(0, uri.indexOf("&"));
        String url = remoteDirectory.getUrl() + uri;
        String version = uri.substring(uri.indexOf("rev=") + 4);
        // Remove all http params, keep only the file name
        String fileName = uri.substring(0, uri.indexOf("?"));
        String cvsName = remoteDirectory.getUri() + fileName;
        remoteDirectory.registerRemoteFile(fileName);
        boolean needUpdate = localRepository.updateFile(cvsName, version);
        if (!needUpdate) {
            return;
        }
        // Make the destination dirs
        try {
            File localDir = new File(remoteDirectory.getLocalDir());
            localDir.mkdirs();
            File destFile = new File(localDir, fileName);
            log.verbose("Updating " + destFile);
            InputStream in = null;
            FileOutputStream out = null;
            try {
                in = new BufferedInputStream(new URL(url).openStream());
                out = new FileOutputStream(destFile);

                byte[] buffer = new byte[8 * 1024];
                int count = 0;
                do {
                    out.write(buffer, 0, count);
                    count = in.read(buffer, 0, buffer.length);
                } while (count != -1);
            } finally {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }

        } catch (IOException ex) {
            localRepository.unregisterFile(cvsName);
            remoteDirectory.unregisterRemoteFile(fileName);
            log.error("IO Error: " + ex);
        }
    }
}
