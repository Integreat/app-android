package augsburg.se.alltagsguide.common;


import java.io.Serializable;

public class Location implements Serializable {
    private String mPath;
    private String mName;
    private String mUrl;

    public String getPath() {
        return mPath;
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

}
