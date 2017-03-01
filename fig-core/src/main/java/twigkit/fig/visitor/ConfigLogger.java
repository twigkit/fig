package twigkit.fig.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twigkit.fig.Config;
import twigkit.fig.Value;

/**
 * @author mr.olafsson
 */
public class ConfigLogger implements ConfigVisitor {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLogger.class);

    public enum PrivateValues {
        PASSWORD("password");

        private String privateValue;

        PrivateValues(String privateValue) {
            this.privateValue = privateValue;
        }

        @Override
        public String toString() {
            return privateValue;
        }
    }

    private Config config;
    private int initialLevel;

    public ConfigLogger(Config config) {
        logger.info(config.name().toUpperCase());
        initialLevel = config.parents().size();
        config.accept(this);
    }

    public void visit(Config config) {
        this.config = config;
    }

    public void value(Value value) {
        String valueStr = value.get().toString();
        if (isValuePrivate(value.label())) {
            valueStr = "*************";
        }

        logger.info( pad() + "  |-- " + value.label() + ( value.as_string().length()> 0 ? " = " + valueStr : "") );
    }

    public void extension(Config extension) {
        logger.info( pad(extension.parents().size() - 1) + "  |" );
        logger.info( pad(extension.parents().size() - 1) + "  +-- " + extension.name().toUpperCase() );
    }

    private String pad() {
        return pad(config.parents().size());
    }

    private String pad(int level) {
        StringBuilder indent = new StringBuilder();

        for (int i = 0; i < level - initialLevel; i++) indent.append("      ");

        return indent.toString();
    }

    private boolean isValuePrivate(String value) {
        for (PrivateValues privateValue : PrivateValues.values()) {
            if (privateValue.toString().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
