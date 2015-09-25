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

    public MergedPropertiesLoader(String pathToPrimaryFig, String pathToSecondaryFig) {
        this.pathToPrimaryFig = pathToPrimaryFig;
        this.pathToSecondaryFig = pathToSecondaryFig;
    }

    /**
     * This method loads the given primary {@link Fig} and a second {@link Fig} then merges the second {@link Fig}
     * with the primary {@link Fig}.
     *
     * @param fig The primary {@link Fig}
     */
    public void load(Fig fig) {
        if (pathToPrimaryFig != null) {
            new PropertiesLoader(pathToPrimaryFig).load(fig);

            if (pathToSecondaryFig != null) {
                File secondaryFigRootFolder = FileUtils.getResourceAsFile(pathToSecondaryFig);

                if (secondaryFigRootFolder != null && secondaryFigRootFolder.exists()) {
                    FigUtils.merge(fig, Fig.getInstance(new PropertiesLoader(pathToSecondaryFig)));
                } else {
                    logger.trace("Secondary fig {} not found. Falling back to primary.", pathToSecondaryFig);
                }
            } else {
                logger.trace("Secondary fig {} not found. Falling back to primary.", pathToSecondaryFig);
            }
        } else {
            logger.trace("Primary fig {} not found. Falling back to secondary", pathToPrimaryFig);
            new PropertiesLoader(pathToSecondaryFig).load(fig);
        }
    }

    public void write(Config config) throws IOException {
        throw new IOException("Write operation not supported.");
    }

    public void delete(Config config) throws IOException {
        throw new IOException("Delete operation not supported.");
    }
}
