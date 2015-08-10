package twigkit.fig.loader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import twigkit.fig.Config;
import twigkit.fig.Fig;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * @author scottbrown
 */
public class MergedPropertiesLoaderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testMergedPropertiesLoader() {
        Loader loader = new MergedPropertiesLoader("confs", "confs_dev");
        Fig fig = Fig.getInstance(loader);

        assertEquals(3, fig.get("root").values().size());
        assertEquals("root-1-value", fig.get("root").value("root-1-key").as_string());
        assertEquals("root-2-new-value", fig.find("root").value("root-2-key").as_string());
        assertEquals("ex-2-new-new-value", fig.find("extension-2").value("ex-2-new-key").as_string());
        assertEquals("ex-3-value", fig.find("extension-3").value("ex-3-key").as_string());
        assertEquals("newsub-1-value", fig.find("new_sub").value("newsub-1-key").as_string());
        assertEquals("sub-1-new-value", fig.find("group").value("sub-1-key").as_string());
    }

    @Test
    public void testWriteOperationIsNotSupported() throws IOException {
        Loader loader = new MergedPropertiesLoader("confs", "confs_dev");
        Fig fig = Fig.getInstance(loader);

        Iterator<Config> iterator = fig.configs().iterator();

        while (iterator.hasNext()) {
            exception.expect(IOException.class);
            loader.write(iterator.next());
        }
    }

    @Test
    public void testDeleteOperationIsNotSupported() throws IOException {
        Loader loader = new MergedPropertiesLoader("confs", "confs_dev");
        Fig fig = Fig.getInstance(loader);

        Iterator<Config> iterator = fig.configs().iterator();

        while (iterator.hasNext()) {
            exception.expect(IOException.class);
            loader.delete(iterator.next());
        }
    }
}
