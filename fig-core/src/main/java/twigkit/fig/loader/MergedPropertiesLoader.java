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
     * This method firstly populates the given Fig instance with configurations found
     * under the primary root path stored by this loader. Then it populates a second
     * Fig instance with configurations found under the secondary root path stored by
     * this loader. Finally, it merges the configurations found under the secondary root
     * into the configurations found under the primary root.
     *
     * @param primaryFig The primary Fig instance
     */
    public void load(Fig primaryFig) {
        // Load the primary fig
        new PropertiesLoader(pathToPrimaryFig).load(primaryFig);

        File secondaryFigRootFolder = FileUtils.getResourceAsFile(pathToSecondaryFig);

        if (secondaryFigRootFolder != null && secondaryFigRootFolder.exists()) {
            // Load and merge the secondary fig into the primary
            Fig secondaryFig = Fig.getInstance(new PropertiesLoader(pathToSecondaryFig));

            if (secondaryFig.configs().size() != 0) {
                // Merge the secondary fig into the primary fig.
                FigUtils.mergeFig(primaryFig, secondaryFig);
            } else {
                logger.warn("No configs found at: {}. Using primary fig.", pathToSecondaryFig);
            }
        } else {
            logger.warn("Fig folder not found: {}. Using primary fig.", pathToSecondaryFig);
        }
    }

    public void write(Config config) throws IOException {
        throw new IOException("Write operation not supported.");
    }

    public void delete(Config config) throws IOException {
        throw new IOException("Delete operation not supported.");
    }
}
