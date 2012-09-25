package twigkit.fig.annotation;

import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.sample.InjectedSample;
import twigkit.fig.sample.InjectedWithSample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author mr.olafsson
 */
public class InjectionConfiguratorTest {

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

        new InjectionConfigurator(config).configure(sample);

        sample.validate();
    }

    @Test
    public void testAnnotationWith() {
        final Fig fig = new Fig();
        fig.add(fig.create("level_1").set("l1", "v1").extend_with(
                Fig.create("level_2").set("l2", "v2").extend_with(
                        Fig.create("level_3").set("l3", "v3")
                )
            )
        );

        assertNotNull(fig);

        InjectedWithSample sample = new InjectedWithSample() {
            @Override
            public void validate() {
                assertNotNull(config);
                assertEquals("v2", config.value("l2").as_string());
                assertEquals("v2", value);

            }
        };

        new InjectionConfigurator(fig).configure(sample);

        sample.validate();
    }
}
