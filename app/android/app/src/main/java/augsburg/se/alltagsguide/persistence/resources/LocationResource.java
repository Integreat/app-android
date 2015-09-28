package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.inject.Inject;

import java.util.List;

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class LocationResource implements PersistableResource<Location> {
    private NetworkService mNetwork;

    @Inject
    public LocationResource(NetworkService network) {
        mNetwork = network;
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_LOCATION);
        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @Override
    public Location loadFrom(Cursor cursor) {
        Location location = new Location();
        location.setName(cursor.getString(0));
        location.setPath(cursor.getString(1));
        location.setDescription(cursor.getString(2));
        location.setColor(cursor.getInt(3));
        return location;
    }

    @Override
    public void store(SQLiteDatabase db, List<Location> locations) {
        if (locations.isEmpty()) {
            return;
        }
        db.delete(CacheHelper.TABLE_LOCATION, null, null);

        ContentValues values = new ContentValues(4);
        for (Location location : locations) {
            values.clear();
            values.put(CacheHelper.LOCATION_NAME, location.getName());
            values.put(CacheHelper.LOCATION_PATH, location.getPath());
            values.put(CacheHelper.LOCATION_URL, location.getUrl());
            values.put(CacheHelper.LOCATION_COLOR, location.getColor());

            db.replace(CacheHelper.TABLE_LOCATION, null, values);
        }
    }

    @Override
    public List<Location> request() {
        return mNetwork.getAvailableLocations();
    }
}
