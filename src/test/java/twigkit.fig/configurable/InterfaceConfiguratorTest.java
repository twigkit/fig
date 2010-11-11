package twigkit.fig.configurable;

import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.sample.Sample;

import static org.junit.Assert.*;

/**
 * @author mr.olafsson
 */
public class InterfaceConfiguratorTest {

    @Test
    public void testConfigurable() {
        Sample sample1 = new Sample() {
            @Override
            public void validate() {
                assertNotNull(config.value("element"));
                assertEquals("Krypton", config.value("element").get());

                assertNotNull(config.value("symbol"));
                assertEquals("kr", config.value("symbol").get());
            }
        };

        Config config = new Config("sample").set("element", "Krypton").set("symbol", "kr");

        new InterfaceConfigurator(config).configure(sample1);

        sample1.validate();
    }
}
