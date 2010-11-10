package twigkit.fig.sample;

import twigkit.fig.Config;
import twigkit.fig.annotation.Configure;
import twigkit.fig.configurable.Configurable;

/**
 * @author mr.olafsson
 */
@Configure
public abstract class CombinationSample implements Configurable {

    @Configure
    protected Config viaInjection;

    @Configure.Value( name = "element" )
    protected String el;
    @Configure.Value
    protected String symbol;

    protected Config viaInterface;

    public void configure(Config config) {
        viaInterface = config;
    }

    public abstract void validate();
}
