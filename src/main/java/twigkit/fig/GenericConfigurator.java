package twigkit.fig;

import twigkit.fig.annotation.InjectionConfigurator;
import twigkit.fig.configurable.Configurable;
import twigkit.fig.configurable.InterfaceConfigurator;

/**
 * @author mr.olafsson
 */
public class GenericConfigurator implements Configurator<Object> {

    private Config config;

    public GenericConfigurator(Config config) {
        this.config = config;
    }

    public Object configure(Object target) {
        if (target instanceof Configurable) {
            new InterfaceConfigurator(config).configure((Configurable) target);
        } else {
            new InjectionConfigurator(config).configure(target);
        }
        
        return target;
    }
}
