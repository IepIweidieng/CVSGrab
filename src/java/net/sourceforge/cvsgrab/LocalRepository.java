/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
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

    private AdminHandler _handler = new StandardAdminHandler();
    private GlobalOptions _globalOptions = new GlobalOptions();
    /**
     * A store of potentially empty directories. When a directory has a file
     * in it, it is removed from this set. This set allows the prune option
     * to be implemented.
     */
    private final Set _emptyDirectories = new HashSet();
    /** 
     * performs checkout to specified directory other than the module.
     * @todo use this variable to store files in a directory with different name than the package
     */
    private String _checkoutDirectory;
    
    /**
     * The local root of the repository
     */
    private File _localRootDir;
    private int _newFiles = 0;
    private int _updatedFiles = 0;
    private int _removedFiles = 0;
    private int _failedUpdates = 0;

    /**
     * Constructor for the LocalRepository object
     *
     * @param cvsRoot The cvs root 
     * @param destDir The destination directory
     * @param packageName The package 
     */
    public LocalRepository(String cvsRoot, String destDir, String packageName) {
        _globalOptions.setCVSRoot(cvsRoot);
        _globalOptions.setCheckedOutFilesReadOnly(true);
        _localRootDir = new File(destDir);
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
     * Description of the Method
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
    public boolean needUpdate(RemoteFile remoteFile) {
        boolean needUpdate = true;
        File file = getLocalFile(remoteFile);
        
        if (file.exists()) {
            try {
                Entry entry = _handler.getEntry(file);
                if (entry != null) {
                    needUpdate = !remoteFile.getVersion().equals(entry.getRevision());
                    if (needUpdate) {
                        Date fileLastModified = new Date(file.lastModified());
                        if (fileLastModified.after(entry.getLastModified())) {
                            DefaultLogger.getInstance().info("File " + file + " was modified since last update, cannot upload the new version of this file");
                            needUpdate = false;
                        }
                    }
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
            entry = _handler.getEntry(file);
            if (entry == null) {
                throw new IOException("Entry not found");
            }
            entry.setRevision(remoteFile.getVersion());
            entry.setDate(new Date());
            _updatedFiles++;
        } catch (IOException ex) {
            String lastModified = Entry.getLastModifiedDateFormatter().format(new Date());
            entry = new Entry("/" + remoteFile.getName() + "/" + remoteFile.getVersion() + "/" + lastModified + "//");
            _newFiles++;
        }
        
        String localDirectory = WebBrowser.removeFinalSlash(dir.getAbsolutePath());
        String repositoryPath = remoteFile.getDirectory().getDirectoryName();
        try {
            _handler.updateAdminData(localDirectory, repositoryPath, entry, _globalOptions);
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
    public void cleanRemovedFiles(RemoteDirectory remoteDirectory) {
        try {
            File dir = getLocalDir(remoteDirectory);
            Entry dirEntry = _handler.getEntry(dir);
            Vector dirFiles = new Vector();
            if (dirEntry == null) {
                //System.out.println("Null entries in " + dir);
                return;
            }
            _emptyDirectories.add(dir);
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
                DefaultLogger.getInstance().verbose("Removing " + file);
                file.delete();
                _handler.removeEntry(file);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            DefaultLogger.getInstance().error("Error while removing files marked for deletion");
        }
    }

    /**
     * Remove any directories that don't contain any files
     */
    public void pruneEmptyDirectories() throws IOException {
        final Iterator it = _emptyDirectories.iterator();
        while (it.hasNext()) {
            final File dir = (File)it.next();
            // we might have deleted it already (due to recursive delete)
            // so we need to check existence
            if (dir.exists()) {
                pruneEmptyDirectory(dir);
            }
        }
        _emptyDirectories.clear();
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
                }
                else {
                    if (!contents[i].getName().equals("CVS")) { //NOI18N
                        empty = pruneEmptyDirectory(contents[i]);
                    }
                }

                if (!empty) {
                    break;
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
                    adminDir.delete();
                    directory.delete();
                }
            }
        }

        return empty;
    }

    /**
     * @param remoteDir
     */
    public void add(RemoteDirectory remoteDir) {
        File dir = getLocalDir(remoteDir);
        Entry entry = null;
        String dirName = WebBrowser.removeFinalSlash(dir.getName());
        try {
            entry = _handler.getEntry(dir);
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
            _handler.updateAdminData(localDirectory, repositoryPath, entry, _globalOptions);
        } catch (IOException ex) {
            ex.printStackTrace();
            DefaultLogger.getInstance().error("Cannot update CVS entry for directory " + dir);
            throw new RuntimeException("Cannot update CVS entry for directory " + dir);
        }
    }
    
}
