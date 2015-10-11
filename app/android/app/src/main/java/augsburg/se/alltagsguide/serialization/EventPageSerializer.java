package augsburg.se.alltagsguide.serialization;

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
import augsburg.se.alltagsguide.common.Page;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class EventPageSerializer implements JsonDeserializer<List<EventPage>> {

    @Override
    public List<EventPage> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return parsePages(json.getAsJsonArray());
    }

    private List<EventPage> parsePages(final JsonArray jsonPages) {
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

    private List<EventPage> getPagesByParentId(final EventPage parent, final int parentId, final JsonArray jsonPages) {
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
