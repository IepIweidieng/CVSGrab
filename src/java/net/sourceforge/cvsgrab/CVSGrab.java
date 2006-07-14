/*
 * CVSGrab
 * Author: Ludovic Claude (ludovicc@users.sourceforge.net)
 * Distributable under BSD license.
 */
package net.sourceforge.cvsgrab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.cvsgrab.util.CVSGrabLog;
import net.sourceforge.cvsgrab.util.ThreadPool;

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

    /**
     * The dummy root to use if the cvsRoot option is not set
     */
    public static final String DUMMY_ROOT = ":pserver:anonymous@dummyhost:/dummyroot";

    public static final String CONNECTIONS_OPTION = "connections";
    public static final String WEB_PASSWORD_OPTION = "webPassword";
    public static final String WEB_USER_OPTION = "webUser";
    public static final String PROXY_PASSWORD_OPTION = "proxyPassword";
    public static final String PROXY_USER_OPTION = "proxyUser";
    public static final String PROXY_NTDOMAIN_OPTION = "proxyNTDomain";
    public static final String PROXY_PORT_OPTION = "proxyPort";
    public static final String PROXY_HOST_OPTION = "proxyHost";
    public static final String PRUNE_OPTION = "prune";
    public static final String QUIET_OPTION = "quiet";
    public static final String VERBOSE_OPTION = "verbose";
    public static final String DEBUG_WIRE_OPTION = "debugWire";
    public static final String DEBUG_OPTION = "debug";
    public static final String CVS_ROOT_OPTION = "cvsRoot";
    public static final String WEB_INTERFACE_OPTION = "webInterface";
    public static final String PROJECT_ROOT_OPTION = "projectRoot";
    public static final String QUERY_PARAMS_OPTION = "queryParams";
    public static final String TAG_OPTION = "tag";
    public static final String PACKAGE_DIR_OPTION = "packageDir";
    public static final String DEST_DIR_OPTION = "destDir";
    public static final String PACKAGE_PATH_OPTION = "packagePath";
    public static final String ROOT_URL_OPTION = "rootUrl";
    public static final String URL_OPTION = "url";
    public static final String CLEAN_UPDATE_OPTION = "clean";
    public static final String LIST_WEB_INTERFACES_CMD = "listWebInterfaces";
    public static final String DIFF_CMD = "diff";
    public static final String HELP_CMD = "help";

    private static final String[] WEB_OPTIONS = {ROOT_URL_OPTION, PACKAGE_PATH_OPTION,
            PROJECT_ROOT_OPTION, TAG_OPTION, QUERY_PARAMS_OPTION, WEB_INTERFACE_OPTION,
            PROXY_HOST_OPTION, PROXY_PORT_OPTION, PROXY_NTDOMAIN_OPTION, PROXY_USER_OPTION,
            PROXY_PASSWORD_OPTION, WEB_USER_OPTION, WEB_PASSWORD_OPTION};
    private static final String FORUM_URL = "http://sourceforge.net/forum/forum.php?forum_id=174128";
    private static final String VERSION = "2.2.3-SNAPSHOT";
    private static final String DEFAULT_DEST_DIR = ".";
    private static Log LOG;

    private boolean _pruneEmptyDirs = false;
    private boolean _cleanUpdate;
    private boolean _error = false;
    private String _packageDir;
    private String _destDir = DEFAULT_DEST_DIR;
    private String _cvsRoot = DUMMY_ROOT;
    private Options _options;
    private WebOptions _webOptions = new WebOptions();
    private CvsWebInterface _webInterface;

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
        _options.addOption(HELP_CMD, false, "[Command] Prints this help message");
        _options.addOption(LIST_WEB_INTERFACES_CMD, false, "[Command] Lists the web interfaces to the CVS repository that are"
                + "\t supported by this tool");
        _options.addOption(new OptionBuilder()
                .withDescription("[Command] Builds the differences against the same remote version. Result is stored in the file patch.txt")
                .create(DIFF_CMD));
        _options.addOption(new OptionBuilder().withArgName("url")
                .hasArg()
                .withDescription("The full url used to access the CVS repository from a web browser")
                .create(URL_OPTION));
        _options.addOption(new OptionBuilder().withArgName("url")
                .hasArg()
                .withDescription("[if full url not used] The root url used to access the CVS repository from a web browser")
                .create(ROOT_URL_OPTION));
        _options.addOption(new OptionBuilder().withArgName("path")
                .hasArg()
                .withDescription("[if full url not used] The path relative to rootUrl of the package or module to download")
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
                .withDescription("[optional] The destination directory.")
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
        _options.addOption(new OptionBuilder()
                .withDescription("[optional] Clean update. Backup locally modified files and download anyway the latest version of the file.")
                .create(CLEAN_UPDATE_OPTION));
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
            System.err.println(e.getMessage());
            grabber.printHelp();
        }
    }

    private void run(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse( _options, args);

        // Help and list supported web interfaces
        if (cmd.hasOption(HELP_CMD)) {
            printHelp();
            return;
        }
        if (cmd.hasOption(LIST_WEB_INTERFACES_CMD)) {
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
        Properties webProperties = new Properties();
        for (int i = 0; i < WEB_OPTIONS.length; i++) {
            String option = WEB_OPTIONS[i];
            if (cmd.hasOption(option)) {
                webProperties.put(option, cmd.getOptionValue(option));
            }
        }
        _webOptions.readProperties(webProperties);

        // Handle local repository options
        
        // By default, projectRoot and cvsRoot should match (they are configured in different parameters,
        // because projectRoot is the root seen from the web repository, and cvsRoot is the root seen from 
        // the cvs client, and some sites can have different names for them, but so far that's only speculation
        // and no real case). cvsRoot exists also because of historical reasons, it was introduced for
        // compatibility with cvs clients.
        if (cmd.hasOption(PROJECT_ROOT_OPTION)) {
            setCvsRoot(cmd.getOptionValue(PROJECT_ROOT_OPTION));
        }
        if (cmd.hasOption(CVS_ROOT_OPTION)) {
            setCvsRoot(cmd.getOptionValue(CVS_ROOT_OPTION));
        }
        if (cmd.hasOption(DEST_DIR_OPTION)) {
            setDestDir(cmd.getOptionValue(DEST_DIR_OPTION));
        }
        if (cmd.hasOption(PACKAGE_DIR_OPTION)) {
            setPackageDir(cmd.getOptionValue(PACKAGE_DIR_OPTION));
        }
        if (cmd.hasOption(PRUNE_OPTION)) {
            setPruneEmptyDirs(true);
        }
        if (cmd.hasOption(CLEAN_UPDATE_OPTION)) {
            setCleanUpdate(true);
        }

        // Handle connection settings
        _webOptions.setupConnectionSettings();
        if (cmd.hasOption(CONNECTIONS_OPTION)) {
            int connections = Integer.parseInt(cmd.getOptionValue(CONNECTIONS_OPTION));
            if (connections > 1) {
                ThreadPool.init(connections);
                WebBrowser.getInstance().useMultithreading();
            }
        }

        // Handle file types
        String cvsGrabHome = System.getProperty("cvsgrab.home");
        File file = new File(cvsGrabHome, "FileTypes.properties");
        try {
            Properties fileTypes = new Properties();
            InputStream is = new FileInputStream(file);
            fileTypes.load(is);
            RemoteFile.setFileTypes(fileTypes);
        } catch (IOException ex) {
            getLog().error("Cannot read the file " + file.getPath());
        }

        // Handle autodetection of the full url
        if (cmd.hasOption(URL_OPTION)) {
            setUrl(cmd.getOptionValue(URL_OPTION));
        }

        if (cmd.hasOption(DIFF_CMD)) {
            diffCVSRepository();
        } else {
            grabCVSRepository();
        }
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
        String[] webInterfaces = CvsWebInterface.getInterfaceIds(new CVSGrab());
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
     * Gets the cleanUpdate.
     *
     * @return the cleanUpdate.
     */
    public boolean isCleanUpdate() {
        return _cleanUpdate;
    }

    /**
     * Sets the cleanUpdate.
     *
     * @param cleanUpdate The cleanUpdate to set.
     */
    public void setCleanUpdate(boolean cleanUpdate) {
        _cleanUpdate = cleanUpdate;
    }

    /**
     * @return Returns the rootUrl.
     */
    public String getRootUrl() {
        return _webOptions.getRootUrl();
    }

    /**
     * @return Returns the packagePath.
     */
    public String getPackagePath() {
        return _webOptions.getPackagePath();
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
     * @return The project root, used by CVS with multiple repositories
     */
    public String getProjectRoot() {
        return _webOptions.getProjectRoot();
    }

    /**
     * @return Returns the versionTag.
     */
    public String getVersionTag() {
        return _webOptions.getVersionTag();
    }

    /**
     * @return Returns the queryParams.
     */
    public String getQueryParams() {
        return _webOptions.getQueryParams();
    }

    /**
     * Gets the webInterfaceId.
     * @return the webInterfaceId.
     */
    public String getWebInterfaceId() {
        return _webOptions.getWebInterfaceId();
    }

    /**
     * @return the web options
     */
    public WebOptions getWebOptions() {
        return _webOptions;
    }

    /**
     * Analyse the root url and try to extract the package path, version tag and web options parameters from it
     */
    public void setUrl(String url) {
    	Properties webProperties;
    	if (getWebInterfaceId() != null) {
    		try {
				webProperties = getWebInterface().guessWebProperties(url);
			} catch (Exception e) {
	            getLog().error(e.getMessage());
	            _error = true;
	            return;
			}
    	} else {
    		webProperties = CvsWebInterface.getWebProperties(this, url);
            _webInterface = (CvsWebInterface) webProperties.get(CvsWebInterface.DETECTED_WEB_INTERFACE);
    	}
        // put back the result in the WebOptions
        _webOptions.readProperties(webProperties);
    }

    /**
     * Main method for getting and updating files.
     */
    public void grabCVSRepository() {
        printHeader();
        loadExistingAdminFiles();

        if (!checkMandatoryParameters()) return;

        try {
            checkDestDir();
            checkWebConnection();

            CvsWebInterface webInterface = getWebInterface();

            LocalRepository localRepository = new LocalRepository(this);
            RemoteRepository remoteRepository = new RemoteRepository(getRootUrl(), localRepository);
            remoteRepository.setWebInterface(webInterface);

            RemoteDirectory remoteDir = new RemoteDirectory(remoteRepository, getPackagePath(), getPackageDir());
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

    /**
     * Builds the differences against the remote repository
     */
    public void diffCVSRepository() {
        printHeader();
        loadExistingAdminFiles();

        if (!checkMandatoryParameters()) return;

        try {
            checkDestDir();
            checkWebConnection();

            CvsWebInterface webInterface = getWebInterface();
            File diffFile = new File("patch.txt");
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(diffFile)));

            LocalRepository localRepository = new LocalRepository(this);
            RemoteRepository remoteRepository = new RemoteRepository(getRootUrl(), localRepository);
            remoteRepository.setWebInterface(webInterface);

            RemoteDirectory remoteDir = new RemoteDirectory(remoteRepository, getPackagePath(), getPackageDir());
            remoteRepository.registerDirectoryToProcess(remoteDir);
            while (remoteRepository.hasDirectoryToProcess()) {
                try {
                    remoteDir = remoteRepository.nextDirectoryToProcess();
                    remoteDir.diffContents(writer);
                    writer.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    getLog().error("Error while getting files from " + remoteDir.getUrl());
                    _error = true;
                }
            }
            writer.close();

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
            getLog().info("Differences stored in " + diffFile.getAbsolutePath());
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

    private CvsWebInterface getWebInterface() throws Exception {
    	if (_webInterface == null) {
    		if (getWebInterfaceId() != null) {
    			// Forces the use of a particular web interface and version of that interface
    			_webInterface = CvsWebInterface.getInterface(this, getWebInterfaceId());
    		} else {
    			// Auto detection of the type of the remote interface
    			_webInterface = detectWebInterface();
    		}
    	}
        if (_webInterface == null) {
            getLog().error("Could not detect the type of the web interface");
            throw new RuntimeException("Could not detect the type of the web interface");
        } else {
            getLog().info("Detected cvs web interface: " + _webInterface.getType());
        }

        _webInterface.setQueryParams(getQueryParams());
        if (getVersionTag() != null) {
            _webInterface.setVersionTag(getVersionTag());
        }
        return _webInterface;
    }

    private void checkWebConnection() throws Exception {
        // Tests the connection to the website
        String[] urls = CvsWebInterface.getBaseUrls(this);
        Map errors = new HashMap();
        for (int i = 0; i < urls.length; i++) {
            try {
                getLog().debug("Connecting to " + urls[i]);
                GetMethod connectMethod = new GetMethod(urls[i]);
                WebBrowser.getInstance().executeMethod(connectMethod, urls[i]);
                getLog().debug("Connection successful");
                return;
            } catch (Exception ex) {
                errors.put(urls[i], ex.getMessage());
            }
        }
        for (Iterator i = errors.keySet().iterator(); i.hasNext();) {
            String  url = (String) i.next();
            System.err.println("When attempting to connect to " + url + ", got error: " + errors.get(url));
        }
        throw new Exception("Cannot connect to the website, check your proxy settings");
    }

    private void checkDestDir() {
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
    }

    private boolean checkMandatoryParameters() {
        if (getRootUrl() == null || getPackagePath() == null) {
            if (getRootUrl() == null) {
                System.out.println("Error: rootUrl parameter is mandatory");
            }
            if (getPackagePath() == null) {
                System.out.println("Error: packagePath parameter is mandatory");
            }
            printHelp();
            return false;
        }
        return true;
    }

    private void loadExistingAdminFiles() {
        // Try to load the web options from the CVS admin files
        File webRepositoryAdmin = new File(_destDir, "CVS/WebRepository");
        if (webRepositoryAdmin.exists()) {
            Properties webProperties = new Properties();
            try {
                webProperties.load(new FileInputStream(webRepositoryAdmin));
                // Now merge with the current properties
                _webOptions.writeProperties(webProperties);
                // And put back the result in the WebOptions
                _webOptions.readProperties(webProperties);
                // Update local settings
                if (getDestDir().equals(DEFAULT_DEST_DIR)) {
                    File currentDir = new File(".");
                    currentDir = currentDir.getAbsoluteFile();
                    setPackageDir(currentDir.getName());
                }
            } catch (IOException e) {
                getLog().warn("Cannot read file " + webRepositoryAdmin.getAbsolutePath(), e);
            }
        }
        // Read the cvsRoot from CVS admin files
        File rootAdmin = new File(_destDir, "CVS/Root");
        if (rootAdmin.exists() && _cvsRoot.equals(DUMMY_ROOT)) {
            try {
                FileReader reader = new FileReader(rootAdmin);
                LineNumberReader lnReader = new LineNumberReader(reader);
                _cvsRoot = lnReader.readLine();
            } catch (IOException e) {
                getLog().warn("Cannot read file " + rootAdmin.getAbsolutePath(), e);
            }
        }
    }

    private void printHeader() {
        if (getLog().isInfoEnabled()) {
            getLog().info("CVSGrab version " + VERSION + " starting...");
        } else {
            System.out.println("CVSGrab version " + VERSION + " starting...");
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
