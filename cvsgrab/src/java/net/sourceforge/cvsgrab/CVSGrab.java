/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */
package net.sourceforge.cvsgrab;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;

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
    private static final String VERSION = "2.0-beta";

    private boolean _verbose = true;
    private boolean _pruneEmptyDirs = false;
    private boolean _error = false;
    private String _rootUrl;
    private String _packageName;
    private String _destDir;
    private String _versionTag;
    private String _queryParams;
    private String _cvsRoot = DUMMY_ROOT;

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
            } else if (args[i].toLowerCase().equals("-package")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setPackageName(args[i + 1]);
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-destdir")) {
                if (!args[i + 1].startsWith("-")) {
                    grabber.setDestDir(args[i + 1]);
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
            } else if (args[i].toLowerCase().equals("-verbose")) {
                if (!args[i + 1].startsWith("-")) {
                    DefaultLogger.getInstance().setVerbose(args[i + 1].toLowerCase().equals("true"));;
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

    /**
     * Prints help for the command line program
     */
    public static void printHelp() {
        System.out.println("CVSGrab version " + VERSION);
        System.out.println("Command line options:");
        System.out.println("\t-help This help message");
        System.out.println("\t-rootUrl <url> The root url used to access the CVS repository from a web browser");
        System.out.println("\t-destDir <dir> The destination directory");
        System.out.println("\t-package <package> The package or module to download");
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
        System.out.println("\t-proxyPassword [optional] Password for the proxy");
        System.out.println("\t-webUser [optional] Username for the web server");
        System.out.println("\t-webPassword [optional] Password for the web server");
        System.out.println("Copyright (c) 2002-2003 - Ludovic Claude.");
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
        _rootUrl = rootUrl;
    }

    /**
     * @return Returns the packageName.
     */
    public String getPackageName() {
        return _packageName;
    }

    /**
     * Sets the name of the package/module to update from CVS
     * @param packageName The packageName to set.
     */
    public void setPackageName(String packageName) {
        _packageName = packageName;
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
        _destDir = destDir;
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
        _cvsRoot = cvsRoot;
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
        DefaultLogger.getInstance().info("CVSGrab version " + VERSION + " stating...");

        if (_rootUrl == null || _destDir == null || _packageName == null) {
            if (_rootUrl == null) {
                System.out.println("Error: rootUrl parameter is mandatory");
            }
            if (_destDir == null) {
                System.out.println("Error: destDir parameter is mandatory");
            }
            if (_packageName == null) {
                System.out.println("Error: package parameter is mandatory");
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
            _rootUrl = WebBrowser.forceFinalSlash(_rootUrl);

            // Auto detection of the type of the remote interface
            CvsWebInterface webInterface = null;
            try {
                Document doc = WebBrowser.getInstance().getDocument(new GetMethod(WebBrowser.addQueryParam(_rootUrl, _queryParams)));
                webInterface = CvsWebInterface.findInterface(doc);
                DefaultLogger.getInstance().info("Detected cvs web interface: " + webInterface.getType());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (webInterface == null) {
                DefaultLogger.getInstance().error("Could not detect the type of the web interface");
                throw new RuntimeException("Could not detect the type of the web interface");
            }
            
            webInterface.setQueryParams(_queryParams);
            if (_versionTag != null) {
                webInterface.setVersionTag(_versionTag);
            }
            
            LocalRepository localRepository = new LocalRepository(_cvsRoot, _destDir, _packageName);
            RemoteRepository remoteRepository = new RemoteRepository(_rootUrl, localRepository);
            remoteRepository.setWebInterface(webInterface);

            remoteRepository.registerDirectoryToProcess(_packageName);
            while (remoteRepository.hasDirectoryToProcess()) {
                RemoteDirectory remoteDir = null;
                try {
                    remoteDir = remoteRepository.nextDirectoryToProcess();
                    remoteDir.loadContents();
                    localRepository.cleanRemovedFiles(remoteDir);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    DefaultLogger.getInstance().error("Error while getting files from " + remoteDir.getUrl());
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
            DefaultLogger.getInstance().info("-----");
            if (newFileCount > 0) {
                DefaultLogger.getInstance().info(newFileCount + " new files");
            }
            if (updatedFileCount > 0) {
                DefaultLogger.getInstance().info(updatedFileCount + " updated files");
            }
            if (removedFileCount > 0) {
                DefaultLogger.getInstance().info(removedFileCount + " removed files");
            }
            if (failedUpdateCount > 0) {
                DefaultLogger.getInstance().error(failedUpdateCount + " files could not be downloaded");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            DefaultLogger.getInstance().error(ex.getMessage());
            _error = true;
        }
        
        if (ThreadPool.getInstance() != null) {
            ThreadPool.getInstance().destroy();
        }
        
        if (_error) {
            DefaultLogger.getInstance().error("There were some errors.");
            DefaultLogger.getInstance().error("If you cannot find an obvious answer, report the problem to " + FORUM_URL);
        }
    }

}
