/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */
package net.sourceforge.cvsgrab;

import java.io.File;
import java.io.IOException;

import net.sourceforge.cvsgrab.util.*;
import net.sourceforge.cvsgrab.util.CVSGrabLog;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.w3c.dom.Document;

/**
 * Application class for CVSGrab. <br>
 * CVSGrab loads and updates the files from the ViewCVS web interface of a CVS
 * repository
 *
 * @author Ludovic Claude
 * @created April 19, 2002
 * @version 1.0
 */
public class CVSGrab {
    private static final String CONNECTIONS_OPTION = "connections";
    private static final String WEB_PASSWORD_OPTION = "webPassword";
    private static final String WEB_USER_OPTION = "webUser";
    private static final String PROXY_PASSWORD_OPTION = "proxyPassword";
    private static final String PROXY_USER_OPTION = "proxyUser";
    private static final String PROXY_NTDOMAIN_OPTION = "proxyNTDomain";
    private static final String PROXY_PORT_OPTION = "proxyPort";
    private static final String PROXY_HOST_OPTION = "proxyHost";
    private static final String PRUNE_OPTION = "prune";
    private static final String QUIET_OPTION = "quiet";
    private static final String VERBOSE_OPTION = "verbose";
    private static final String DEBUG_WIRE_OPTION = "debugWire";
    private static final String DEBUG_OPTION = "debug";
    private static final String CVS_ROOT_OPTION = "cvsRoot";
    private static final String WEB_INTERFACE_OPTION = "webInterface";
    private static final String PROJECT_ROOT_OPTION = "projectRoot";
    private static final String QUERY_PARAMS_OPTION = "queryParams";
    private static final String TAG_OPTION = "tag";
    private static final String PACKAGE_DIR_OPTION = "packageDir";
    private static final String DEST_DIR_OPTION = "destDir";
    private static final String PACKAGE_PATH_OPTION = "packagePath";
    private static final String ROOT_URL_OPTION = "rootUrl";
    private static final String LIST_WEB_INTERFACES_OPTION = "listWebInterfaces";
    private static final String HELP_OPTION = "help";
    public static final String DUMMY_ROOT = ":pserver:anonymous@dummyhost:/dummyroot";
    private static final String FORUM_URL = "http://sourceforge.net/forum/forum.php?forum_id=174128";
    private static final String VERSION = "2.0.3";
    private static Log LOG;

