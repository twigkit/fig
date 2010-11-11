package twigkit.fig;

import org.junit.Assert;
import org.junit.Test;
import twigkit.fig.loader.PropertiesLoader;
import twigkit.fig.sample.InjectedSample;
import twigkit.fig.visitor.ConfigTreeWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
    public void testGetConfig() {
        Fig fig = Fig.load(new PropertiesLoader("confs"), new PropertiesLoader("elements"));

        Config config = fig.get("does-not-exist");
        Assert.assertNull(config);

        config = fig.get("root");
        Assert.assertNotNull(config);
        Assert.assertEquals("root", config.name());

        config = fig.get("root", "extension-1", "extension-1-1");
        Assert.assertNotNull(config);
        Assert.assertEquals("extension-1-1", config.name());
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

    @Test
    public void testStaticWith() {
        final Config config = new Config("sample").set("element", "Krypton").set("symbol", "kr");

        InjectedSample sample = new InjectedSample() {
            @Override
            public void validate() {
                assertNotNull(config);
                assertEquals(config, this.config);
                assertNotNull(el);
                assertEquals("Krypton", el);

                assertNotNull(symbol);
                assertEquals("kr", symbol);
            }
        };

        Fig.with(config).configure(sample);

        sample.validate();
    }

    @Test
    public void testInstanceWith() {
        InjectedSample sample = new InjectedSample() {
            @Override
            public void validate() {
                assertNotNull(config);
                assertEquals(config, this.config);
                assertNull(el);

                assertNotNull(symbol);
                assertEquals("po", symbol);
            }
        };

        Fig.load(new PropertiesLoader("elements")).with("elements", "solids", "metalloids", "polonium").configure(sample);

        sample.validate();
    }
}
