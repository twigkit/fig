package twigkit.fig;

import twigkit.fig.loader.Loader;
import twigkit.fig.visitor.ConfigFinder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author mr.olafsson
 */
public class Fig {

    private Map<String, Config> configs;

    /**
     * Create an empty {@link Config}s set.
     * 
     */
    public Fig() {
        configs = new LinkedHashMap<String, Config>();
    }

    /**
     * Create with {@link twigkit.fig.loader.Loader}s.
     * 
     * @param loader
     */
    public Fig(Loader... loader) {
        this();
        for (Loader l : loader) {
            l.load(this);
        }
    }

    /**
     * Create with {@link twigkit.fig.loader.Loader}s.
     * 
     * @param loader
     * @return
     */
    public static Fig load(Loader... loader) {
        return new Fig(loader);
    }

    /**
     * Add a {@link Config}.
     * 
     * @param config
     * @return
     */
    public Fig add(Config config) {
        return add(config, config.name());
    }

    /**
     * Create a new {@link Config} but not add it to {@link Fig}.
     * 
     * @param name
     * @return
     */
    public Config create(String name) {
        return new Config(name);
    }

    /**
     * Add a given {@link Config} anywhere in the hierarchy.
     * 
     * @param path
     * @param config
     */
    public Fig add(Config config, String... path) {
        if (path.length == 1) {
            configs.put(config.name(), config);
        } else {
            if (configs.containsKey(path[0])) {
                Config parent = configs.get(path[0]);
                for (int i = 1; i < path.length; i++) {
                    Config c = parent.extension(path[i]);
                    if (c != null) {
                        parent = c;
                    } else {
                        c = new Config(path[i]);
                        parent.extendWith(c);
                    }
                }
                parent.extendWith(config);
            }
        }

        return this;
    }

    /**
     * List of all top level {@link Config}s.
     *
     * @return
     */
    public Collection<Config> configs() {
        return configs.values();
    }

    /**
     * Get a {@link Config} by name ({@link Config#name()}.
     *
     * @param name
     * @return
     */
    public Config get(String... name) {
        Config c = configs.get(name[0]);

        if (name.length == 1) {
            return c;
        } else {
            for (int i = 1; i < name.length; i++) {
                c = c.extension(name[i]);
            }
            return c;
        }
    }

    /**
     * Find a {@link Config} by name, traversing the hierarchy of {@link Config}s and their extensions.
     *
     * @param name
     * @return
     */
    public Config find(String name) {
        Config config = null;
        if (configs.containsKey(name)) {
            config = configs.get(name);
        } else {
            ConfigFinder finder = new ConfigFinder(name);
            for (Config c : configs()) {
                c.accept(finder);
                if (finder.getConfig() != null) {
                    config = finder.getConfig();
                    break;
                }
            }
        }

        return config;
    }

    /**
     * Configure objects that implement {@link twigkit.fig.configurable.Configurable} or use
     * {@link twigkit.fig.annotation.Configure} annotations with the {@link Config} represented by the name (or full path)
     * given. Uses the {@link #get(String...)} method to retrieve the {@link Config}.
     *
     * @param name
     * @return
     */
    public GenericConfigurator with(String... name) {
        return with(get(name));
    }

    /**
     * Configure objects that implement {@link twigkit.fig.configurable.Configurable} or use
     * {@link twigkit.fig.annotation.Configure} annotations with the {@link Config} provided.
     * 
     * @param config
     * @return
     */
    public static GenericConfigurator with(Config config) {
        return new GenericConfigurator(config);
    }
}
