package twigkit.fig;

import twigkit.fig.visitor.ConfigVisitor;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author mr.olafsson
 */
public class Config implements Serializable {

    private String name;
    private List<Config> parents;
    private Map<String, Config> extensions;
    private Map<String, Value> values;

    public Config(String name) {
        super();
        this.name = name;
        this.parents = new ArrayList<Config>();
        this.extensions = new LinkedHashMap<String, Config>();
        this.values = new LinkedHashMap<String, Value>();
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
        return values.get(name);
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
    public Config extendWith(Config configuration) {
        configuration.parents().addAll(parents());
        configuration.parent(this);

        extensions.put(configuration.name(), configuration);
        return this;
    }

    /**
     * Get all {@link Config}s that extend this one.
     * @return
     */
    public Collection<Config> extensions() {
        return extensions.values();
    }

    /**
     * Get all {@link twigkit.fig.Value}s for this {@link Config}. If this {@link Config} extends others, it will
     * include and override any values from the parents.
     * 
     * @return
     */
    public Collection<Value> values() {
        Map<String, Value> combined = new LinkedHashMap<String, Value>();
        for (Config conf : parents()) {
            combined.putAll(conf.values);
        }
        combined.putAll(values);
        return combined.values();
    }

    /**
     * Get all parent {@link Config}s.
     * 
     * @return
     */
    public List<Config> parents() {
        return parents;
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
}
