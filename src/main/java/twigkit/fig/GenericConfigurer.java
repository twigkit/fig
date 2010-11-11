package twigkit.fig;

import twigkit.fig.annotation.Configure;
import twigkit.fig.annotation.InjectionConfigurer;
import twigkit.fig.configurable.Configurable;
import twigkit.fig.configurable.InterfaceConfigurer;

/**
 * @author mr.olafsson
 */
public class GenericConfigurer {

    private Config config;

    public GenericConfigurer(Config config) {
        this.config = config;
    }

    public GenericConfigurer configure(Object target) {
        if (target instanceof Configurable) {
            new InterfaceConfigurer<Configurable>(config).configure((Configurable) target);
        } else {
            new InjectionConfigurer(config).configure(target);
        }
        return this;
    }
}
