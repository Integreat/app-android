package augsburg.se.alltagsguide.persistence;

/**
 * Created by Daniel-L on 01.09.2015.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.persistence.resources.PersistableNetworkResource;
import augsburg.se.alltagsguide.persistence.resources.PersistableResource;
import roboguice.util.Ln;

/**
 * Given a PersistableNetworkResource, this class will take support loading/storing
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
    @Nullable
    protected SQLiteDatabase getWritable(@NonNull SQLiteOpenHelper helper) {
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


    @Nullable
    public Cursor executeRawQuery(@NonNull String query, String[] args) {
        SQLiteOpenHelper helper = helperProvider.get();
        final SQLiteDatabase db = getReadable(helper);
        if (db == null) {
            return null;
        }
        return db.rawQuery(query, args);
    }

    /**
     * Get readable database
     *
     * @param helper
     * @return readable database or null if it failed to create/open
     */
    @Nullable
    protected SQLiteDatabase getReadable(@NonNull SQLiteOpenHelper helper) {
        try {
            return helper.getReadableDatabase();
        } catch (SQLiteException e1) {
            Ln.e(e1);
            // Make second attempt
            try {
                return helper.getReadableDatabase();
            } catch (SQLiteException e2) {
                Ln.e(e2);
                return null;
            }
        }
    }

    @NonNull
    public <E> List<E> load(@NonNull PersistableResource<E> persistableResource) {
        SQLiteOpenHelper helper = helperProvider.get();
        return loadFromDB(helper, persistableResource);
    }

    @Nullable
    public <E> E load(@NonNull PersistableResource<E> persistableResource, int id) {
        SQLiteOpenHelper helper = helperProvider.get();
        return loadFromDB(helper, persistableResource, id);
    }

    /**
     * Load or get given resources
     *
     * @param persistableResource
     * @return resource
     * @throws IOException
     */
    @Deprecated
    @NonNull
    public <E> List<E> loadOrRequestDeprecated(@NonNull PersistableNetworkResource<E> persistableResource)
            throws IOException {
        SQLiteOpenHelper helper = helperProvider.get();
        try {
            List<E> items = loadFromDB(helper, persistableResource);
            if (!items.isEmpty()) {
                if (persistableResource.shouldUpdate()) {
                    Ln.d("Items exist in database. ShouldUpdate is true, so check network data first");
                    List<? extends E> newItems = requestAndStore(helper, persistableResource);
                    if (!newItems.isEmpty()) {
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


    @NonNull
    public <E> List<E> loadOrRequest(@NonNull PersistableNetworkResource<E> persistableResource)
            throws IOException {
        SQLiteOpenHelper helper = helperProvider.get();
        try {
            if (!persistableResource.shouldUpdate()) {
                Ln.d("Should update is false, try to load data from database");
                // should not update, so check database
                List<E> items = loadFromDB(helper, persistableResource);
                if (!items.isEmpty()) {
                    Ln.d("Database has items. Return them.");
                    return items;
                }
                Ln.d("Database has no items.");
            }
            Ln.d("database did not have any items, load them from network");
            requestAndStore(helper, persistableResource);
            Ln.d("Return merged (database + network) data");
            return loadFromDB(helper, persistableResource);
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
    @NonNull
    public <E> List<E> requestAndStore(@NonNull PersistableNetworkResource<E> persistableResource) throws IOException {
        SQLiteOpenHelper helper = helperProvider.get();
        try {
            return requestAndStore(helper, persistableResource);
        } finally {
            helper.close();
        }
    }

    @NonNull
    private <E> List<E> requestAndStore(@NonNull final SQLiteOpenHelper helper,
                                        @NonNull final PersistableNetworkResource<E> persistableResource)
            throws IOException {
        final List<E> items = persistableResource.request();
        if (items.isEmpty()) {
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
            persistableResource.loadedFromNetwork();
        } finally {
            db.endTransaction();
        }
        return items;
    }

    @NonNull
    private <E> List<E> loadFromDB(@NonNull final SQLiteOpenHelper helper,
                                   @NonNull final PersistableResource<E> persistableResource) {
        final SQLiteDatabase db = getReadable(helper);
        if (db == null) {
            Ln.d("SQLiteDatabase is null");
            return new ArrayList<>();
        }

        Cursor cursor = persistableResource.getCursor(db);
        try {
            if (!cursor.moveToFirst()) {
                return new ArrayList<>();
            }

            List<E> cached = new ArrayList<>();
            do {
                cached.add(persistableResource.loadFrom(cursor, db));
            }
            while (cursor.moveToNext());
            return cached;
        } finally {
            cursor.close();
        }
    }

    @Nullable
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
            return persistableResource.loadFrom(cursor, db);
        } finally {
            cursor.close();
        }
    }
}