package augsburg.se.alltagsguide.common;

import java.io.Serializable;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Article implements Serializable, Comparable {
    String mTitle;
    String mSummary;
    String mDescription;
    String mUrl;
    String mImage;

    public Article(String title, String description, String image, String url) {
        mTitle = title;
        mDescription = description;
        mImage = image;
        mUrl = url;
    }

    public Article(String title, String summary, String description, String image, String url) {
        mTitle = title;
        mSummary = summary;
        mDescription = description;
        mImage = image;
        mUrl = url;
    }

    public Article(String title, String description) {
        this(title, description, null, null);
    }

    public String getUrl() {
        return mUrl;
    }

    public String getSummary() {
        return mSummary;
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

    @Override
    public int compareTo(Object another) {
        return 0;
    }
}
