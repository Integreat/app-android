package augsburg.se.alltagsguide.common;


import java.io.Serializable;

public class Location implements Serializable{
    private String mIconPath;
    private String mName;

    public String getIconPath() {
        return mIconPath;
    }

    public String getName() {
        return mName;
    }

    public Location(String iconPath, String name) {
        mIconPath = iconPath;
        mName = name;
    }

}
