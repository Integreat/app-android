package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel-L on 09.10.2015.
 */
public class EventTag implements Serializable {
    private String mName;

    public EventTag(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public static EventTag fromJson(@NonNull final JsonElement jsonTag) {
        String name = jsonTag.getAsString();
        return new EventTag(name);
    }

    public static List<EventTag> fromJson(JsonArray array) {
        List<EventTag> tags = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            tags.add(fromJson(array.get(i)));
        }
        return tags;
    }
}