    private boolean _pruneEmptyDirs = false;
    private boolean _error = false;
    private String _rootUrl;
    private String _packagePath;
    private String _projectRoot;
    private String _packageDir;
    private String _destDir;
    private String _versionTag;
    private String _queryParams;
    private String _cvsRoot = DUMMY_ROOT;
    private String _webInterfaceId = null;
    private Options _options;

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
    public CVSGrab() {
        _options = new Options();
        _options.addOption(HELP_OPTION, false, "This help message");
        _options.addOption(LIST_WEB_INTERFACES_OPTION, false, "Lists the web interfaces to the CVS repository that are" 
                + "\t supported by this tool");
        _options.addOption(new OptionBuilder().withArgName("url")
                .hasArg()
                .withDescription("The root url used to access the CVS repository from a web browser")
                .create(ROOT_URL_OPTION));
        _options.addOption(new OptionBuilder().withArgName("path")
                .hasArg()
                .withDescription("The path relative to rootUrl of the package or module to download")
                .create(PACKAGE_PATH_OPTION));
        _options.addOption(new OptionBuilder().withArgName("root")
                .hasArg()
                .withDescription("[optional] The project root, for cvs with multiple repositories")
                .create(PROJECT_ROOT_OPTION));
        _options.addOption(new OptionBuilder().withArgName("version tag")
                .hasArg()
                .withDescription("[optional] The version tag of the files to download")
                .create(TAG_OPTION));
        _options.addOption(new OptionBuilder().withArgName("query params")
                .hasArg()
                .withDescription("[optional] Additional query parameters")
                .create(QUERY_PARAMS_OPTION));
        _options.addOption(new OptionBuilder().withArgName("web interface id")
                .hasArg()
                .withDescription("[optional] The id for the web interface for the CVS repository to use. " +
                        "If this option is not set, autodetect the web interface." +
                "Call cvsgrab -listWebInterfaces to get a list of valid values for this option.")
                .create(WEB_INTERFACE_OPTION));
        _options.addOption(new OptionBuilder().withArgName("dir")
                .hasArg()
                .withDescription("The destination directory")
                .create(DEST_DIR_OPTION));
        _options.addOption(new OptionBuilder().withArgName("dir")
                .hasArg()
                .withDescription("The name of the package to use locally, relative to destDir, overrides packagePath")
                .create(PACKAGE_DIR_OPTION));
        _options.addOption(new OptionBuilder().withArgName("cvs root")
                .hasArg()
                .withDescription("[optional] The original cvs root, used to maintain compatibility with a standard CVS client")
                .create(CVS_ROOT_OPTION));
        _options.addOption(new OptionBuilder()
                .withDescription("[optional] Prune (remove) the empty directories.")
                .create(PRUNE_OPTION));
        Option debugOption = new OptionBuilder()
                .withDescription("[optional] Turn debugging on.")
                .create(DEBUG_OPTION);
        Option debugWireOption = new OptionBuilder()
                .withDescription("[optional] Turn debugging on, including very verbose network traffic.")
                .create(DEBUG_WIRE_OPTION);
        Option verboseOption = new OptionBuilder()
                .withDescription("[optional] Turn verbosity on.")
                .create(VERBOSE_OPTION);
        Option quietOption = new OptionBuilder()
                .withDescription("[optional] Be extra quiet.")
                .create(QUIET_OPTION);
        _options.addOptionGroup(new OptionGroup().addOption(debugOption)
                .addOption(debugWireOption)
                .addOption(verboseOption)
                .addOption(quietOption));
        _options.addOption(new OptionBuilder().withArgName("nb of connections")
                .hasArg()
                .withDescription("[optional] The number of simultaneous connections to use for downloads, default 1")
                .create(CONNECTIONS_OPTION));
        _options.addOption(new OptionBuilder().withArgName("host")
                .hasArg()
                .withDescription("[optional] Proxy host")
                .create(PROXY_HOST_OPTION));
        _options.addOption(new OptionBuilder().withArgName("port")
                .hasArg()
                .withDescription("[optional] Proxy port")
                .create(PROXY_PORT_OPTION));
        _options.addOption(new OptionBuilder().withArgName("domain")
                .hasArg()
                .withDescription("[optional] NT Domain for the authentification on a MS proxy")
                .create(PROXY_NTDOMAIN_OPTION));
        _options.addOption(new OptionBuilder().withArgName("user")
                .hasArg()
                .withDescription("[optional] Username for the proxy")
                .create(PROXY_USER_OPTION));
        _options.addOption(new OptionBuilder().withArgName("password")
                .hasArg()
                .withDescription("[optional] Password for the proxy. If this option is omitted, then cvsgrab will prompt securely for the password.")
                .create(PROXY_PASSWORD_OPTION));
        _options.addOption(new OptionBuilder().withArgName("user")
                .hasArg()
                .withDescription("[optional] Username for the web server")
                .create(WEB_USER_OPTION));
        _options.addOption(new OptionBuilder().withArgName("password")
                .hasArg()
                .withDescription("[optional] Password for the web server. If this option is omitted, then cvsgrab will prompt securely for the password.")
                .create(WEB_PASSWORD_OPTION));
    }

