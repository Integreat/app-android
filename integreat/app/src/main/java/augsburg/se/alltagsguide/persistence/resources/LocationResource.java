/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.inject.Inject;

import java.util.Date;
import java.util.List;

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.PrefUtilities;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class LocationResource implements PersistableNetworkResource<Location> {
    private NetworkService mNetwork;
    private PrefUtilities mPreferences;

    @Inject
    public LocationResource(NetworkService network, PrefUtilities preferences) {
        mNetwork = network;
        mPreferences = preferences;
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_LOCATION);
        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE);
        return builder.query(readableDatabase, new String[]{}, CacheHelper.LOCATION_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    @Nullable
    @Override
    public Location loadFrom(@NonNull Cursor cursor, @NonNull SQLiteDatabase db) {
        return Location.fromCursor(cursor);
    }

    @Override
    public void store(@NonNull SQLiteDatabase db, @NonNull List<? extends Location> locations) {
        if (locations.isEmpty()) {
            return;
        }
        db.delete(CacheHelper.TABLE_LOCATION, null, null);

        ContentValues values = new ContentValues(11);
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
            values.put(CacheHelper.LOCATION_DEBUG, location.isDebug() ? 1 : 0); //11

            db.replace(CacheHelper.TABLE_LOCATION, null, values);
        }
    }

    @NonNull
    @Override
    public List<Location> request() {
        return mNetwork.getAvailableLocations();
    }

    @Override
    public boolean shouldUpdate() {
        long lastUpdate = mPreferences.lastLocationUpdateTime();
        long now = new Date().getTime();
        long updateCachingTime = 1000 * 60 * 60 * 4; // 4 hours
        return now - lastUpdate > updateCachingTime;
    }

    @Override
    public void loadedFromNetwork() {
        mPreferences.setLastLocationUpdateTime();
    }
}
