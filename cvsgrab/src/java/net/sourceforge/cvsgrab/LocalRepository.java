/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab;

import java.io.*;
import java.util.*;
import org.netbeans.lib.cvsclient.admin.*;
import org.netbeans.lib.cvsclient.command.GlobalOptions;

/**
 * The local repository where the files are stored on this computer.
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 */

public class LocalRepository {

    private AdminHandler handler = new StandardAdminHandler();
    private GlobalOptions globalOptions = new GlobalOptions();
    /**
     * A store of potentially empty directories. When a directory has a file
     * in it, it is removed from this set. This set allows the prune option
     * to be implemented.
     */
    private final Set emptyDirectories = new HashSet();
    /** 
     * performs checkout to specified directory other than the module.
     */
    private String checkoutDirectory;
    
    /**
     * The local root of the repository
     */
    private File localRootDir;
    private int newFiles = 0;
    private int updatedFiles = 0;
    private int removedFiles = 0;
    private int failedUpdates = 0;

    /**
     * Constructor for the LocalRepository object
     *
     * @param cvsRoot The cvs root 
     * @param destDir The destination directory
     * @param packageName The package 
     */
    public LocalRepository(String cvsRoot, String destDir, String packageName) throws IOException {
        globalOptions.setCVSRoot(cvsRoot);
        globalOptions.setCheckedOutFilesReadOnly(true);
        localRootDir = new File(destDir);
    }
    
    /**
     * Gets the root directory
     *
     * @return The rootDirectory value
     */
    public File getLocalRootDir() {
        return localRootDir;
    }

    /**
     * Gets the local directory to use for storing the contents of the remote directory
     * @param remoteDir The remote directory
     * @return The local directory
     */
    public File getLocalDir(RemoteDirectory remoteDir) {
        return new File(getLocalRootDir(), remoteDir.getDirectoryName());
    }
    
    /**
     * Gets the local file to use for storing the contents of the remote file
     * @param remoteFile The remote file
     * @return The local file
     */
    public File getLocalFile(RemoteFile remoteFile) {
        File dir = getLocalDir(remoteFile.getDirectory());
        File file = new File(dir, remoteFile.getName());
        return file;
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
     * Description of the Method
     */
    public void resetFileCounts() {
        newFiles = 0;
        updatedFiles = 0;
        removedFiles = 0;
        failedUpdates = 0;
    }

    /**
     * Return true if the file needs to be updated
     *
     * @param remoteFile The remote file
     * @return true if the file needs to be loaded from the server
     */
    public boolean needUpdate(RemoteFile remoteFile) {
        boolean needUpdate = true;
        File file = getLocalFile(remoteFile);
        
        if (file.exists()) {
            try {
                Entry entry = handler.getEntry(file);
                if (entry != null) {
                    needUpdate = !remoteFile.getVersion().equals(entry.getRevision());
                }
            } catch (IOException ex) {
                // ignore
                ex.printStackTrace();
            }
        }
        return needUpdate;
    }

    /**
     * Update a file version in the local CVS entries
     *
     * @param remoteFile The remote file
     */
    public void updateFileVersion(RemoteFile remoteFile) {
        File dir = getLocalDir(remoteFile.getDirectory());
        File file = getLocalFile(remoteFile);
        Entry entry = null;
        try {
            entry = handler.getEntry(file);
            if (entry == null) {
                throw new IOException("Entry not found");
            }
            updatedFiles++;
        } catch (IOException ex) {
            String lastModified = Entry.getLastModifiedDateFormatter().format(new Date());
            entry = new Entry("/" + remoteFile.getName() + "/" + remoteFile.getVersion() + "/" + lastModified + "//");
            newFiles++;
        }
        
        String localDirectory = WebBrowser.removeFinalSlash(dir.getAbsolutePath());
        String repositoryPath = remoteFile.getDirectory().getDirectoryName();
        try {
            handler.updateAdminData(localDirectory, repositoryPath, entry, globalOptions);
        } catch (IOException ex) {
            ex.printStackTrace();
            DefaultLogger.getInstance().error("Cannot update CVS entry for file " + file);
            throw new RuntimeException("Cannot update CVS entry for file " + file);
        }
    }

    /**
     * Unregister a file from the CVS entries, forcing it to be reloaded next
     * time the program is run
     *
     * @param remoteFile The remote file
     */
    public void unregisterFile(RemoteFile remoteFile) {
        File file = getLocalFile(remoteFile);
        Entry entry = null;
        try {
            handler.removeEntry(file);
            failedUpdates++;
        } catch (IOException ex) {
            ex.printStackTrace();
            // ignore
        }
    }

    /**
     * Remove the local copies of the files that have been deleted in the remote
     * repository.
     *
     * @param remoteDirectory The remote directory to clean-up
     */
    public void cleanRemovedFiles(RemoteDirectory remoteDirectory) {
        /*
        String ldir = remoteDirectory.getDirectoryName();
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
        RemoteFile[] lastDirFiles = remoteDirectory.getRemoteFiles();
        for (int i = 0; i < lastDirFiles.length; i++) {
            dirFiles.remove(lastDirFiles[i].getName());
        }
        removedFiles += dirFiles.size();
        for (Iterator i = dirFiles.iterator(); i.hasNext(); ) {
            String fileName = (String) i.next();
            File file = new File(getLocalDir(remoteDirectory), fileName);
            DefaultLogger.getInstance().verbose("Removing " + file);
            file.delete();
            CVSEntry oldEntry = dirEntry.getEntryList().locateEntry(fileName);
            dirEntry.removeEntry(oldEntry);
        }
        if (!dirFiles.isEmpty()) {
            cvsProject.writeAdminFiles();
        }
        */
    }

    /**
     * Remove the empty directories from the local repository
     */
    public void pruneEmptyDirs() {
        //cvsProject.pruneEmptySubDirs(true);
    }

    /**
     * @param remoteDir
     */
    public void add(RemoteDirectory remoteDir) {
        File dir = getLocalDir(remoteDir);
        Entry entry = null;
        String dirName = WebBrowser.removeFinalSlash(dir.getName());
        try {
            entry = handler.getEntry(dir);
            if (entry == null) {
                throw new IOException("Entry not found");
            }
        } catch (IOException ex) {
            String lastModified = Entry.getLastModifiedDateFormatter().format(new Date());
            entry = new Entry("D/" + dirName + "////");
        }
        
        String localDirectory = WebBrowser.removeFinalSlash(dir.getParent());
        String repositoryPath = WebBrowser.removeFinalSlash(remoteDir.getDirectoryName());
        int lastSlash = repositoryPath.lastIndexOf('/');
        if (lastSlash > 0) {
            repositoryPath = repositoryPath.substring(0, lastSlash);
        }
        try {
            handler.updateAdminData(localDirectory, repositoryPath, entry, globalOptions);
        } catch (IOException ex) {
            ex.printStackTrace();
            DefaultLogger.getInstance().error("Cannot update CVS entry for directory " + dir);
            throw new RuntimeException("Cannot update CVS entry for directory " + dir);
        }
    }
    
}
