package augsburg.se.alltagsguide.common;


import android.support.annotation.NonNull;

import java.io.Serializable;

public class Location implements Serializable, Comparable {
    private String mPath;
    private String mName;
    private String mUrl;
    private int mColor;

    public String getPath() {
        return mPath;
    }

    public int getColor() {
        return mColor;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getName() {
        return mName;
    }


    public void setName(String name) {
        mName = name;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public Location() {
    }

    public Location(String path, String name, String url) {
        mPath = path;
        mName = name;
        mUrl = url;
    }

    public Location(String path, String name, String url, int color) {
        mPath = path;
        mName = name;
        mUrl = url;
        mColor = color;
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return 0;
    }

    @Override
    public String toString() {
        return mName;
    }
}
