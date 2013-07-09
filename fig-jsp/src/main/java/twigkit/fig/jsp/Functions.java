package twigkit.fig.jsp;

import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.Value;
import twigkit.fig.loader.PropertiesLoader;

import java.util.Collection;
import java.util.Map;

/**
 * @author mr.olafsson
 */
public class Functions {

	public static Fig load(String config) {
		return Fig.getInstance(new PropertiesLoader(config));
	}

	public static Config get(Fig fig, String conf) {
		return fig.get(conf.split("\\."));
	}

	public static Value value(Config conf, String name) {
		return conf.value(name);
	}

    public static Collection<Config> extensions(Config conf) {
        return conf.extensions();
    }
}
