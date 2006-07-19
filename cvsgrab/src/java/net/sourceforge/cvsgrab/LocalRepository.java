/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */
package net.sourceforge.cvsgrab;

import org.netbeans.lib.cvsclient.admin.AdminHandler;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.command.GlobalOptions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

/**
 * The local repository where the files are stored on this computer.
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 */

public class LocalRepository {
    
    public static final int UPDATE_NEEDED = 1;
    public static final int UPDATE_NO_CHANGES = 2;
    public static final int UPDATE_LOCAL_CHANGE = 3;
    public static final int UPDATE_MERGE_NEEDED = 4;
    public static final int UPDATE_IMPOSSIBLE = 5;
    
    private AdminHandler _handler;
    private GlobalOptions _globalOptions = new GlobalOptions();
    /**
     * The local root of the repository
     */
    private File _localRootDir;
    /**
     * The local root of the project
     */
    private File _localProjectDir;
    private int _newFiles = 0;
    private int _updatedFiles = 0;
    private int _removedFiles = 0;
    private int _failedUpdates = 0;
    private boolean _cleanUpdate;

    /**
     * Constructor for the LocalRepository object
     *
     * @param cvsGrab The cvs grabber
     */
    public LocalRepository(CVSGrab cvsGrab) {
        _handler = new CVSGrabAdminHandler(cvsGrab);
        _globalOptions.setCVSRoot(cvsGrab.getCvsRoot());
        _globalOptions.setCheckedOutFilesReadOnly(true);
        _localRootDir = new File(cvsGrab.getDestDir());
        _cleanUpdate = cvsGrab.isCleanUpdate();
        _localProjectDir = new File(_localRootDir, cvsGrab.getPackagePath());
    }
    
    /**
     * Gets the root directory
     *
     * @return The rootDirectory value
     */
    public File getLocalRootDir() {
        return _localRootDir;
    }

    /**
     * Gets the local directory to use for storing the contents of the remote directory
     * @param remoteDir The remote directory
     * @return The local directory
     */
    public File getLocalDir(RemoteDirectory remoteDir) {
        return new File(getLocalRootDir(), remoteDir.getLocalDir());
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
        return _newFiles;
    }

    /**
     * Gets the updated file count
     *
     * @return The updatedFileCount value
     */
    public int getUpdatedFileCount() {
        return _updatedFiles;
    }

    /**
     * Gets the removed file count
     *
     * @return The deletedFileCount value
     */
    public int getRemovedFileCount() {
        return _removedFiles;
    }

    /**
     * Gets the failed update count
     *
     * @return The failedUpdates value
     */
    public int getFailedUpdateCount() {
        return _failedUpdates;
    }

    /**
     * Gets the cleanUpdate flag.
     * 
     * @return the cleanUpdate.
     */
    public boolean isCleanUpdate() {
        return _cleanUpdate;
    }
    
    /**
     * Resets the counters in this class 
     */
    public void resetFileCounts() {
        _newFiles = 0;
        _updatedFiles = 0;
        _removedFiles = 0;
        _failedUpdates = 0;
    }

    /**
     * Return true if the file needs to be updated
     *
     * @param remoteFile The remote file
     * @return true if the file needs to be loaded from the server
     */
    public int checkUpdateStatus(RemoteFile remoteFile) {
        boolean needUpdate = true;
        File file = getLocalFile(remoteFile);
        
        if (file.exists()) {
            try {
                Entry entry = _handler.getEntry(file);
                if (entry == null) {
                    CVSGrab.getLog().debug("No entry for file " + file);
                    _failedUpdates++;
                    return UPDATE_IMPOSSIBLE;
                } else {
                    needUpdate = !remoteFile.getVersion().equals(entry.getRevision());
                    boolean locallyModified = isLocallyModified(file, entry);
                    if (locallyModified) {
                        CVSGrab.getLog().debug("File " + file + " is locally modified");
                    }
                    if (needUpdate) {
                        if (locallyModified) {
                            CVSGrab.getLog().debug("File " + file + " was modified since last update, cannot upload the new version of this file");
                            CVSGrab.getLog().debug("Last modified date on disk: " + new Date(file.lastModified()));
                            CVSGrab.getLog().debug("Last modified date on cvs: " + entry.getLastModified());
                            // TODO: remove this when the merge functionality works
                            _failedUpdates++;
                            return UPDATE_MERGE_NEEDED;
                        } else {
                            CVSGrab.getLog().debug("New version available on the remote repository for file " + file);
                            return UPDATE_NEEDED;
                        }
                    } else {
                        if (locallyModified) {
                            CVSGrab.getLog().debug("File " + file + " was modified since last update, cannot upload the new version of this file");
                            CVSGrab.getLog().debug("Last modified date on disk: " + new Date(file.lastModified()));
                            CVSGrab.getLog().debug("Last modified date on cvs: " + entry.getLastModified());
                        }
                        return locallyModified ? UPDATE_LOCAL_CHANGE : UPDATE_NO_CHANGES;
                    }
                }
            } catch (IOException ex) {
                // ignore
                ex.printStackTrace();
                _failedUpdates++;
                return UPDATE_IMPOSSIBLE;
            }
        } else {
            return UPDATE_NEEDED;
        }
    }

