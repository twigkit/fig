package twigkit.fig.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Olafur Gauti Gudmundsson
 */
public class ReflectionUtils {

    public static Field[] getDeclaredAndInheritedFields(final Class type, final boolean returnFinalFields) {
        List<Field> allFields = new ArrayList<Field>();
        allFields.addAll(getValidFields(type.getDeclaredFields(), returnFinalFields));

        Class parent = type.getSuperclass();

        while ((parent != null) && (parent != Object.class)) {
            allFields.addAll(getValidFields(parent.getDeclaredFields(), returnFinalFields));
            parent = parent.getSuperclass();
        }

        return allFields.toArray(new Field[allFields.size()]);
    }

    public static List<Field> getValidFields(final Field[] fields, final boolean returnFinalFields) {
        List<Field> validFields = new ArrayList<Field>();

        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())
                    && (returnFinalFields || !Modifier.isFinal(field.getModifiers()))) {
                validFields.add(field);
            }
        }

        return validFields;
    }
}
