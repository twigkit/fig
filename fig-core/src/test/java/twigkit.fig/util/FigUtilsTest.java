package twigkit.fig.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.loader.PropertiesLoader;
import twigkit.fig.visitor.ConfigTreeWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author scottbrown
 */
public class FigUtilsTest {

    private Fig primary;
    private Fig secondary;

    @Before
    public void loadFigs() {
        primary = Fig.getInstance(new PropertiesLoader("confs"));
        secondary = Fig.getInstance(new PropertiesLoader("confs_dev"));
    }

    @After
    public void disposeFigs() {
        primary = null;
        secondary = null;
    }

    @Test
    public void testOriginalStates() {
        // Primary root fig values
        Config primaryRootConfig = primary.find("root");
        assertEquals(2, primaryRootConfig.values().size());
        assertEquals("root-1-value", primaryRootConfig.value("root-1-key").as_string());
        assertEquals("root-2-value", primaryRootConfig.value("root-2-key").as_string());

        // Secondary root config values
        Config secondaryRootConfig = secondary.find("root");
        assertEquals(3, secondaryRootConfig.values().size());
        assertEquals(2, secondaryRootConfig.extensions().size());

        // Secondary extension config values for "extension-2"
        Config secondaryExtensionConfig = secondary.find("extension-2");
        assertEquals(5, secondaryExtensionConfig.values().size());
        assertEquals("ex-2-new-value", secondaryExtensionConfig.value("ex-2-key").as_string());
        assertEquals("ex-2-new-new-value", secondaryExtensionConfig.value("ex-2-new-key").as_string());

        // Secondary extension config values for "extension-3"
        secondaryExtensionConfig = secondary.find("extension-3");
        assertEquals(4, secondaryExtensionConfig.values().size());
        assertEquals("ex-3-value", secondaryExtensionConfig.value("ex-3-key").as_string());
    }

    @Test
    public void testExistingConfigPropertiesAreLeftUnchanged() {
        String originalRoot1KeyValue = primary.find("root").value("root-1-key").as_string();
        String originalExtension2Value = primary.find("extension-2").value("root-1-key").as_string();

        FigUtils.mergeFig(primary, secondary);

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
        FigUtils.mergeFig(primary, secondary);

        // Expect the primary root config value "root-2-key" to be updated
        assertEquals("root-2-new-value", primary.find("root").value("root-2-key").as_string());

        // Expect the primary extension config value "root-2-key" to be updated
        assertEquals("root-2-new-value", primary.find("extension-2").value("root-2-key").as_string());

        // Expect the primary extension config value "ex-2-key" to be updated
        assertEquals("ex-2-new-value", primary.find("extension-2").value("ex-2-key").as_string());
    }

    @Test
    public void testExistingConfigsAreUpdatedWithNewProperties() {
        FigUtils.mergeFig(primary, secondary);

        // Expect there to be a new primary root config value "root-3-key"
        Config updatedPrimaryRootConfig = primary.find("root");
        assertEquals("root-3-value", updatedPrimaryRootConfig.value("root-3-key").as_string());

        // Expect there to be an additional primary extension config
        assertEquals("ex-2-new-new-value", primary.find("extension-2").value("ex-2-new-key").as_string());
    }

    @Test
    public void testExistingConfigsAreUpdatedWithNewExtensions() {
        FigUtils.mergeFig(primary, secondary);

        for (Config config : primary.configs()) {
            new ConfigTreeWriter(config);
        }

        // Expect there to be a new primary extension config
        assertEquals("ex-3-value", primary.find("extension-3").value("ex-3-key").as_string());
    }

    @Test
    public void testNewConfigsCanBeAdded() {
        FigUtils.mergeFig(primary, secondary);

        assertNotNull(primary.get("new_sub"));
        assertEquals("newsub-1-value", primary.find("new_sub").value("newsub-1-key").as_string());
    }

    @Test
    public void testChildConfigPropertyValuesCanBeUpdated() {
        FigUtils.mergeFig(primary, secondary);

        assertEquals("sub-1-new-value", primary.find("group").value("sub-1-key").as_string());
    }
}