    /**
     * Returns the local version of the remote file
     * @param remoteFile The remote file
     * @return The version of the local file, or null if it doesn't exist
     */
    public String getLocalVersion(RemoteFile remoteFile) {
        File file = getLocalFile(remoteFile);
        
        if (file.exists()) {
            try {
                Entry entry = _handler.getEntry(file);
                if (entry == null) {
                    return null;
                } else {
                    return entry.getRevision();
                }
            } catch (IOException ex) {
                // ignore
                ex.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
    
    private boolean isLocallyModified(File file, Entry entry) {
        // Allow a tolerance of 1 minute because on Windows seconds are omitted
        return (file.lastModified() > entry.getLastModified().getTime() + 60*1000);
    }
    
    /**
     * Update a file version in the local CVS entries
     *
     * @param remoteFile The remote file
     */
    // synchronized as it can be accessed from multiple download threads
    public synchronized void updateFileVersion(RemoteFile remoteFile) {
        File dir = getLocalDir(remoteFile.getDirectory());
        File file = getLocalFile(remoteFile);
        Entry entry = null;
        Date lastModified = remoteFile.getLastModified();
        if (lastModified == null) {
            lastModified = new Date();
        }
        try {
            entry = _handler.getEntry(file);
            if (entry == null) {
                throw new IOException("Entry not found");
            }
            entry.setRevision(remoteFile.getVersion());
            entry.setDate(lastModified);
            _updatedFiles++;
        } catch (IOException ex) {
            boolean binary = remoteFile.isBinary();
            String lastModifiedStr = Entry.getLastModifiedDateFormatter().format(lastModified);
            entry = new Entry("/" + remoteFile.getName() + "/" + remoteFile.getVersion() + "/" 
                    + lastModifiedStr + "/" + (binary ? "-kb/" : "/"));
            _newFiles++;
        }
        
        String localDirectory = WebBrowser.removeFinalSlash(dir.getAbsolutePath());
        String repositoryPath = remoteFile.getDirectory().getDirectoryPath();
        try {
            _handler.updateAdminData(localDirectory, repositoryPath, entry, _globalOptions);
        } catch (IOException ex) {
            _failedUpdates++;
            CVSGrab.getLog().error("Cannot update CVS entry for file " + file, ex);
            throw new RuntimeException("Cannot update CVS entry for file " + file);
        }
    }
    
    /**
     * Backup a remote file that was locally modified
     * 
     * @param remoteFile The remote file
     */
    public void backupFile(RemoteFile remoteFile) {
        File dir = getLocalDir(remoteFile.getDirectory());
        File file = getLocalFile(remoteFile);
        File backupFile = new File(dir, ".#" + remoteFile.getName() + "." + remoteFile.getVersion());
        CVSGrab.getLog().info("Move " + remoteFile.getName() + " to " + backupFile.getName());
        //file.renameTo(backupFile);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            try {
                in = new BufferedInputStream(new FileInputStream(file));
                out = new FileOutputStream(backupFile);
                
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
            _failedUpdates++;
            ex.printStackTrace();
            CVSGrab.getLog().error("Cannot create backup for file " + file);
            throw new RuntimeException("Cannot create backup for file " + file);
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
        try {
            _handler.removeEntry(file);
            _failedUpdates++;
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
    // synchronized as it can be accessed from multiple download threads
    public synchronized void cleanRemovedFiles(RemoteDirectory remoteDirectory) {
        try {
            File dir = getLocalDir(remoteDirectory);
            Entry dirEntry = _handler.getEntry(dir);
            Vector dirFiles = new Vector();
            if (dirEntry == null) {
                //System.out.println("Null entries in " + dir);
                return;
            }
            for (Iterator i = _handler.getEntries(dir); i.hasNext(); ) {
                Entry cvsFile = (Entry) i.next();
                if (cvsFile.isDirectory()) {
                    continue;
                }
                dirFiles.add(cvsFile.getName());
            }
            RemoteFile[] lastDirFiles = remoteDirectory.getRemoteFiles();
            for (int i = 0; i < lastDirFiles.length; i++) {
                dirFiles.remove(lastDirFiles[i].getName());
            }
            _removedFiles += dirFiles.size();
            for (Iterator i = dirFiles.iterator(); i.hasNext(); ) {
                String fileName = (String) i.next();
                File file = new File(getLocalDir(remoteDirectory), fileName);
                CVSGrab.getLog().debug("Removing " + file);
                _handler.removeEntry(file);
                file.delete();
            }
        } catch (IOException ex) {
            _failedUpdates++;
            ex.printStackTrace();
            CVSGrab.getLog().error("Error while removing files marked for deletion");
        }
    }

    /**
     * Remove any directory that doesn't contain any files
     */
    public void pruneEmptyDirectories() throws IOException {
        pruneEmptyDirectory(_localProjectDir);
    }
    
    /**
     * Prunes a directory, recursively pruning its subdirectories
     * @param directory the directory to prune
     */
    private boolean pruneEmptyDirectory(File directory) {
        boolean empty = true;

        final File[] contents = directory.listFiles();

        // should never be null, but just in case...
        if (contents != null) {
            for (int i = 0; i < contents.length; i++) {
                if (contents[i].isFile()) {
                    empty = false;
                } else {
                    if (!contents[i].getName().equals("CVS")) { //NOI18N
                        empty = pruneEmptyDirectory(contents[i]);
                    }
                }
            }

            if (empty) {
                // check this is a CVS directory and not some directory the user
                // has stupidly called CVS...
                final File entriesFile = new File(directory, "CVS/Entries"); //NOI18N
                if (entriesFile.exists()) {
                    final File adminDir = new File(directory, "CVS"); //NOI18N
                    final File[] adminFiles = adminDir.listFiles();
                    for (int i = 0; i < adminFiles.length; i++) {
                        adminFiles[i].delete();
                    }
                    CVSGrab.getLog().debug("Removing empty directory " + directory);
                    adminDir.delete();
                    try {
                        // Remove the directory from the entries
                        _handler.removeEntry(directory);
                    } catch (IOException ex) {
                        _failedUpdates++;
                        ex.printStackTrace();
                        CVSGrab.getLog().error("Error while removing empty directory");
                    }
                    directory.delete();
                    _removedFiles++;
                }
            }
        }

        return empty;
    }

    /**
     * Adds a remote directory in the local filesystem, and update the CVS admin entries for that directory.
     * 
     * @param remoteDir The remote directory
     */
    // synchronized as it can be access from the multiple download threads
    public synchronized void add(RemoteDirectory remoteDir) {
        File dir = getLocalDir(remoteDir);
        if (dir.equals(_localProjectDir)) {
            return;
        }

        Entry entry = null;
        String dirName = WebBrowser.removeFinalSlash(dir.getName());
        try {
            entry = _handler.getEntry(dir);
            if (entry == null) {
                throw new IOException("Entry not found");
            }
        } catch (IOException ex) {
            entry = new Entry("D/" + dirName + "////");
        }
        
        String localDirectory = WebBrowser.removeFinalSlash(dir.getParent());
        String repositoryPath = WebBrowser.removeFinalSlash(remoteDir.getDirectoryPath());
        int lastSlash = repositoryPath.lastIndexOf('/');
        if (lastSlash > 0) {
            repositoryPath = repositoryPath.substring(0, lastSlash);
        }
        try {
            _handler.updateAdminData(localDirectory, repositoryPath, entry, _globalOptions);
        } catch (IOException ex) {
            _failedUpdates++;
            ex.printStackTrace();
            CVSGrab.getLog().error("Cannot update CVS entry for directory " + dir);
            throw new RuntimeException("Cannot update CVS entry for directory " + dir);
        }
    }
    
}
