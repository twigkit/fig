package twigkit.fig.sample;

import twigkit.fig.Config;
import twigkit.fig.annotation.Configure;

/**
 * @author mr.olafsson
 */
public abstract class InjectedWithSample {

    @Configure( with = "level_1.level_2")
    protected Config config;

    @Configure.Value( with = "level_1.level_2", name = "l2" )
    protected String value;

    public abstract void validate();
}
