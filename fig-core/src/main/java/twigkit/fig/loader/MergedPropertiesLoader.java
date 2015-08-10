package twigkit.fig.loader;


import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.util.FigUtils;

import java.io.IOException;

/**
 * @author scottbrown
 */
public class MergedPropertiesLoader implements Loader {

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
     * @param primaryFig  The primary Fig instance
     */
    public void load(Fig primaryFig) {
        // Load the primary fig
        new PropertiesLoader(pathToPrimaryFig).load(primaryFig);

        // Load the secondary fig
        Fig secondaryFig = Fig.getInstance(new PropertiesLoader(pathToSecondaryFig));

        // Merge the secondary fig into the primary fig.
        FigUtils.mergeFig(primaryFig, secondaryFig);
    }

    public void write(Config config) throws IOException {
        throw new IOException("Write operation not supported.");
    }

    public void delete(Config config) throws IOException {
        throw new IOException("Delete operation not supported.");
    }
}
