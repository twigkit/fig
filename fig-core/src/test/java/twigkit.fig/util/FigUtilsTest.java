package twigkit.fig.util;

import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.loader.MergedPropertiesLoader;
import twigkit.fig.loader.PropertiesLoader;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author scottbrown
 */
public class FigUtilsTest {

    @Test
    public void testExistingConfigPropertiesAreLeftUnchanged() {
        Fig primary = Fig.getInstance(new PropertiesLoader("confs"));
        Fig secondary = Fig.getInstance(new PropertiesLoader("confs_dev"));

        String originalRoot1KeyValue = primary.find("root").value("root-1-key").as_string();
        String originalExtension2Value = primary.find("extension-2").value("root-1-key").as_string();

        FigUtils.merge(primary, secondary);

        // Expect the primary root config to have been updated
        Config updatedPrimaryRootConfig = primary.find("root");
        assertEquals(3, updatedPrimaryRootConfig.values().size());

        // Expect the primary root config value "root-1-key" to be the same
        String updatedValue = updatedPrimaryRootConfig.value("root-1-key").as_string();
        assertEquals(originalRoot1KeyValue, updatedValue);

        // Expect the primary extension config value "root-1-key" to be the same
        updatedValue = primary.find("extension-2").value("root-1-key").as_string();
        assertEquals(originalExtension2Value, updatedValue);
    }

    @Test
    public void testExistingConfigsAreUpdatedWithNewPropertyValues() {
        Fig primary = Fig.getInstance(new PropertiesLoader("confs"));
        Fig secondary = Fig.getInstance(new PropertiesLoader("confs_dev"));

        FigUtils.merge(primary, secondary);

        // Expect the primary root config value "root-2-key" to be updated
        assertEquals("root-2-new-value", primary.find("root").value("root-2-key").as_string());

        // Expect the primary extension config value "root-2-key" to be updated
        assertEquals("root-2-new-value", primary.find("extension-2").value("root-2-key").as_string());

        // Expect the primary extension config value "ex-2-key" to be updated
        assertEquals("ex-2-new-value", primary.find("extension-2").value("ex-2-key").as_string());
    }

    @Test
    public void testExistingConfigsAreUpdatedWithNewProperties() {
        Fig primary = Fig.getInstance(new PropertiesLoader("confs"));
        Fig secondary = Fig.getInstance(new PropertiesLoader("confs_dev"));

        FigUtils.merge(primary, secondary);

        // Expect there to be a new primary root config value "root-3-key"
        Config updatedPrimaryRootConfig = primary.find("root");
        assertEquals("root-3-value", updatedPrimaryRootConfig.value("root-3-key").as_string());

        // Expect there to be an additional primary extension config
        assertEquals("ex-2-new-new-value", primary.find("extension-2").value("ex-2-new-key").as_string());
    }

    @Test
    public void testExistingConfigsAreUpdatedWithNewExtensions() {
        Fig primary = Fig.getInstance(new PropertiesLoader("confs"));
        Fig secondary = Fig.getInstance(new PropertiesLoader("confs_dev"));

        FigUtils.merge(primary, secondary);

        // Expect there to be a new primary extension config
        assertEquals("ex-3-value", primary.find("extension-3").value("ex-3-key").as_string());
    }

    @Test
    public void testNewConfigsCanBeAdded() {
        Fig primary = Fig.getInstance(new PropertiesLoader("confs"));
        Fig secondary = Fig.getInstance(new PropertiesLoader("confs_dev"));

        FigUtils.merge(primary, secondary);

        assertNotNull(primary.get("new_sub"));
        assertEquals("newsub-1-value", primary.find("new_sub").value("newsub-1-key").as_string());
    }

    @Test
    public void testChildConfigPropertyValuesCanBeUpdated() {
        Fig primary = Fig.getInstance(new PropertiesLoader("confs"));
        Fig secondary = Fig.getInstance(new PropertiesLoader("confs_dev"));

        FigUtils.merge(primary, secondary);

        assertEquals("sub-1-new-value", primary.find("group").value("sub-1-key").as_string());
    }

    @Test
    public void testFigRemainsUnchangedWhenMergingNullFig() {
        Fig fig = Fig.getInstance(new PropertiesLoader("confs_dev"));
        assertEquals(5, fig.configs().size());

        FigUtils.merge(null, fig);
        assertEquals(5, fig.configs().size());

        FigUtils.merge(fig, null);
        assertEquals(5, fig.configs().size());
    }

    @Test
    public void testMergingCollectionOfConfigsIntoFig() {
        PropertiesLoader loader = new PropertiesLoader("confs");
        Fig fig = Fig.getInstance(loader);

        String configsToMerge = "people.detail[enabled:true&sub.group.folder-extension[group-folder-extension-key:test";
        List<Config> configs = ConfigUtils.asList(configsToMerge, loader);

        FigUtils.mergeConfigs(fig, configs);

        assertEquals(2, fig.get("people", "detail").values().size());
        assertEquals("true", fig.get("people", "detail").value("enabled").as_string());

        assertEquals(2, fig.get("sub", "group", "folder-extension").values().size());
        assertEquals("test", fig.get("sub", "group", "folder-extension").value("group-folder-extension-key").as_string());
    }
}
