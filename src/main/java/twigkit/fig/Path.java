package twigkit.fig;

/**
 * @author mr.olafsson
 */
public class Path {

    private static final String HIERARCHICAL_SEPARATOR = "_";
    private String path;
    private String[] levels;

    public Path(String path) {
        this.path = path;
        this.levels = path.split(HIERARCHICAL_SEPARATOR);
    }

    public Path(String[] levels) {
        StringBuilder buf = new StringBuilder();
        for (String level : levels) {
            if (buf.length() > 0) {
                buf.append(HIERARCHICAL_SEPARATOR);
            }
            buf.append(level);
        }
        this.path = buf.toString();
        this.levels = levels;
    }

    public String get() {
        return path;
    }
    public String[] levels() {
        return levels;
    }
    
    public String level(int level) {
        return levels[level];
    }

    public int count() {
        return levels.length;
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path that = (Path) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
