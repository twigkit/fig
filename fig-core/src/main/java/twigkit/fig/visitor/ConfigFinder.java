package twigkit.fig.visitor;

import twigkit.fig.Config;
import twigkit.fig.Value;

/**
 * {@link twigkit.fig.visitor.ConfigVisitor} that goes through all {@link twigkit.fig.Config} objects that match
 * the 'label' provided via the constructor. {@link ConfigFinder#getConfig()} will return the last one
 * that matches.
 *
 * @author mr.olafsson
 */
public class ConfigFinder implements ConfigVisitor {

    private String name;
    private Config config;

    public ConfigFinder(String name) {
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