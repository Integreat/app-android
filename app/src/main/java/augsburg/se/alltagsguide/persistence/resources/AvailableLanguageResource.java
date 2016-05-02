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

import java.util.List;

import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.persistence.CacheHelper;

/**
 * Created by Daniel-L ON 07.09.2015.
 */
public class AvailableLanguageResource implements PersistableResource<AvailableLanguage> {
    @NonNull private Language mLanguage;
    @NonNull private Location mLocation;

    /**
     * Creation factory
     */
    public interface Factory {
        AvailableLanguageResource under(Language lang, Location loc);
    }

    @Inject
    public AvailableLanguageResource(@NonNull @Assisted Language language,
                                     @NonNull @Assisted Location location) {
        mLanguage = language;
        mLocation = location;
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE_AVAILABLE_LANGUAGE + " JOIN " + CacheHelper.TABLE_LANGUAGE + " ON "
                + CacheHelper.LANGUAGE_SHORT + "=" + CacheHelper.PAGE_AVAIL_OTHER_LANGUAGE);

        builder.appendWhere(CacheHelper.PAGE_AVAIL_PAGE_LOCATION + "=" + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE + "=" + String.valueOf(mLanguage.getId()));
        builder.appendWhere(" AND " + CacheHelper.LANGUAGE_LOCATION + "=" + String.valueOf(mLocation.getId()));
        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @NonNull
    @Override
    public Cursor getCursor(@NonNull SQLiteDatabase readableDatabase, int pageId) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE_AVAILABLE_LANGUAGE + " join " + CacheHelper.TABLE_LANGUAGE + " on "
                + CacheHelper.LANGUAGE_SHORT + "=" + CacheHelper.PAGE_AVAIL_OTHER_LANGUAGE);

        builder.appendWhere(CacheHelper.PAGE_AVAIL_PAGE_LOCATION + "=" + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE + "=" + String.valueOf(mLanguage.getId()));
        builder.appendWhere(" AND " + CacheHelper.LANGUAGE_LOCATION + "=" + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_AVAIL_PAGE_ID + "=" + String.valueOf(pageId));

        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @NonNull
    @Override
    public AvailableLanguage loadFrom(@NonNull Cursor cursor) {
        AvailableLanguage availableLanguage = AvailableLanguage.loadFrom(cursor);
        Language language = Language.fromCursor(cursor);
        availableLanguage.setLanguage(language);
        return availableLanguage;
    }

    @Override
    public void store(@NonNull SQLiteDatabase writableDatabase, @NonNull List<? extends AvailableLanguage> languages) {
        throw new IllegalStateException("Should not be called");
    }

    public void store(@NonNull SQLiteDatabase db, @NonNull Page page) {
        ContentValues languageValues = new ContentValues(5);
        for (AvailableLanguage language : page.getAvailableLanguages()) {
            languageValues.clear();
            languageValues.put(CacheHelper.PAGE_AVAIL_PAGE_ID, page.getId());
            languageValues.put(CacheHelper.PAGE_AVAIL_PAGE_LOCATION, mLocation.getId());
            languageValues.put(CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE, mLanguage.getId());
            languageValues.put(CacheHelper.PAGE_AVAIL_OTHER_PAGE, language.getOtherPageId());
            languageValues.put(CacheHelper.PAGE_AVAIL_OTHER_LANGUAGE, language.getLanguage());
            db.replace(CacheHelper.TABLE_PAGE_AVAILABLE_LANGUAGE, null, languageValues);
        }
    }
}
