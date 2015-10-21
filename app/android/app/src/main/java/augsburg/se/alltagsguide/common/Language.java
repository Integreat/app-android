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
public class Language implements Serializable, Newer {

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
    public int compareTo(@NonNull Object another) {
        if (another instanceof Language) {
            return mShortName.compareTo(((Language) another).mShortName);
        }
        return 1;
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
    public static Language fromCursor(Cursor cursor) {
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
