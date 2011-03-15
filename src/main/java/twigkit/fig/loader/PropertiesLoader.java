package twigkit.fig.loader;

import twigkit.fig.Config;
import twigkit.fig.Fig;
import twigkit.fig.Value;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author mr.olafsson
 */
public class PropertiesLoader implements Loader {

	private String path;
	private int parentPathLength;

	public PropertiesLoader(String path) {
		this.path = path;
	}

	public void load(Fig fig) {
		try {
			File f = new File(this.getClass().getClassLoader().getResource(path).toURI());
			parentPathLength = f.getAbsolutePath().length();
			readFolder(fig, f);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	protected void readFolder(Fig fig, File folder) {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File file, String s) {
				return s.endsWith(".conf");
			}
		};

		File[] files = folder.listFiles(filter);
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File file, File file1) {
				return file.getName().compareTo(file1.getName());
			}
		});

		// Files
		if (folder.getAbsolutePath().length() > parentPathLength) {
			// Accounting for trailing slash (maybe should check if it's there first)
			readFiles(fig, folder.getAbsolutePath().substring(parentPathLength + 1).split(File.separator), files);
		} else {
			readFiles(fig, null, files);
		}

		// Nested folders
		FileFilter folderFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		File[] nestedFolders = folder.listFiles(folderFilter);
		for (File nestedfolder : nestedFolders) {
			readFolder(fig, nestedfolder);
		}
	}

	protected void readFiles(Fig fig, String[] parents, File... files) {
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

				if (parents != null && parents.length > 0) {
					levels = combine(parents, levels);
				}

				fig.add(config, levels);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String[] combine(String[] one, String[] other) {
		String[] combined = new String[one.length + other.length];
		System.arraycopy(one, 0, combined, 0, one.length);
		System.arraycopy(other, 0, combined, one.length, other.length);

		return combined;
	}
}
