package twigkit.fig.loader;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.util.FileUtils;
import twigkit.fig.visitor.ConfigTreeWriter;

import java.io.File;

import static org.junit.Assert.*;

public class PropertiesLoaderTest {

    private ClassLoader cl;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private String copyToTempFolder(String node) throws Exception {
        File source = FileUtils.getResourceAsFile(node);

        File destination = new File(tempFolder.getRoot(), source.getName());
        System.out.println("Source is " + source.getAbsolutePath());
        System.out.println("Destination is " + destination.getAbsolutePath());
        org.apache.commons.io.FileUtils.copyDirectoryToDirectory(source, destination.getParentFile());
        System.out.println("Success? " + destination.exists());
        return  FileUtils.FILE_PROTOCOL + source.getAbsolutePath();
    }

    @Before
    public void setUp() throws Exception {
        cl = Thread.currentThread().getContextClassLoader();
        tempFolder.create();
        tempFolder.getRoot().mkdirs();
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

        String figPath = copyToTempFolder("writables");
        Loader loader = new PropertiesLoader(figPath);
        Fig fig = Fig.getInstance(loader);

        Config conf = fig.get("files", "writable", "override");
        assertNotNull(conf);
        assertEquals(2, conf.values().size());

        conf.set("new", "new-value");
        loader.write(conf);

        // Reloading the configuration
        fig = Fig.getInstance(new PropertiesLoader(figPath));
        Config changed = fig.get("files", "writable", "override");
        assertEquals(3, changed.values().size());

        Config parent = changed.parent();
        parent.set("new-value", "in-parent");
        loader.write(parent);

        fig = Fig.getInstance(new PropertiesLoader(figPath));
        changed = fig.get("files", "writable", "override");
        assertEquals(4, changed.values().size());

        Config newExistingFolder = new Config("more-override");
        newExistingFolder.set("new-file", "new-value");
        changed.extend_with(newExistingFolder);
        loader.write(newExistingFolder);
        assertTrue(getFile(figPath, "files", "writable.override.more-override.conf").exists());
        assertTrue(getFile(figPath, "files", "writable.override.more-override.conf").isFile());
    }

    @Test
    public void testDelete() throws Exception {
        String figPath = copyToTempFolder("confs");
        System.out.println(figPath);
        PropertiesLoader loader = new PropertiesLoader(figPath);
        Fig fig = Fig.getInstance(loader);
        System.out.println(fig.configs().size());
        for (Config config: fig.configs()) {
            System.out.println("\t" + config.name());
        }
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
        String figPath = copyToTempFolder("writables");
        Loader loader = new PropertiesLoader(figPath);
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
        String figPath = copyToTempFolder("writables");
        Fig fig = Fig.getInstance(new PropertiesLoader(figPath));
        Config parent = fig.get("files", "writable");

        Config newFolder = new Config("i-do-not-exist");
        newFolder.set("new-folder", "with-a-value");
        parent.parent().extend_with(newFolder);
        newFolder.save();

        // Since we saved the parent configuration a file is created (because it has no extensions yet)
        assertTrue(getFile(figPath, "files", "i-do-not-exist.conf").exists());
        assertTrue(getFile(figPath, "files", "i-do-not-exist.conf").isFile());

        Config newFile = new Config("i-also-do-not-exist");
        newFile.set("new-file-in-new", "folder");
        newFolder.extend_with(newFile);
        newFile.save();

        // The parent exists as a file so this will be a sibling file override (with LEVEL_SEPARATOR)
        assertTrue(getFile(figPath, "files", "i-do-not-exist.i-also-do-not-exist.conf").exists());
        assertTrue(getFile(figPath, "files", "i-do-not-exist.i-also-do-not-exist.conf").isFile());
    }

