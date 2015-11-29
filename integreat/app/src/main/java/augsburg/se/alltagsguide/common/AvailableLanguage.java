/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    @NonNull private String mLanguage;
    private int mOtherPageId;
    private int mOwnPageId;

    @Nullable private Language mLoadedLanguage;

    public AvailableLanguage(@NonNull String language, int otherPageId) {
        mLanguage = language;
        mOtherPageId = otherPageId;
    }

    public void setLanguage(@NonNull Language language) {
        mLoadedLanguage = language;
    }

    public void setOwnPageId(int ownPageId) {
        mOwnPageId = ownPageId;
    }

    public int getOwnPageId() {
        return mOwnPageId;
    }

    @NonNull
    public static List<AvailableLanguage> fromJson(@NonNull JsonElement elem) {
        List<AvailableLanguage> languages = new ArrayList<>();
        if (!elem.isJsonArray()){
            return languages;
        }
        Map<String, Integer> languagesMap = new Gson().fromJson(elem, new TypeToken<Map<String, Integer>>() {
        }.getType());
        for (Map.Entry<String, Integer> entry : languagesMap.entrySet()) {
            languages.add(new AvailableLanguage(entry.getKey(), entry.getValue()));
        }
        return languages;
    }

    @Nullable
    public Language getLoadedLanguage() {
        return mLoadedLanguage;
    }

    @NonNull
    public String getLanguage() {
        return mLanguage;
    }

    public int getOtherPageId() {
        return mOtherPageId;
    }

    @NonNull
    public static AvailableLanguage loadFrom(@NonNull Cursor cursor) {
        String shortLanguage = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE));
        int otherPageId = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_AVAIL_OTHER_PAGE));
        int ownPageId = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_AVAIL_PAGE_ID));
        AvailableLanguage language = new AvailableLanguage(shortLanguage, otherPageId);
        language.setOwnPageId(ownPageId);
        return language;
    }
}
