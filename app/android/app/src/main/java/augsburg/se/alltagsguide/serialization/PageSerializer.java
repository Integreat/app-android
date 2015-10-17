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

import augsburg.se.alltagsguide.common.Page;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class PageSerializer implements JsonDeserializer<List<Page>> {

    @Override
    public List<Page> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Page> rootPages = parsePages(json.getAsJsonArray());
        //printPages(rootPages);
        return rootPages;
    }

    private List<Page> parsePages(final JsonArray jsonPages) {
        List<Page> rootPages = getPagesByParentId(null, 0, jsonPages);
        Stack<Page> pagesLeft = new Stack<>();
        pagesLeft.addAll(rootPages);
        while (!pagesLeft.isEmpty()) {
            Page page = pagesLeft.pop();
            List<Page> subPages = getPagesByParentId(page, page.getId(), jsonPages);
            page.addSubPages(subPages);
            for (Page subPage : subPages) {
                pagesLeft.push(subPage);
            }
        }
        return rootPages;
    }

    private List<Page> getPagesByParentId(final Page parent, final int parentId, final JsonArray jsonPages) {
        final List<Page> result = new ArrayList<>();
        for (int i = 0; i < jsonPages.size(); i++) {
            JsonObject jsonPage = jsonPages.get(i).getAsJsonObject();
            if (jsonPage.get("parent").getAsInt() == parentId) {
                Page page = Page.fromJson(jsonPage);
                page.setParent(parent);
                result.add(page);
            }
        }
        return result;
    }

    final int INDENTS_PER_LEVEL = 4;

    private void printIndent(final int hierarchyLevel) {
        if (hierarchyLevel == 0) {
            return;
        }
        System.out.printf("%" + hierarchyLevel * INDENTS_PER_LEVEL + "s", "");
    }

    private void printPages(final List<Page> rootPages) {
        printPages(rootPages, 0);
    }

    private void printPages(final List<Page> pages, final int hierarchyLevel) {
        for (Page page : pages) {
            printIndent(hierarchyLevel);
            printPage(page);
            printPages(page.getSubPages(), hierarchyLevel + 1);
        }
    }

    private void printPage(final Page page) {
        System.out.println(page.getId() + " | " + page.getTitle());
    }
}
