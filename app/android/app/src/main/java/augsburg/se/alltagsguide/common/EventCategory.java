package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel-L on 09.10.2015.
 */
public class EventCategory implements Serializable {
    private int mId;
    private String mName;
    private int mParent;

    public EventCategory(int id, @NonNull String name, int parent) {
        mId = id;
        mName = name;
        mParent = parent;
    }

    public static EventCategory fromJson(@NonNull final JsonObject jsonTag) {
        int id = jsonTag.get("id").getAsInt();
        String name = jsonTag.get("name").getAsString();
        int parent = jsonTag.get("parent").getAsInt();
        return new EventCategory(id, name, parent);
    }

    public static List<EventCategory> fromJson(JsonArray array) {
        List<EventCategory> tags = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            tags.add(fromJson(obj));
        }
        return tags;
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
}
