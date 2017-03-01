package twigkit.fig.visitor;

import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.loader.PropertiesLoader;

/**
 * @author scottbrown 
 */
public class ConfigLoggerTest {

    @Test
    public void hidePrivateValues() {
        for (Config config : Fig.getInstance(new PropertiesLoader("confs/private")).configs()) {
            new ConfigLogger(config);
        }
    }
}
