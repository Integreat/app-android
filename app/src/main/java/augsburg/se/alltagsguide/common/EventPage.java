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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import augsburg.se.alltagsguide.utilities.Newer;
import augsburg.se.alltagsguide.utilities.Objects;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class EventPage extends Page implements Newer<Page> {

    @NonNull private Event mEvent;
    @Nullable private EventLocation mLocation;
    @NonNull private List<EventTag> mTags;
    @NonNull private List<EventCategory> mCategories;

    public EventPage(@NonNull Page page, @NonNull Event event, @Nullable EventLocation location, @NonNull List<EventTag> tags, @NonNull List<EventCategory> categories) {
        super(page.getId(), page.getTitle(), page.getType(), page.getStatus(), page.getModified(), page.getDescription(),
                page.getContent(), page.getParentId(), page.getOrder(), page.getThumbnail(), page.getAuthor(), page.isAutoTranslated(), page.getAvailableLanguages(), page.getPageUrl());
        mEvent = event;
        mLocation = location;
        mTags = tags;
        mCategories = categories;
    }

    @NonNull
    public static EventPage fromJson(@NonNull final JsonObject jsonPage) {
        Page page = Page.fromJson(jsonPage);
        //TODO jsonPage.get("page") !?
        Event event = Event.fromJson(jsonPage.get("event").getAsJsonObject(), page.getId());
        JsonElement locationElement = jsonPage.get("location");
        EventLocation location = null;
        if (locationElement != null && !locationElement.isJsonNull()) {
            location = EventLocation.fromJson(locationElement.getAsJsonObject());
        }
        List<EventTag> tags = EventTag.fromJson(jsonPage.get("tags").getAsJsonArray());
        List<EventCategory> categories = EventCategory.fromJson(jsonPage.get("categories").getAsJsonArray());
        return new EventPage(page, event, location, tags, categories);
    }

    @Override
    public int compareTo(@NonNull Page o) {
        EventPage other = (EventPage) o;
        return Long.valueOf(getEvent().getStartTime()).compareTo(other.getEvent().getStartTime());
    }

    @NonNull
    public Event getEvent() {
        return mEvent;
    }

    @NonNull
    public List<EventCategory> getCategories() {
        return mCategories;
    }

    @Nullable
    public EventLocation getLocation() {
        return mLocation;
    }

    @NonNull
    public List<EventTag> getTags() {
        return mTags;
    }

    public static void recreateRelations(@NonNull List<EventPage> pages, @NonNull List<EventCategory> categories, @NonNull List<EventTag> tags, @NonNull List<AvailableLanguage> languages, @NonNull Language currentLanguage) {
        Page.recreateRelations(pages, languages, currentLanguage);

        Map<Integer, List<EventCategory>> eventIdCategoryMap = new HashMap<>();
        for (EventCategory category : categories) {
            if (!eventIdCategoryMap.containsKey(category.getEventId())) {
                eventIdCategoryMap.put(category.getEventId(), new ArrayList<EventCategory>());
            }
            eventIdCategoryMap.get(category.getEventId()).add(category);
        }
        Map<Integer, List<EventTag>> eventIdTagMap = new HashMap<>();
        for (EventTag tag : tags) {
            if (!eventIdTagMap.containsKey(tag.getEventId())) {
                eventIdTagMap.put(tag.getEventId(), new ArrayList<EventTag>());
            }
            eventIdTagMap.get(tag.getEventId()).add(tag);
        }

        for (EventPage page : pages) {
            int eventId = page.getEvent().getId();
            if (eventIdCategoryMap.containsKey(eventId)) {
                page.getCategories().addAll(eventIdCategoryMap.get(eventId));
            }
            if (eventIdTagMap.containsKey(eventId)) {
                page.getTags().addAll(eventIdTagMap.get(eventId));
            }
        }
    }

    @NonNull
    @Override
    public String getSearchableString() {
        String searchableString = super.getSearchableString();
        searchableString += " " + Objects.join(mCategories);
        searchableString += " " + Objects.join(mTags);
        return searchableString;
    }

    @Override
    public long getTimestamp() {
        return super.getTimestamp();
    }
}
