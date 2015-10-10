package augsburg.se.alltagsguide.common;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel-L on 10.10.2015.
 */
public class AvailableLanguage implements Serializable {
    private String mLanguage;
    private int mPageId;

    public AvailableLanguage(String language, int pageId) {
        mLanguage = language;
        mPageId = pageId;
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

    public String getLanguage() {
        return mLanguage;
    }

    public int getPageId() {
        return mPageId;
    }

    public static AvailableLanguage loadFrom(Cursor cursor) {
        return new AvailableLanguage(cursor.getString(0), cursor.getInt(1));
    }
}
