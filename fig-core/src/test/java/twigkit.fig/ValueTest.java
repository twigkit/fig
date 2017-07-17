package twigkit.fig;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author mr.olafsson
 */
public class ValueTest {

    @Test
    public void createValue() {
        Value value = new Value();
        Assert.assertEquals("", value.label());
        Assert.assertNull(value.get());
        Assert.assertFalse(value.required());
    }

    @Test
    public void fluentCreation() {
        Value value = new Value().label("attr1").set("myValue");
        Assert.assertEquals("attr1", value.label());
        Assert.assertEquals("myValue", value.get());
        Assert.assertFalse(value.required());

        value.require();
        Assert.assertTrue(value.required());
    }

    @Test
    public void testTypes() {
        Value v = new Value("label", "100");
        Assert.assertEquals("100", v.as_string());
        Assert.assertEquals("100", v.toString());
        Assert.assertEquals(100, v.as_int());
        
        v.set(100);
        Assert.assertEquals(100, v.as_int());
	    Assert.assertEquals(100l, v.as_long());
        Assert.assertEquals("100", v.as_string());

        v.set(true);
        Assert.assertEquals(true, v.as_boolean());
        Assert.assertEquals("true", v.as_string());

        v.set("true");
        Assert.assertEquals(true, v.as_boolean());
        Assert.assertEquals("true", v.as_string());

	    v.set(100.12);
        Assert.assertEquals(100.12f, v.as_float(), 2);
	    Assert.assertEquals(100.12d, v.as_double(), 2);
        Assert.assertEquals("100.12", v.as_string());

    }
    
    @Test
    public void testEquality() {
        Value v1 = new Value().label("attr1").set("myValue");
        Value v2 = new Value().label("attr1").set("myValue");
        Value v3 = new Value().label("attr1").set("different");

        Assert.assertEquals(v1, v2);
        Assert.assertFalse(v1.equals(v3));

        Assert.assertEquals(v1.hashCode(), v2.hashCode());
        Assert.assertFalse(v1.hashCode() == v3.hashCode());
    }

    @Test
    public void testEmptyValues() {
        Value v1 = new Value().label("attr1").set("");

        Assert.assertEquals("", v1.as_string());
        Assert.assertEquals(0L, v1.as_long());
        Assert.assertEquals(0, v1.as_int());
        Assert.assertEquals(0d, v1.as_double(), 0.00001);
        Assert.assertEquals(0f, v1.as_float(), 0.00001);
        Assert.assertFalse(v1.as_boolean());
    }

    @Test
    public void testValueWithWhiteSpaceIsTrimmed() {
        Value v1 = new Value().label("attr1").set("   myValue");
        Assert.assertTrue(v1.as_string().equals("myValue"));

        Value v2 = new Value().label("attr2").set("myValue    ");
        Assert.assertTrue(v2.as_string().equals("myValue"));
    }
}
