package twigkit.fig.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twigkit.fig.Config;
import twigkit.fig.Value;
import twigkit.fig.loader.Loader;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to perform operations on a {@link Config} or set of {@link Config}s.
 *
 * @author scottbrown
 */
public class ConfigUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

    /**
     * Convert the given config or sequence of configs into a {@link List} of {@link Config}s.
     *
     * A config should be given in the following form:
     *
     * parent.child[property:value
     *
     * If more than one config is to be merged, subsequent configs should be separated with an ampersand, for
     * example:
     *
     * parent.child[property:value&parent[property:value
     *
     * @param configurations The config or sequence of configs to be converted into a {@link List} of {@link Config}s.
     * @param loader The {@link Loader} that backs each instance of the {@link Config}.
     * @return A {@link List} of {@link Config}s.
     */
    public static List<Config> asList(String configurations, Loader loader) {
        List<Config> configs = null;

        if (configurations != null) {
            configs = new ArrayList<Config>();

            for (String c : configurations.split("&")) {

                String[] configDetails = c.split(":");

                String property = configDetails[0].substring(configDetails[0].indexOf("[") + 1, configDetails[0].length());
                String[] path = configDetails[0].substring(0, configDetails[0].indexOf("[")).split("\\.");

                Config config = null;
                if (path.length == 1) {
                    config = new Config(path[0], loader);
                } else if (path.length > 1) {
                    config = new Config(path[path.length - 1], loader);
                } else {
                    logger.warn("No configuration given for property {}. This property will be ignored", property);
                }

                if (config != null) {
                    config.set(new Value<Object>(property, configDetails[1], false));

                    if (path.length == 1) {
                        configs.add(config);
                    } else {
                        Config parent = new Config(path[0], loader);
                        addToParent(config, parent, path);
                        configs.add(config.parents().get(0));
                    }
                }
            }
        }

        return configs;
    }

    /**
     * Add the given {@link Config} to the given parent {@link Config}.
     *
     * @param config The {@link Config} to be added as a child of the given parent {@link Config}.
     * @param parent The {@link Config} that will be a parent of the given {@link Config}
     * @param path The configuration path pointing from the parent down to the child.
     */
    public static void addToParent(Config config, Config parent, String... path) {
        if (parent != null) {
            for (int i = 1; i < path.length - 1; i++) {
                Config child = parent.extension(path[i]);

                if (child == null && i < path.length) {
                    child = new Config(path[i]);
                    parent.extend_with(child);
                }

                parent = child;
            }

            if (config != null) {
                parent.extend_with(config);
            }
        }
    }
}
