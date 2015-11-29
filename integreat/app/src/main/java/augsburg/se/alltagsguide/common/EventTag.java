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
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.Helper;

/**
 * Created by Daniel-L on 09.10.2015.
 */
public class EventTag implements Serializable {
    @NonNull private String mName;
    private int mEventId;

    public EventTag(@NonNull String name) {
        mName = name;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public static EventTag fromJson(@NonNull final JsonObject jsonTag) {
        String name = Helper.getStringOrDefault(jsonTag.get("name"), "");
        //TODO Tags have an id now.
        return new EventTag(name);
    }

    @NonNull
    public static List<EventTag> fromJson(@NonNull JsonArray array) {
        List<EventTag> tags = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            EventTag tag = fromJson(array.get(i).getAsJsonObject());
            tags.add(tag);
        }
        return tags;
    }

    @NonNull
    public static EventTag loadFrom(@NonNull Cursor cursor) {
        EventTag tag = new EventTag(cursor.getString(cursor.getColumnIndex(CacheHelper.EVENT_TAG_NAME))); //TODO sooner or later to TAG_NAME with join
        tag.setEventId(cursor.getColumnIndex(CacheHelper.EVENT_TAG_EVENT_ID));
        return tag;
    }

    @Nullable
    @Override
    public String toString() {
        return mName;
    }

    public int getEventId() {
        return mEventId;
    }

    private void setEventId(int eventId) {
        mEventId = eventId;
    }
}
