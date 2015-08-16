package augsburg.se.alltagsguide.common;

import java.io.Serializable;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Information implements Serializable {
    String mTitle;
    String mDescription;
    String mUrl;
    String mImage;

    public Information(String title, String description, String image, String url) {
        mTitle = title;
        mDescription = description;
        mImage = image;
        mUrl = url;
    }

    public Information(String title, String description) {
        this(title, description, null, null);
    }

    public String getUrl() {
        return mUrl;
    }

    public String getImage() {
        return mImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
}
