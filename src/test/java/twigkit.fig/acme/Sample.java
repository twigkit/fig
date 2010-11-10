package twigkit.fig.acme;

import twigkit.fig.Config;
import twigkit.fig.Configurable;

/**
 * @author mr.olafsson
 */
public abstract class Sample implements Configurable {

    protected Config config;

    public abstract void validate();

    public void configure(Config config) {
        this.config = config;
    }
}
