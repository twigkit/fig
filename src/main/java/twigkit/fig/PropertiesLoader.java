package twigkit.fig;

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

    private Configurations configs;

    public PropertiesLoader() {
        configs = new Configurations();
        
        try {
            File f = new File(this.getClass().getClassLoader().getResource("confs").toURI());

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
                Properties p = new Properties();
                try {
                    p.load(new FileInputStream(file));
                    Configuration config = new Configuration(file.getName().substring(0, file.getName().lastIndexOf(".")));
                    for (Map.Entry prop : p.entrySet()) {
                        config.values().put(prop.getKey().toString(), new Value<Object>(prop.getKey().toString(), prop.getValue(), false));
                    }
                    configs.add(config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (Configuration config : configs.values()) {
                System.out.println(config.name());
                for (Value value : config.values().values()) {
                    System.out.println("> " + value.name() + " : " + value.get());
                }
            }


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesLoader get() {
        return new PropertiesLoader();
    }
}
