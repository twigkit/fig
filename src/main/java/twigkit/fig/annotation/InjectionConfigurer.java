package twigkit.fig.annotation;

import twigkit.fig.Config;
import twigkit.fig.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author mr.olafsson
 */
public class InjectionConfigurer {

    private Config config;

    public InjectionConfigurer(Config config) {
        this.config = config;
    }

    public InjectionConfigurer configure(Object target) {
        inject(target);
        return this;
    }

    private void inject(Object target) {
        Field[] fields = ReflectionUtils.getDeclaredAndInheritedFields(target.getClass(), false);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Configure.Value.class)) {
                Configure.Value annotation = field.getAnnotation(Configure.Value.class);
                String name = annotation.name();
                if (name == null || name.equals("")) {
                    name = field.getName();
                }

                try {
                    field.setAccessible(true);
                    field.set(target, config.value(name).get());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
