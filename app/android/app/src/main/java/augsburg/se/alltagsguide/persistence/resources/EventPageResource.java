package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import augsburg.se.alltagsguide.BuildConfig;
import augsburg.se.alltagsguide.common.Event;
import augsburg.se.alltagsguide.common.EventCategory;
import augsburg.se.alltagsguide.common.EventLocation;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.EventTag;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.PrefUtilities;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class EventPageResource implements PersistableResource<EventPage> {
    private PageResource mPageResource;
    private Language mLanguage;
    private Location mLocation;
    private PrefUtilities mPrefUtilities;
    private NetworkService mNetworkService;

    /**
     * Creation factory
     */
    public interface Factory {
        EventPageResource under(Language lang, Location loc);
    }

    @Inject
    public EventPageResource(@Assisted Language language,
                             @Assisted Location location,
                             NetworkService network,
                             PrefUtilities prefUtilities) {
        mLanguage = language;
        mLocation = location;
        mNetworkService = network;
        mPrefUtilities = prefUtilities;
        mPageResource = new PageResource(language, location, network, prefUtilities);
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE + "," +
                CacheHelper.TABLE_EVENT + "," +
                CacheHelper.TABLE_CATEGORY + "," +
                CacheHelper.TABLE_EVENT_CATEGORY + "," +
                CacheHelper.TABLE_EVENT_LOCATION + "," +
                CacheHelper.TABLE_EVENT_TAG + "," +
                CacheHelper.TABLE_TAG + "," +
                CacheHelper.TABLE_PAGE_AVAILABLE_LANGUAGE + "," +
                CacheHelper.TABLE_AUTHOR);

        builder.appendWhere(CacheHelper.PAGE_AUTHOR + "=" + CacheHelper.AUTHOR_USERNAME); // connect page and author

        builder.appendWhere(CacheHelper.PAGE_ID + "=" + CacheHelper.EVENT_PAGE);
        builder.appendWhere(CacheHelper.PAGE_ID + "=" + CacheHelper.EVENT_CATEGORY_PAGE);
        builder.appendWhere(CacheHelper.PAGE_ID + "=" + CacheHelper.EVENT_LOCATION_PAGE);
        builder.appendWhere(CacheHelper.PAGE_ID + "=" + CacheHelper.EVENT_TAG_PAGE);
        builder.appendWhere(CacheHelper.PAGE_ID + "=" + CacheHelper.PAGE_AVAIL_PAGE_ID);

        builder.appendWhere(CacheHelper.PAGE_LANGUAGE + "=" + CacheHelper.EVENT_LANGUAGE);
        builder.appendWhere(CacheHelper.PAGE_LANGUAGE + "=" + CacheHelper.EVENT_CATEGORY_LANGUAGE);
        builder.appendWhere(CacheHelper.PAGE_LANGUAGE + "=" + CacheHelper.EVENT_LOCATION_LANGUAGE);
        builder.appendWhere(CacheHelper.PAGE_LANGUAGE + "=" + CacheHelper.EVENT_TAG_LANGUAGE);
        builder.appendWhere(CacheHelper.PAGE_LANGUAGE + "=" + CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE);

        builder.appendWhere(CacheHelper.PAGE_LOCATION + "=" + CacheHelper.EVENT_LOCATION);
        builder.appendWhere(CacheHelper.PAGE_LOCATION + "=" + CacheHelper.EVENT_CATEGORY_LOCATION);
        builder.appendWhere(CacheHelper.PAGE_LOCATION + "=" + CacheHelper.EVENT_LOCATION_LOCATION);
        builder.appendWhere(CacheHelper.PAGE_LOCATION + "=" + CacheHelper.EVENT_TAG_LOCATION);
        builder.appendWhere(CacheHelper.PAGE_LOCATION + "=" + CacheHelper.PAGE_AVAIL_PAGE_LOCATION);

        builder.appendWhere(CacheHelper.TAG_NAME + "=" + CacheHelper.EVENT_TAG_NAME);
        builder.appendWhere(CacheHelper.CATEGORY_ID + "=" + CacheHelper.EVENT_CATEGORY_ID);

        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase, int id) {
        return null;
    }

    @Override
    public EventPage loadFrom(Cursor cursor, SQLiteDatabase db) {
        // SELECT statement must include every attribute from page
        Page page = mPageResource.loadFrom(cursor, db);
        if (BuildConfig.DEBUG) {
            if (!cursor.isClosed()) {
                throw new IllegalStateException("Cursor should not be closed");
            }
        }
        int index = CacheHelper.PAGE_OFFSET;
        int id = cursor.getInt(index++);
        long starttime = cursor.getLong(index++);
        long endtime = cursor.getLong(index++);
        boolean allDay = cursor.getInt(index++) == 1;
        int pageId = cursor.getInt(index++);
        Event event = new Event(id, starttime, endtime, allDay, pageId);
        EventLocation eventLocation = EventLocation.fromCursor(cursor, index);

        List<EventCategory> eventCategories = new ArrayList<>(); //TODO
        List<EventTag> eventTags = new ArrayList<>(); //TODO

        return new EventPage(page, event, eventLocation, eventTags, eventCategories);
    }

    @Override
    public void store(SQLiteDatabase writableDatabase, List<? extends EventPage> mPages) {
        if (mPages.isEmpty()) {
            return;
        }
        mPageResource.store(writableDatabase, mPages); // stores pages
        for (EventPage page : mPages) {
            storeCategories(writableDatabase, page);
            storeLocation(writableDatabase, page);
            storeEvent(writableDatabase, page);
            storeTags(writableDatabase, page);
        }
        mPrefUtilities.setEventUpdateTime(mLocation, mLanguage, new Date().getTime());
    }

    private void storeTags(SQLiteDatabase writableDatabase, EventPage page) {
        List<EventTag> tags = page.getTags();
        ContentValues tagValues = new ContentValues(1);
        ContentValues eventTagValues = new ContentValues(5);
        for (EventTag tag : tags) {
            tagValues.clear();
            tagValues.put(CacheHelper.TAG_NAME, tag.getName());
            writableDatabase.replace(CacheHelper.TABLE_TAG, null, tagValues);

            eventTagValues.clear();
            eventTagValues.put(CacheHelper.EVENT_TAG_NAME, tag.getName());
            eventTagValues.put(CacheHelper.EVENT_TAG_LOCATION, mLocation.getId());
            eventTagValues.put(CacheHelper.EVENT_TAG_LANGUAGE, mLanguage.getId());
            eventTagValues.put(CacheHelper.EVENT_TAG_PAGE, page.getId());
            eventTagValues.put(CacheHelper.EVENT_TAG_EVENT_ID, page.getEvent().getId());
            writableDatabase.replace(CacheHelper.TABLE_EVENT_TAG, null, eventTagValues);
        }
    }

    private void storeEvent(SQLiteDatabase writableDatabase, EventPage page) {
        Event event = page.getEvent();

        ContentValues values = new ContentValues(8);
        values.put(CacheHelper.EVENT_ID, event.getId());
        values.put(CacheHelper.EVENT_START, event.getStartTime());
        values.put(CacheHelper.EVENT_END, event.getEndTime());
        values.put(CacheHelper.EVENT_ALL_DAY, event.isAllDay() ? 1 : 0);
        values.put(CacheHelper.EVENT_LOCATION, mLocation.getId());
        values.put(CacheHelper.EVENT_LANGUAGE, mLanguage.getId());
        values.put(CacheHelper.EVENT_PAGE, page.getId());
        values.put(CacheHelper.EVENT_PAGE_ID, event.getPageId());
        writableDatabase.replace(CacheHelper.TABLE_EVENT, null, values);
    }

    private void storeLocation(SQLiteDatabase writableDatabase, EventPage page) {
        EventLocation location = page.getLocation();

        ContentValues values = new ContentValues(12);
        values.put(CacheHelper.EVENT_LOCATION_NAME, location.getName());
        values.put(CacheHelper.EVENT_LOCATION_ADDRESS, location.getAddress());
        values.put(CacheHelper.EVENT_LOCATION_TOWN, location.getTown());
        values.put(CacheHelper.EVENT_LOCATION_STATE, location.getState());
        values.put(CacheHelper.EVENT_LOCATION_POSTCODE, location.getPostcode());
        values.put(CacheHelper.EVENT_LOCATION_REGION, location.getRegion());
        values.put(CacheHelper.EVENT_LOCATION_COUNTRY, location.getCountry());
        values.put(CacheHelper.EVENT_LOCATION_LATITUDE, location.getLatitude());
        values.put(CacheHelper.EVENT_LOCATION_LONGITUDE, location.getLongitude());
        values.put(CacheHelper.EVENT_LOCATION_PAGE, page.getId());
        values.put(CacheHelper.EVENT_LOCATION_LOCATION, mLocation.getId());
        values.put(CacheHelper.EVENT_LOCATION_LANGUAGE, mLanguage.getId());
        writableDatabase.replace(CacheHelper.TABLE_EVENT_LOCATION, null, values);
    }

    private void storeCategories(SQLiteDatabase writableDatabase, EventPage page) {
        List<EventCategory> categories = page.getCategories();

        ContentValues categoryValues = new ContentValues(3);
        ContentValues eventCategoryValues = new ContentValues(5);
        for (EventCategory category : categories) {
            categoryValues.clear();
            categoryValues.put(CacheHelper.CATEGORY_ID, category.getId());
            categoryValues.put(CacheHelper.CATEGORY_NAME, category.getName());
            categoryValues.put(CacheHelper.CATEGORY_PARENT, category.getParent());
            writableDatabase.replace(CacheHelper.TABLE_CATEGORY, null, categoryValues);

            eventCategoryValues.clear();
            eventCategoryValues.put(CacheHelper.EVENT_CATEGORY_ID, category.getId());
            eventCategoryValues.put(CacheHelper.EVENT_CATEGORY_EVENT_ID, page.getEvent().getId());
            eventCategoryValues.put(CacheHelper.EVENT_CATEGORY_LANGUAGE, mLanguage.getId());
            eventCategoryValues.put(CacheHelper.EVENT_CATEGORY_LOCATION, mLocation.getId());
            eventCategoryValues.put(CacheHelper.EVENT_CATEGORY_PAGE, page.getId());
            writableDatabase.replace(CacheHelper.TABLE_EVENT_CATEGORY, null, eventCategoryValues);
        }
    }


    @Override
    public List<EventPage> request() {
        return mNetworkService.getEventPages(mLanguage, mLocation, mPrefUtilities.getUpdateTime(mLocation, mLanguage));
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
