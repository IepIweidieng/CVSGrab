/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab;

import java.io.*;
import java.util.*;
import com.ice.cvsc.*;

/**
 * The local repository where the files are stored on this computer.
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 */

public class LocalRepository {

    private CVSProject cvsProject;
    private String localRootDir;
    private int newFiles = 0;
    private int updatedFiles = 0;
    private int removedFiles = 0;
    private int failedUpdates = 0;
    private Logger log = new DefaultLogger();

    /**
     * Constructor for the LocalRepository object
     *
     * @param cvsHost Description of the Parameter
     * @param cvsRoot Description of the Parameter
     * @param destDir Description of the Parameter
     * @param cvsUser Description of the Parameter
     * @param packageName Description of the Parameter
     */
    public LocalRepository(String cvsUser, String cvsHost, String cvsRoot, String destDir, String packageName) {
        //CVSProject.debugEntryIO = true;
        //CVSTracer.turnOn();
        cvsProject = new CVSProject();
        localRootDir = destDir;
        if (localRootDir.endsWith("/")) {
            localRootDir = localRootDir.substring(0, localRootDir.length() - 1);
        }
        cvsProject.setLocalRootDirectory(localRootDir);
        cvsProject.setRepository("");
        cvsProject.setRootDirectory(cvsRoot);
        cvsProject.setConnectionMethod(CVSRequest.METHOD_INETD);
        cvsProject.setPServer(true);
        cvsProject.setUserName(cvsUser);
        CVSClient cvsClient = new CVSClient();
        cvsClient.setHostName(cvsHost);
        cvsProject.setClient(cvsClient);
        if (cvsProject.getRootEntry() == null) {
            // Build the entry files for the root directory
            cvsProject.establishRootEntry("");
            CVSEntry entry = new CVSEntry();
            String ldir = "./";
            entry.setLocalDirectory(ldir);
            entry.setName(packageName);
            entry.setRepository(getRepo(ldir));
            entry.setDirectoryEntryList(new CVSEntryVector());
            cvsProject.addNewEntry(entry);

            CVSEntry dirEntry = cvsProject.getRootEntry();
            String localDir = ldir;
            String adminRootPath =
                    CVSProject.rootPathToAdminPath(localRootDir);

            File adminFile = new File
                    (CVSCUtilities.exportPath(adminRootPath));

            File rootFile = new File
                    (CVSCUtilities.exportPath
                    (CVSProject.getAdminRootPath(adminRootPath)));

            File reposFile = new File
                    (CVSCUtilities.exportPath
                    (CVSProject.getAdminRepositoryPath(adminRootPath)));

            File entriesFile = new File
                    (CVSCUtilities.exportPath
                    (CVSProject.getAdminEntriesPath(adminRootPath)));

            CVSEntryVector entries = dirEntry.getEntryList();

            if (!adminFile.exists()) {
                if (!adminFile.mkdir()) {
                    log.error("Could not create the admin directory '"
                            + adminFile.getPath() + "'");
                }
            }

            cvsProject.writeAdminEntriesFile(entriesFile, entries);
            String rootDirStr = "";
            cvsProject.writeAdminRootFile(rootFile, rootDirStr);
            cvsProject.writeAdminRepositoryFile(reposFile, dirEntry.getRepository());
        }
        // Read the previous entries for the files and their versions
        cvsProject.readEntries();
    }

    /**
     * Gets the root directory
     *
     * @return The rootDirectory value
     */
    public String getRootDirectory() {
        return localRootDir;
    }

    /**
     * Gets the repository
     *
     * @param ldir The local directory, relative to the destination directory
     * @return The repo value
     */
    public String getRepo(String ldir) {
        String repo = cvsProject.getRepository() + "/" + ldir.substring(2);
        if (repo.endsWith("/")) {
            repo = repo.substring(0, repo.length() - 1);
        }
        if (repo.startsWith("/")) {
            repo = repo.substring(1);
        }
        return repo;
    }

    /**
     * Gets the new file count
     *
     * @return The newFileCount value
     */
    public int getNewFileCount() {
        return newFiles;
    }

    /**
     * Gets the updated file count
     *
     * @return The updatedFileCount value
     */
    public int getUpdatedFileCount() {
        return updatedFiles;
    }

    /**
     * Gets the removed file count
     *
     * @return The deletedFileCount value
     */
    public int getRemovedFileCount() {
        return removedFiles;
    }

