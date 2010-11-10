package twigkit.fig.configurable;

import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.sample.Sample;

import static org.junit.Assert.*;

/**
 * @author mr.olafsson
 */
public class InterfaceConfigurerTest {

    @Test
    public void testConfigurable() {
        Sample sample1 = new Sample() {
            @Override
            public void validate() {
                assertNotNull(config.value("element"));
                assertEquals("Krypton", config.value("element").get());
            }
        };

        Sample sample2 = new Sample() {
            @Override
            public void validate() {
                assertNotNull(config.value("symbol"));
                assertEquals("kr", config.value("symbol").get());
            }
        };

        Config config = new Config("sample").set("element", "Krypton").set("symbol", "kr");

        new InterfaceConfigurer(config).configure(sample1).configure(sample2);

        sample1.validate();
        sample2.validate();
    }
}
