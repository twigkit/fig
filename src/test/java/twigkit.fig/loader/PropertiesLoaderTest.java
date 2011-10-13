package twigkit.fig.loader;

import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.visitor.ConfigTreeWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author mr.olafsson
 */
public class PropertiesLoaderTest {

    @Test
    public void testLoadingFolderWithoutParentConf() throws Exception {
        Fig fig = Fig.load(new PropertiesLoader("folders"));

        Config conf = fig.get("folder", "sub-folder", "nested-in-subfolder-without-parent", "override");
        assertNotNull(conf);
        assertEquals("sub-value", conf.value("sub-property").as_string());
        new ConfigTreeWriter(fig.get("folder"));
    }
}
