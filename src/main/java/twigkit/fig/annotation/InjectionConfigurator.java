package twigkit.fig.annotation;

import twigkit.fig.Config;
import twigkit.fig.Configurator;
import twigkit.fig.Fig;
import twigkit.fig.Value;
import twigkit.fig.util.ReflectionUtils;
import twigkit.fig.visitor.ConfigTreeWriter;

import java.lang.reflect.Field;

/**
 * @author mr.olafsson
 */
public class InjectionConfigurator implements Configurator<Object> {

    private Fig fig;
    private Config config;

    public InjectionConfigurator(Fig fig) {
        this.fig = fig;
    }

    public InjectionConfigurator(Config config) {
        this.config = config;
    }

    public Object configure(Object target) {
        inject(target);

        return target;
    }

    private void inject(Object target) {
        Field[] fields = ReflectionUtils.getDeclaredAndInheritedFields(target.getClass(), false);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Configure.class)) {
                Configure annotation = field.getAnnotation(Configure.class);

                field.setAccessible(true);

                if (config == null && fig != null && !annotation.with().equals("")) {
                    config = fig.get(annotation.with().split(Configure.SEPARATOR));
                }

                try {
                    field.set(target, config);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (field.isAnnotationPresent(Configure.Value.class)) {
                Configure.Value annotation = field.getAnnotation(Configure.Value.class);

                String name = annotation.name();
                if (name == null || name.equals("")) {
                    name = field.getName();
                }

                Value value = config.value(name);
                if (value != null) {
                    field.setAccessible(true);
                    try {
                        field.set(target, value.get());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
