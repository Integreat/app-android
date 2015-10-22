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
import augsburg.se.alltagsguide.common.Author;
import augsburg.se.alltagsguide.common.AvailableLanguage;
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
public class PageResource implements PersistableNetworkResource<Page> {
    public static final String PAGE_STATUS_TRASH = "trash";
    public static final String PAGE_TYPE = "page";

    /**
     * Creation factory
     */
    public interface Factory {
        PageResource under(Language lang, Location loc);
    }

    @NonNull private final Language mLanguage;
    @NonNull private final Location mLocation;
    @NonNull private NetworkService mNetwork;
    @NonNull private DatabaseCache mCache;
    @NonNull private AvailableLanguageResource mAvailableLanguageResource;
    @NonNull private PrefUtilities mPreferences;

    @Inject
    public PageResource(@NonNull @Assisted Language language,
                        @NonNull @Assisted Location location,
                        @NonNull NetworkService network,
                        @NonNull DatabaseCache cache, @NonNull PrefUtilities preferences) {
        mLanguage = language;
        mLocation = location;
        mNetwork = network;
        mCache = cache;
        mPreferences = preferences;
        mAvailableLanguageResource = new AvailableLanguageResource(mLanguage, mLocation);
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase) {
        return getCursorQueryBuilder(getTables()).query(readableDatabase, null, null, null, null, null, null);
    }

    private SQLiteQueryBuilder getCursorQueryBuilder(String tables) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tables);
        builder.appendWhere(CacheHelper.PAGE_LANGUAGE + " = " + String.valueOf(mLanguage.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_LOCATION + " = " + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_STATUS + " != " + Helper.quote(PAGE_STATUS_TRASH));
        builder.appendWhere(" AND " + CacheHelper.PAGE_TYPE + " = " + Helper.quote(PAGE_TYPE));
        return builder;
    }


    @NonNull
    private String getTables() {
        return CacheHelper.TABLE_PAGE
                + " join " + CacheHelper.TABLE_AUTHOR + " ON " + CacheHelper.PAGE_AUTHOR + " = " + CacheHelper.AUTHOR_USERNAME;
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
    public Page loadFrom(@NonNull Cursor cursor, @NonNull SQLiteDatabase db) {
        int id = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_ID));
        String title = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_TITLE));
        String type = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_TYPE));
        String status = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_STATUS));
        long modified = cursor.getLong(cursor.getColumnIndex(CacheHelper.PAGE_MODIFIED));
        String description = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_DESCRIPTION));
        String content = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_CONTENT));
        int parentId = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_PARENT_ID));
        int order = cursor.getInt(cursor.getColumnIndex(CacheHelper.PAGE_ORDER));
        String thumbnail = cursor.getString(cursor.getColumnIndex(CacheHelper.PAGE_THUMBNAIL));

        Author author = Author.fromCursor(cursor);
        return new Page(id, title, type, status, modified, description, content, parentId, order, thumbnail, author, new ArrayList<AvailableLanguage>());
    }

    @Override
    public void store(@NonNull SQLiteDatabase db, @NonNull List<? extends Page> mPages) {
        if (mPages.isEmpty()) {
            return;
        }

        ContentValues pageValues = new ContentValues(13);
        ContentValues authorValues = new ContentValues(3);
        for (Page mPage : mPages) {
            List<Page> pages = new ArrayList<>();
            pages.add(mPage);
            pages.addAll(mPage.getSubPages());
            for (Page page : pages) {
                pageValues.clear();
                pageValues.put(CacheHelper.PAGE_ID, page.getId());
                pageValues.put(CacheHelper.PAGE_TITLE, page.getTitle());
                pageValues.put(CacheHelper.PAGE_TYPE, page.getType());
                pageValues.put(CacheHelper.PAGE_STATUS, page.getStatus());
                pageValues.put(CacheHelper.PAGE_MODIFIED, page.getModified());
                pageValues.put(CacheHelper.PAGE_DESCRIPTION, page.getDescription());
                pageValues.put(CacheHelper.PAGE_CONTENT, page.getContent());
                pageValues.put(CacheHelper.PAGE_PARENT_ID, page.getParentId());
                pageValues.put(CacheHelper.PAGE_ORDER, page.getOrder());
                pageValues.put(CacheHelper.PAGE_THUMBNAIL, page.getThumbnail());
                pageValues.put(CacheHelper.PAGE_LOCATION, mLocation.getId());
                pageValues.put(CacheHelper.PAGE_LANGUAGE, mLanguage.getId());
                pageValues.put(CacheHelper.PAGE_AUTHOR, page.getAuthor().getLogin());
                db.replace(CacheHelper.TABLE_PAGE, null, pageValues);

                authorValues.clear();
                authorValues.put(CacheHelper.AUTHOR_USERNAME, page.getAuthor().getLogin());
                authorValues.put(CacheHelper.AUTHOR_FIRSTNAME, page.getAuthor().getFirstName());
                authorValues.put(CacheHelper.AUTHOR_LASTNAME, page.getAuthor().getLastName());
                db.replace(CacheHelper.TABLE_AUTHOR, null, authorValues);

                mAvailableLanguageResource.store(db, page);
            }
        }
    }

    public long getLastModificationDate() {
        long time = 0;
        String query = "SELECT max(" + CacheHelper.PAGE_MODIFIED + ") FROM " + CacheHelper.TABLE_PAGE + " WHERE " +
                CacheHelper.PAGE_LANGUAGE + "=" + String.valueOf(mLanguage.getId()) +
                " AND " + CacheHelper.PAGE_LOCATION + "=" + String.valueOf(mLocation.getId()) +
                " AND " + CacheHelper.PAGE_STATUS + " NOT IN (" + Helper.quote(PAGE_STATUS_TRASH) + "," + Helper.quote(EventPageResource.PAGE_TYPE_EVENT) + ")";
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

    @NonNull
    @Override
    public List<Page> request() {
        UpdateTime time = new UpdateTime(getLastModificationDate());
        return mNetwork.getPages(mLanguage, mLocation, time);
    }

    @Override
    public boolean shouldUpdate() {
        long lastUpdate = mPreferences.lastPageUpdateTime(mLanguage, mLocation);
        long now = new Date().getTime();
        long updateCachingTime = 1000 * 60 * 60 * 4; // 4 hours
        return now - lastUpdate > updateCachingTime;
    }

    @Override
    public void loadedFromNetwork() {
        mPreferences.setLastPageUpdateTime(mLanguage, mLocation);
    }
}
