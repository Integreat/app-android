package augsburg.se.alltagsguide.persistence.resources;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Describes how to store, load or get-an-update-for a particular set of
 * data.
 *
 * @param <E> type of item
 */
public interface PersistableResource<E> {

    /**
     * @param readableDatabase
     * @return a cursor capable of reading the required information out of the
     * database.
     */
    @NonNull
    Cursor getCursor(@NonNull SQLiteDatabase readableDatabase);

    /**
     * @param readableDatabase
     * @return a cursor capable of reading the required information out of the
     * database.
     */
    @NonNull
    Cursor getCursor(@NonNull SQLiteDatabase readableDatabase, int id);

    /**
     * @param cursor
     * @param db     for having n:m joins
     * @return a single item, read from this row of the cursor
     */
    @Nullable
    E loadFrom(@NonNull Cursor cursor, @NonNull SQLiteDatabase db);

    /**
     * Store supplied items in DB, removing or updating prior entries
     *
     * @param writableDatabase
     * @param items
     */
    void store(@NonNull SQLiteDatabase writableDatabase,
               @NonNull List<? extends E> items);

}