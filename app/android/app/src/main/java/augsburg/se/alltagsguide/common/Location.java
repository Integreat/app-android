package augsburg.se.alltagsguide.common;


import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;

import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.Newer;
import augsburg.se.alltagsguide.utilities.Objects;

public class Location implements Serializable, Newer {
    private int mId;
    @NonNull private String mName;
    private String mIcon;
    @NonNull private String mPath;
    private String mDescription;
    private boolean mGlobal;
    private int mColor;
    private String mCityImage;
    private float mLatitude;
    private float mLongitude;

    public Location(int id, @NonNull String name, String icon, @NonNull String path, String description, boolean global, int color, String cityImage, float latitude, float longitude) {
        mId = id;
        mName = name;
        mIcon = icon;
        mPath = path;
        mDescription = description;
        mGlobal = global;
        mColor = color;
        mCityImage = cityImage;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    public int compareTo(@NonNull Object another) {
        if (another instanceof Location) {
            return mName.compareTo(((Location) another).mName);
        }
        return 0;
    }

    @Override
    public String toString() {
        //TODO currently required for retrofit get parameter
        return mPath.substring(1, mPath.length() - 1);
    }

    @NonNull
    public static Location fromJson(JsonObject jsonPage) {
        int id = jsonPage.get("id").getAsInt();
        String name = jsonPage.get("name").getAsString();
        String icon = jsonPage.get("icon").isJsonNull() ? null : jsonPage.get("icon").getAsString();
        String path = jsonPage.get("path").getAsString();
        String description = jsonPage.get("description").getAsString();
        boolean global = jsonPage.get("global").getAsBoolean();
        int color = id; //TODO CALCULATE
        String cityImage = loadCityImage(name.toLowerCase());  //TODO
        float latitude = 0.0f; //TODO
        float longitude = 0.0f; //TODO
        return new Location(id, name, icon, path, description, global, color, cityImage, latitude, longitude);
    }

    @NonNull
    private static String loadCityImage(String name) {
        if (Objects.equals("muenchen", name) || Objects.equals("münchen", name)) {
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/M%C3%BCnchen_Panorama.JPG/300px-M%C3%BCnchen_Panorama.JPG";
        }
        if (Objects.equals("augsburg", name)) {
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Augsburg_-_Markt.jpg/297px-Augsburg_-_Markt.jpg";
        }
        if (Objects.equals("pre arrival", name)) {
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/EU-Germany.svg/800px-EU-Germany.svg.png";
        }
        if (Objects.equals("deutschland", name)) {
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Brandenburger_Tor_abends.jpg/300px-Brandenburger_Tor_abends.jpg";
        }
        return "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Brandenburger_Tor_abends.jpg/300px-Brandenburger_Tor_abends.jpg";
    }

    public float getLatitude() {
        return mLatitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public int getId() {
        return mId;
    }

    public int getColor() {
        return mColor;
    }

    @NonNull
    public String getPath() {
        return mPath;
    }

    public boolean isGlobal() {
        return mGlobal;
    }

    public String getIcon() {
        return mIcon;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public String getCityImage() {
        return mCityImage;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public boolean equals(@NonNull Object another) {
        return another instanceof Location && mId == ((Location) another).getId();
    }

    @Override
    public long getTimestamp() {
        return -1;
    }

    @NonNull
    public static Location fromCursor(@NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(CacheHelper.LOCATION_ID));
        String name = cursor.getString(cursor.getColumnIndex(CacheHelper.LOCATION_NAME));
        String icon = cursor.getString(cursor.getColumnIndex(CacheHelper.LOCATION_ICON));
        String path = cursor.getString(cursor.getColumnIndex(CacheHelper.LOCATION_PATH));
        String description = cursor.getString(cursor.getColumnIndex(CacheHelper.LOCATION_DESCRIPTION));
        boolean global = cursor.getInt(cursor.getColumnIndex(CacheHelper.LOCATION_GLOBAL)) == 1;
        int color = cursor.getInt(cursor.getColumnIndex(CacheHelper.LOCATION_COLOR));
        String cityImage = cursor.getString(cursor.getColumnIndex(CacheHelper.LOCATION_CITY_IMAGE));
        float latitude = cursor.getFloat(cursor.getColumnIndex(CacheHelper.LOCATION_LATITUDE));
        float longitude = cursor.getFloat(cursor.getColumnIndex(CacheHelper.LOCATION_LONGITUDE));
        return new Location(id, name, icon, path, description, global, color, cityImage, latitude, longitude);
    }
}
