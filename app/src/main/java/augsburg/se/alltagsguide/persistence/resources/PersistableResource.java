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
     * @return a single item, read from this row of the cursor
     */
    @Nullable
    E loadFrom(@NonNull Cursor cursor);

    /**
     * Store supplied items in DB, removing or updating prior entries
     *
     * @param writableDatabase
     * @param items
     */
    void store(@NonNull SQLiteDatabase writableDatabase,
               @NonNull List<? extends E> items);

}