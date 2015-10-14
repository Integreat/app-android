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
public class LocationResource implements PersistableNetworkResource<Location> {
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
    public Cursor getCursor(SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE);
        return builder.query(readableDatabase, new String[]{}, CacheHelper.LOCATION_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    @Override
    public Location loadFrom(Cursor cursor, SQLiteDatabase db) {
        int index = 0;
        int id = cursor.getInt(index++);
        String name = cursor.getString(index++);
        String icon = cursor.getString(index++);
        String path = cursor.getString(index++);
        String description = cursor.getString(index++);
        boolean global = cursor.getInt(index++) == 1;
        int color = cursor.getInt(index++);
        String cityImage = cursor.getString(index++);
        float latitude = cursor.getFloat(index++);
        float longitude = cursor.getFloat(index++);
        return new Location(id, name, icon, path, description, global, color, cityImage, latitude, longitude);
    }

    @Override
    public void store(SQLiteDatabase db, List<? extends Location> locations) {
        if (locations.isEmpty()) {
            return;
        }
        db.delete(CacheHelper.TABLE_LOCATION, null, null);

        ContentValues values = new ContentValues(10);
        for (Location location : locations) {
            values.clear();
            values.put(CacheHelper.LOCATION_ID, location.getId()); //1
            values.put(CacheHelper.LOCATION_NAME, location.getName()); //2
            values.put(CacheHelper.LOCATION_ICON, location.getIcon()); //3
            values.put(CacheHelper.LOCATION_PATH, location.getPath()); //4
            values.put(CacheHelper.LOCATION_DESCRIPTION, location.getDescription()); //5
            values.put(CacheHelper.LOCATION_GLOBAL, location.isGlobal() ? 1 : 0); //6
            values.put(CacheHelper.LOCATION_COLOR, location.getColor()); //7
            values.put(CacheHelper.LOCATION_CITY_IMAGE, location.getCityImage()); //8
            values.put(CacheHelper.LOCATION_LATITUDE, location.getLatitude()); //9
            values.put(CacheHelper.LOCATION_LONGITUDE, location.getLongitude()); //10

            db.replace(CacheHelper.TABLE_LOCATION, null, values);
        }
    }

    @Override
    public List<Location> request() {
        return mNetwork.getAvailableLocations();
    }

    @Override
    public boolean shouldUpdate() {
        return false;
    }
}
