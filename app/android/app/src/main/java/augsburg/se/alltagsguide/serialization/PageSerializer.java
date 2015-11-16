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
import android.support.annotation.Nullable;

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
    public List<Page> deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return parsePages(json.getAsJsonArray());
    }

    private List<Page> parsePages(@NonNull final JsonArray jsonPages) {
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

    @NonNull
    private List<Page> getPagesByParentId(@Nullable final Page parent, final int parentId, @NonNull final JsonArray jsonPages) {
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

    private void printPages(@NonNull final List<Page> rootPages) {
        printPages(rootPages, 0);
    }

    private void printPages(@NonNull final List<Page> pages, final int hierarchyLevel) {
        for (Page page : pages) {
            printIndent(hierarchyLevel);
            printPage(page);
            printPages(page.getSubPages(), hierarchyLevel + 1);
        }
    }

    private void printPage(@NonNull final Page page) {
        System.out.println(page.getId() + " | " + page.getTitle());
    }
}
