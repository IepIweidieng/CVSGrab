/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */
package net.sourceforge.cvsgrab;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Application class for CVS Grab. <br>
 * CVSGrab loads and updates the files from the ViewCVS web interface of a CVS
 * repository
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 * @todo Read and parse modules file to read a module from the repository
 */

public class CVSGrab {
    static final String DUMMY_ROOT = ":pserver:anonymous@dummyhost:/dummyroot";
    private static final String FORUM_URL = "http://sourceforge.net/forum/forum.php?forum_id=174128";
    private static final String VERSION = "2.0";
    private static Log LOG;

    private boolean _verbose = true;
    private boolean _pruneEmptyDirs = false;
    private boolean _error = false;
    private String _rootUrl;
    private String _packagePath;
    private String _packageDir;
    private String _destDir;
    private String _versionTag;
    private String _queryParams;
    private String _cvsRoot = DUMMY_ROOT;

    public static Log getLog() {
        if (LOG == null) {
            LOG = LogFactory.getLog(CVSGrab.class);
        }
        return LOG;
    }
    
    public static void setLog(Log log) {
        LOG = log;
    }
    
    /**
     * Constructor for the CVSGrab object
     */
    public CVSGrab() { }

    /**
     * The main program for the CVSGrab class
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        String proxyHost = null;
        int proxyPort = 0;
        String proxyNTDomain = null;
        String proxyUser = null;
        String proxyPassword = null;
        String webUser = null;
        String webPassword = null;
        CVSGrab grabber = new CVSGrab();

        cvsgrabLogLevel("info", "INFO");
        httpclientLogLevel("error", "SEVERE");
        for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().equals("-help")) {
                printHelp();
                return;
            }
            if (args[i].toLowerCase().equals("-rooturl")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setRootUrl(args[i + 1]);
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-packagepath")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setPackagePath(args[i + 1]);
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-destdir")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setDestDir(args[i + 1]);
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-packagedir")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setPackageDir(args[i + 1]);
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-tag")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setVersionTag(args[i + 1]);
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-queryparams")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setQueryParams(args[i + 1]);
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-cvsroot")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setCvsRoot(args[i + 1]);
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-debug")) {
                if (!args[i + 1].startsWith("-")) {
                    boolean debug = args[i + 1].toLowerCase().equals("true");
                    if (debug) {
                        cvsgrabLogLevel("debug", "FINE");                        
                        httpclientLogLevel("info", "INFO");
                    }
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-debug:wire")) {
                if (!args[i + 1].startsWith("-")) {
                    boolean debug = args[i + 1].toLowerCase().equals("true");
                    if (debug) {
                        httpclientLogLevel("debug", "FINE");
                        logLevel("httpclient.wire", "debug", "FINE");
                    }
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-verbose")) {
                if (!args[i + 1].startsWith("-")) {
                    boolean verbose = args[i + 1].toLowerCase().equals("true");
                    if (verbose) {
                        cvsgrabLogLevel("info", "INFO");                        
                        httpclientLogLevel("error", "SEVERE");
                    } else {
                        cvsgrabLogLevel("warn", "WARNING");                        
                        httpclientLogLevel("error", "SEVERE");
                    }
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-prune")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setPruneEmptyDirs(args[i + 1].toLowerCase().equals("true"));;
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-proxyhost")) {
                if (!args[i + 1].startsWith("-")) {
                    proxyHost = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-proxyport")) {
                if (!args[i + 1].startsWith("-")) {
                    proxyPort = Integer.parseInt(args[i + 1]);
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-proxyntdomain")) {
                if (!args[i + 1].startsWith("-")) {
                    proxyNTDomain = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-proxyuser")) {
                if (!args[i + 1].startsWith("-")) {
                    proxyUser = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-proxypassword")) {
                if (!args[i + 1].startsWith("-")) {
                    proxyPassword = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-webuser")) {
                if (!args[i + 1].startsWith("-")) {
                    webUser = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-webpassword")) {
                if (!args[i + 1].startsWith("-")) {
                    webPassword = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-connections")) {
                if (!args[i + 1].startsWith("-")) {
                    int connections = Integer.parseInt(args[i + 1]);
                    if (connections > 0) {
                        ThreadPool.init(connections);
                    }
                    i++;
                }
            }
        }
        if (proxyHost != null) {
            WebBrowser.getInstance().useProxy(proxyHost, proxyPort, proxyNTDomain, proxyUser, proxyPassword);
        }
        if (webUser != null) {
            WebBrowser.getInstance().useWebAuthentification(webUser, webPassword);
        }
        grabber.grabCVSRepository();
    }

    private static void cvsgrabLogLevel(String simpleLevel, String jdk14Level) {
        logLevel("net.sourceforge.cvsgrab", simpleLevel, jdk14Level);
    }

    private static void httpclientLogLevel(String simpleLevel, String jdk14Level) {
        logLevel("org.apache.commons.httpclient", simpleLevel, jdk14Level);
    }

    private static void logLevel(String packageName, String simpleLevel, String jdk14Level) {
        if (SystemUtils.isJavaVersionAtLeast(1.4f)) {
            setJdk14LogLevel(packageName + ".level", jdk14Level);
        }
        System.setProperty("org.apache.commons.logging.simplelog.log." + packageName, simpleLevel);                        
    }
    
    /**
     * @param properties
     */
    private static void setJdk14LogLevel(String className, String level) {
        try {
            Class loggerClass = Class.forName("java.util.logging.Logger");
            Class levelClass = Class.forName("java.util.logging.Level");
            Object logger = loggerClass.getMethod("getLogger", new Class[] {String.class}).invoke(null, new Object[] {className});
            Object levelObj = levelClass.getField(level).get(null);
            loggerClass.getMethod("setLevel", new Class[] {levelClass}).invoke(logger, new Object[] {levelObj});
        } catch (Exception ex) {
            System.err.println("Cannot change the configuration of the Java 1.4 logger");
            ex.printStackTrace();
        }
    }

