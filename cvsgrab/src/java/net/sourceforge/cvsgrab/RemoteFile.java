/*
 *  CVSGrab
 *  Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 *  Distributable under BSD license.
 *  See terms of license at gnu.org.
 */
 
package net.sourceforge.cvsgrab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.myers.MyersDiff;

/**
 * Represent a file stored in the remote repository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 12 oct. 2003
 */
public class RemoteFile {
    
    private RemoteDirectory _directory;
    private String _name;
    private String _version; 
    private boolean _inAttic = false;

    /**
     * Constructor for VersionedFile
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

    /**
     * @param repository
     */
    public void grab(LocalRepository repository) {
        // Remove all http params, keep only the file name
        _directory.registerRemoteFile(this);
        RemoteRepository remoteRepository = _directory.getRemoteRepository();
        LocalRepository localRepository = remoteRepository.getLocalRepository();
        boolean needUpdate = localRepository.needUpdate(this);
        if (!needUpdate) {
            return;
        }
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

    protected void upload() {
        RemoteRepository remoteRepository = _directory.getRemoteRepository();
        LocalRepository localRepository = remoteRepository.getLocalRepository();
        try {
            // Make the destination dirs
            File localDir = localRepository.getLocalDir(_directory);
            localDir.mkdirs();
            File destFile = new File(localDir, _name);
            CVSGrab.getLog().info("Updating " + destFile);
            String url = remoteRepository.getDownloadUrl(this);
            
            // Download the file
            WebBrowser.getInstance().loadFile(new GetMethod(url), destFile);

            localRepository.updateFileVersion(this);

        } catch (Exception ex) {
            localRepository.unregisterFile(this);
            _directory.unregisterRemoteFile(this);
            CVSGrab.getLog().error("IO Error: " + ex);
            ex.printStackTrace();
        }
    }

    static final String[] loadFile(String name) throws IOException
    {
        BufferedReader data = new BufferedReader(new FileReader(name));
        List lines = new ArrayList();
        String s;
        while ((s = data.readLine()) != null)
        {
            lines.add(s);
        }
        return (String[])lines.toArray(new String[lines.size()]);
    }
    
    public static void main(String[] args) throws Exception {
        Object[] orig = loadFile("RELEASE.txt");
        Object[] rev = loadFile("RELEASE2.txt");

        MyersDiff df = new MyersDiff();
        Revision r = df.diff(orig, rev);

        System.err.println("------");
        System.out.print(r.toRCSString());
        System.err.println("------" + new Date());

        try
        {
            Object[] reco = r.patch(orig);
            //String recos = Diff.arrayToString(reco);
            if (!Diff.compare(rev, reco))
            {
                System.err.println("INTERNAL ERROR:"
                        + "files differ after patching!");
            }
        }
        catch (Throwable o)
        {
            System.out.println("Patch failed");
        }
    }
    
}
