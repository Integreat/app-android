package augsburg.se.alltagsguide.persistence.resources;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.List;

/**
 * Describes how to store, load or request-an-update-for a particular set of
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
    Cursor getCursor(SQLiteDatabase readableDatabase);

    /**
     * @param readableDatabase
     * @return a cursor capable of reading the required information out of the
     * database.
     */
    Cursor getCursor(SQLiteDatabase readableDatabase, int id);

    /**
     * @param cursor
     * @param db     for having n:m joins
     * @return a single item, read from this row of the cursor
     */
    E loadFrom(Cursor cursor, SQLiteDatabase db);

    /**
     * Store supplied items in DB, removing or updating prior entries
     *
     * @param writableDatabase
     * @param items
     */
    void store(SQLiteDatabase writableDatabase, List<? extends E> items);

}