    /**
     * Prints help for the command line program
     */
    public static void printHelp() {
        System.out.println("CVSGrab version " + VERSION);
        System.out.println("Command line options:");
        System.out.println("\t-help This help message");
        System.out.println("\t-rootUrl <url> The root url used to access the CVS repository from a web browser");
        System.out.println("\t-packagePath <path> The path relative to rootUrl of the package or module to download");
        System.out.println("\t-destDir <dir> The destination directory");
        System.out.println("\t-packageDir <path> [optional] The name of the package to use locally, relative to destDir, overrides packagePath");
        System.out.println("\t-tag <tag> [optional] The version tag of the files to download");
        System.out.println("\t-queryParams <query params> [optional] Additional query parameters");
        System.out.println("\t-cvsRoot <cvs root> [optional] The original cvs root, used to maintain compatibility with a standard CVS client");
        System.out.println("\t-verbose true|false [optional] Verbosity. Default is verbose");
        System.out.println("\t-prune true|false [optional] Prune (remove) the empty directories. Default is false");
        System.out.println("\t-connections <nb of connections> [optional] The number of simultaneous connections to use for downloads, default 1");
        System.out.println("\t-proxyHost [optional] Proxy host");
        System.out.println("\t-proxyPort [optional] Proxy port");
        System.out.println("\t-proxyNTDomain [optional] NT Domain for the authentification on a MS proxy");
        System.out.println("\t-proxyUser [optional] Username for the proxy");
        System.out.println("\t-proxyPassword [optional] Password for the proxy. If this option is omitted, then cvsgrab will prompt securely for the password.");
        System.out.println("\t-webUser [optional] Username for the web server");
        System.out.println("\t-webPassword [optional] Password for the web server. If this option is omitted, then cvsgrab will prompt securely for the password.");
        System.out.println("Copyright (c) 2002-2003 - Ludovic Claude.");
        // Undocumented options: debug, debug:wire
    }

    /**
     * Gets the prune empty dirs
     *
     * @return The pruneEmptyDirs value
     */
    public boolean getPruneEmptyDirs() {
        return _pruneEmptyDirs;
    }

    /**
     * Sets the prune empty dirs
     *
     * @param value The new pruneEmptyDirs value
     */
    public void setPruneEmptyDirs(boolean value) {
        _pruneEmptyDirs = value;
    }
    
    /**
     * @return Returns the rootUrl.
     */
    public String getRootUrl() {
        return _rootUrl;
    }

    /**
     * Sets the url for the root of the CVS repository accessible via ViewCVS
     * @param rootUrl The rootUrl to set.
     */
    public void setRootUrl(String rootUrl) {
        _rootUrl = WebBrowser.forceFinalSlash(rootUrl);
    }

    /**
     * @return Returns the packagePath.
     */
    public String getPackagePath() {
        return _packagePath;
    }

    /**
     * Sets the path of the package/module to update from CVS
     * @param packagePath The packagePath to set.
     */
    public void setPackagePath(String packagePath) {
        _packagePath = WebBrowser.forceFinalSlash(packagePath);
    }

    /**
     * @return Returns the destDir.
     */
    public String getDestDir() {
        return _destDir;
    }

    /**
     * Sets the destination directory for the files to be retrieved from the repository
     * @param destDir The destDir to set.
     */
    public void setDestDir(String destDir) {
        _destDir = WebBrowser.forceFinalSlash(destDir);
    }

    /**
     * @return Returns the packageDir.
     */
    public String getPackageDir() {
        if (_packageDir == null) {
            return getPackagePath();
        }
        return _packageDir;
    }

    /**
     * @param packageDir The packageDir to set.
     */
    public void setPackageDir(String packageDir) {
        _packageDir = WebBrowser.forceFinalSlash(packageDir);
    }
    
