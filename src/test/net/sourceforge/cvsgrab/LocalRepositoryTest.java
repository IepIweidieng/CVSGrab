package net.sourceforge.cvsgrab;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


/**
 * Test for LocalRepository
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 8 mars 2004
 */
public class LocalRepositoryTest extends AbstractTestCase {

    private LocalRepository localRepository;
    private File localRepoDir;
    private RemoteRepository remoteRepository;
    
    /**
     * Constructor for LocalRepositoryTest
     * @param name
     */
    public LocalRepositoryTest(String name) {
        super(name);
    }
    
    /** 
     * {@inheritDoc}
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        File localRootDir = new File(basedir, "target/testRepo");
        CVSGrab cvsGrab = new CVSGrab();
        cvsGrab.setCvsRoot("testRoot");
        cvsGrab.setDestDir(localRootDir.getPath());
        cvsGrab.getWebOptions().setRootUrl("rootUrl");
        cvsGrab.getWebOptions().setPackagePath("package");
        cvsGrab.getWebOptions().setWebInterfaceId("WebInterface");
        cvsGrab.setCleanUpdate(true);
        localRepoDir = new File(localRootDir, "package");
        localRepoDir.mkdirs();
        localRepository = new LocalRepository(cvsGrab);
        remoteRepository = new RemoteRepository("testRoot", localRepository);
        File file = new File(basedir, "src/conf/FileTypes.properties");
        Properties fileTypes = new Properties();
        InputStream is = new FileInputStream(file);
        fileTypes.load(is);
        RemoteFile.setFileTypes(fileTypes);
    }

    /** 
     * {@inheritDoc}
     * @throws java.lang.Exception
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        cleanDir(localRepoDir);
        localRepoDir.delete();
    }
    
    private void cleanDir(File dir) {
        final File[] contents = dir.listFiles();
        if (contents == null) {
            return;
        }

        for (int i = 0; i < contents.length; i++) {
            if (contents[i].isDirectory()) {
                cleanDir(contents[i]);
            }
            contents[i].delete();
        }
    }
    
    public void testPruneEmptyDirs() throws Exception {
        RemoteDirectory dirRootRemote = new RemoteDirectory(remoteRepository, "package", "package");
        localRepository.add(dirRootRemote);
        File a = new File(localRepoDir, "a.txt");
        a.createNewFile();
        RemoteFile aRemote = new RemoteFile("a.txt", "1.1");
        aRemote.setDirectory(dirRootRemote);
        localRepository.updateFileVersion(aRemote);
        File dir1 = new File(localRepoDir, "dir1");
        dir1.mkdir();
        RemoteDirectory dir1Remote = new RemoteDirectory(dirRootRemote, "dir1");
        localRepository.add(dir1Remote);
        File b = new File(dir1, "b.txt");
        b.createNewFile();
        RemoteFile bRemote = new RemoteFile("b.txt", "1.1");
        bRemote.setDirectory(dir1Remote);
        localRepository.updateFileVersion(bRemote);
        
        File dir2 = new File(localRepoDir, "dir2");
        dir2.mkdir();
        RemoteDirectory dir2Remote = new RemoteDirectory(dirRootRemote, "dir2");
        localRepository.add(dir2Remote);
        
        File dir21 = new File(dir2, "dir21");
        dir21.mkdir();
        RemoteDirectory dir21Remote = new RemoteDirectory(dir2Remote, "dir21");
        localRepository.add(dir21Remote);
        
        File c = new File(dir21, "c.txt");
        c.createNewFile();
        RemoteFile cRemote = new RemoteFile("c.txt", "1.1");
        cRemote.setDirectory(dir21Remote);
        localRepository.updateFileVersion(cRemote);
        
        c.delete();
        localRepository.cleanRemovedFiles(dir21Remote);
        
        File dir3 = new File(localRepoDir, "dir3");
        dir3.mkdir();
        RemoteDirectory dir3Remote = new RemoteDirectory(dirRootRemote, "dir3");
        localRepository.add(dir3Remote);
        
        // Add a file in dir3 but don't manage it with cvs
        File d = new File(dir3, "d.txt");
        d.createNewFile();
        
        localRepository.pruneEmptyDirectories();
        
        assertFalse(dir21.exists());
        assertFalse(dir2.exists());
        assertTrue(dir1.exists());
        assertTrue(dir3.exists());
        assertTrue(d.exists());
    }
}
