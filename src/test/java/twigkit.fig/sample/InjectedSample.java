package twigkit.fig.sample;

import twigkit.fig.annotation.Configure;

/**
 * @author mr.olafsson
 */
@Configure( with = "elements" )
public abstract class InjectedSample {

    private String dummy;
    
    @Configure.Value( name = "element" )
    protected String el;
    @Configure.Value
    protected String symbol;

    public abstract void validate();

}
