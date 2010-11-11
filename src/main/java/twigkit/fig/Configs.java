package twigkit.fig;

import twigkit.fig.configurable.InterfaceConfigurer;
import twigkit.fig.loader.Loader;
import twigkit.fig.loader.PropertiesLoader;
import twigkit.fig.visitor.ConfigVisitor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author mr.olafsson
 */
public class Configs {

    private Map<String, Config> configs;

    /**
     * Create an empty {@link Config}s set.
     * 
     */
    public Configs() {
        configs = new LinkedHashMap<String, Config>();
    }

    /**
     * Create with {@link twigkit.fig.loader.Loader}s.
     * 
     * @param loader
     */
    public Configs(Loader... loader) {
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
    public static Configs load(Loader... loader) {
        return new Configs(loader);
    }

    public Map<String, Config> configs() {
        return configs;
    }

    /**
     * Get a {@link Config} by name ({@link Config#name()}.
     * 
     * @param name
     * @return
     */
    public Config get(String name) {
        return configs.get(name);
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
            for (Config c : configs().values()) {
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
     * Add a given {@link Config} anywhere in the hierarchy.
     * 
     * @param path
     * @param config
     */
    public void add(String[] path, Config config) {
        if (path.length == 1) {
            configs.put(config.name(), config);
        } else {
            if (configs().containsKey(path[0])) {
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
    }

    public static GenericConfigurer with(Config config) {
        return new GenericConfigurer(config);
    }

    private class ConfigFinder implements ConfigVisitor {

        private String name;
        private Config config;

        private ConfigFinder(String name) {
            this.name = name;
        }

        public void visit(Config config) {
            if (config.name().equals(name)) {
                this.config = config;
            }
        }

        public void value(Value value) {
        }

        public void extension(Config extension) {
        }

        public Config getConfig() {
            return config;
        }
    }
}
