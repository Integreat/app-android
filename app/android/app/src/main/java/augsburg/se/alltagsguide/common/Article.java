package augsburg.se.alltagsguide.common;

import java.io.Serializable;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Article implements Serializable, Comparable {
    int mId;
    String mTitle;
    String mSummary;
    String mDescription;
    String mUrl;
    String mImage;
    Category mCategory;
    Location mLocation;
    Language mLanguage;

    public Article() {
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

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

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String gemSummary() {
        return mSummary;
    }

    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }


    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setLanguage(Language language) {
        mLanguage = language;
    }

    public Language getLanguage() {
        return mLanguage;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

}
