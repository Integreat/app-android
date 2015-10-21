package augsburg.se.alltagsguide.persistence;

import android.support.annotation.NonNull;

/**
 * Created by Daniel-L on 02.10.2015.
 */
public class DatabaseInfo {

    public DatabaseInfo(@NonNull String name, int version) {
        this.name = name;
        this.version = version;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    @NonNull private final String name;
    private final int version;
}
