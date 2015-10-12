package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;
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
import augsburg.se.alltagsguide.common.UpdateTime;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.utilities.Helper;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class EventPageResource implements PersistableNetworkResource<EventPage> {
    public static final String PAGE_TYPE_EVENT = "event";
    private PageResource mPageResource;
    private EventCategoryResource mEventCategoryResource;
    private EventTagResource mEventTagResource;
    private Language mLanguage;
    private Location mLocation;
    private NetworkService mNetworkService;
    private DatabaseCache mCache;

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
                             DatabaseCache cache) {
        mLanguage = language;
        mLocation = location;
        mNetworkService = network;
        mPageResource = new PageResource(language, location, network, cache);
        mEventCategoryResource = new EventCategoryResource(language, location);
        mEventTagResource = new EventTagResource(language, location);
        mCache = cache;
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase) {
        String tables = CacheHelper.TABLE_PAGE
                + " left join " + CacheHelper.TABLE_AUTHOR + " ON "
                + CacheHelper.PAGE_AUTHOR + "=" + CacheHelper.AUTHOR_USERNAME
                + " left join " + CacheHelper.TABLE_EVENT + " ON "
                + CacheHelper.PAGE_ID + "=" + CacheHelper.EVENT_PAGE_ID + " AND "
                + CacheHelper.PAGE_LOCATION + "=" + CacheHelper.EVENT_LOCATION + " AND "
                + CacheHelper.PAGE_LANGUAGE + "=" + CacheHelper.EVENT_LANGUAGE
                + " left join " + CacheHelper.TABLE_EVENT_LOCATION + " ON "
                + CacheHelper.PAGE_ID + "=" + CacheHelper.EVENT_LOCATION_PAGE + " AND "
                + CacheHelper.PAGE_LOCATION + "=" + CacheHelper.EVENT_LOCATION + " AND "
                + CacheHelper.PAGE_LANGUAGE + "=" + CacheHelper.EVENT_LANGUAGE;

        SQLiteQueryBuilder builder = getCursorQueryBuilder(tables); // applies where location, language filter
        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    public SQLiteQueryBuilder getCursorQueryBuilder(String tables) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tables);
        builder.appendWhere(CacheHelper.PAGE_LANGUAGE + " = " + String.valueOf(mLanguage.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_LOCATION + " = " + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_STATUS + " != " + Helper.quote(PageResource.PAGE_STATUS_TRASH));
        builder.appendWhere(" AND " + CacheHelper.PAGE_TYPE + " = " + Helper.quote(EventPageResource.PAGE_TYPE_EVENT));
        return builder;
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase, int id) {
        return null;
    }

    @Override
    public EventPage loadFrom(Cursor cursor, SQLiteDatabase db) {
        String[] columnNames = cursor.getColumnNames();
        for (String column : columnNames) {
            Ln.d(column);
        }
        // SELECT statement must include every attribute from page
        Page page = mPageResource.loadFrom(cursor, db);
        if (BuildConfig.DEBUG) {
            if (cursor.isClosed()) {
                throw new IllegalStateException("Cursor should not be closed");
            }
        }
        int eventId = cursor.getInt(cursor.getColumnIndex(CacheHelper.EVENT_ID));
        long startTime = cursor.getLong(cursor.getColumnIndex(CacheHelper.EVENT_START));
        long endTime = cursor.getLong(cursor.getColumnIndex(CacheHelper.EVENT_END));
        boolean allDay = cursor.getInt(cursor.getColumnIndex(CacheHelper.EVENT_ALL_DAY)) == 1;
        int pageId = cursor.getInt(cursor.getColumnIndex(CacheHelper.EVENT_PAGE_ID));
        Event event = new Event(eventId, startTime, endTime, allDay, pageId);
        EventLocation eventLocation = EventLocation.fromCursor(cursor);
        return new EventPage(page, event, eventLocation, new ArrayList<EventTag>(), new ArrayList<EventCategory>());
    }

    @Override
    public void store(SQLiteDatabase writableDatabase, List<? extends EventPage> mPages) {
        if (mPages.isEmpty()) {
            return;
        }
        mPageResource.store(writableDatabase, mPages); // stores pages
        for (EventPage page : mPages) {
            storeLocation(writableDatabase, page);
            storeEvent(writableDatabase, page);
            mEventCategoryResource.store(writableDatabase, page);
            mEventTagResource.store(writableDatabase, page);
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
        if (location != null) {
            ContentValues values = new ContentValues(13);
            values.put(CacheHelper.EVENT_LOCATION_ID, location.getId());
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
    }

    public long getLastModificationDate() {
        long time = 0;
        String query = "SELECT max(" + CacheHelper.PAGE_MODIFIED + ") FROM " + CacheHelper.TABLE_PAGE + " WHERE " +
                CacheHelper.PAGE_LANGUAGE + "=" + String.valueOf(mLanguage.getId()) +
                " AND " + CacheHelper.PAGE_LOCATION + "=" + String.valueOf(mLocation.getId()) +
                " AND " + CacheHelper.PAGE_STATUS + "=" + Helper.quote(PAGE_TYPE_EVENT);
        Cursor cursor = mCache.executeRawQuery(query, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    if (BuildConfig.DEBUG) {
                        if (cursor.getColumnCount() != 1) {
                            throw new IllegalStateException("Cursor column count should be 1");
                        }
                    }
                    time = cursor.getLong(0);
                }
            } finally {
                cursor.close();
            }
        }
        return time;
    }

    @Override
    public List<EventPage> request() {
        UpdateTime time = new UpdateTime(getLastModificationDate());
        return mNetworkService.getEventPages(mLanguage, mLocation, time);
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
