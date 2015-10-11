package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;

import augsburg.se.alltagsguide.BuildConfig;

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
        return new EventLocation(
                jsonPage.get("id").getAsInt(),
                jsonPage.get("name").getAsString(),
                jsonPage.get("address").getAsString(),
                jsonPage.get("town").getAsString(),
                jsonPage.get("state").getAsString(),
                jsonPage.get("postcode").getAsInt(),
                jsonPage.get("region").getAsString(),
                jsonPage.get("country").getAsString(),
                jsonPage.get("latitude").getAsFloat(),
                jsonPage.get("longitude").getAsFloat());
    }

    public static EventLocation fromCursor(Cursor cursor, int index) {
        if (BuildConfig.DEBUG) {
            if (!cursor.isClosed()) {
                throw new IllegalStateException("Cursor should not be closed");
            }
        }
        int locationId = cursor.getInt(index++);
        String locationName = cursor.getString(index++);
        String locationAddress = cursor.getString(index++);
        String locationTown = cursor.getString(index++);
        String locationState = cursor.getString(index++);
        int locationPostcode = cursor.getInt(index++);
        String locationRegion = cursor.getString(index++);
        String locationCountry = cursor.getString(index++);
        float locationLatitude = cursor.getFloat(index++);
        float locationLongitude = cursor.getFloat(index++);
        return new EventLocation(
                locationId, locationName, locationAddress, locationTown, locationState, locationPostcode,
                locationRegion, locationCountry, locationLatitude, locationLongitude);
    }
}
