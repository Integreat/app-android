package augsburg.se.alltagsguide.common;


import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Location implements Serializable, Comparable {
    private String mIcon;
    private String mPath;
    private String mName;
    private String mUrl;
    private int mColor;

    public String getPath() {
        return mPath;
    }

    public String getIcon() {
        return mIcon;
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


    public void setName(@NonNull String name) {
        mName = name;
    }

    public void setDescription(String url) {
        mUrl = url;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public Location() {
    }

    public Location(String path, @NonNull String name, String url) {
        mPath = path;
        mName = name;
        mUrl = url;
    }

    public Location(String path, @NonNull String name, String url, int color) {
        mPath = path;
        mName = name;
        mUrl = url;
        mColor = color;
    }

    @Override
    public int compareTo(@NonNull Object another) {
        if (another instanceof Location) {
            return mName.compareTo(((Location) another).mName);
        }
        return 1;
    }

    @Override
    public String toString() {
        return mPath.substring(1, mPath.length() - 1);
    } //TODO currently required for retrofit get parameter

    public static Location fromJson(JsonObject jsonPage) {
        Location location = new Location();
        int id = jsonPage.get("id").getAsInt();
        String name = jsonPage.get("name").getAsString();
        String path = jsonPage.get("path").getAsString();
        String description = jsonPage.get("description").getAsString();
        String icon = jsonPage.get("icon").getAsString();
        location.setPath(path);
        location.setName(name);
        location.setIcon(icon);
        location.setColor(id); //TODO
        location.setDescription(description); //TODO or not TODO :D
        return location;
    }
}
