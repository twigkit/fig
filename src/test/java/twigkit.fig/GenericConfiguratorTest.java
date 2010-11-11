package twigkit.fig;

import org.junit.Test;
import twigkit.fig.sample.InjectedSample;
import twigkit.fig.sample.Sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author mr.olafsson
 */
public class GenericConfiguratorTest {

    @Test
    public void testInjection() {
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

        new GenericConfigurator(config).configure(sample);

        sample.validate();

        InjectedSample sample2 = new InjectedSample() {
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

        Fig.with(config).configure(sample2);

        sample2.validate();
    }

    @Test
    public void testInterface() {
        Sample sample = new Sample() {
            @Override
            public void validate() {
                assertNotNull(config.value("element"));
                assertEquals("Krypton", config.value("element").get());
                assertNotNull(config.value("symbol"));
                assertEquals("kr", config.value("symbol").get());
            }
        };

        Config config = new Config("sample").set("element", "Krypton").set("symbol", "kr");

        new GenericConfigurator(config).configure(sample);

        sample.validate();
    }
}
