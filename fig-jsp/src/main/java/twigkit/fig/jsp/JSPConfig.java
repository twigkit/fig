package twigkit.fig.jsp;

import twigkit.fig.Config;
import twigkit.fig.Value;

import java.util.*;

/**
 * @author mr.olafsson
 */
public class JSPConfig implements Map<String, Value> {

    private Config config;
    

    public JSPConfig(Config config, String name) {
        this.config = (config != null) ? config : new Config(name);
    }

    public String getName() {
        return config.name();
    }

    public Map<String, JSPConfig> getExtension() {
        Map<String, JSPConfig> map = new LinkedHashMap<String, JSPConfig>(config.extensions().size());
        for (Config conf : config.extensions()) {
            map.put(conf.name(), new JSPConfig(conf, conf.name()));
        }
        return map;
    }

    public Collection<JSPConfig> getExtensions() {
        return wrap(config.extensions());
    }

    public Map<String, Value> getMap() {
        return config.map();
    }

    public Collection<Value> getValues() {
        return config.values();
    }

    public Collection<Value> getLocal() {
        return config.values(false);
    }

    public List<JSPConfig> getParents() {
        return wrap(config.parents());
    }

    public Config getParent() {
        return config.parent();
    }

    public Config getActual() {
        return config;
    }

    public String getPath() {
        return config.path();
    }

    private List<JSPConfig> wrap(Collection<Config> configs) {
        List<JSPConfig> wrapped = new ArrayList<JSPConfig>(configs.size());
        for (Config config : configs) {
            wrapped.add(new JSPConfig(config, config.name()));
        }
        return wrapped;
    }
    
    /** Delegate methods for Map */

    public int size() {return config.map().size();}

    public boolean isEmpty() {return config.map().isEmpty();}

    public boolean containsKey(Object o) {return config.map().containsKey(o);}

    public boolean containsValue(Object o) {return config.map().containsValue(o);}

    public Value get(Object o) {return config.map().get(o);}

    public Value put(String s, Value value) {return config.map().put(s, value);}

    public Value remove(Object o) {return config.map().remove(o);}

    public void putAll(Map<? extends String, ? extends Value> map) {config.map().putAll(map);}

    public void clear() {config.map().clear();}

    public Set<String> keySet() {return config.map().keySet();}

    public Collection<Value> values() {return config.map().values();}

    public Set<Entry<String,Value>> entrySet() {return config.map().entrySet();}

    public boolean equals(Object o) {return config.map().equals(o);}

    public int hashCode() {return config.map().hashCode();}
}
