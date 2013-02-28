package twigkit.fig.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.Value;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author mr.olafsson
 */
public class PropertiesLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    public static final String FILE_EXTENSION = ".conf";
    public static final String LEVEL_SEPARATOR = "_";

    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF_8 = "UTF-8";

    private String path;
    private int parentPathLength;

    private File rootFolder;

    private Map<String, File> filePaths;

    public PropertiesLoader(String path) {
        this.path = path;
        filePaths = new HashMap<String, File>();
    }

    public void load(Fig fig) {
        try {
            URL url = getResource(path);
            if (url != null) {
                rootFolder = new File(url.toURI());
                parentPathLength = rootFolder.getAbsolutePath().length();
                readFolder(fig, rootFolder);
            }
        } catch (URISyntaxException e) {
            logger.error("Failed to load Config", e);
        }
    }

    private static URL getResource(String name) {
        if (name.startsWith("file://")) {
            try {
                return new File(name.substring(7)).toURL();
            } catch (IOException e) {
                logger.error("Failed to load file: {}", name);
            }
        } else {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource(name);
            if (url == null) {
                url = loader.getResource("/" + name);
            }
            return url;
        }

        return null;
    }

    protected void readFolder(Fig fig, final File folder) {
        // If there is a configuration file with the same name as the folder, then add that first
        File conf = new File(folder, folder.getName() + FILE_EXTENSION);
        if (conf.isFile()) {
            if (parentPathLength < folder.getParentFile().getAbsolutePath().length()) {
                fromProperties(fig, folder.getParentFile().getAbsolutePath().substring(parentPathLength + 1).split(Pattern.quote(File.separator)), conf);
            } else {
                fromProperties(fig, null, conf);
            }
        }

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String s) {
                // All files except the one with the same name as the folder
                return s.endsWith(FILE_EXTENSION) && !s.substring(0, s.length() - FILE_EXTENSION.length()).equals(folder.getName());
            }
        };

        File[] files = folder.listFiles(filter);
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File file, File file1) {
                return file.getName().compareTo(file1.getName());
            }
        });

        // Files
        if (folder.getAbsolutePath().length() > parentPathLength) {
            // Accounting for trailing slash (maybe should check if it's there first)
            readFiles(fig, folder, folder.getAbsolutePath().substring(parentPathLength + 1).split(Pattern.quote(File.separator)), files);
        } else {
            readFiles(fig, folder, null, files);
        }

        // Nested folders
        FileFilter folderFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        File[] nestedFolders = folder.listFiles(folderFilter);
        for (File nestedFolder : nestedFolders) {
            readFolder(fig, nestedFolder);
        }
    }

    protected void readFiles(Fig fig, File folder, String[] parents, File... files) {
        for (File file : files) {
            fromProperties(fig, parents, file);
        }
    }

    private void fromProperties(Fig fig, String[] parents, File file) {
        try {
            ResourceBundle p = new PropertyResourceBundle(new FileInputStream(file));
            String[] levels = file.getName().substring(0, file.getName().lastIndexOf(FILE_EXTENSION.substring(0, 1))).split(LEVEL_SEPARATOR);

            Config config = new Config(levels[levels.length - 1], this);

            Enumeration<String> keys = p.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                config.set(new Value<Object>(new String(key.getBytes(ISO_8859_1), UTF_8), new String(p.getString(key).getBytes(ISO_8859_1), UTF_8), false));
            }

            if (parents != null && parents.length > 0) {
                levels = combine(parents, levels);
            }

            fig.add(config, levels);
            // Keeping a reference to the source file
            filePaths.put(config.path(), file);
        } catch (IOException e) {
            logger.error("Failed to load Config from Properties", e);
        }
    }

    /**
     * Write the Config back to the file that backs it.
     *
     * @param config
     * @throws IOException
     */
    public void write(Config config) throws IOException {
        File file = fileForConfig(config);
        write(config, file);
    }

    protected void write(Config config, File file) throws IOException {
        Writer out = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            logger.debug("Writing to file: {}", file);
            out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            for (Value val : config.values(false)) {
                out.write(val.label() + ": " + val.as_string());
                out.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected File fileForConfig(Config config) {
        if (filePaths.containsKey(config.path())) {
            return filePaths.get(config.path());
        } else {
            Iterator<Config> parentIterator = config.parents().iterator();
            logger.debug("Existing file not found for config [{}], creating...", config.path());
            File f = findFolderForConfig(parentIterator, config, rootFolder);
            return f;
        }
    }

    private File findFolderForConfig(Iterator<Config> iterator, Config config, File folder) {
        if (iterator.hasNext()) {
            // I have not reached the final configuration level
            Config parent = iterator.next();
            File f = new File(folder, parent.name());
            logger.debug("Checking level [{}]", parent.path());
            if (f.exists()) {
                // The parent folder exists
                logger.debug("Found folder for [{}]: {}", parent.path(), f);
                if (iterator.hasNext()) {
                    // Config has more parent folders, trying again
                    return findFolderForConfig(iterator, config, f);
                } else {
                    // No more parent folders, and this one exists, looking for configuration file
                    return findFileForConfig(iterator, config, f, "");
                }
            } else {
                // Folder does not exist, I must either create a folder or I am looking for a file
                if (iterator.hasNext()) {
                    logger.debug("No folder for [{}], still processing parent configurations", parent.path());
                    if (configFileExists(folder, parent)) {
                        logger.debug("Found configuration file for parent [{}]", parent.path());
                        // A config file for this level exists so will not create a folder
                        return findFileForConfig(iterator, config, folder, parent.name());
                    } else {
                        logger.debug("No configuration found for parent [{}]", parent.path());
                        return createFolderForConfig(config, f);
                    }
                } else {
                    logger.debug("No folder for [{}], no more parent configurations", parent.path());
                    if (configFileExists(folder, parent)) {
                        logger.debug("Found configuration file for parent [{}]", parent.path());
                        return findFileForConfig(iterator, config, folder, parent.name());
                    } else {
                        logger.debug("No configuration found for parent [{}]", parent.path());
                        return findFileForConfig(iterator, config, createFolderForConfig(parent, folder), "");
                    }
                }
            }
        }
        return folder;
    }

    private File createFolderForConfig(Config config, File folder) {
        File newFolder = new File(folder, config.name());
        newFolder.mkdir();
        return newFolder;
    }

    private File findFileForConfig(Iterator<Config> iterator, Config config, File folder, String prefix) {
        logger.debug("Checking for config file in: {} (prefix: {})", folder, prefix);
        Config currentConfiguration = config;
        if (iterator.hasNext()) {
            currentConfiguration = iterator.next();
        }
        File f = new File(folder, (prefix.length() > 0 ? prefix + LEVEL_SEPARATOR : "") + currentConfiguration.name() + FILE_EXTENSION);
        logger.debug("Checking file: {}", f);
        if (f.exists()) {
            // A configuration file for this level exists
            // Config has more parent files, trying again with this level as a prefix
            logger.debug("Found file for parent configuration: {}", f);
            return findFileForConfig(iterator, config, folder, prefix + LEVEL_SEPARATOR + currentConfiguration.name());
        } else {
            // File does not exist
            try {
                logger.debug("File [{}] not found, creating...", f);
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.debug("Final file found [{}]", f);
        return f;
    }

    private File createFileForConfig(Iterator<Config> iterator, Config config, File folder) {
        return null;
    }

    private boolean configFileExists(File folder, Config config) {
        File f = new File(folder, config.name() + FILE_EXTENSION);
        logger.debug("Checking if config file exists for [{}]: {}", config.path(), f);
        return f.exists();
    }

    private String[] combine(String[] one, String[] other) {
        String[] combined = new String[one.length + other.length];
        System.arraycopy(one, 0, combined, 0, one.length);
        System.arraycopy(other, 0, combined, one.length, other.length);

        return combined;
    }
}