package augsburg.se.alltagsguide.persistence;

/**
 * Created by Daniel-L on 01.09.2015.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.persistence.resources.PersistableResource;
import roboguice.util.Ln;

/**
 * Given a PersistableResource, this class will take support loading/storing
 * it's data or requesting fresh data, as appropriate.
 */
public class DatabaseCache {

    @Inject
    private Provider<CacheHelper> helperProvider;

    /**
     * Get writable database
     *
     * @param helper
     * @return writable database or null if it failed to create/open
     */
    protected SQLiteDatabase getWritable(SQLiteOpenHelper helper) {
        try {
            return helper.getWritableDatabase();
        } catch (SQLiteException e1) {
            // Make second attempt
            try {
                return helper.getWritableDatabase();
            } catch (SQLiteException e2) {
                return null;
            }
        }
    }

    /**
     * Get readable database
     *
     * @param helper
     * @return readable database or null if it failed to create/open
     */
    protected SQLiteDatabase getReadable(SQLiteOpenHelper helper) {
        try {
            return helper.getReadableDatabase();
        } catch (SQLiteException e1) {
            // Make second attempt
            try {
                return helper.getReadableDatabase();
            } catch (SQLiteException e2) {
                return null;
            }
        }
    }

    public <E> List<E> load(PersistableResource<E> persistableResource) {
        SQLiteOpenHelper helper = helperProvider.get();
        return loadFromDB(helper, persistableResource);
    }

    public <E> E load(PersistableResource<E> persistableResource, int id) {
        SQLiteOpenHelper helper = helperProvider.get();
        return loadFromDB(helper, persistableResource, id);
    }

    /**
     * Load or request given resources
     *
     * @param persistableResource
     * @return resource
     * @throws IOException
     */
    public <E> List<E> loadOrRequest(PersistableResource<E> persistableResource)
            throws IOException {
        SQLiteOpenHelper helper = helperProvider.get();
        try {
            List<E> items = loadFromDB(helper, persistableResource);
            if (items != null && !items.isEmpty()) {
                if (persistableResource.shouldUpdate()) {
                    Ln.d("Items exist in database. ShouldUpdate is true, so check network data first");
                    List<E> newItems = requestAndStore(helper, persistableResource);
                    if (newItems != null && !newItems.isEmpty()) {
                        Ln.d("shouldUpdate is true and new requested items are not null -> save and return new date");
                        return loadFromDB(helper, persistableResource);
                    }
                    Ln.d("shouldUpdate is true but no new requested items -> return data from database");
                }
                Ln.d("CACHE HIT: Found %d items for %s", items.size(), persistableResource);
                return items;
            }
            return requestAndStore(helper, persistableResource);
        } finally {
            helper.close();
        }
    }

    /**
     * Request and store given resources
     *
     * @param persistableResource
     * @return resources
     * @throws IOException
     */
    public <E> List<E> requestAndStore(
            PersistableResource<E> persistableResource) throws IOException {
        SQLiteOpenHelper helper = helperProvider.get();
        try {
            return requestAndStore(helper, persistableResource);
        } finally {
            helper.close();
        }
    }

    private <E> List<E> requestAndStore(final SQLiteOpenHelper helper,
                                        final PersistableResource<E> persistableResource)
            throws IOException {
        final List<E> items = persistableResource.request();
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        final SQLiteDatabase db = getWritable(helper);
        if (db == null) {
            return items;
        }

        db.beginTransaction();
        try {
            persistableResource.store(db, items);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return items;
    }

    private <E> List<E> loadFromDB(final SQLiteOpenHelper helper,
                                   final PersistableResource<E> persistableResource) {
        final SQLiteDatabase db = getReadable(helper);
        if (db == null)
            return null;

        Cursor cursor = persistableResource.getCursor(db);
        try {
            if (!cursor.moveToFirst()) {
                return new ArrayList<>();
            }

            List<E> cached = new ArrayList<>();
            do {
                cached.add(persistableResource.loadFrom(cursor));
            }
            while (cursor.moveToNext());
            return cached;
        } finally {
            cursor.close();
        }
    }

    private <E> E loadFromDB(final SQLiteOpenHelper helper,
                             final PersistableResource<E> persistableResource, int id) {
        final SQLiteDatabase db = getReadable(helper);
        if (db == null)
            return null;

        Cursor cursor = persistableResource.getCursor(db, id);
        try {
            if (!cursor.moveToFirst()) {
                return null;
            }
            return persistableResource.loadFrom(cursor);
        } finally {
            cursor.close();
        }
    }
}