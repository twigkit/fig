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
     * @param primary The {@link Fig} to be updated.
     * @param secondary The {@link Fig} to be merged.
     */
    public static void merge(Fig primary, Fig secondary) {
        if (primary != null && secondary != null) {
            merge(primary, primary.configs(), secondary.configs());
        }
    }

    /**
     * Merge a collection of {@link Config}s into a primary {@link Fig}.
     * @param primary The {@link Fig} to be updated.
     * @param configs The {@link Config}s to be merged.
     */
    public static void mergeConfigs(Fig primary, Collection<Config> configs) {
        if (primary != null && configs != null) {
            merge(primary, primary.configs(), configs);
        }
    }

    /**
     * Merge a collection of secondary {@link Config}s into a collection of primary {@link Config}s.
     * @param fig The {@link Fig} to be updated.
     * @param primaryConfigs   The {@link Config}s to be updated.
     * @param secondaryConfigs   The {@link Config}s to be merged.
     */
    private static void merge(Fig fig, Collection<Config> primaryConfigs, Collection<Config> secondaryConfigs) {
        for (Config secondaryConfig : secondaryConfigs) {
            // Get the primary config that has the same name as the secondary at this level in the fig hierarchy.
            Config primaryConfig = get(secondaryConfig.name(), primaryConfigs);

            if (primaryConfig != null) {
                // Merge the secondary config with the primary
                merge(primaryConfig, secondaryConfig);
            } else {
                fig.add(secondaryConfig, getPathTo(secondaryConfig));

                // Refresh the contents of the primary config
                primaryConfig = get(secondaryConfig.name(), primaryConfigs);
            }

            if (secondaryConfig.has_extensions()) {
                merge(fig, primaryConfig.extensions(), secondaryConfig.extensions());
            }
        }
    }

    /**
     * Get the {@link Config} with the given name from the collection of given {@link Config}s
     * @param name The name of the {@link Config}
     * @param configs The list of {@link Config}s that will be searched though to find the {@link Config} with
     *                the given name.
     * @return A {@link Config} from the collection of {@link Config}s that has the given name.
     */
    private static Config get(String name, Collection<Config> configs) {
        Config config = null;

        for (Config c : configs) {
            if (c.name().equals(name)) {
                config = c;
                break;
            }
        }

        return config;
    }

    /**
     * Merge the second {@link Config} with the first one by either adding a new entry or updating an existing value.
     * @param existingConfig The {@link Config} to be updated.
     * @param configToBeMerged The {@link Config} to be merged.
     */
    public static void merge(Config existingConfig, Config configToBeMerged) {
        Map<String, Value> originalConfig = existingConfig.map(true);
        Map<String, Value> newConfig = configToBeMerged.map(true);

        for (Map.Entry<String, Value> entry : newConfig.entrySet()) {
            String key = entry.getKey();
            Value value = entry.getValue();

            if (!originalConfig.containsKey(key) || !originalConfig.get(key).equals(value)) {
                existingConfig.set(key, value.get());
            }
        }
    }

    /**
     * Get the path to the given {@link Config}. The path is constructed of the names of the {@link Config}
     * parents as well as the name of the {@link Config} itself.
     * @param config The {@link Config} for which the path will be acquired.
     * @return The path to the given {@link Config}.
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