package twigkit.fig;

import org.junit.Assert;
import org.junit.Test;
import twigkit.fig.Path;

/**
 * @author mr.olafsson
 */
public class PathTest {

    @Test
    public void singleLevelPathFromString() {
        Path path = new Path("level1");
        Assert.assertEquals(1, path.count());
        Assert.assertEquals("level1", path.get());
    }

    @Test
    public void twoLevelPathFromString() {
        Path path = new Path("level1_level2");
        Assert.assertEquals(2, path.count());
        Assert.assertEquals("level1", path.level(0));
        Assert.assertEquals("level2", path.level(1));
        Assert.assertEquals("level1_level2", path.get());
    }

    @Test
    public void singleLevelPathFromStringArray() {
        Path path = new Path(new String[]{"level1"});
        Assert.assertEquals(1, path.count());
        Assert.assertEquals("level1", path.get());
    }

    @Test
    public void twoLevelPathFromStringArray() {
        Path path = new Path(new String[]{"level1", "level2"});
        Assert.assertEquals(2, path.count());
        Assert.assertEquals("level1", path.level(0));
        Assert.assertEquals("level2", path.level(1));
        Assert.assertEquals("level1_level2", path.get());
    }
}
