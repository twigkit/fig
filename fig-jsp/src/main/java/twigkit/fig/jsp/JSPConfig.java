package twigkit.fig.jsp;

import twigkit.fig.Config;
import twigkit.fig.Value;

import java.util.*;

/**
 * @author mr.olafsson
 */
public class JSPConfig {

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
}
