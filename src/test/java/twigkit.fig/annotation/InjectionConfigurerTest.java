package twigkit.fig.annotation;

import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.sample.InjectedSample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author mr.olafsson
 */
public class InjectionConfigurerTest {

    @Test
    public void testAnnotation() {
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

        new InjectionConfigurer(config).configure(sample);

        sample.validate();
    }
}
