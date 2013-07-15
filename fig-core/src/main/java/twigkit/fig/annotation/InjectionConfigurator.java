package twigkit.fig.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(InjectionConfigurator.class);

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
            System.out.println("Field: " + field.getName());
            if (field.isAnnotationPresent(Configure.class)) {
                Configure annotation = field.getAnnotation(Configure.class);

                field.setAccessible(true);

                if (fig != null && !annotation.with().equals("")) {
                    config = fig.get(annotation.with().split(Configure.SEPARATOR));
                }

                try {
                    field.set(target, config);
                } catch (IllegalAccessException e) {
                    logger.error("Failed to configure field", e);
                }
            } else if (field.isAnnotationPresent(Configure.Value.class)) {
                Configure.Value annotation = field.getAnnotation(Configure.Value.class);

                String name = annotation.name();
                if (name == null || name.equals("")) {
                    name = field.getName();
                }

                if (fig != null && !annotation.with().equals("")) {
                    System.out.println("with() = " + annotation.with());
                    config = fig.get(annotation.with().split(Configure.SEPARATOR));
                }

                System.out.println("name = " + name);
                Value value = config.value(name);
                if (value.get() != null) {
                    field.setAccessible(true);
                    try {
                        field.set(target, value.get());
                    } catch (IllegalAccessException e) {
                        logger.error("Failed to configure value", e);
                    }
                }
            }
        }
    }
}
