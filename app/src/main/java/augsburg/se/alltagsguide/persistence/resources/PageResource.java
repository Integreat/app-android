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
import android.support.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public String getType() {
        return "page";
    }


    /**
     * Creation factory
     */
    public interface Factory {
        PageResource under(Language lang, Location loc);
    }

    @NonNull protected final Language mLanguage;
    @NonNull protected final Location mLocation;
    @NonNull protected NetworkService mNetwork;
    @NonNull protected DatabaseCache mCache;
    @NonNull protected AvailableLanguageResource mAvailableLanguageResource;
    @NonNull protected PrefUtilities mPreferences;

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
        return getCursorQueryBuilder(Page.TABLES).query(readableDatabase, null, null, null, null, null, null);
    }

    private SQLiteQueryBuilder getCursorQueryBuilder(String tables) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tables);
        builder.appendWhere(CacheHelper.PAGE_LANGUAGE + " = " + String.valueOf(mLanguage.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_LOCATION + " = " + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_STATUS + " != " + Helper.quote(PAGE_STATUS_TRASH));
        builder.appendWhere(" AND " + CacheHelper.PAGE_TYPE + " = " + Helper.quote(getType()));
        return builder;
    }


    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = getCursorQueryBuilder(Page.TABLES);
        builder.appendWhere(" AND " + CacheHelper.PAGE_ID + "=" + String.valueOf(id));
        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @Nullable
    @Override
    public Page loadFrom(@NonNull Cursor cursor, @NonNull SQLiteDatabase db) {
        return Page.loadFrom(cursor);
    }


    @Override
    public void store(@NonNull SQLiteDatabase db, @NonNull List<? extends Page> mPages) {
        if (mPages.isEmpty()) {
            return;
        }

        ContentValues pageValues = new ContentValues(14);
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
                pageValues.put(CacheHelper.PAGE_AUTO_TRANSLATED, page.isAutoTranslated() ? 1 : 0);
                pageValues.put(CacheHelper.PAGE_PERMALINK, page.getPermalink());
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
                " AND " + CacheHelper.PAGE_TYPE + "=" + Helper.quote(getType());
        Cursor cursor = mCache.executeRawQuery(query, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
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
