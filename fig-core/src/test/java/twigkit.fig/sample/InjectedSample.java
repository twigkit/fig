package twigkit.fig.sample;

import twigkit.fig.Config;
import twigkit.fig.annotation.Configure;

/**
 * @author mr.olafsson
 */
public abstract class InjectedSample {

    private String dummy;

    @Configure
    protected Config config;
    
    @Configure.Value( name = "element" )
    protected String el;

    @Configure.Value
    protected String symbol;

    public abstract void validate();

}
