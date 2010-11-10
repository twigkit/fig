package twigkit.fig;

import twigkit.fig.visitor.ConfigVisitor;

import java.util.*;

/**
 * @author mr.olafsson
 */
public class Config {

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

    public String name() {
        return name;
    }

    public Value value(String name) {
        return values.get(name);
    }

    public Config set(String name, Object value) {
        return set(new Value().name(name).set(value));
    }

    public Config set(Value value) {
        values.put(value.name(), value);
        return this;
    }

    public Config set(Collection<Value> values) {
        for (Value value : values) {
            set(value);
        }
        return this;
    }

    public Value find(String name) {
        ValueVisitor v = new ValueVisitor(name);
        this.accept(v);
        return v.getValue();
    }

    public Config extension(String name) {
        return extensions.get(name);
    }

    public Config extendWith(Config configuration) {
        configuration.parents().addAll(parents());
        configuration.parent(this);

        extensions.put(configuration.name(), configuration);
        return this;
    }

    public Collection<Config> extensions() {
        return extensions.values();
    }

    public Collection<Value> values() {
        Map<String, Value> combined = new LinkedHashMap<String, Value>();
        for (Config conf : parents()) {
            combined.putAll(conf.values);
        }
        combined.putAll(values);
        return combined.values();
    }

    public List<Config> parents() {
        return parents;
    }

    Config parent(Config parent) {
        this.parents.add(parent);
        return this;
    }

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

    private class ValueVisitor implements ConfigVisitor {

        private String name;
        private Value value;

        public ValueVisitor(String name) {
            this.name = name;
        }

        public void visit(Config config) {}

        public void value(Value value) {
            if (value.name().equals(name)) {
                this.value = value;
            }
        }

        public void extension(Config extension) {}

        public Value getValue() {
            return value;
        }
    }
}
