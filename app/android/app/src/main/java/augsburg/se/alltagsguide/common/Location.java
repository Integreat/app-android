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

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.Newer;
import augsburg.se.alltagsguide.utilities.Objects;

public class Location implements Serializable, Newer<Location> {
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
    public int compareTo(@NonNull Location another) {
        return mName.compareTo(another.mName);
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
        String cityImage = loadCityImage(name);  //TODO
        float latitude = 0.0f; //TODO
        float longitude = 0.0f; //TODO
        return new Location(id, name, icon, path, description, global, color, cityImage, latitude, longitude);
    }

    //TODO we definitely need to change loading city images.
    @NonNull
    private static String loadCityImage(String name) {
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("augsburg", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Augsburg_-_Markt.jpg/297px-Augsburg_-_Markt.jpg");
        locationMap.put("muenchen", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/M%C3%BCnchen_Panorama.JPG/300px-M%C3%BCnchen_Panorama.JPG");
        locationMap.put("münchen", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/M%C3%BCnchen_Panorama.JPG/300px-M%C3%BCnchen_Panorama.JPG");
        locationMap.put("main-taunus-kreis", "http://vmkrcmar21.informatik.tu-muenchen.de/wordpress/main-taunus-kreis/wp-content/uploads/sites/15/2015/09/pix-Landratsamt.jpg");
        locationMap.put("pre arrival", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/EU-Germany.svg/800px-EU-Germany.svg.png");
        locationMap.put("deutschland", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/M%C3%BCnchen_Panorama.JPG/300px-M%C3%BCnchen_Panorama.JPG");
        String cityPath = locationMap.get(name.toLowerCase());
        return cityPath != null ? cityPath : "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Brandenburger_Tor_abends.jpg/300px-Brandenburger_Tor_abends.jpg";
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

    public String getSearchString() {
        return mName + " " + mDescription;
    }
}
