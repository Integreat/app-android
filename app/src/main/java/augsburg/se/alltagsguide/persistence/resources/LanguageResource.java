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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import java.util.Date;
import java.util.List;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.PrefUtilities;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class LanguageResource implements PersistableNetworkResource<Language> {

    /**
     * Creation factory
     */
    public interface Factory {
        LanguageResource under(Location location);
    }

    @NonNull private final Location mLocation;
    @NonNull private NetworkService mNetwork;
    @NonNull private PrefUtilities mPreferences;

    @AssistedInject
    public LanguageResource(@NonNull @Assisted Location location, @NonNull NetworkService network, @NonNull PrefUtilities preferences) {
        mLocation = location;
        mNetwork = network;
        mPreferences = preferences;
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_LANGUAGE);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.LANGUAGE_LOCATION + "=?",
                new String[]{String.valueOf(mLocation.getId())}, null, null,
                null);
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.LANGUAGE_LOCATION + "=? AND " + CacheHelper.LANGUAGE_ID + "=?",
                new String[]{String.valueOf(mLocation.getId()), String.valueOf(id)}, null, null,
                null);
    }

    @Nullable
    @Override
    public Language loadFrom(@NonNull Cursor cursor) {
        Language language = Language.fromCursor(cursor);
        language.setLocation(mLocation);
        return language;
    }

    @Override
    public void store(@NonNull SQLiteDatabase db, @NonNull List<? extends Language> languages) {
        if (languages.isEmpty()) {
            return;
        }

        ContentValues values = new ContentValues(5);
        for (Language language : languages) {
            values.clear();
            values.put(CacheHelper.LANGUAGE_ID, language.getId()); //1
            values.put(CacheHelper.LANGUAGE_SHORT, language.getShortName()); //2
            values.put(CacheHelper.LANGUAGE_NAME, language.getName()); //3
            values.put(CacheHelper.LANGUAGE_PATH, language.getIconPath()); //4
            values.put(CacheHelper.LANGUAGE_LOCATION, mLocation.getId()); //5

            db.replace(CacheHelper.TABLE_LANGUAGE, null, values);
        }
    }

    @NonNull
    @Override
    public List<Language> request() {
        return mNetwork.getAvailableLanguages(mLocation);
    }


    @Override
    public boolean shouldUpdate() {
        long lastUpdate = mPreferences.lastLanguageUpdateTime(mLocation);
        long now = new Date().getTime();
        long updateCachingTime = 1000 * 60 * 60 * 4; // 4 hours
        return now - lastUpdate > updateCachingTime;
    }

    @Override
    public void loadedFromNetwork() {
        mPreferences.setLastLanguageUpdateTime(mLocation);
    }
}
