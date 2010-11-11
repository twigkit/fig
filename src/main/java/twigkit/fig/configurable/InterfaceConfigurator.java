package twigkit.fig.configurable;

import twigkit.fig.Config;
import twigkit.fig.Configurator;

/**
 * @author mr.olafsson
 */
public class InterfaceConfigurator implements Configurator<Configurable> {

    private Config config;

    public InterfaceConfigurator(Config config) {
        this.config = config;
    }

    public Configurable configure(Configurable configurable) {
        configurable.configure(config);
        
        return configurable;
    }
}
