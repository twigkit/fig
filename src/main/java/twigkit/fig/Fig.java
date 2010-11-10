package twigkit.fig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author mr.olafsson
 */
public class Fig {

    private Map<String, Config> configs;

    public Fig() {
        configs = new LinkedHashMap<String, Config>();
    }

    public Map<String, Config> configs() {
        return configs;
    }

    public void add(String[] path, Config config) {
        if (path.length == 1) {
            configs.put(config.name(), config);
        } else {
            if (configs().containsKey(path[0])) {
                Config parent = configs.get(path[0]);
                for (int i = 1; i < path.length; i++) {
                    Config c = parent.extension(path[i]);
                    if (c != null) {
                        parent = c;
                    } else {
                        c = new Config(path[i]);
                        parent.extendWith(c);
                    }
                }
                parent.extendWith(config);
            }
        }
    }
}
