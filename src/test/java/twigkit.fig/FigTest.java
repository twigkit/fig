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
        assertNull(config);

        config = fig.get("root");
        assertNotNull(config);
        assertEquals("root", config.name());

        config = fig.get("root", "extension-1", "extension-1-1");
        assertNotNull(config);
        assertEquals("extension-1-1", config.name());
    }

    @Test
    public void testFindConfig() {
        Fig fig = Fig.load(new PropertiesLoader("confs"), new PropertiesLoader("elements"));

        Config config = fig.find("does-not-exist");
        assertNull(config);

        config = fig.get("elements");
        assertNotNull(config);
        assertEquals(1869, config.value("year").as_int());

        config = fig.find("extension-1-2");
        assertNotNull(config);

        config = fig.find("metalloids");
        assertNotNull(config);
    }

	@Test
	public void testLoadSubFolder() {
		Fig fig = Fig.load(new PropertiesLoader("confs/sub"));

        Config config = fig.get("group", "folder-extension");
        assertNotNull(config);
		assertEquals("sub-1-value", config.value("sub-1-key").as_string());
		assertEquals("group-folder-extension-value", config.value("group-folder-extension-key").as_string());
		assertNotNull(config.value("sub-group-child-1-key"));
		assertNull(config.value("sub-group-child-1-key").get());
		assertEquals("", config.value("sub-group-child-1-key").as_string());

		new ConfigTreeWriter(fig.get("group"));
	}

    @Test
    public void testCreate() {
        Fig fig = new Fig();
        Config config = fig.create("conf").set("label", "value");
        assertNotNull(config);
        assertEquals("value", config.value("label").as_string());
        assertNull(fig.get("conf"));
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
