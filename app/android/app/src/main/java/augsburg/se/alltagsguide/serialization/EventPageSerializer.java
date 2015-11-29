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

package augsburg.se.alltagsguide.serialization;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import augsburg.se.alltagsguide.common.EventPage;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class EventPageSerializer implements JsonDeserializer<List<EventPage>> {
    @NonNull
    @Override
    public List<EventPage> deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()){
            return new ArrayList<>();
        }
        return parsePages(json.getAsJsonArray());
    }

    @NonNull
    private List<EventPage> parsePages(@NonNull final JsonArray jsonPages) {
        List<EventPage> rootPages = getPagesByParentId(null, 0, jsonPages);
        Stack<EventPage> pagesLeft = new Stack<>();
        pagesLeft.addAll(rootPages);
        while (!pagesLeft.isEmpty()) {
            EventPage page = pagesLeft.pop();
            List<EventPage> subPages = getPagesByParentId(page, page.getId(), jsonPages);
            for (EventPage subPage : subPages) {
                page.getSubPages().add(subPage);
            }
            for (EventPage subPage : subPages) {
                pagesLeft.push(subPage);
            }
        }
        return rootPages;
    }

    @NonNull
    private List<EventPage> getPagesByParentId(final EventPage parent, final int parentId, @NonNull final JsonArray jsonPages) {
        final List<EventPage> result = new ArrayList<>();
        for (int i = 0; i < jsonPages.size(); i++) {
            JsonObject jsonPage = jsonPages.get(i).getAsJsonObject();
            if (jsonPage.get("parent").getAsInt() == parentId) {
                EventPage page = EventPage.fromJson(jsonPage);
                page.setParent(parent);
                result.add(page);
            }
        }
        return result;
    }
}
