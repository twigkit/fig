package twigkit.fig.loader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.util.ConfigUtils;
import twigkit.fig.util.FigUtils;
import twigkit.fig.visitor.ConfigTreeWriter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    @Test
    public void testFallbackFigIsReturnedWhenTheOtherCannotBeFound() {
        Fig fig1 = Fig.getInstance(new PropertiesLoader("confs"));
        Fig fig2 = Fig.getInstance(new MergedPropertiesLoader("confs", "invalid_path"));

        Iterator iterator = fig1.configs().iterator();
        for (Config config2 : fig2.configs()) {
            assertEquals(iterator.next(), config2);
        }

        fig1 = Fig.getInstance(new PropertiesLoader("confs"));
        fig2 = Fig.getInstance(new MergedPropertiesLoader("invalid_path", "confs"));

        iterator = fig1.configs().iterator();
        for (Config config2 : fig2.configs()) {
            assertEquals(iterator.next(), config2);
        }
    }

    @Test
    public void testFallbackFigIsReturnedWhenTheOtherIsUndefined() {
        Fig fig1 = Fig.getInstance(new PropertiesLoader("confs"));
        Fig fig2 = Fig.getInstance(new MergedPropertiesLoader("confs", null));

        Iterator iterator = fig1.configs().iterator();
        for (Config config2 : fig2.configs()) {
            assertEquals(iterator.next(), config2);
        }

        fig1 = Fig.getInstance(new PropertiesLoader("confs"));
        fig2 = Fig.getInstance(new MergedPropertiesLoader(null, "confs"));

        iterator = fig1.configs().iterator();
        for (Config config2 : fig2.configs()) {
            assertEquals(iterator.next(), config2);
        }
    }

    @Test
    public void testExtensionsWithTheSameNameUnderDifferentParentConfigsAreHandledCorrectly() {
        Fig fig = Fig.getInstance(new PropertiesLoader("confs"));
        assertNotNull(fig);

        String valueRoot = "http://some-dev-internal-server.com/";

        Config config = fig.get("companies");
        assertEquals(valueRoot + "default-companies-search", config.value("host").as_string());
        Config extension = config.extension("detail");
        assertEquals(valueRoot + "default-companies-detail", extension.value("host").as_string());

        config = fig.get("people");
        assertEquals(valueRoot + "default-people-search", config.value("host").as_string());
        extension = config.extension("detail");
        assertEquals(valueRoot + "default-people-detail", extension.value("host").as_string());

        new ConfigTreeWriter(fig.get("companies"));
        new ConfigTreeWriter(fig.get("people"));

        fig = Fig.getInstance(new MergedPropertiesLoader("confs", "confs_dev"));
        assertNotNull(fig);

        config = fig.get("companies");
        assertEquals(valueRoot + "overlay-companies-search", config.value("host").as_string());
        extension = config.extension("detail");
        assertEquals(valueRoot + "overlay-companies-search", extension.value("host").as_string());

        config = fig.get("people");
        assertEquals(valueRoot + "overlay-people-search", config.value("host").as_string());
        extension = config.extension("detail");
        assertEquals(valueRoot + "overlay-people-detail", extension.value("host").as_string());

        new ConfigTreeWriter(fig.get("companies"));
        new ConfigTreeWriter(fig.get("people"));
    }

    @Test
    public void testMergedPropertiesLoaderMergesConfigList() {
        MergedPropertiesLoader loader = new MergedPropertiesLoader("confs", "confs_dev");

        String configsToMerge = "people.detail[enabled:true&sub.group.folder-extension[group-folder-extension-key:test";
        loader.setConfigsToMerge(configsToMerge);

        Fig fig = Fig.getInstance(loader);

        assertEquals(2, fig.get("people", "detail").values().size());
        assertEquals("true", fig.get("people", "detail").value("enabled").as_string());

        assertEquals(2, fig.get("sub", "group", "folder-extension").values().size());
        assertEquals("test", fig.get("sub", "group", "folder-extension").value("group-folder-extension-key").as_string());
    }

    @Test
    public void testFallbackFigIsReturnedWhenTheOtherIsEmpty() {
        Fig fig1 = Fig.getInstance(new PropertiesLoader("confs"));
        Fig fig2 = Fig.getInstance(new MergedPropertiesLoader("confs", ""));

        Iterator iterator = fig1.configs().iterator();
        for (Config config2 : fig2.configs()) {
            assertEquals(iterator.next(), config2);
        }

        fig1 = Fig.getInstance(new PropertiesLoader("confs"));
        fig2 = Fig.getInstance(new MergedPropertiesLoader("", "confs"));

        iterator = fig1.configs().iterator();
        for (Config config2 : fig2.configs()) {
            assertEquals(iterator.next(), config2);
        }
    }

    @Test
    public void testFigIsEmptyWhenBothPrimaryAndSecondaryFigPathsAreEmpty() {
        Fig fig = Fig.getInstance(new MergedPropertiesLoader("", ""));
        assertEquals(fig.configs().size(), 0);
    }
}
