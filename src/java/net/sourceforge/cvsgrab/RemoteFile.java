/*
 *  CVSGrab
 *  Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 *  Distributable under BSD license.
 */
 
package net.sourceforge.cvsgrab;

import net.sourceforge.cvsgrab.util.ThreadPool;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.myers.MyersDiff;
import org.apache.commons.jrcs.diff.print.UnifiedPrint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Represents a file stored in the remote repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 12 oct. 2003
 */
public class RemoteFile {
    /**
     * File is a text file
     */
    public static final String TEXT_TYPE = "text";
    /**
     * File is a binary file
     */
    public static final String BINARY_TYPE = "binary";
    
    private static List textMatches = new ArrayList();
    private static List binaryMatches = new ArrayList();
    private RemoteDirectory _directory;
    private String _name;
    private String _version; 
    private boolean _inAttic = false;
    private Date _lastModified;
    
    /**
     * Sets the file types recognized by CVSGrab. Valid file types are text and binary.
     * The key is the pattern for the file type, and takes the form FileName or *.ext,
     * the value is the type of the file, text or binary.
     * @param fileTypes The new file types
     */
    public static void setFileTypes(Properties fileTypes) {
        for (Iterator i = fileTypes.keySet().iterator(); i.hasNext();) {
            String pattern = (String) i.next();
            String fileType = (String) fileTypes.get(pattern);
            if (TEXT_TYPE.equalsIgnoreCase(fileType)) {
                textMatches.add(new FileMatcher(pattern));
            } else if (BINARY_TYPE.equals(fileType)) {
                binaryMatches.add(new FileMatcher(pattern));
            } else {
                CVSGrab.getLog().error("Invalid file type for pattern " + pattern +
                        ". Was expecting text or binary instead of " + fileType);
            }        
        }
    }

    /**
     * Constructor for RemoteFile
     */
    public RemoteFile(String name, String version) {
        super();
        _name = name;
        _version = version;        
    }

    /**
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return _version;
    }

    /**
     * @return the directory
     */
    public RemoteDirectory getDirectory() {
        return _directory;
    }

