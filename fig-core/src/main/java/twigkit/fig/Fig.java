package twigkit.fig;

import twigkit.fig.annotation.InjectionConfigurator;
import twigkit.fig.loader.Loader;
import twigkit.fig.util.ConfigUtils;
import twigkit.fig.visitor.ConfigFinder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link Fig} is a forest of root {@link Config}s, backed by an <em>ordered</em>list of
 * configuration {@code Loader}s.
 *
 * @author mr.olafsson
 */
public class Fig {

    /** Map of singleton instances, parameterised by ordered lists of loaders. */
    private static Map<FigKey, Fig> figs = new HashMap<FigKey, Fig>();

    /** The <em>ordered</em> list of loaders that back this Fig instance. */
    private Loader[] loaders;

    /** Root nodes of the configuration forest. */
    private Map<String, Config> configs;


    /**
	 * Create an empty {@link Config}s set.
	 */
	private Fig() {
		this(new Loader[0]);
	}

	/**
	 * Create with {@link twigkit.fig.loader.Loader}s.
	 *
	 * @param loaders the {@link Loader}s that back this {@link Fig} instance.
	 */
	private Fig(Loader... loaders) {
        this.loaders = loaders;
        this.configs = new LinkedHashMap<String, Config>();
	}

    /**
     * Returns the singleton {@link Fig} instance corresponding to the given list of
     * {@link Loader}s.
     *
     * @param loaders an <em>ordered</em> list of {@link Loader} instances.
     * @return the singleton {@link Fig} instance corresponding to the given list of
     *  {@link Loader}s.
     */
    public static Fig getInstance(Loader... loaders) {
        FigKey key = new FigKey(loaders);
        if (figs.containsKey(key)) {
            return figs.get(key);
        } else {
            Fig fig = new Fig(loaders);
            fig.reload();
            figs.put(key, fig);
            return fig;
        }
    }


    /**
     * Reloads the configuration tree of this {@link Fig} instance, running through
     * the {@link Loader} instances in sequence.
     */
    public void reload() {
		this.configs = new LinkedHashMap<String, Config>();

        for (Loader l : loaders) {
            l.load(this);
        }
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
	public static Config create(String name) {
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
			if (!configs.containsKey(path[0])) {
				add(new Config(path[0], config.loader));
			}

			Config parent = configs.get(path[0]);
			ConfigUtils.addToParent(config, parent, path);
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
				if (c != null) {
                    c = c.extension(name[i]);
                } else {
                    return null;
                }
			}
			return c;
		}
	}

	/**
	 * Find a {@link Config} by name, traversing the hierarchy of {@link Config}s and their extensions.
	 *
	 * @param   name   The name of the {@link Config} to be found.
	 * @return a {@link Config} with the given name. If no {@link Config} can be found a
     * null is returned.
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
     * Use Fig to configure object that have been annotated with {@link twigkit.fig.annotation.Configure#with()} specifying
     * which {@link Config} to use.
     *
     * @param target
     * @return
     */
    public Object configure(Object target) {
        return new InjectionConfigurator(this).configure(target);
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

    /**
     * Primary key to reference {@link Fig} singleton instances.
     *
     */
    static class FigKey {

        /** An ordered list of loaders. */
        private Loader[] loaders;

        private FigKey(Loader[] loaders) {
            this.loaders = loaders;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FigKey)) return false;

            FigKey figKey = (FigKey) o;

            if (!Arrays.equals(loaders, figKey.loaders)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return loaders != null ? Arrays.hashCode(loaders) : 0;
        }
    }

}