    /**
     * Gets the failed update count
     *
     * @return The failedUpdates value
     */
    public int getFailedUpdateCount() {
        return failedUpdates;
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
     */
    public void resetFileCounts() {
        newFiles = 0;
        updatedFiles = 0;
        removedFiles = 0;
        failedUpdates = 0;
    }

    /**
     * Removes the Cvs directory in the destDir top-level folder
     */
    public void removeRootEntries() {
        try {
            String adminRootPath =
                    CVSProject.rootPathToAdminPath(localRootDir);
            File adminRootDir = new File(adminRootPath);
            File[] entryFiles = adminRootDir.listFiles();
            for (int i = 0; i < entryFiles.length; i++) {
                entryFiles[i].delete();
            }
            adminRootDir.delete();
        } catch (Exception ignore) {}
    }

    /**
     * Updates the CVS admin files
     */
    public void update() {
        cvsProject.writeAdminFiles();
    }

    /**
     * Update a file in the local CVS entries
     *
     * @param cvsName CVS name of the file
     * @param version Version of the file
     * @return true if the file needs to be loaded from the server
     */
    public boolean updateFile(String cvsName, String version) {
        boolean needUpdate = false;
        cvsName = "./" + cvsName;
        String ldir;
        String name;
        if (cvsName.indexOf("/", 2) > 0) {
            int slash = cvsName.lastIndexOf("/");
            if (slash == -1) {
                slash = 0;
            }
            name = cvsName.substring(slash + 1);
            ldir = cvsName.substring(0, slash + 1);
        } else {
            name = cvsName.substring(2);
            ldir = "./";
        }
        File dir = new File(cvsProject.getLocalRootDirectory(), ldir);
        dir.mkdirs();
        CVSEntry dirEntry = cvsProject.getDirEntryForLocalDir(ldir);
        CVSEntry entry = null;
        if (dirEntry != null && dirEntry.getEntryList() != null) {
            entry = dirEntry.getEntryList().locateEntry(name);
        }
        if (entry == null) {
            entry = new CVSEntry();
            entry.setLocalDirectory(ldir);
            entry.setName(name);
            entry.setRepository("/"+getRepo(ldir));
            entry.setVersion(version);
            cvsProject.addNewEntry(entry);
            newFiles++;
            needUpdate = true;
        } else {
            if (!version.equals(entry.getVersion())) {
                entry.setVersion(version);
                cvsProject.updateEntriesItem(entry);
                updatedFiles++;
                needUpdate = true;
            }
        }
        return needUpdate;
    }

    /**
     * Unregister a file from the CVS entries, forcing it to be reloaded next
     * time the program is run
     *
     * @param cvsName CVS name of the file
     */
    public void unregisterFile(String cvsName) {
        cvsName = "./" + cvsName;
        String ldir;
        String name;
        if (cvsName.indexOf("/", 2) > 0) {
            int slash = cvsName.lastIndexOf("/");
            if (slash == -1) {
                slash = 0;
            }
            name = cvsName.substring(slash + 1);
            ldir = cvsName.substring(0, slash + 1);
        } else {
            name = cvsName.substring(2);
            ldir = "./";
        }
        CVSEntry dirEntry = cvsProject.getDirEntryForLocalDir(ldir);
        CVSEntry entry = null;
        if (dirEntry != null && dirEntry.getEntryList() != null) {
            entry = dirEntry.getEntryList().locateEntry(name);
            if (entry != null) {
                dirEntry.removeEntry(entry);
                failedUpdates++;
            }
        }
    }

    /**
     * Remove the local copies of the files that have been deleted in the remote
     * repository.
     *
     * @param remoteDirectory The remote directory to clean-up
     */
    public void cleanRemovedFiles(RemoteDirectory remoteDirectory) {
        String ldir = remoteDirectory.getUri();
        CVSEntry dirEntry = cvsProject.getDirEntryForLocalDir("./" + ldir);
        Vector dirFiles = new Vector();
        if (dirEntry == null || dirEntry.getEntryList() == null) {
            //System.out.println("Null entries in " + ldir);
            return;
        }
        for (Iterator i = dirEntry.getEntryList().iterator(); i.hasNext(); ) {
            CVSEntry cvsFile = (CVSEntry) i.next();
            if (cvsFile == null || cvsFile.isDirectory()) {
                continue;
            }
            dirFiles.add(cvsFile.getName());
        }
        Vector lastDirFiles = remoteDirectory.getRemoteFiles();
        if (lastDirFiles == null) {
            lastDirFiles = new Vector();
        }
        dirFiles.removeAll(lastDirFiles);
        removedFiles += dirFiles.size();
        for (Iterator i = dirFiles.iterator(); i.hasNext(); ) {
            String fileName = (String) i.next();
            File file = new File(remoteDirectory.getLocalDir(), fileName);
            log.verbose("Removing " + file);
            file.delete();
            CVSEntry oldEntry = dirEntry.getEntryList().locateEntry(fileName);
            dirEntry.removeEntry(oldEntry);
        }
        if (!dirFiles.isEmpty()) {
            cvsProject.writeAdminFiles();
        }
    }

}
