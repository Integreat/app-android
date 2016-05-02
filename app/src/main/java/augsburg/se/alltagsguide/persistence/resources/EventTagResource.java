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

import java.util.List;

import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.EventTag;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class EventTagResource implements PersistableResource<EventTag> {
    @NonNull private Language mLanguage;
    @NonNull private Location mLocation;

    /**
     * Creation factory
     */
    public interface Factory {
        EventTagResource under(Language lang, Location loc);
    }

    @Inject
    public EventTagResource(@NonNull @Assisted Language language,
                            @NonNull @Assisted Location location) {
        mLanguage = language;
        mLocation = location;
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_EVENT_TAG);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.EVENT_TAG_LANGUAGE + "=? AND " + CacheHelper.EVENT_TAG_LOCATION + "=?",
                new String[]{String.valueOf(mLanguage.getId()), String.valueOf(mLocation.getId())}, null, null,
                null);
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_EVENT_TAG);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.EVENT_TAG_LANGUAGE + "=? AND " + CacheHelper.EVENT_TAG_LOCATION + "=?" + CacheHelper.EVENT_TAG_EVENT_ID + "=?",
                new String[]{String.valueOf(mLanguage.getId()), String.valueOf(mLocation.getId()), String.valueOf(id)}, null, null,
                null);
    }

    @Nullable
    @Override
    public EventTag loadFrom(@NonNull Cursor cursor) {
        return EventTag.loadFrom(cursor);
    }

    @Override
    public void store(@NonNull SQLiteDatabase writableDatabase, @NonNull List<? extends EventTag> tags) {
        throw new IllegalStateException("Should not be called");
    }

    public void store(@NonNull SQLiteDatabase writableDatabase, @NonNull EventPage page) {
        List<EventTag> tags = page.getTags();
        ContentValues tagValues = new ContentValues(1);
        ContentValues eventTagValues = new ContentValues(5);
        for (EventTag tag : tags) {
            Ln.d("Saving EventTag: " + tag.getName());
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
}
