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

    @Test
    public void testWrite() throws Exception {
        Loader loader = new PropertiesLoader("writables");
        Fig fig = Fig.load(loader);
        Config conf = fig.get("files", "writable", "override");

        assertNotNull(conf);
        assertEquals(2, conf.values().size());

        new ConfigTreeWriter(conf);

        conf.set("new", "new-value");

        loader.write(conf);

        fig = Fig.load(new PropertiesLoader("writables"));
        Config changed = fig.get("files", "writable", "override");
        assertEquals(3, changed.values().size());

        new ConfigTreeWriter(changed);

        Config parent = changed.parent();
        parent.set("new-value", "in-parent");
        loader.write(parent);

        fig = Fig.load(new PropertiesLoader("writables"));
        changed = fig.get("files", "writable", "override");
        assertEquals(4, changed.values().size());

        new ConfigTreeWriter(changed);

        Config newExistingFolder = new Config("more-override");
        newExistingFolder.set("new-file", "new-value");
        changed.extend_with(newExistingFolder);
        loader.write(newExistingFolder);

        new ConfigTreeWriter(changed);

        Config newFolder = new Config("i-do-not-exist");
        newFolder.set("new-folder", "with-a-value");
        parent.parent().extend_with(newFolder);
        loader.write(newFolder);

        Config newFile = new Config("i-also-do-not-exist");
        newFile.set("new-file-in-new", "folder");
        newFolder.extend_with(newFile);
//        loader.write(newFile);
        newFile.save();

        new ConfigTreeWriter(newFile);

        Config newFolderNoFile = new Config("i-will-not-exist");
        parent.parent().extend_with(newFolderNoFile);

        Config newFileInFolder = new Config("i-also-do-not-exist");
        newFileInFolder.set("new-file-in-", "folder-with-no-config");
        newFolderNoFile.extend_with(newFileInFolder);
//        loader.write(newFileInFolder);
        newFileInFolder.save();

        new ConfigTreeWriter(newFile);

        System.out.println("FINAL STRUCTURE");

        fig = Fig.load(new PropertiesLoader("writables"));
        new ConfigTreeWriter(fig.get("files"));
    }
}
