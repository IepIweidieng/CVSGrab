/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under LGPL license.
 */
package net.sourceforge.cvsgrab;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;

import com.ice.cvsc.CVSProject;

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
    private static final String VERSION = "2.0-alpha";

    private String dir = null;
    private CVSProject cvsProject = null;
    private boolean verbose = true;
    private boolean pruneEmptyDirs = false;

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
        String rootUrl = null;
        String destDir = null;
        String packageName = null;
        String tag = null;
        String cvsHost = "dummyHost";
        String cvsRoot = "dummyRoot";
        String cvsUser = "anonymous";
        String verbose = "true";
        String prune = "false";
        String proxyHost = null;
        int proxyPort = 0;
        String proxyNTDomain = null;
        String proxyUser = null;
        String proxyPassword = null;
        String webUser = null;
        String webPassword = null;
        int threads = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().equals("-help")) {
                printHelp();
                return;
            }
            if (args[i].toLowerCase().equals("-rooturl")) {
                if (!args[i + 1].startsWith("-")) {
                    rootUrl = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-destdir")) {
                if (!args[i + 1].startsWith("-")) {
                    destDir = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-package")) {
                if (!args[i + 1].startsWith("-")) {
                    packageName = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-tag")) {
                if (!args[i + 1].startsWith("-")) {
                    tag = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-cvsuser")) {
                if (!args[i + 1].startsWith("-")) {
                    cvsUser = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-cvshost")) {
                if (!args[i + 1].startsWith("-")) {
                    cvsHost = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-cvsroot")) {
                if (!args[i + 1].startsWith("-")) {
                    cvsRoot = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-verbose")) {
                if (!args[i + 1].startsWith("-")) {
                    verbose = args[i + 1];
                    i++;
                }
            } else if (args[i].toLowerCase().equals("-prune")) {
                if (!args[i + 1].startsWith("-")) {
                    prune = args[i + 1];
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
            } else if (args[i].toLowerCase().equals("-threads")) {
                if (!args[i + 1].startsWith("-")) {
                    threads = Integer.parseInt(args[i + 1]);
                    i++;
                }
            }
        }
        if (rootUrl == null || destDir == null || packageName == null) {
            if (rootUrl == null) {
                System.out.println("Error: rootUrl parameter is mandatory");
            }
            if (destDir == null) {
                System.out.println("Error: destDir parameter is mandatory");
            }
            if (packageName == null) {
                System.out.println("Error: package parameter is mandatory");
            }
            printHelp();
            return;
        }
        DefaultLogger.getInstance().setVerbose(verbose.toLowerCase().equals("true"));
        CVSGrab grabber = new CVSGrab();
        grabber.setPruneEmptyDirs(prune.toLowerCase().equals("true"));
        if (proxyHost != null) {
            WebBrowser.getInstance().useProxy(proxyHost, proxyPort, proxyNTDomain, proxyUser, proxyPassword);
        }
        if (webUser != null) {
            WebBrowser.getInstance().useWebAuthentification(webUser, webPassword);
        }
        grabber.grabCVSRepository(rootUrl, destDir, packageName, tag, cvsUser, cvsHost, cvsRoot);
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
        System.out.println("\t-cvsHost <cvs host> [optional] The original cvs host, used to maintain compatibility with a standard CVS client");
        System.out.println("\t-cvsRoot <cvs root> [optional] The original cvs root, used to maintain compatibility with a standard CVS client");
        System.out.println("\t-verbose true|false [optional] Verbosity. Default is verbose");
        System.out.println("\t-prune true|false [optional] Prune (remove) the empty directories. Default is false");
        // TODO: System.out.println("\t-threads <nb of threads> [optional] The number of threads to use for simultaneous downloads");
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
        return pruneEmptyDirs;
    }

    /**
     * Sets the prune empty dirs
     *
     * @param value The new pruneEmptyDirs value
     */
    public void setPruneEmptyDirs(boolean value) {
        pruneEmptyDirs = value;
    }

    /**
     * Main method for getting and updating files. <br>
     * This method uses only the minimun nuner of arguments
     *
     * @param rootUrl The url for the root of the CVS repository accessible via
     *      ViewCVS
     * @param destDir Destination directory for the files to be retrieved from
     *      the repository
     * @param packageName Name of the package/module to update from CVS
     */
    public void grabCVSRepository(String rootUrl, String destDir, String packageName) {
        grabCVSRepository(rootUrl, destDir, packageName, null, "anonymous", "", "");
    }

    /**
     * Main method for getting and updating files.
     *
     * @param rootUrl The url for the root of the CVS repository accessible via
     *      ViewCVS
     * @param destDir Destination directory for the files to be retrieved from
     *      the repository
     * @param packageName Name of the package/module to update from CVS
     * @param tag The name of the tagged version of the files to retrieve, or
     *      null
     * @param cvsHost The cvs host. This is used by CVSGrab only to rebuild the
     *      CVS admin files that may be used later by a standard CVS client.
     * @param cvsRoot The cvs root. This is used by CVSGrab only to rebuild the
     *      CVS admin files that may be used later by a standard CVS client.
     * @param cvsUser The cvs user. This is used by CVSGrab only to rebuild the
     *      CVS admin files that may be used later by a standard CVS client.
     */
    public void grabCVSRepository(String rootUrl, String destDir, String packageName, String tag, String cvsUser, String cvsHost, String cvsRoot) {
        DefaultLogger.getInstance().info("CVSGrab version " + VERSION + " stating...");

        // check the parameters
        File dd = new File(destDir);
        if (!dd.exists()) {
            throw new RuntimeException("Destination directory " + destDir + " doesn't exist");
        }
        if (!dd.isDirectory()) {
            throw new RuntimeException("Destination " + destDir + " is not a directory");
        }
        try {
            destDir = dd.getCanonicalPath().replace(File.separatorChar, '/');
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not locate the destination directory " + destDir + ", error was " + ex.getMessage());
        }
        rootUrl = WebBrowser.forceFinalSlash(rootUrl);

        try {
            // Auto detection of the type of the remote interface
            CvsWebInterface webInterface = null;
            try {
                Document doc = WebBrowser.getInstance().getDocument(new GetMethod(rootUrl));
                webInterface = CvsWebInterface.findInterface(doc);
                DefaultLogger.getInstance().info("Detected cvs web interface: " + webInterface.getType());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (webInterface == null) {
                DefaultLogger.getInstance().error("Could not detect the type of the web interface");
                throw new RuntimeException("Could not detect the type of the web interface");
            }
            
            if (tag != null) {
                webInterface.setVersionTag(tag);
            }
            
            LocalRepository localRepository = new LocalRepository(cvsUser, cvsHost, cvsRoot, destDir, packageName);
            RemoteRepository remoteRepository = new RemoteRepository(rootUrl, localRepository);
            remoteRepository.setWebInterface(webInterface);

            remoteRepository.registerDirectoryToProcess(packageName);
            while (remoteRepository.hasDirectoryToProcess()) {
                RemoteDirectory remoteDir = null;
                try {
                    remoteDir = remoteRepository.nextDirectoryToProcess();
                    remoteDir.loadContents();
                    localRepository.update();
                    // TODO
                    //if (handler.isPageFullyLoaded()) {
                    localRepository.cleanRemovedFiles(remoteDir);
                    //} else {
                    //    DefaultLogger.getInstance().warn("Could not load the full html content at " + remoteDir.getUrl() + ", open this page in a browser and if you don't find an obvious solution, report the problem to http://sourceforge.net/forum/forum.php?forum_id=174128");
                    //}
                } catch (Exception ex) {
                    ex.printStackTrace();
                    DefaultLogger.getInstance().error("Error while getting files from " + remoteDir.getUrl());
                }
            }
            if (pruneEmptyDirs) {
                localRepository.pruneEmptyDirs();
            }
            localRepository.removeRootEntries();

            // Print a summary
            int newFileCount = localRepository.getNewFileCount();
            int updatedFileCount = localRepository.getUpdatedFileCount();
            int removedFileCount = localRepository.getRemovedFileCount();
            int failedUpdateCount = localRepository.getFailedUpdateCount();
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
                DefaultLogger.getInstance().error(failedUpdateCount + " files");
            }
        } catch (IOException ex) {
            DefaultLogger.getInstance().error("Could not initialise cvsgrab because the CVS admin files are corrupted");
            ex.printStackTrace();
        }
    }

}
