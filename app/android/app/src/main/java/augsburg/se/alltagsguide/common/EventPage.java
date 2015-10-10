package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class EventPage extends Page implements Serializable {

    private Event mEvent;
    private EventLocation mLocation;
    private List<EventTag> mTags;
    private List<EventCategory> mCategories;

    public EventPage(@NonNull Page page, @NonNull Event event, @NonNull EventLocation location, @NonNull List<EventTag> tags, @NonNull List<EventCategory> categories) {
        super(page.getId(), page.getTitle(), page.getType(), page.getStatus(), page.getModified(), page.getTitle(),
                page.getDescription(), page.getParentId(), page.getOrder(), page.getThumbnail(), page.getAuthor(), page.getAvailableLanguages());
        mEvent = event;
        mLocation = location;
        mTags = tags;
        mCategories = categories;
    }

    public static EventPage fromJson(@NonNull final JsonObject jsonPage) {
        Page page = Page.fromJson(jsonPage);
        Event event = Event.fromJson(jsonPage.get("event").getAsJsonObject(), jsonPage.get("page_id").getAsInt());
        EventLocation location = EventLocation.fromJson(jsonPage.get("location").getAsJsonObject());
        List<EventTag> tags = EventTag.fromJson(jsonPage.get("tags").getAsJsonArray());
        List<EventCategory> categories = EventCategory.fromJson(jsonPage.get("categories").getAsJsonArray());
        return new EventPage(page, event, location, tags, categories);
    }


    @Override
    public int compareTo(@NonNull Object o) {

        return 0; //TODO compare by start_date of event
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

}
