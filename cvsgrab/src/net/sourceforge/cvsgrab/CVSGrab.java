/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under LGPL license.
 */
package net.sourceforge.cvsgrab;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import com.ice.cvsc.*;
import hotsax.html.sax.*;

/**
 * Application class for CVS Grab. <br>
 * CVSGrab loads and updates the files from the ViewCVS web interface of a CVS
 * repository
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 * @todo Read and parse modules file to read a module from the repository
 * @toto Prune empty directories
 */

public class CVSGrab {

    private String dir = null;
    private CVSProject cvsProject = null;
    private boolean verbose = true;
    private boolean pruneEmptyDirs = false;
    private Logger log = new DefaultLogger();

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
        String proxyUser = null;
        String proxyPassword = null;

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
        CVSGrab grabber = new CVSGrab();
        grabber.getLog().setVerbose(verbose.toLowerCase().equals("true"));
        grabber.setPruneEmptyDirs(prune.toLowerCase().equals("true"));
        if (proxyHost != null) {
            grabber.useProxy(proxyHost, proxyPort, proxyUser, proxyPassword);
        }
        grabber.grabCVSRepository(rootUrl, destDir, packageName, tag, cvsUser, cvsHost, cvsRoot);
    }

    /**
     * Prints help for the command line program
     */
    public static void printHelp() {
        System.out.println("CVS Grab version 1.0");
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
        System.out.println("\t-proxyHost [optional] Proxy host");
        System.out.println("\t-proxyPort [optional] Proxy port");
        System.out.println("\t-proxyUser [optional] Username for the proxy");
        System.out.println("\t-proxyPassword [optional] Password for the proxy");
        System.out.println("Copyright (c) 2002 - Ludovic Claude.");
    }

    /**
     * Gets the log attribute
     *
     * @return The log value
     */
    public Logger getLog() {
        return log;
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
     * Sets the log attribute
     *
     * @param value The new log value
     */
    public void setLog(Logger value) {
        log = value;
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
     * Use a proxy to bypass the firewall
     *
     * @param proxyHost Host of the proxy
     * @param proxyPort Port of the proxy
     * @param userName Username (if authentification is required), or null
     * @param password Password (if authentification is required), or null
     */
    public void useProxy(String proxyHost, int proxyPort, final String userName, final String password) {
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", String.valueOf(proxyPort));
        if (userName != null) {
            Authenticator.setDefault(
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userName, password == null ? new char[0] : password.toCharArray());
                    }
                });
        }
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
        if (!rootUrl.endsWith("/")) {
            rootUrl += "/";
        }

        try {
            SaxParser parser = new SaxParser();
            LocalRepository localRepository = new LocalRepository(cvsUser, cvsHost, cvsRoot, destDir + "/", packageName);
            RemoteRepository remoteRepository = new RemoteRepository(rootUrl, localRepository);
            CVSGrabHandler handler = new CVSGrabHandler(remoteRepository);
            localRepository.setLog(log);
            remoteRepository.setLog(log);
            parser.setContentHandler(handler);

            remoteRepository.registerDirectoryToProcess(packageName + "/");
            while (remoteRepository.hasDirectoryToProcess()) {
                RemoteDirectory rDir = null;
                try {
                    rDir = remoteRepository.nextDirectoryToProcess();
                    String rDirUrl = rDir.getUrl();
                    if (tag != null && !tag.equals("")) {
                        rDirUrl += "?only_with_tag=" + tag;
                    }
                    log.debug("Parsing page: " + rDirUrl);
                    handler.setCurrentRemoteDirectory(rDir);
                    parser.parse(rDirUrl);
                    localRepository.update();
                    if (handler.isPageFullyLoaded()) {
                        localRepository.cleanRemovedFiles(rDir);
                    } else {
                        log.warn("Could not load the full html content at " + rDir.getUrl() + ", open this page in a browser and if you don't find an obvious solution, report the problem to http://sourceforge.net/forum/forum.php?forum_id=174128");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.error("Error while getting files from " + rDir.getUrl());
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
                log.info(newFileCount + " new files");
            }
            if (updatedFileCount > 0) {
                log.info(updatedFileCount + " updated files");
            }
            if (removedFileCount > 0) {
                log.info(removedFileCount + " removed files");
            }
            if (failedUpdateCount > 0) {
                log.error(failedUpdateCount + " files");
            }
        } catch (IOException ex) {
            System.out.println("Could not initialise cvsgrab because the CVS admin files are corrupted");
            ex.printStackTrace();
        }
    }

}
