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

import augsburg.se.alltagsguide.common.EventCategory;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.persistence.CacheHelper;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class EventCategoryResource implements PersistableResource<EventCategory> {
    @NonNull private Language mLanguage;
    @NonNull private Location mLocation;

    /**
     * Creation factory
     */
    public interface Factory {
        EventCategoryResource under(Language lang, Location loc);
    }

    @Inject
    public EventCategoryResource(@NonNull @Assisted Language language,
                                 @NonNull @Assisted Location location) {
        mLanguage = language;
        mLocation = location;
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_EVENT_CATEGORY + " join " + CacheHelper.TABLE_CATEGORY + " on "
                + CacheHelper.CATEGORY_ID + "=" + CacheHelper.EVENT_CATEGORY_EVENT_ID);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.EVENT_CATEGORY_LANGUAGE + "=? AND " + CacheHelper.EVENT_CATEGORY_LOCATION + "=?",
                new String[]{String.valueOf(mLanguage.getId()), String.valueOf(mLocation.getId())}, null, null,
                null);
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_EVENT_CATEGORY);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.EVENT_CATEGORY_LANGUAGE + "=? AND " + CacheHelper.EVENT_CATEGORY_LOCATION + "=?" + CacheHelper.EVENT_PAGE_ID + "=?",
                new String[]{String.valueOf(mLanguage.getId()), String.valueOf(mLocation.getId()), String.valueOf(id)}, null, null,
                null);
    }

    @NonNull
    @Override
    public EventCategory loadFrom(@NonNull Cursor cursor, @NonNull SQLiteDatabase db) {
        return EventCategory.loadFrom(cursor);
    }

    @Override
    public void store(@NonNull SQLiteDatabase writableDatabase, @NonNull List<? extends EventCategory> items) {
        throw new IllegalStateException("Should not be called");
    }

    public void store(@NonNull SQLiteDatabase writableDatabase, @NonNull EventPage page) {
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

}
