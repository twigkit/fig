package twigkit.fig;

import org.junit.Test;
import twigkit.fig.visitor.ConfigTreeWriter;

/**
 * @author mr.olafsson
 */
public class FigTest {

    @Test
    public void testLoadProperties() {
        Fig fig = new Fig();
        PropertiesLoader loader = new PropertiesLoader(fig);

        for (Config config : fig.configs().values()) {
            new ConfigTreeWriter(config);
        }
    }
}
