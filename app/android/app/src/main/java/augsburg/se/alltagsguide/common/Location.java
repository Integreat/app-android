package augsburg.se.alltagsguide.common;


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
    public int compareTo(Object another) {
        return 0;
    }
}
