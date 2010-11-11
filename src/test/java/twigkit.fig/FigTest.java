package twigkit.fig;

import org.junit.Assert;
import org.junit.Test;
import twigkit.fig.loader.PropertiesLoader;
import twigkit.fig.visitor.ConfigTreeWriter;

/**
 * @author mr.olafsson
 */
public class FigTest {

    @Test
    public void testLoadProperties() {
        for (Config config : Fig.load(new PropertiesLoader("confs")).configs()) {
            new ConfigTreeWriter(config);
        }
    }

    @Test
    public void testLoadElements() {
        for (Config config : Fig.load(new PropertiesLoader("elements")).configs()) {
            new ConfigTreeWriter(config);
        }
    }

    @Test
    public void testFindConfig() {
        Fig fig = Fig.load(new PropertiesLoader("confs"), new PropertiesLoader("elements"));

        Config config = fig.find("does-not-exist");
        Assert.assertNull(config);

        config = fig.find("extension-1-2");
        Assert.assertNotNull(config);

        config = fig.find("metalloids");
        Assert.assertNotNull(config);
    }
}
