package twigkit.fig.loader;

import org.junit.Before;
import org.junit.Test;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.visitor.ConfigTreeWriter;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author mr.olafsson
 */
public class PropertiesLoaderTest {

    private ClassLoader cl;

    @Before
    public void setUp() throws Exception {
        cl = Thread.currentThread().getContextClassLoader();
    }

    @Test
    public void testLoadingFolderWithoutParentConf() throws Exception {
        Fig fig = Fig.getInstance(new PropertiesLoader("folders"));

        Config conf = fig.get("folder", "sub-folder", "nested-in-subfolder-without-parent", "override");
        assertNotNull(conf);
        assertEquals("sub-value", conf.value("sub-property").as_string());
        new ConfigTreeWriter(fig.get("folder"));
    }

    @Test
    public void testWrite() throws Exception {
        Loader loader = new PropertiesLoader("writables");
        Fig fig = Fig.getInstance(loader);
        Config conf = fig.get("files", "writable", "override");

        assertNotNull(conf);
        assertEquals(2, conf.values().size());

        conf.set("new", "new-value");
        loader.write(conf);

        // Reloading the configuration
        fig = Fig.getInstance(new PropertiesLoader("writables"));
        Config changed = fig.get("files", "writable", "override");
        assertEquals(3, changed.values().size());

        Config parent = changed.parent();
        parent.set("new-value", "in-parent");
        loader.write(parent);

        fig = Fig.getInstance(new PropertiesLoader("writables"));
        changed = fig.get("files", "writable", "override");
        assertEquals(4, changed.values().size());

        Config newExistingFolder = new Config("more-override");
        newExistingFolder.set("new-file", "new-value");
        changed.extend_with(newExistingFolder);
        loader.write(newExistingFolder);
        assertTrue(getFile("writables", "files", "writable_override_more-override.conf").exists());
        assertTrue(getFile("writables", "files", "writable_override_more-override.conf").isFile());
    }

    @Test
    public void testDelete() throws Exception {
        PropertiesLoader loader = new PropertiesLoader("confs");
        Fig fig = Fig.getInstance(loader);
        Config root = fig.get("delete");

        Config a = new Config("a");
        root.extend_with(a);
        a.save();
        Config b = new Config("b");
        b.set("major", "domo");
        a.extend_with(b);
        b.save();
        Config c = new Config("c");
        b.extend_with(c);
        c.set("foo", "bar");
        c.save();

        new ConfigTreeWriter(root);

        c.delete();
        assertEquals(b.extensions().size(), 0);

        a.delete();
        assertNull(fig.get("delete", "a"));
    }

    @Test
    public void testMove() throws Exception {
        Loader loader = new PropertiesLoader("writables");
        Fig fig = Fig.getInstance(loader);
        Config root = fig.get("files");

        Config a = new Config("a");
        root.extend_with(a);
        Config b = new Config("b");
        a.extend_with(b);
        Config c = new Config("c");
        b.extend_with(c);
        c.set("foo", "bar");
        loader.write(c);

        assertEquals(a, fig.get("files", "a"));
        assertEquals(a.extensions().size(), 1);
        assertEquals(b.extensions().size(), 1);
        assertEquals(c.extensions().size(), 0);

        Config newParent = new Config("new-parent");
        root.extend_with(newParent);
        a.move(newParent);

        new ConfigTreeWriter(root);
    }

    @Test
    public void testNewSiblingConfiguration() throws Exception {
        Fig fig = Fig.getInstance(new PropertiesLoader("writables"));
        Config parent = fig.get("files", "writable");

        Config newFolder = new Config("i-do-not-exist");
        newFolder.set("new-folder", "with-a-value");
        parent.parent().extend_with(newFolder);
        newFolder.save();

        // Since we saved the parent configuration a file is created (because it has no extensions yet)
        assertTrue(getFile("writables", "files", "i-do-not-exist.conf").exists());
        assertTrue(getFile("writables", "files", "i-do-not-exist.conf").isFile());

        Config newFile = new Config("i-also-do-not-exist");
        newFile.set("new-file-in-new", "folder");
        newFolder.extend_with(newFile);
        newFile.save();

        // The parent exists as a file so this will be a sibling file override (with LEVEL_SEPARATOR)
        assertTrue(getFile("writables", "files", "i-do-not-exist_i-also-do-not-exist.conf").exists());
        assertTrue(getFile("writables", "files", "i-do-not-exist_i-also-do-not-exist.conf").isFile());
    }

    @Test
    public void testNewConfigurationFileInNewDirectory() throws Exception {
        Fig fig = Fig.getInstance(new PropertiesLoader("writables"));
        Config parent = fig.get("files", "writable");

        // If no sibling config file exists to override and an extension is made then create directory with config file
        Config newFolderNoFile = new Config("i-will-not-exist");
        parent.parent().extend_with(newFolderNoFile);

        Config newFileInFolder = new Config("i-also-do-not-exist");
        newFileInFolder.set("new-file-in-", "folder-with-no-config");
        newFolderNoFile.extend_with(newFileInFolder);
        newFileInFolder.save();

        // The parent was not saved when this one was created so a directory hierarchy is created
        assertTrue(getFile("writables", "files", "i-will-not-exist").exists());
        assertTrue(getFile("writables", "files", "i-will-not-exist").isDirectory());

        assertTrue(getFile("writables", "files", "i-will-not-exist", "i-also-do-not-exist.conf").exists());
        assertTrue(getFile("writables", "files", "i-will-not-exist", "i-also-do-not-exist.conf").isFile());

    }

    @Test
    public void testLoadRewrittenConfiguration() throws Exception {
        Fig fig = Fig.getInstance(new PropertiesLoader("writables"));
        new ConfigTreeWriter(fig.get("files"));
    }

    @Test
    public void testReadFromFile() throws Exception {
        File folder = getFile("writables");
        assertNotNull(folder);
        String path = "file://" + folder;
        Fig fig = Fig.getInstance(new PropertiesLoader(path));
        new ConfigTreeWriter(fig.get("files", "writable"));
        assertEquals("in-parent", fig.get("files", "writable").value("new-value").as_string());
    }

    private File getFile(String... path) {
        StringBuilder url = new StringBuilder();
        for (String s : path) {
            if (url.length() > 0) {
                url.append(File.separator);
            }
            url.append(s);
        }
        return new File(cl.getResource(url.toString()).getFile());
    }
}
