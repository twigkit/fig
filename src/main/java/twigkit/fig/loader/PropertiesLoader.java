package twigkit.fig.loader;

import twigkit.fig.Config;
import twigkit.fig.Configs;
import twigkit.fig.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author mr.olafsson
 */
public class PropertiesLoader {

    public PropertiesLoader(Configs configs, String path) {
        try {
            File f = new File(this.getClass().getClassLoader().getResource(path).toURI());

            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File file, String s) {
                    return s.endsWith(".conf");
                }
            };

            File[] files = f.listFiles(filter);
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File file, File file1) {
                    return file.getName().compareTo(file1.getName());
                }
            });

            for (File file : files) {
                try {
                    ResourceBundle p = new PropertyResourceBundle(new FileInputStream(file));
                    String[] levels = file.getName().substring(0, file.getName().lastIndexOf(".")).split("_");

                    Config config = new Config(levels[levels.length - 1]);

                    Enumeration<String> keys = p.getKeys();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement();
                        config.set(new Value<Object>(new String(key.getBytes("ISO-8859-1"), "UTF-8"), new String(p.getString(key).getBytes("ISO-8859-1"), "UTF-8"), false));
                    }

                    configs.add(levels, config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
