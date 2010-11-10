package twigkit.fig.visitor;

import twigkit.fig.Config;
import twigkit.fig.Value;

/**
 * @author mr.olafsson
 */
public class ConfigTreeWriter implements ConfigVisitor {

    private Config config;
    private int initialLevel;

    public ConfigTreeWriter(Config config) {
        System.out.println(config.name().toUpperCase());
        initialLevel = config.parents().size();
        config.accept(this);
    }

    public void visit(Config config) {
        this.config = config;
    }

    public void value(Value value) {
        System.out.println( pad() + "  ¦-- " + value.name() + " = " + value.get() );
    }

    public void extension(Config extension) {
        System.out.println( pad(extension.parents().size() - 1) + "  ¦" );
        System.out.println( pad(extension.parents().size() - 1) + "  +-- " + extension.name().toUpperCase() );
    }

    private String pad() {
        return pad(config.parents().size());
    }

    private String pad(int level) {
        StringBuilder indent = new StringBuilder();

        for (int i = 0; i < level - initialLevel; i++) indent.append("      ");

        return indent.toString();
    }
}
