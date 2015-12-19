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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.Helper;

/**
 * Created by Daniel-L on 09.10.2015.
 */
public class EventCategory implements Serializable {
    private int mId;
    @NonNull private String mName;
    private int mParent;
    private int mEventId;
    private int mPageId;

    public EventCategory(int id, @NonNull String name, int parent) {
        mId = id;
        mName = name;
        mParent = parent;
    }

    @Nullable
    public static EventCategory fromJson(@NonNull final JsonObject jsonCategory) {
        if (jsonCategory.isJsonNull()) {
            return null;
        }
        JsonElement idElement = jsonCategory.get("id");
        if (idElement.isJsonNull()) {
            return null;
        }
        int id = Helper.getIntOrDefault(idElement, -1);
        if (id == -1) {
            return null;
        }
        String name = jsonCategory.get("name").getAsString();
        int parent = jsonCategory.get("parent").getAsInt();
        return new EventCategory(id, name, parent);
    }

    @NonNull
    public static List<EventCategory> fromJson(@NonNull JsonArray array) {
        List<EventCategory> categories = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            EventCategory category = fromJson(obj);
            if (category != null) {
                categories.add(category);
            }
        }
        return categories;
    }

    @Override
    public String toString() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    public int getParent() {
        return mParent;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public static EventCategory loadFrom(@NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(CacheHelper.CATEGORY_ID));
        String name = cursor.getString(cursor.getColumnIndex(CacheHelper.CATEGORY_NAME));
        int parent = cursor.getInt(cursor.getColumnIndex(CacheHelper.CATEGORY_PARENT));
        int pageId = cursor.getInt(cursor.getColumnIndex(CacheHelper.EVENT_CATEGORY_PAGE));
        int eventId = cursor.getInt(cursor.getColumnIndex(CacheHelper.EVENT_CATEGORY_EVENT_ID));
        EventCategory category = new EventCategory(id, name, parent);
        category.setEventId(eventId);
        category.setPageId(pageId);
        return category;
    }

    public int getPageId() {
        return mPageId;
    }

    public int getEventId() {
        return mEventId;
    }

    private void setPageId(int pageId) {
        mPageId = pageId;
    }

    private void setEventId(int eventId) {
        mEventId = eventId;
    }
}
