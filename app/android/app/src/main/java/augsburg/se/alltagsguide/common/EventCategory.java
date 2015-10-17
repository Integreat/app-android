package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;

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
    private String mName;
    private int mParent;
    private int mEventId;
    private int mPageId;

    public EventCategory(int id, @NonNull String name, int parent) {
        mId = id;
        mName = name;
        mParent = parent;
    }

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

    public static List<EventCategory> fromJson(JsonArray array) {
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

    public int getId() {
        return mId;
    }

    public int getParent() {
        return mParent;
    }

    public String getName() {
        return mName;
    }

    public static EventCategory loadFrom(Cursor cursor) {
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
