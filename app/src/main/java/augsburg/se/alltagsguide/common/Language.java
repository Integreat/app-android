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

package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;

import java.io.Serializable;

import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.Newer;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Language implements Serializable, Newer<Language> {

    private int mId;
    @NonNull private String mShortName;
    @NonNull private String mName;
    @Nullable private String mIconPath;
    @Nullable private Location mLocation;

    public Language(int id, @NonNull String shortName, @NonNull String name, @Nullable String iconPath) {
        mId = id;
        mShortName = shortName;
        mName = name;
        mIconPath = iconPath;
    }

    @Override
    public int compareTo(@NonNull Language another) {
        return mShortName.compareTo(another.mShortName);
    }

    public void setLocation(@NonNull Location location) {
        mLocation = location;
    }

    @Nullable
    public Location getLocation() {
        return mLocation;
    }

    @Override
    public String toString() {
        return mShortName;
    }

    @NonNull
    public static Language fromJson(JsonObject jsonPage) {
        int id = jsonPage.get("id").getAsInt();
        String shortName = jsonPage.get("code").getAsString();
        String nativeName = jsonPage.get("native_name").getAsString();
        String countryFlagUrl = jsonPage.get("country_flag_url").getAsString();
        return new Language(id, shortName, nativeName, countryFlagUrl);
    }

    public int getId() {
        return mId;
    }

    @Nullable
    public String getIconPath() {
        return mIconPath;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getShortName() {
        return mShortName;
    }

    @Override
    public boolean equals(@NonNull Object another) {
        return another instanceof Language && mId == ((Language) another).getId();
    }

    @NonNull
    public static Language fromCursor(@NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(CacheHelper.LANGUAGE_ID));
        String shortName = cursor.getString(cursor.getColumnIndex(CacheHelper.LANGUAGE_SHORT));
        String name = cursor.getString(cursor.getColumnIndex(CacheHelper.LANGUAGE_NAME));
        String path = cursor.getString(cursor.getColumnIndex(CacheHelper.LANGUAGE_PATH));
        return new Language(id, shortName, name, path);
    }

    @Override
    public long getTimestamp() {
        return -1;
    }
}
