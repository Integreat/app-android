package augsburg.se.alltagsguide.common;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import augsburg.se.alltagsguide.persistence.CacheHelper;

/**
 * Created by Daniel-L on 10.10.2015.
 */
public class AvailableLanguage implements Serializable {
    private String mLanguage;
    private int mPageId;

    private Language mLoadedLanguage;

    public AvailableLanguage(String language, int pageId) {
        mLanguage = language;
        mPageId = pageId;
    }

    public void setLanguage(Language language) {
        mLoadedLanguage = language;
    }

    public static List<AvailableLanguage> fromJson(JsonElement elem) {
        List<AvailableLanguage> languages = new ArrayList<>();
        Map<String, Integer> languagesMap = new Gson().fromJson(elem, new TypeToken<Map<String, Integer>>() {
        }.getType());
        for (Map.Entry<String, Integer> entry : languagesMap.entrySet()) {
            languages.add(new AvailableLanguage(entry.getKey(), entry.getValue()));
        }
        return languages;
    }

    public Language getLoadedLanguage() {
        return mLoadedLanguage;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public int getPageId() {
        return mPageId;
    }

    public static AvailableLanguage loadFrom(Cursor cursor) {
        String shortLanguage = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE));
        int otherPageId = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_AVAIL_OTHER_PAGE));
        return new AvailableLanguage(shortLanguage, otherPageId);
    }
}
