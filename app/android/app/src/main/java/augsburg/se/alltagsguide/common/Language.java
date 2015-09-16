package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Language implements Serializable, Comparable {
    private String mIconPath;
    private String mName;
    private String mShortName;
    private Location mLocation;

    public Language() {
    }

    public String getIconPath() {
        return mIconPath;
    }

    public String getName() {
        return mName;
    }

    public String getShortName() {
        return mShortName;
    }

    public Language(String iconPath, String name, String shortName) {
        mIconPath = iconPath;
        mName = name;
        mShortName = shortName;
    }

    @Override
    public int compareTo(@NonNull Object another) {
        if (another instanceof Language) {
            return mShortName.compareTo(((Language) another).mShortName);
        }
        return 1;
    }

    public void setShortName(String shortName) {
        mShortName = shortName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setIconPath(String imagePath) {
        mIconPath = imagePath;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }
}
