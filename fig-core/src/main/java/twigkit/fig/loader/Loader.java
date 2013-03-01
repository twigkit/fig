package twigkit.fig.loader;

import twigkit.fig.Config;
import twigkit.fig.Fig;

import java.io.IOException;

/**
 * @author mr.olafsson
 */
public interface Loader {

    public void load(Fig configs);

    public void write(Config config) throws IOException;

    public void delete(Config config) throws IOException;

}
