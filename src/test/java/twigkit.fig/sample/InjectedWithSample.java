package twigkit.fig.sample;

import twigkit.fig.Config;
import twigkit.fig.annotation.Configure;

/**
 * @author mr.olafsson
 */
public abstract class InjectedWithSample {

    @Configure( with = "level_1,level_2")
    protected Config config;

    public abstract void validate();
}