    /**
     * @return Returns the cvsRoot.
     */
    public String getCvsRoot() {
        return _cvsRoot;
    }

    /**
     * Sets the cvs root. This is used by CVSGrab only to rebuild the
     * CVS admin files that may be used later by a standard CVS client.
     * @param cvsRoot The cvsRoot to set.
     */
    public void setCvsRoot(String cvsRoot) {
        _cvsRoot = WebBrowser.forceFinalSlash(cvsRoot);
    }

    /**
     * @return Returns the versionTag.
     */
    public String getVersionTag() {
        return _versionTag;
    }

    /**
     * Sets the name of the tagged version of the files to retrieve, or null  
     * @param versionTag The versionTag to set.
     */
    public void setVersionTag(String versionTag) {
        _versionTag = versionTag;
    }

    /**
     * @return Returns the queryParams.
     */
    public String getQueryParams() {
        return _queryParams;
    }

    /**
     * @param queryParams The queryParams to set.
     */
    public void setQueryParams(String queryParams) {
        _queryParams = queryParams;
    }

    /**
     * Main method for getting and updating files.
     */
    public void grabCVSRepository() {
        getLog().info("CVSGrab version " + VERSION + " starting...");

        if (_rootUrl == null || _destDir == null || _packagePath == null) {
            if (_rootUrl == null) {
                System.out.println("Error: rootUrl parameter is mandatory");
            }
            if (_destDir == null) {
                System.out.println("Error: destDir parameter is mandatory");
            }
            if (_packagePath == null) {
                System.out.println("Error: packagePath parameter is mandatory");
            }
            printHelp();
            return;
        }
        
        try {
            // check the parameters
            File dd = new File(_destDir);
            if (!dd.exists()) {
                throw new RuntimeException("Destination directory " + _destDir + " doesn't exist");
            }
            if (!dd.isDirectory()) {
                throw new RuntimeException("Destination " + _destDir + " is not a directory");
            }
            try {
                _destDir = dd.getCanonicalPath().replace(File.separatorChar, '/');
            } catch (IOException ex) {
                throw new IllegalArgumentException("Could not locate the destination directory " + _destDir + ", error was " + ex.getMessage());
            }

            // Auto detection of the type of the remote interface
            CvsWebInterface webInterface = detectWebInterface();
            if (webInterface == null) {
                getLog().error("Could not detect the type of the web interface");
                throw new RuntimeException("Could not detect the type of the web interface");
            }
            
            webInterface.setQueryParams(_queryParams);
            if (_versionTag != null) {
                webInterface.setVersionTag(_versionTag);
            }
            
            LocalRepository localRepository = new LocalRepository(_cvsRoot, _destDir, _packagePath);
            RemoteRepository remoteRepository = new RemoteRepository(_rootUrl, localRepository);
            remoteRepository.setWebInterface(webInterface);

            RemoteDirectory remoteDir = new RemoteDirectory(remoteRepository, _packagePath, getPackageDir());
            remoteRepository.registerDirectoryToProcess(remoteDir);
            while (remoteRepository.hasDirectoryToProcess()) {
                try {
                    remoteDir = remoteRepository.nextDirectoryToProcess();
                    remoteDir.loadContents();
                    localRepository.cleanRemovedFiles(remoteDir);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    getLog().error("Error while getting files from " + remoteDir.getUrl());
                    _error = true;
                }
            }
            if (_pruneEmptyDirs) {
                localRepository.pruneEmptyDirectories();
            }

            // Print a summary
            int newFileCount = localRepository.getNewFileCount();
            int updatedFileCount = localRepository.getUpdatedFileCount();
            int removedFileCount = localRepository.getRemovedFileCount();
            int failedUpdateCount = localRepository.getFailedUpdateCount();
            getLog().info("-----");
            if (newFileCount > 0) {
                getLog().info(newFileCount + " new files");
            }
            if (updatedFileCount > 0) {
                getLog().info(updatedFileCount + " updated files");
            }
            if (removedFileCount > 0) {
                getLog().info(removedFileCount + " removed files");
            }
            if (failedUpdateCount > 0) {
                getLog().error(failedUpdateCount + " files could not be downloaded");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            getLog().error(ex.getMessage());
            _error = true;
        }
        
        if (ThreadPool.getInstance() != null) {
            ThreadPool.getInstance().destroy();
        }
        
        if (_error) {
            getLog().error("There were some errors.");
            getLog().error("If you cannot find an obvious answer, report the problem to " + FORUM_URL);
        }
    }

    private CvsWebInterface detectWebInterface() {
        CvsWebInterface webInterface = null;
        try {
            webInterface = CvsWebInterface.findInterface(this);
            getLog().info("Detected cvs web interface: " + webInterface.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
        }    
        return webInterface;
    }
}
