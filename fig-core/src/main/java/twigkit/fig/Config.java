package twigkit.fig;

import twigkit.fig.loader.Loader;
import twigkit.fig.visitor.ConfigVisitor;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author mr.olafsson
 */
public class Config implements Serializable {

    private String name;
    private List<Config> parents;
    private Map<String, Config> extensions;
    private Map<String, Value> values;
    protected Loader loader;

    public Config(String name) {
        this(name, null);
    }

    public Config(String name, Loader loader) {
        super();
        this.name = name;
        this.parents = new ArrayList<Config>();
        this.extensions = new LinkedHashMap<String, Config>();
        this.values = new LinkedHashMap<String, Value>();
        this.loader = loader;
    }

    /**
     * Name used to identify this {@link Config}.
     *
     * @return
     */
    public String name() {
        return name;
    }

    /**
     * Get a {@link Value} from this {@link Config} by label.
     *
     * @param name
     * @return
     */
    public Value value(String name) {
        if (has(name)) {
            return values.get(name);
        }

        List<Config> parents = parents();
        if (parents != null && parents.size() > 0) {
            for (int i = parents.size() - 1; i >= 0; i--) {
                Config conf = parents.get(i);
                if (conf.has(name)) {
                    return conf.value(name);
                }
            }
        }

        return new Value(name, null);
    }

    /**
     * Create and set a {@link Value} for this {@link Config}.
     *
     * @param name
     * @param value
     * @return
     */
    public Config set(String name, Object value) {
        return set(new Value().label(name).set(value));
    }

    /**
     * Set a {@link Value} for this {@link Config}.
     *
     * @param value
     * @return
     */
    public Config set(Value value) {
        values.put(value.label(), value);
        return this;
    }

    /**
     * Set multiple {@link Value}s for this {@link Config}.
     *
     * @param values
     * @return
     */
    public Config set(Collection<Value> values) {
        for (Value value : values) {
            set(value);
        }
        return this;
    }

    /**
     * Check if a {@link Value} is locally set for the given name (key).
     *
     * @param name
     * @return
     */
    public boolean has(String name) {
        return values.containsKey(name);
    }

    /**
     * Get an extension of this {@link Config} by name ({@link Config#name()}. Retrieve {@link Config} extensions further
     * down in the hierarchy by providing the name of each extension as a String e.g. extension("level1", "level2").
     *
     * @param name
     * @return
     */
    public Config extension(String... name) {
        Config c = extensions.get(name[0]);

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
     * Add a {@link Config} as an extension to this one.
     *
     * @param configuration
     * @return
     */
    public void remove_extension(Config configuration) {
        extensions.remove(configuration.name());
    }

    /**
     * Add a {@link Config} as an extension to this one.
     *
     * @param configuration
     * @return
     */
    public Config  extend_with(Config configuration) {
        if (configuration.parents().isEmpty()) {
            configuration.parents().addAll(parents());
            configuration.parent(this);
        }

        if (configuration.loader == null) {
            configuration.loader = loader;
        }

        extensions.put(configuration.name(), configuration);
        return this;
    }

    public boolean has_extensions() {
        return !extensions().isEmpty();
    }

    /**
     * Get all {@link Config}s that extend this one.
     *
     * @return
     */
    public Collection<Config> extensions() {
        return extensions.values();
    }

    /**
     * Get all {@link twigkit.fig.Value}s for this {@link Config} as a {@link Map<String, Value>}. If this {@link Config} extends others, it will
     * include and override any values from the parents.
     *
     * @return
     */
    public Map<String, Value> map() {
        return map(true);
    }

    /**
     * Get all {@link twigkit.fig.Value}s for this {@link Config} as a {@link Map<String, Value>}. If this {@link Config}
     * extends others and {@link #map(boolean)} parameter is true, it will include and override any values
     * from the parents.
     *
     * @param inherit
     * @return
     */
    public Map<String, Value> map(boolean inherit) {
        if (inherit) {
            Map<String, Value> combined = new LinkedHashMap<String, Value>();
            for (Config conf : parents()) {
                combined.putAll(conf.values);
            }
            combined.putAll(values);
            return combined;
        } else {
            return values;
        }
    }

    /**
     * Get all {@link twigkit.fig.Value}s for this {@link Config}. If this {@link Config} extends others, it will
     * include and override any values from the parents.
     *
     * @return
     */
    public Collection<Value> values() {
        return values(true);
    }

    /**
     * Get all {@link twigkit.fig.Value}s for this {@link Config}. If this {@link Config} extends others and
     * {@link #map(boolean)} parameter is true, it will include and override any values from the parents.
     *
     * @param inherit
     * @return
     */
    public Collection<Value> values(boolean inherit) {
        return map(inherit).values();
    }

    /**
     * Get all parent {@link Config}s.
     *
     * @return
     */
    public List<Config> parents() {
        return parents;
    }

    public Config parent() {
        return (parents().isEmpty()) ? null : parents().get(parents().size() - 1);
    }

    /**
     * Add a {@link Config} as a parent.
     *
     * @param parent
     * @return
     */
    Config parent(Config parent) {
        this.parents.add(parent);
        return this;
    }

    public String path() {
        StringBuilder buf = new StringBuilder();
        for (Config parent : parents()) {
            buf.append("/");
            buf.append(parent.name());
        }
        buf.append("/");
        buf.append(name());
        return buf.toString();
    }
    /**
     * Accept a {@link ConfigVisitor}.
     *
     * @param visitor
     */
    public void accept(ConfigVisitor visitor) {
        visitor.visit(this);
        for (Value value : values()) {
            visitor.value(value);
        }
        for (Config extension : extensions()) {
            visitor.extension(extension);
            extension.accept(visitor);
        }
    }

    /**
     * Assigns the config a new parent. Files backing the config will be moved accordingly.
     *
     * @param newParent new parent of this config.
     * @return true of the move was successful.
     */
    public boolean move(Config newParent) {
        if (delete()) {
            newParent.extend_with(this);
            return save();
        }
        return false;
    }

    public boolean delete() {
        try {
            loader.delete(this);
            parent().remove_extension(this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean save() {
        try {
            loader.write(this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Config)) return false;

        Config config = (Config) o;

        if (name != null ? !name.equals(config.name) : config.name != null) return false;
        if (parents != null ? !parents.equals(config.parents) : config.parents != null) return false;
        if (values != null ? !values.equals(config.values) : config.values != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (parents != null ? parents.hashCode() : 0);
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
