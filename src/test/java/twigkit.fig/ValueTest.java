package twigkit.fig;

import org.junit.Assert;
import org.junit.Test;
import twigkit.fig.Value;

/**
 * @author mr.olafsson
 */
public class ValueTest {

    @Test
    public void createValue() {
        Value value = new Value();
        Assert.assertEquals("", value.name());
        Assert.assertNull(value.get());
        Assert.assertFalse(value.required());
    }

    @Test
    public void fluentCreation() {
        Value value = new Value().name("attr1").set("myValue");
        Assert.assertEquals("attr1", value.name());
        Assert.assertEquals("myValue", value.get());
        Assert.assertFalse(value.required());

        value.require();
        Assert.assertTrue(value.required());
    }
}
