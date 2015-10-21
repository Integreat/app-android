package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;

import augsburg.se.alltagsguide.BuildConfig;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.Helper;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 08.10.2015.
 */
public class EventLocation implements Serializable {

    @Nullable private String mName;
    @Nullable private String mAddress;
    @Nullable private String mTown;
    @Nullable private String mState;
    @Nullable private String mRegion;
    @Nullable private String mCountry;
    private float mLatitude;
    private float mLongitude;
    private int mPostcode;
    private int mId;

    public EventLocation(int id, @Nullable String name, @Nullable String address, @Nullable String town, @Nullable String state, int postcode, @Nullable String region, @Nullable String country, float latitude, float longitude) {
        mId = id;
        mName = name;
        mAddress = address;
        mTown = town;
        mState = state;
        mPostcode = postcode;
        mRegion = region;
        mCountry = country;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public int getId() {
        return mId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }

    @Nullable
    public String getTown() {
        return mTown;
    }

    @Nullable
    public String getState() {
        return mState;
    }

    @Nullable
    public String getRegion() {
        return mRegion;
    }

    public int getPostcode() {
        return mPostcode;
    }

    @Nullable
    public String getCountry() {
        return mCountry;
    }

    public float getLatitude() {
        return mLatitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    @Nullable
    public static EventLocation fromJson(@NonNull JsonObject jsonPage) {
        if (jsonPage.isJsonNull()) {
            return null;
        }
        JsonElement idElement = jsonPage.get("id");
        if (idElement.isJsonNull()) {
            return null;
        }
        int id = Helper.getIntOrDefault(idElement, -1);
        if (id == -1) {
            return null;
        }
        return new EventLocation(
                id,
                Helper.getStringOrDefault(jsonPage.get("name"), null),
                Helper.getStringOrDefault(jsonPage.get("address"), null),
                Helper.getStringOrDefault(jsonPage.get("town"), null),
                Helper.getStringOrDefault(jsonPage.get("state"), null),
                Helper.getIntOrDefault(jsonPage.get("postcode"), 0),
                Helper.getStringOrDefault(jsonPage.get("region"), null),
                Helper.getStringOrDefault(jsonPage.get("country"), null),
                Helper.getFloatOrDefault(jsonPage.get("latitude"), 0.0f),
                Helper.getFloatOrDefault(jsonPage.get("longitude"), 0.0f));
    }

    @NonNull
    public static EventLocation fromCursor(@NonNull Cursor cursor) {
        if (BuildConfig.DEBUG) {
            Ln.d("Column count: %d", cursor.getColumnCount());
            if (cursor.isClosed()) {
                throw new IllegalStateException("Cursor should not be closed");
            }
        }
        int locationId = cursor.getInt(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_ID));
        String locationName = cursor.getString(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_NAME));
        String locationAddress = cursor.getString(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_ADDRESS));
        String locationTown = cursor.getString(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_TOWN));
        String locationState = cursor.getString(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_STATE));
        int locationPostcode = cursor.getInt(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_POSTCODE));
        String locationRegion = cursor.getString(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_REGION));
        String locationCountry = cursor.getString(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_COUNTRY));
        float locationLatitude = cursor.getFloat(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_LATITUDE));
        float locationLongitude = cursor.getFloat(cursor.getColumnIndex(CacheHelper.EVENT_LOCATION_LONGITUDE));
        return new EventLocation(locationId, locationName, locationAddress, locationTown, locationState, locationPostcode,
                locationRegion, locationCountry, locationLatitude, locationLongitude);
    }
}
