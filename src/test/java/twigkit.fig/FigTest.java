package twigkit.fig;

import org.junit.Assert;
import org.junit.Test;
import twigkit.fig.visitor.ConfigTreeWriter;

/**
 * @author mr.olafsson
 */
public class FigTest {

    @Test
    public void testLoadProperties() {
        Fig fig = new Fig();
        PropertiesLoader loader = new PropertiesLoader(fig);

        for (Config config : fig.configs().values()) {
            new ConfigTreeWriter(config);
        }
    }

    @Test
    public void testFindConfig() {
        Fig fig = new Fig();
        PropertiesLoader loader = new PropertiesLoader(fig);

        Config config = fig.find("does-not-exist");
        Assert.assertNull(config);

        config = fig.find("extension-1-2");
        Assert.assertNotNull(config);
    }
}