    /**
     * @return true if the file is stored in the Attic (after delection or recovery from deletion)
     */
    public boolean isInAttic() {
        return _inAttic;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * @param inAttic
     */
    public void setInAttic(boolean inAttic) {
        _inAttic = inAttic;
    }

    /**
     * Sets the directory
     * @param directory
     */
    public void setDirectory(RemoteDirectory directory) {
        _directory = directory;
    }

    public Date getLastModified() {
        return _lastModified;
    }
    
    /**
     * @param repository
     */
    public void grab(LocalRepository repository) {
        // Remove all http params, keep only the file name
        _directory.registerRemoteFile(this);
        RemoteRepository remoteRepository = _directory.getRemoteRepository();
        LocalRepository localRepository = remoteRepository.getLocalRepository();
        int updateStatus = localRepository.checkUpdateStatus(this);
        switch (updateStatus) {
            case LocalRepository.UPDATE_NO_CHANGES:
                return;
            case LocalRepository.UPDATE_LOCAL_CHANGE:
                CVSGrab.getLog().info("M " + this);
                return;
            case LocalRepository.UPDATE_IMPOSSIBLE:
                CVSGrab.getLog().warn("cvs update: warning: cannot update " + this + ". A file with the same name is in the way.");
                break;
            case LocalRepository.UPDATE_NEEDED:
                CVSGrab.getLog().info("U " + this);
                doUpload();
                break;
            case LocalRepository.UPDATE_MERGE_NEEDED:
                if (repository.isCleanUpdate()) {
                    repository.backupFile(this);
                    CVSGrab.getLog().info("U " + this);
                    doUpload();
                } else {
                    CVSGrab.getLog().info("C " + this);
                    CVSGrab.getLog().warn("cvs update: warning: cannot merge this modified file with the new remote version, feature not yet supported");
                }
                break;
            default:
                throw new RuntimeException("Invalid status " + updateStatus);
        }
    }
    
    /**
     * @param repository
     * @param diffFile
     */
    public void diff(LocalRepository repository, PrintWriter writer, CVSGrab grabber) {
        RemoteRepository remoteRepository = _directory.getRemoteRepository();
        LocalRepository localRepository = remoteRepository.getLocalRepository();
        // Force the version of the remote file to be the same as the local file
        String localVersion = localRepository.getLocalVersion(this);
        if (localVersion != null) {
            _version = localVersion;
        }
        int updateStatus = localRepository.checkUpdateStatus(this);
        switch (updateStatus) {
            case LocalRepository.UPDATE_NO_CHANGES:
                CVSGrab.getLog().debug("File not updated " + this);
                return;
            case LocalRepository.UPDATE_IMPOSSIBLE:
                CVSGrab.getLog().warn("cvs update: warning: cannot update " + this + ". A file with the same name is in the way.");
                return;
            case LocalRepository.UPDATE_NEEDED:
                CVSGrab.getLog().info("U " + this);
                return;
            case LocalRepository.UPDATE_LOCAL_CHANGE:
            case LocalRepository.UPDATE_MERGE_NEEDED:
                CVSGrab.getLog().info("M " + this);
                if (isBinary()) {
                    CVSGrab.getLog().warn("File is modified locally but its type is binary. Update FileTypes.properties if needed.");
                    return;
                }
                try {
                    String url = remoteRepository.getDownloadUrl(this);
                    File origFile = File.createTempFile(_name, null);
                    File revFile = localRepository.getLocalFile(this);
                    
                    // Download the remote file
                    String[] orig = new String[0];
                    if (localVersion != null) {
                        // This file already exists on the repository as there is a version
                        WebBrowser.getInstance().loadFile(new GetMethod(url), origFile);
                        orig = loadFile(origFile.getAbsolutePath());
                    }
                    
                    String[] rev = loadFile(revFile.getAbsolutePath());
                    MyersDiff diff = new MyersDiff();
                    Revision revision = diff.diff(orig, rev);
                    StringBuffer sb = new StringBuffer();
                    UnifiedPrint print = new UnifiedPrint(sb, orig, rev);
                    // Local name: "src/java/net/sourceforge/cvsgrab/CVSGrabTask.java"
                    String localName = localRepository.getLocalFile(this).getCanonicalPath();
                    localName = localName.substring(new File(grabber.getDestDir()).getCanonicalPath().length() + 1);
                    print.setFileName(localName);
                    // RCS name : "/cvsroot/cvsgrab/cvsgrab/src/java/net/sourceforge/cvsgrab/CVSGrabTask.java,v"
                    String rcsName = WebBrowser.forceFinalSlash(grabber.getCvsRoot()) 
                        + WebBrowser.forceFinalSlash(getDirectory().getDirectoryPath()) + getName();
                    rcsName = rcsName.substring(rcsName.lastIndexOf(':') + 1);
                    print.setRCSFileName(rcsName);
                    print.setOriginalVersion(localRepository.getLocalVersion(this));
                    print.setRevisedModifDate(new Date(revFile.lastModified()));
                    revision.accept(print);
                    print.close();
                    writer.print(sb.toString());
                } catch (Exception ex) {
                    CVSGrab.getLog().error("IO Error: " + ex);
                    ex.printStackTrace();
                }

                break;
            default:
                throw new RuntimeException("Invalid status " + updateStatus);
        }
    }

    /**
     * @return true if the file is binary, false if it's a text file
     */
    public boolean isBinary() {
        for (Iterator i = textMatches.iterator(); i.hasNext();) {
            FileMatcher matcher = (FileMatcher) i.next();
            if (matcher.match(getName())) {
                return false;
            }
        }
        for (Iterator i = binaryMatches.iterator(); i.hasNext();) {
            FileMatcher matcher = (FileMatcher) i.next();
            if (matcher.match(getName())) {
                return true;
            }
        }
        CVSGrab.getLog().warn("Unknown file type for " + getName() + ", assuming it's binary");
        return true;
    }

    private void doUpload() {
        if (ThreadPool.getInstance() != null) {
            ThreadPool.getInstance().doTask(new Runnable() {
                public void run() {
                    upload();
                }
            });
        } else {
            upload();
        }
    }

    /**
     * {@inheritDoc}
     * @return a string representation
     */
    public String toString() {
        return getDirectory().toString() + getName();
    }
    
    protected void upload() {
        RemoteRepository remoteRepository = _directory.getRemoteRepository();
        LocalRepository localRepository = remoteRepository.getLocalRepository();
        try {
            // Make the destination dirs
            File localDir = localRepository.getLocalDir(_directory);
            localDir.mkdirs();
            File destFile = new File(localDir, _name);
            String url = remoteRepository.getDownloadUrl(this);
            
            // Download the file
            WebBrowser.getInstance().loadFile(new GetMethod(url), destFile);
            _lastModified = new Date();

            localRepository.updateFileVersion(this);

        } catch (Exception ex) {
            localRepository.unregisterFile(this);
            _directory.unregisterRemoteFile(this);
            CVSGrab.getLog().error("IO Error: " + ex);
            ex.printStackTrace();
        }
    }

    private static final String[] loadFile(String name) throws IOException {
        BufferedReader data = new BufferedReader(new FileReader(name));
        List lines = new ArrayList();
        String s;
        while ((s = data.readLine()) != null) {
            lines.add(s);
        }
        return (String[])lines.toArray(new String[lines.size()]);
    }
    
    private static class FileMatcher {
        private String pattern;
        
        /**
         * Constructor for FileMatcher
         * @param pattern the file pattern
         */
        public FileMatcher(String pattern) {
            super();
            this.pattern = pattern;
        }
        
        public boolean match(String fileName) {
            if (pattern.startsWith("*")) {
                return fileName.endsWith(pattern.substring(1));
            } else {
                return pattern.equalsIgnoreCase(fileName);
            }
        }
    }

}
