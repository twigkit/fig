package twigkit.fig.util;

import org.junit.After;
import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.loader.PropertiesLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author scottbrown
 */
public class FigUtilsTest {

    /**
     * {@link Fig#getInstance(twigkit.fig.loader.Loader...)} returns a process-wide singleton
     * keyed on the loader(s) used. {@link FigUtils#merge(Fig, Fig)} mutates its first
     * argument in place, so merging into the singleton for "confs" here would otherwise
     * permanently leave that shared instance with merged-in data for the rest of the test
     * run, corrupting unrelated tests (e.g. in {@code MergedPropertiesLoaderTest}) that
     * expect to see the pristine "confs" configuration. Reloading after each test restores
     * the singleton to its original, unmerged state.
     */
    private Fig primary;

    @After
    public void restoreSharedPrimaryFig() {
        if (primary != null) {
            primary.reload();
        }
    }

    @Test
    public void testExistingConfigPropertiesAreLeftUnchanged() {
        primary = Fig.getInstance(new PropertiesLoader("confs"));
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
        primary = Fig.getInstance(new PropertiesLoader("confs"));
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
        primary = Fig.getInstance(new PropertiesLoader("confs"));
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
        primary = Fig.getInstance(new PropertiesLoader("confs"));
        Fig secondary = Fig.getInstance(new PropertiesLoader("confs_dev"));

        FigUtils.merge(primary, secondary);

        // Expect there to be a new primary extension config
        assertEquals("ex-3-value", primary.find("extension-3").value("ex-3-key").as_string());
    }

    @Test
    public void testNewConfigsCanBeAdded() {
        primary = Fig.getInstance(new PropertiesLoader("confs"));
        Fig secondary = Fig.getInstance(new PropertiesLoader("confs_dev"));

        FigUtils.merge(primary, secondary);

        assertNotNull(primary.get("new_sub"));
        assertEquals("newsub-1-value", primary.find("new_sub").value("newsub-1-key").as_string());
    }

    @Test
    public void testChildConfigPropertyValuesCanBeUpdated() {
        primary = Fig.getInstance(new PropertiesLoader("confs"));
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
}
