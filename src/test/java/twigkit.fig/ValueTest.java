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

    @Test
    public void testEquality() {
        Value v1 = new Value().name("attr1").set("myValue");
        Value v2 = new Value().name("attr1").set("myValue");
        Value v3 = new Value().name("attr1").set("different");

        Assert.assertEquals(v1, v2);
        Assert.assertFalse(v1.equals(v3));

        Assert.assertEquals(v1.hashCode(), v2.hashCode());
        Assert.assertFalse(v1.hashCode() == v3.hashCode());
    }
}
