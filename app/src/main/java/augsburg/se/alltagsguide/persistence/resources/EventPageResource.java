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

package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;

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
import augsburg.se.alltagsguide.common.UpdateTime;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.utilities.Helper;
import augsburg.se.alltagsguide.utilities.PrefUtilities;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class EventPageResource implements PersistableNetworkResource<EventPage> {
    public static final String PAGE_TYPE_EVENT = "event";
    @NonNull private PageResource mPageResource;
    @NonNull private EventCategoryResource mEventCategoryResource;
    @NonNull private EventTagResource mEventTagResource;
    @NonNull private Language mLanguage;
    @NonNull private Location mLocation;
    @NonNull private NetworkService mNetworkService;
    @NonNull private DatabaseCache mCache;
    @NonNull private PrefUtilities mPreferences;

    private static final long OFFSET_EVENT_VISIBLE = 1000 * 60 * 60 * 24 * 3; //Three days offset

    /**
     * Creation factory
     */
    public interface Factory {
        EventPageResource under(Language lang, Location loc);
    }

    @Inject
    public EventPageResource(@NonNull @Assisted Language language,
                             @NonNull @Assisted Location location,
                             @NonNull NetworkService network,
                             @NonNull DatabaseCache cache, @NonNull PrefUtilities preferences) {
        mLanguage = language;
        mLocation = location;
        mNetworkService = network;
        mPageResource = new PageResource(language, location, network, cache, preferences);
        mEventCategoryResource = new EventCategoryResource(language, location);
        mEventTagResource = new EventTagResource(language, location);
        mCache = cache;
        mPreferences = preferences;
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = getCursorQueryBuilder(getTables()); // applies where location, language filter
        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @NonNull
    private String getTables() {
        return CacheHelper.TABLE_PAGE
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
    }

    @NonNull
    public SQLiteQueryBuilder getCursorQueryBuilder(@NonNull String tables) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tables);
        builder.appendWhere(CacheHelper.PAGE_LANGUAGE + " = " + String.valueOf(mLanguage.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_LOCATION + " = " + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_STATUS + " != " + Helper.quote(PageResource.PAGE_STATUS_TRASH));
        builder.appendWhere(" AND " + CacheHelper.PAGE_TYPE + " = " + Helper.quote(EventPageResource.PAGE_TYPE_EVENT));
        builder.appendWhere(" AND " + CacheHelper.EVENT_END + " > " + String.valueOf(new Date().getTime() - OFFSET_EVENT_VISIBLE));
        return builder;
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = getCursorQueryBuilder(getTables());
        builder.appendWhere(" AND " + CacheHelper.PAGE_ID + "=" + String.valueOf(id));
        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @NonNull
    @Override
    public EventPage loadFrom(@NonNull Cursor cursor) {
        // SELECT statement must include every attribute from page
        Page page = mPageResource.loadFrom(cursor);
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
    public void store(@NonNull SQLiteDatabase writableDatabase, @NonNull List<? extends EventPage> mPages) {
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

    private void storeEvent(@NonNull SQLiteDatabase writableDatabase, @NonNull EventPage page) {
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

    private void storeLocation(@NonNull SQLiteDatabase writableDatabase, @NonNull EventPage page) {
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
                " AND " + CacheHelper.PAGE_TYPE + "=" + Helper.quote(PAGE_TYPE_EVENT);
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
    @NonNull
    public List<EventPage> request() {
        UpdateTime time = new UpdateTime(getLastModificationDate());
        return mNetworkService.getEventPages(mLanguage, mLocation, time);
    }

    @Override
    public boolean shouldUpdate() {
        long lastUpdate = mPreferences.lastEventPageUpdateTime(mLanguage, mLocation);
        long now = new Date().getTime();
        long updateCachingTime = 1000 * 60 * 60 * 4; // 4 hours
        return now - lastUpdate > updateCachingTime;
    }

    @Override
    public void loadedFromNetwork() {
        mPreferences.setLastEventPageUpdateTime(mLanguage, mLocation);
    }
}
