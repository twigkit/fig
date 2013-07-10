package twigkit.fig.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Config loader utilities.
 *
 * @author bjarkih
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /** Protocol prefic to indicate a file resource. */
    public static final String FILE_PROTOCOL = "file://";

    private static final int FILE_PROTOCOL_LENGTH = FILE_PROTOCOL.length();


    /**
     * Tries to load the resource with the given name.
     *
     * @param name name of a resource.
     * @return the URL of a matching resource.
     */
    public static File getResourceAsFile(String name) {
        if (name.startsWith(FILE_PROTOCOL)) {
            return new File(name.substring(FILE_PROTOCOL_LENGTH));
        } else {
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                URL url = loader.getResource(name);
                if (url == null) {
                    url = loader.getResource("/" + name);
                }
                return (url != null) ? new File(url.toURI()) : null;
            } catch (URISyntaxException e) {
                logger.error("Failed to load File resource for {}", name, e);
                return null;
            }
        }
    }
}
