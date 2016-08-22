package twigkit.fig.util;

import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.loader.PropertiesLoader;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author scottbrown
 */
public class ConfigUtilsTest {

    @Test
    public void testConvertingSingleConfigStringToConfigList() {
        String configsToMerge = "companies[host:test";
        List<Config> configs = ConfigUtils.asList(configsToMerge, new PropertiesLoader("conf"));

        assertEquals(1, configs.size());
        assertEquals("companies", configs.get(0).name());
        assertEquals("test", configs.get(0).value("host").as_string());
    }

    @Test
    public void testConvertingMultipleConfigStringToConfigList() {
        String configsToMerge = "people.detail[enabled:true&sub.group.folder-extension[group-folder-extension-key:test";
        List<Config> configs = ConfigUtils.asList(configsToMerge, new PropertiesLoader("conf"));

        assertEquals(2, configs.size());
        assertEquals("people", configs.get(0).name());
        assertEquals("true", configs.get(0).extension("detail").value("enabled").as_string());
        assertEquals("sub", configs.get(1).name());
        assertEquals("test", configs.get(1).extension("group").extension("folder-extension").value("group-folder-extension-key").as_string());
    }
}
