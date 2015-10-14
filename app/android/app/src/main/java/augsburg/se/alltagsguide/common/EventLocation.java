package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;

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
    private int mId;
    private String mName;
    private String mAddress;
    private String mTown;
    private String mState;
    private int mPostcode;
    private String mRegion;
    private String mCountry;
    private float mLatitude;
    private float mLongitude;

    public EventLocation(int id, String name, String address, String town, String state, int postcode, String region, String country, float latitude, float longitude) {
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

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getTown() {
        return mTown;
    }

    public String getState() {
        return mState;
    }

    public String getRegion() {
        return mRegion;
    }

    public int getPostcode() {
        return mPostcode;
    }

    public String getCountry() {
        return mCountry;
    }

    public float getLatitude() {
        return mLatitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public static EventLocation fromJson(@NonNull JsonObject jsonPage) {
        int id = Helper.getIntOrDefault(jsonPage.get("id"), -1);
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

    public static EventLocation fromCursor(Cursor cursor) {
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
