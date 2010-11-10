package twigkit.fig;

import org.junit.Test;
import twigkit.fig.annotation.Configure;
import twigkit.fig.annotation.InjectionConfigurer;
import twigkit.fig.sample.CombinationSample;
import twigkit.fig.sample.InjectedSample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author mr.olafsson
 */
public class GenericConfigurerTest {

    @Test
    public void testGenericConfigurer() {
        final Config config = new Config("sample").set("element", "Krypton").set("symbol", "kr");

        CombinationSample sample = new CombinationSampleImpl();

        new GenericConfigurer(config).configure(sample);

        sample.validate();
    }

    @Test
    public void testConfigsConfigurer() {
        final Config config = new Config("sample").set("element", "Krypton").set("symbol", "kr");

        CombinationSample sample = new CombinationSampleImpl();

        Configs.with(config).configure(sample);

        sample.validate();
    }

    @Configure
    public class CombinationSampleImpl extends CombinationSample {
        @Override
        public void validate() {
            assertNotNull(viaInterface);

            assertNotNull(viaInjection);

            assertNotNull(el);
            assertEquals("Krypton", el);
            assertNotNull(symbol);
            assertEquals("kr", symbol);
        }
    }
}
