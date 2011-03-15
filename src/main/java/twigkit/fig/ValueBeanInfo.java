package twigkit.fig;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * @author mr.olafsson
 */
public class ValueBeanInfo extends SimpleBeanInfo {

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor[] desc = new PropertyDescriptor[11];
		try {
			desc[0] = new PropertyDescriptor("label", Value.class, "label", "label");
			desc[1] = new PropertyDescriptor("value", Value.class, "get", "set");
			desc[2] = new PropertyDescriptor("string", Value.class, "as_string", null);
			desc[3] = new PropertyDescriptor("int", Value.class, "as_int", null);
			desc[4] = new PropertyDescriptor("long", Value.class, "as_long", null);
			desc[5] = new PropertyDescriptor("float", Value.class, "as_float", null);
			desc[6] = new PropertyDescriptor("double", Value.class, "as_double", null);
			desc[7] = new PropertyDescriptor("boolean", Value.class, "as_boolean", null);
			desc[8] = new PropertyDescriptor("required", Value.class, "required", "required");
			desc[9] = new PropertyDescriptor("require", Value.class, "require", null); // Is a setter for required
			desc[10] = new PropertyDescriptor("class", Value.class, "getClass", null);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		return desc;
	}
}
