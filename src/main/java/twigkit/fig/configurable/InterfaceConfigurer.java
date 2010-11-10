package twigkit.fig.configurable;

import twigkit.fig.Config;

/**
 * @author mr.olafsson
 */
public class InterfaceConfigurer<T extends Configurable> {

    private Config config;

    public InterfaceConfigurer(Config config) {
        this.config = config;
    }

    public InterfaceConfigurer configure(T configurable) {
        configurable.configure(config);
        return this;
    }
}
