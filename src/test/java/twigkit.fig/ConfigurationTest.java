package twigkit.fig;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author mr.olafsson
 */
public class ConfigurationTest {

    @Test
    public void createConfiguration() {
        Configuration conf = new Configuration("level1");
        conf.set("name1", "value1").set("name2", "value2");
        Assert.assertEquals("value1", conf.value("name1").get());
    }

    @Test
    public void createSubsets() {
        Configuration conf = new Configuration("root level").set("root1", "value1");
        Configuration other = new Configuration("extension_1").set("ex1", "value2");
        Configuration third = new Configuration("extension_1_1").set("ex1", "value2overriden").set("ex2", "value3");
        Configuration last = new Configuration("extension_2").set("ex2", "value4 (not overriding)");
        Configuration fourth = new Configuration("extension_2_1").set("root1", "overriding value1").set("ex3", "value4");

        conf.subset(other);
        conf.subset(last);
        other.subset(third);
        last.subset(fourth);

        Assert.assertNull(conf.subset("root level"));
        Assert.assertNotNull(conf.subset("extension_1"));
        Assert.assertNotNull(other.subset("extension_1_1"));

        walk(0, conf);

        Assert.assertEquals(1, conf.values().size());
        Assert.assertEquals(2, conf.subset("extension_1").values().size());
        Assert.assertEquals(3, conf.subset("extension_1").subset("extension_1_1").values().size());
    }

    private void walk(int level, Configuration config) {
        String indent = "";
        for (int i = 0; i < level; i++) {
            indent += "      ";
        }

        if (level == 0) {
            System.out.println(indent + (level > 0 ? "  +-- " : "") + config.name().toUpperCase());
        }

        for (Value v : config.values().values()) {
            System.out.println(indent + "  ¦-- " + v.name() + " = " + v.get());
        }

        for (Configuration c : config.subsets()) {
            System.out.println(indent + "  ¦");
            System.out.println(indent + "  +-- " + c.name().toUpperCase());
            walk(level + 1, c);
        }
    }
}