    @Test
    public void testNewConfigurationFileInNewDirectory() throws Exception {
        String figPath = copyToTempFolder("writables");
        Fig fig = Fig.getInstance(new PropertiesLoader(figPath));
        Config parent = fig.get("files", "writable");

        // If no sibling config file exists to override and an extension is made then create directory with config file
        Config newFolderNoFile = new Config("i-will-not-exist");
        parent.parent().extend_with(newFolderNoFile);

        Config newFileInFolder = new Config("i-also-do-not-exist");
        newFileInFolder.set("new-file-in-", "folder-with-no-config");
        newFolderNoFile.extend_with(newFileInFolder);
        newFileInFolder.save();

        // The parent was not saved when this one was created so a directory hierarchy is created
        assertTrue(getFile(figPath, "files", "i-will-not-exist").exists());
        assertTrue(getFile(figPath, "files", "i-will-not-exist").isDirectory());

        assertTrue(getFile(figPath, "files", "i-will-not-exist", "i-also-do-not-exist.conf").exists());
        assertTrue(getFile(figPath, "files", "i-will-not-exist", "i-also-do-not-exist.conf").isFile());

    }

    private File getFile(String... path) {
        StringBuilder url = new StringBuilder();
        for (String s : path) {
            if (url.length() > 0) {
                url.append(File.separator);
            }
            url.append(s);
        }
        return FileUtils.getResourceAsFile(url.toString());
    }

    @Test
    public void testRootWithConfig() {
        PropertiesLoader loader = new PropertiesLoader("root-with-config");
        Fig fig = Fig.getInstance(loader);
        Assert.assertEquals(1, fig.configs().size());
        Config rootConfig = fig.get("root-with-config");
        Assert.assertNotNull(rootConfig);
        Assert.assertNull(rootConfig.parent());
        Assert.assertEquals(1, rootConfig.extensions().size());
        for (Config c: rootConfig.extensions()) {
            System.out.println(c.name());
        }
        Assert.assertNotNull(rootConfig.extension("child"));
        Config childConfig = fig.find("child");
        Assert.assertNotNull(childConfig);
        Assert.assertEquals(rootConfig, childConfig.parent());
    }

    @Test
    public void testMultipleLoaders() {
        PropertiesLoader loader1 = new PropertiesLoader("first");
        PropertiesLoader loader2 = new PropertiesLoader("second");
        Fig fig = Fig.getInstance(loader1, loader2);
        Assert.assertEquals(1, fig.configs().size());
        Config conf = fig.get("root", "config");
        Assert.assertEquals(1, conf.values().size()); // second config overwrites the first one
        Assert.assertEquals("different", conf.value("foo").as_string());

    }

    @Test
    public void testRootWithoutConfig() {
        PropertiesLoader loader = new PropertiesLoader("root-without-config");
        Fig fig = Fig.getInstance(loader);
        Assert.assertEquals(1, fig.configs().size());
        Config rootConfig = fig.get("root-without-config");
        Assert.assertNull(rootConfig);
        Config childConfig = fig.find("child");
        Assert.assertNotNull(childConfig);
        Assert.assertNull(childConfig.parent());
    }

    /**
     * Previously, if a configuration contained an extension that alphabetically appeared before the name of the
     * parent config, for example, parent.child.conf compared to parent.conf, parent.conf would appear after
     * parent.child.conf and the latter would never be loaded. This test checks that the configurations are loaded
     * by order of extension and not alphabetically.
     */
    @Test
    public void testConfigsLoadByOrderOfExtension() {
        Fig fig = Fig.getInstance(new PropertiesLoader("order"));
        Assert.assertEquals(1, fig.configs().size());

        Config parentConfig = fig.find("parent");
        Assert.assertNotNull(parentConfig.parent());

        // If the child configs were not loaded this next assertion would fail.
        Assert.assertEquals(2, parentConfig.extensions().size());

        Assert.assertNotNull(parentConfig.extension("child-1"));
        Config childConfig = fig.find("child-1");
        Assert.assertNotNull(childConfig);

        Assert.assertNotNull(parentConfig.extension("child-1").extension("sibling-1"));
        Config siblingConfig = fig.find("sibling-1");
        Assert.assertNotNull(siblingConfig);

        new ConfigTreeWriter(fig.get("order"));
    }
}