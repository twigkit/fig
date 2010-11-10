package twigkit.fig.visitor;

import twigkit.fig.Config;
import twigkit.fig.Value;

/**
 * @author mr.olafsson
 */
public interface ConfigVisitor {

    public void visit(Config config);

    public void value(Value value);

    public void extension(Config extension);

}
