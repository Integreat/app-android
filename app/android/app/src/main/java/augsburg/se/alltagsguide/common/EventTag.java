package augsburg.se.alltagsguide.common;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.Helper;

/**
 * Created by Daniel-L on 09.10.2015.
 */
public class EventTag implements Serializable {
    private String mName;
    private int mEventId;

    public EventTag(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public static EventTag fromJson(@NonNull final JsonElement jsonTag) {
        String name = Helper.getStringOrDefault(jsonTag, null);
        if (name == null) {
            return null;
        }
        return new EventTag(name);
    }

    public static List<EventTag> fromJson(JsonArray array) {
        List<EventTag> tags = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            EventTag tag = fromJson(array.get(i));
            if (tag != null) {
                tags.add(tag);
            }
        }
        return tags;
    }

    public static EventTag loadFrom(Cursor cursor) {
        EventTag tag = new EventTag(cursor.getString(cursor.getColumnIndex(CacheHelper.EVENT_TAG_NAME))); //TODO sooner or later to TAG_NAME with join
        tag.setEventId(cursor.getColumnIndex(CacheHelper.EVENT_TAG_EVENT_ID));
        return tag;
    }

    public int getEventId() {
        return mEventId;
    }

    private void setEventId(int eventId) {
        mEventId = eventId;
    }
}
