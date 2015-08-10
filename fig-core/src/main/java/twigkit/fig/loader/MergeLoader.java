package twigkit.fig.loader;

import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.Value;
import twigkit.fig.annotation.Configure;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by scottbrown on 05/08/2015.
 *
 * There are 2 ways this could be done:
 * 1. Two different fig trees could be created and then the second is compared to the
 * first. If any differences are found they are merged. Finally the first fig is updated
 * to incoporate the second.
 *
 * 2. One fig tree is created using the combination of 2 property loaders. An internal
 * comparison between the fig tree would then remove any duplicates.
 *
 * Trying the first to begin with.
 */
public class MergeLoader implements Loader {

    private Fig fig1;
    private Fig fig2;

    public MergeLoader(String path1, String path2) {
        fig1 = Fig.getInstance(new PropertiesLoader(path1));
        fig2 = Fig.getInstance(new PropertiesLoader(path2));
    }

    public void load(Fig configs) {
        // TO DO
    }

    public void write(Config config) throws IOException {
        throw new IOException("Write operation not supported.");
    }

    public void delete(Config config) throws IOException {
        throw new IOException("Delete operation not supported.");
    }

    /**
     * Merge two Fig instances into one.
     * @param primary
     * @param secondary
     */
    public void merge(Fig primary, Fig secondary) {
        // Walk through the secondary fig and add or override each property to the primary fig

        // Get a list of configs (directories) in the secondary fig
        Collection<Config> configs = secondary.configs();

        // For each config compare the values between the secondary and primary
        for (Config config : configs) {

            // Get the values for this config
            Map<String, Value> properties_secondary = config.map(true); //key-value

            /// Assume primary has the config referred to in the secondary.
            Map<String, Value> properties_primary = primary.get(config.name()).map(true);

            for (Map.Entry<String, Value> entry : properties_secondary.entrySet()) {
                // Check if the key also appears in the primary
                if (properties_primary.containsKey(entry.getKey())) {
                    // Check if the value also appears in the primary
                    if (properties_primary.get(entry.getKey()).equals(entry.getValue())) {
                        // The primary already contains this Value from the secondary
                        continue;
                    } else {
                        // The value is different for this key - update primary
//                        properties_primary.replace(entry.getKey(), entry.getValue());
                    }
                } else {
                    // The primary does not contain this key - add to primary
                    properties_primary.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
