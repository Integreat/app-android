package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

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
public class EventPage extends Page implements Newer {

    private Event mEvent;
    private EventLocation mLocation;
    private List<EventTag> mTags;
    private List<EventCategory> mCategories;

    public EventPage(@NonNull Page page, @NonNull Event event, EventLocation location, @NonNull List<EventTag> tags, @NonNull List<EventCategory> categories) {
        super(page.getId(), page.getTitle(), page.getType(), page.getStatus(), page.getModified(), page.getTitle(),
                page.getDescription(), page.getParentId(), page.getOrder(), page.getThumbnail(), page.getAuthor(), page.getAvailableLanguages());
        mEvent = event;
        mLocation = location;
        mTags = tags;
        mCategories = categories;
    }

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
    public int compareTo(@NonNull Object o) {
        EventPage other = (EventPage) o;
        return Long.valueOf(getEvent().getStartTime()).compareTo(other.getEvent().getStartTime());
    }

    public Event getEvent() {
        return mEvent;
    }

    public List<EventCategory> getCategories() {
        return mCategories;
    }

    public EventLocation getLocation() {
        return mLocation;
    }

    public List<EventTag> getTags() {
        return mTags;
    }

    public static void recreateRelations(List<EventPage> pages, List<EventCategory> categories, List<EventTag> tags, List<AvailableLanguage> languages, Language currentLanguage) {
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

    @Override
    public String getSearchableString() {
        String searchableString = super.getSearchableString();
        if (mCategories != null) {
            searchableString += " " + Objects.join(mCategories);
        }
        if (mTags != null) {
            searchableString += " " + Objects.join(mTags);
        }
        return searchableString;
    }

    @Override
    public long getTimestamp() {
        return super.getTimestamp();
    }
}
