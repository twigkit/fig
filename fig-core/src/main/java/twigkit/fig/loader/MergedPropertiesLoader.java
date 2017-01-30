package twigkit.fig.loader;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.util.FigUtils;
import twigkit.fig.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * The MergedPropertiesLoader loads two {@link Fig}s, a primary and a secondary. Then merges the two by passing over
 * the properties in the secondary over to the primary {@link Fig}.
 *
 * @author scottbrown
 */
public class MergedPropertiesLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(MergedPropertiesLoader.class);

    private String pathToPrimaryFig;
    private String pathToSecondaryFig;
    private PropertiesLoader primaryLoader;

    public MergedPropertiesLoader(String pathToPrimaryFig, String pathToSecondaryFig) {
        this.pathToPrimaryFig = pathToPrimaryFig;
        this.pathToSecondaryFig = pathToSecondaryFig;
    }

    public PropertiesLoader getPrimaryLoader() {
        return primaryLoader;
    }

    /**
     * This method loads the given primary {@link Fig} and a second {@link Fig} then merges the second {@link Fig}
     * with the primary {@link Fig}.
     *
     * @param fig The primary {@link Fig}
     */
    public void load(Fig fig) {
        if (pathToPrimaryFig != null && !pathToPrimaryFig.isEmpty()) {
            this.primaryLoader = new PropertiesLoader(pathToPrimaryFig);
            this.primaryLoader.load(fig);
            logger.trace("Primary fig loaded.");

            if (pathToSecondaryFig != null && !pathToSecondaryFig.isEmpty()) {
                File secondaryFigRootFolder = FileUtils.getResourceAsFile(pathToSecondaryFig);

                if (secondaryFigRootFolder != null && secondaryFigRootFolder.exists()) {
                    Fig secondaryFig = Fig.getInstance(new PropertiesLoader(pathToSecondaryFig));
                    secondaryFig.reload(); // ensure the secondary fig is up-to-date
                    logger.trace("Secondary fig loaded");

                    FigUtils.merge(fig, secondaryFig);
                    logger.trace("Secondary fig merged with primary fig");
                } else {
                    logger.debug("Path to secondary fig: \"{}\" is invalid. Falling back to primary.", pathToSecondaryFig);
                }
            } else {
                logger.debug("Path to secondary fig: \"{}\" is invalid. Falling back to primary.", pathToSecondaryFig);
            }
        } else {
            logger.debug("Path to primary fig: \"{}\" is invalid. Attempting to load secondary...", pathToPrimaryFig);

            if (pathToSecondaryFig != null && !pathToSecondaryFig.isEmpty()) {
                new PropertiesLoader(pathToSecondaryFig).load(fig);
                logger.trace("Secondary fig loaded");
            } else {
                logger.error("Both the path to the primary fig: \"{}\" and the path to the secondary fig: \"{}\" are invalid. Unable to load configurations.", pathToPrimaryFig, pathToSecondaryFig);
            }
        }
    }

    public void write(Config config) throws IOException {
        if (pathToPrimaryFig != null && !pathToPrimaryFig.isEmpty()) {
            new PropertiesLoader(pathToPrimaryFig).write(config);
        }
    }

    public void delete(Config config) throws IOException {
        if (pathToPrimaryFig != null && !pathToPrimaryFig.isEmpty()) {
            new PropertiesLoader(pathToPrimaryFig).delete(config);
        }
    }
}