    /**
     * The main program for the CVSGrab class
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        System.setProperty(LogFactoryImpl.LOG_PROPERTY, CVSGrabLog.class.getName());
        CVSGrab grabber = new CVSGrab();
        try {
            grabber.run(args);
        } catch (ParseException e) {
            e.printStackTrace();
            grabber.printHelp();
        }
    }
    
    private void run(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse( _options, args);
        
        // Help and list supported web interfaces
        if (cmd.hasOption(HELP_OPTION)) {
            printHelp();
            return;
        }
        if (cmd.hasOption(LIST_WEB_INTERFACES_OPTION)) {
            printWebInterfaces();
            return;
        }
        
        // Handle logging levels
        cvsgrabLogLevel("info", "WARNING");                        
        httpclientLogLevel("error", "SEVERE");
        
        if (cmd.hasOption(DEBUG_OPTION)) {
            cvsgrabLogLevel("trace", "FINEST");                        
            httpclientLogLevel("info", "INFO");
        } 
        if (cmd.hasOption(DEBUG_WIRE_OPTION)) {
            httpclientLogLevel("trace", "FINEST");
            logLevel("httpclient.wire", "trace", "FINE");
        } 
        if (cmd.hasOption(VERBOSE_OPTION)) {
            cvsgrabLogLevel("debug", "INFO");                        
            httpclientLogLevel("error", "SEVERE");
        } 
        if (cmd.hasOption(QUIET_OPTION)) {
            cvsgrabLogLevel("warn", "INFO");                        
            httpclientLogLevel("error", "SEVERE");
        } 
        
        // Handle remote repository options
        if (cmd.hasOption(ROOT_URL_OPTION)) {
            setRootUrl(cmd.getOptionValue(ROOT_URL_OPTION));
        } 
        if (cmd.hasOption(PACKAGE_PATH_OPTION)) {
            setPackagePath(cmd.getOptionValue(PACKAGE_PATH_OPTION));
        } 
        if (cmd.hasOption(PROJECT_ROOT_OPTION)) {
            setProjectRoot(cmd.getOptionValue(PROJECT_ROOT_OPTION));
        } 
        if (cmd.hasOption(TAG_OPTION)) {
            setVersionTag(cmd.getOptionValue(TAG_OPTION));
        } 
        if (cmd.hasOption(QUERY_PARAMS_OPTION)) {
            setQueryParams(cmd.getOptionValue(QUERY_PARAMS_OPTION));
        } 
        if (cmd.hasOption(WEB_INTERFACE_OPTION)) {
            setWebInterfaceId(cmd.getOptionValue(WEB_INTERFACE_OPTION));
        } 
        
        // Handle local repository options 
        if (cmd.hasOption(DEST_DIR_OPTION)) {
            setDestDir(cmd.getOptionValue(DEST_DIR_OPTION));
        } 
        if (cmd.hasOption(PACKAGE_DIR_OPTION)) {
            setPackageDir(cmd.getOptionValue(PACKAGE_DIR_OPTION));
        } 
        if (cmd.hasOption(CVS_ROOT_OPTION)) {
            setCvsRoot(cmd.getOptionValue(CVS_ROOT_OPTION));
        } 
        if (cmd.hasOption(PRUNE_OPTION)) {
            setPruneEmptyDirs(true);
        } 
        
        // Handle connection settings
        if (cmd.hasOption(PROXY_HOST_OPTION)) {
            String portStr = cmd.getOptionValue(PROXY_PORT_OPTION);
            if (portStr == null) {
                System.err.println("Argument -" + PROXY_PORT_OPTION + " expected");
                printHelp();
                return;
            }
            int port = -1;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.err.println("Argument -" + PROXY_PORT_OPTION + " must be a number");
                printHelp();
                return;
            }
            WebBrowser.getInstance().useProxy(cmd.getOptionValue(PROXY_HOST_OPTION), 
                    port, 
                    cmd.getOptionValue(PROXY_NTDOMAIN_OPTION), 
                    cmd.getOptionValue(PROXY_USER_OPTION), 
                    cmd.getOptionValue(PROXY_PASSWORD_OPTION));
        } 
        if (cmd.hasOption(WEB_USER_OPTION)) {
            WebBrowser.getInstance().useWebAuthentification(cmd.getOptionValue(WEB_USER_OPTION), 
                    cmd.getOptionValue(WEB_PASSWORD_OPTION));
        } 
        if (cmd.hasOption(CONNECTIONS_OPTION)) {
            int connections = Integer.parseInt(cmd.getOptionValue(CONNECTIONS_OPTION));
            if (connections > 0) {
                ThreadPool.init(connections);
            }
        }
        grabCVSRepository();
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
    public void printHelp() {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(HelpFormatter.DEFAULT_WIDTH, "cvsgrab", "where options are", 
                _options, "CVSGrab version " + VERSION +", copyright (c) 2002-2004 - Ludovic Claude.", false);
    }

    /**
     * Prints the list of available web interfaces
     */
    public static void printWebInterfaces() {
        System.out.println("CVSGrab version " + VERSION);
        System.out.println("Currently supporting the following web interfaces:");
        String[] webInterfaces = CvsWebInterface.getInterfaceIds();
        for (int i = 0; i < webInterfaces.length; i++) {
            System.out.println("\t" + webInterfaces[i]);
        }
        System.out.println("Those ids can be use with the -webInterface option to force cvsgrab to use a specific web interface.");
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
     * @return The project root, used by CVSwith multiple repositories 
     */
    public String getProjectRoot() {
        return _projectRoot;
    }
    
    public void setProjectRoot(String projectRoot) {
        _projectRoot = projectRoot;
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
     * Gets the webInterfaceId.
     * @return the webInterfaceId.
     */
    public String getWebInterfaceId() {
        return _webInterfaceId;
    }

    /**
     * Sets the webInterfaceId.
     * @param webInterfaceId The webInterfaceId to set.
     */
    public void setWebInterfaceId(String webInterfaceId) {
        _webInterfaceId = webInterfaceId;
    }

    /**
     * Main method for getting and updating files.
     */
    public void grabCVSRepository() {
        if (getLog().isInfoEnabled()) {
            getLog().info("CVSGrab version " + VERSION + " starting...");
        } else {
            System.out.println("CVSGrab version " + VERSION + " starting...");
        }

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
            
            // Tests the connection to the website
            try {
                GetMethod connectMethod = new GetMethod(_rootUrl);
                WebBrowser.getInstance().executeMethod(connectMethod);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new Exception("Cannot connect to the website using url " + _rootUrl + ", check your proxy settings");
            }

            CvsWebInterface webInterface = null;
            if (_webInterfaceId != null) {
                // Forces the use of a particular web interface and version of that interface
                webInterface = CvsWebInterface.getInterface(this, _webInterfaceId);
                Document document = webInterface.getDocumentForDetect(this);
                if (document == null) {
                    String testUrl = webInterface.getBaseUrl(this);
                    String testUrl2 = webInterface.getAltBaseUrl(this);
                    getLog().error("Could not detect the type of the web interface. Check that those urls are valid:");
                    if (testUrl != null) {
                        getLog().error(testUrl);
                    }
                    if (testUrl2 != null) {
                        getLog().error(testUrl2);
                    }
                    throw new RuntimeException("Could not detect the type of the web interface");
                }
                try {
                    webInterface.detect(this, document);
                } catch (DetectException ex) {
                    // ignore, suppose that the user knows what he's doing
                }
            } else {
                // Auto detection of the type of the remote interface
                webInterface = detectWebInterface();
            }
            if (webInterface == null) {
                getLog().error("Could not detect the type of the web interface");
                throw new RuntimeException("Could not detect the type of the web interface");
            } else {
                getLog().info("Detected cvs web interface: " + webInterface.getType());
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }    
        return webInterface;
    }
    
}
