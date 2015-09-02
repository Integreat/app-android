package augsburg.se.alltagsguide.common;

import java.io.Serializable;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Language implements Serializable, Comparable {
    private String mIconPath;
    private String mName;
    private String mShortName;

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
    public int compareTo(Object another) {
        return 0;
    }
}
