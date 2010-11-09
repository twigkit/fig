package twigkit.fig;

import java.util.*;

/**
 * @author mr.olafsson
 */
public class Configuration {

    private String name;
    private Configuration parent;
    private Map<String, Configuration> subsets;
    private Map<String, Value> values;

    public Configuration(String name) {
        super();
        this.name = name;
        this.subsets = new LinkedHashMap<String, Configuration>();
        this.values = new LinkedHashMap<String, Value>();
    }

    public String name() {
        return name;
    }

    public Value value(String name) {
        return values.get(name);
    }

    public Configuration set(String name, Object value) {
        return set(new Value().name(name).set(value));
    }

    public Configuration set(Value value) {
        values.put(value.name(), value);
        return this;
    }

    public Configuration set(Collection<Value> values) {
        for (Value value : values) {
            set(value);
        }
        return this;
    }

    public Configuration subset(String name) {
        return subsets.get(name);
    }

    public Configuration subset(Configuration configuration) {
        configuration.parent(this);

        Map<String, Value> newset = new HashMap<String, Value>();
        newset.putAll(this.values());
        newset.putAll(configuration.values());
        configuration.set(newset.values());
        
        subsets.put(configuration.name(), configuration);
        return this;
    }

    public Collection<Configuration> subsets() {
        return subsets.values();
    }

    public Map<String, Value> values() {
        return values;
    }

    Configuration parent() {
        return parent;
    }

    Configuration parent(Configuration parent) {
        this.parent = parent;
        return this;
    }
}
