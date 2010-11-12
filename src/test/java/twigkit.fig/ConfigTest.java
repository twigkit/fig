package twigkit.fig;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author mr.olafsson
 */
public class ConfigTest {

    public static Config sample() {
        Config root = new Config("root").set("root-1-key", "root-1-value").set("root-2-key", "root-2-value");

        Config extension_1 = new Config("extension-1").set("ex-1-key", "ex-1-value");
        Config extension_1_1 = new Config("extension-1-1").set("ex-1-key", "ex-1-value-override").set("ex-1-1-key", "ex-1-1-value");
        Config extension_1_1_1 = new Config("extension-1-1-1").set("ex-1-1-1-key", "ex-1-1-1-value");
        Config extension_1_2 = new Config("extension-1-2");

        Config extension_2 = new Config("extension-2").set("ex-2-key", "ex-2-value");
        Config extension_2_1 = new Config("extension-2-1").set("root-1-key", "root-1-value-override").set("ex-2-1-key", "ex-2-1-value");

        root.extendWith(extension_1);
        extension_1.extendWith(extension_1_1);
        extension_1_1.extendWith(extension_1_1_1);
        extension_1.extendWith(extension_1_2);

        root.extendWith(extension_2);
        extension_2.extendWith(extension_2_1);

        return root;
    }
    
    @Test
    public void createConfiguration() {
        Config conf = new Config("level1");
        conf.set("name1", "value1").set("name2", "value2");
        Assert.assertEquals("value1", conf.value("name1").get());
    }

    @Test
    public void createConfigurationWithExtensions() {
        Config config = sample();

        Assert.assertNull(config.extension("root"));
        Assert.assertNotNull(config.extension("extension-1"));
        Assert.assertNotNull(config.extension("extension-1").extension("extension-1-1"));
        Assert.assertNotNull(config.extension("extension-1", "extension-1-1"));

        Assert.assertEquals(config.extension("extension-1", "extension-1-1"), config.extension("extension-1").extension("extension-1-1"));

        Assert.assertEquals(2, config.values().size());
        Assert.assertEquals(3, config.extension("extension-1").values().size());
        Assert.assertEquals(4, config.extension("extension-1").extension("extension-1-1").values().size());
    }

    @Test
    public void testSettingValueCollection() {
        List<Value> values = new ArrayList<Value>();
        values.add(new Value("key1", "value1"));
        values.add(new Value("key2", "value2"));
        values.add(new Value("key3", "value3"));

        Config config = new Config("test");
        config.set(values);

        Assert.assertEquals(3, config.values().size());
    }
}
