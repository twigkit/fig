package twigkit.fig.util;

import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility class to perform operations on an existing {@link Fig}.
 * @author scottbrown
 */
public class FigUtils {

    /**
     * Merge the contents of a secondary {@link Fig} into a primary {@link Fig}.
     * @param primary  The {@link Fig} to be updated
     * @param secondary The {@link Fig} that will merged into the primary
     */
    public static void mergeFig(Fig primary, Fig secondary) {
        walkThroughFig(primary, secondary.configs());
    }

    /**
     * Walk through the secondary {@link Config}s in order to merge them with the {@link Fig}.
     * @param fig   The {@link Fig} to be updated
     * @param configs   The {@link Config}s to be merged.
     */
    private static void walkThroughFig(Fig fig, Collection<Config> configs) {
        for (Config config : configs) {
            mergeConfig(fig, config);

            if (config.has_extensions()) {
                walkThroughFig(fig, config.extensions());
            }
        }
    }

    /**
     * Merge the {@link Config} into the {@link Fig}.
     * @param fig   The {@link Fig} to be updated.
     * @param config   The {@link Config} to be merged.
     */
    public static void mergeConfig(Fig fig, Config config) {
        // If config is not already in the fig, add it.
        if (fig.find(config.name()) == null) {
            if (config.parents() != null) {
                fig.add(new Config(config.name()), getPathTo(config));
            } else {
                fig.add(new Config(config.name()));
            }
        }

        // Otherwise, update existing configs with new entries or new values.
        Map<String, Value> newConfig = config.map(true);
        Map<String, Value> existingConfig = fig.find(config.name()).map(true);

        for (Map.Entry<String, Value> entry : newConfig.entrySet()) {
            String key = entry.getKey();
            Value value = entry.getValue();

            if (!existingConfig.containsKey(key) || !existingConfig.get(key).equals(value)) {
                fig.find(config.name()).set(key, value.get());
            }
        }
    }

    /**
     * Get the path for this {@link Config}. The path is constructed of the names of the {@link Config}
     * parents as well as the name of the {@link Config} itself.
     * @param config   The path will be acquired for this {@link Config}
     * @return   The path for this {@link Config}.
     */
    public static String[] getPathTo(Config config) {
        // Acquire parent hierarchy
        List<Config> parents = config.parents();
        Iterator<Config> iterator = parents.iterator();

        ArrayList<String> path = new ArrayList<String>();

        while (iterator.hasNext()) {
            path.add(iterator.next().name());
        }

        path.add(config.name());

        return path.toArray(new String[path.size()]);
    }
}