package twigkit.fig.visitor;

import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.ConfigTest;

/**
 * @author mr.olafsson
 */
public class ConfigTreeWriterTest {

    @Test
    public void walkTree() {
        Config conf = ConfigTest.sample();

        System.out.println("Starting from root");
        System.out.println("---");
        new ConfigTreeWriter(conf);

        System.out.println();

        System.out.println("Starting from extension");
        System.out.println("---");
        new ConfigTreeWriter(conf.extension("extension-2"));
    }
}
