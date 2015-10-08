package augsburg.se.alltagsguide.persistence;

/**
 * Created by Daniel-L on 02.10.2015.
 */
public class DatabaseInfo {

    public DatabaseInfo(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    private final String name;
    private final int version;
}
