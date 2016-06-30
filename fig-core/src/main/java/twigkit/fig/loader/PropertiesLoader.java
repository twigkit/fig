package twigkit.fig.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.Value;
import twigkit.fig.util.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class PropertiesLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    public static final String FILE_EXTENSION = ".conf";
    public static final String LEVEL_SEPARATOR = ".";
    public static final String LEVEL_SEPARATOR_PATTERN = "\\.";

    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF_8 = "UTF-8";

    private String path;
    private int rootPathLength;

    private File rootFolder;

    public Map<String, File> filePaths;

    public PropertiesLoader(String path) {
        this.path = path;
        filePaths = new HashMap<String, File>();
    }

    /**
     * Populates the given Fig instance with configurations found under the root path
     * stored by this loader.
     * <p/>
     * Note that if the root folder contains a .conf file with the name of the folder,
     * then we take the root folder to be the sole tree in the Fig forest.
     * Otherwise, each configuration directly stored in the root folder becomes a tree
     * of its own, and Fig is a proper forest.
     *
     * @param fig the current {@code Fig} instance.
     */
    public void load(Fig fig) {
        rootFolder = FileUtils.getResourceAsFile(path);
        if (rootFolder != null) {
            if (!rootFolder.getParentFile().exists() || !new File(rootFolder, rootFolder.getName() + FILE_EXTENSION).exists()) {
                rootPathLength = rootFolder.getAbsolutePath().length();
            } else {
                rootPathLength = rootFolder.getParentFile().getAbsolutePath().length();
            }
            readFolder(fig, rootFolder);
        } else {
            logger.error("Fig folder not found: {}", path);
        }
    }
    protected void readFolder(Fig fig, final File folder) {
        // If there is a configuration file with the same name as the folder, then add that first
        File conf = new File(folder, folder.getName() + FILE_EXTENSION);
        if (conf.isFile()) {
            if (rootPathLength < folder.getParentFile().getAbsolutePath().length()) {
                fromProperties(fig, folder.getParentFile().getAbsolutePath().substring(rootPathLength + 1).split(Pattern.quote(File.separator)), conf);
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
                int numberOfElementsInFilename = file.getName().split("[.]").length;
                int numberOfElementsInFilename1 = file1.getName().split("[.]").length;

                if (numberOfElementsInFilename < numberOfElementsInFilename1) {
                    return -1;
                } else if (numberOfElementsInFilename > numberOfElementsInFilename1) {
                    return 1;
                } else{
                    return file.getName().compareTo(file1.getName());
                }
            }
        });

        // Files
        if (folder.getAbsolutePath().length() > rootPathLength) {
            // Accounting for trailing slash (maybe should check if it's there first)
            readFiles(fig, folder, folder.getAbsolutePath().substring(rootPathLength + 1).split(Pattern.quote(File.separator)), files);
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
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ResourceBundle p = new PropertyResourceBundle(fis);
            String[] levels = file.getName().substring(0, file.getName().lastIndexOf(FILE_EXTENSION.substring(0, 1))).split(LEVEL_SEPARATOR_PATTERN);

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
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("Failed to load Config from Properties", e);
                }
            }
        }
    }

    /**
     * Delete the file backing the config and remove reference to the config
     * from the parent.
     *
     * @param config
     * @throws IOException
     */
    public void delete(Config config) throws IOException {
        logger.debug("Deleting config {}", config.name());
        if (config.has_extensions()) {
            for (Config child: config.extensions()) {
                delete(child);
            }
            File configFile = fileForConfig(config);
            if (configFile.isDirectory()) {
                if (configFile.listFiles().length > 0) {
                    throw new IOException("All extensions of " + config.name()
                        + " have been deleted but there are still files in the config folder "
                        + configFile.getAbsolutePath()
                    );
                }
            }
            logger.debug("Deleting file {} for config {}", configFile, config.name());
            if (!configFile.delete()) {
                throw new IOException("Unable to delete config file directory "
                    + configFile.getAbsolutePath()
                );
            }

        } else {
            File configFile = fileForConfig(config);
            logger.debug("File for config {} is {}", config.name(), configFile);
            if (configFile.isDirectory()) {
                logger.debug("Deleting directory {}", configFile);
                org.apache.commons.io.FileUtils.deleteDirectory(configFile);
            } else {
                logger.debug("Deleting file {}", configFile);
                if (!configFile.delete()) {
                    throw new IOException("Unable to delete config file "
                        + configFile.getAbsolutePath()
                    );
                }
            }
            filePaths.remove(config.path());
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
        logger.debug("Writing to file for config {}", file.getAbsolutePath());
        write(config, file);
    }

    protected void write(Config config, File file) throws IOException {
        Writer out = null;
        try {
            if (!file.exists()) {
                logger.debug("Creating a new config file {} for {}", file.getAbsolutePath(), config.name());
                file.createNewFile();
            }
            logger.debug("Writing to file: {}", file);
            out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            for (Value val : config.values(false)) {
                out.write(val.label() + ": " + val.as_string());
                out.write("\n");
            }
            filePaths.put(config.path(), file);
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
            logger.debug("Returning file [{}]", f.getAbsolutePath());
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
        } else {
            folder = new File(folder, config.name());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertiesLoader)) return false;

        PropertiesLoader that = (PropertiesLoader) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
