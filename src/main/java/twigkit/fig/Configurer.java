package twigkit.fig;

/**
 * @author mr.olafsson
 */
public class Configurer<T extends Configurable> {

    private Config config;

    public Configurer(Config config) {
        this.config = config;
    }

    public Configurer configure(T configurable) {
        configurable.configure(config);
        return this;
    }
}
