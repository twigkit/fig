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

    public PropertiesLoader(Fig fig) {
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
                    String[] path = file.getName().substring(0, file.getName().lastIndexOf(".")).split("_");

                    Config config = new Config(path[path.length - 1]);
                    for (Map.Entry prop : p.entrySet()) {
                        config.set(new Value<Object>(prop.getKey().toString(), prop.getValue(), false));
                    }

                    fig.add(path, config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
