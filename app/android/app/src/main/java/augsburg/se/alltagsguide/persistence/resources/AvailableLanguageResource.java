package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

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
    private Language mLanguage;
    private Location mLocation;

    /**
     * Creation factory
     */
    public interface Factory {
        AvailableLanguageResource under(Language lang, Location loc);
    }

    @Inject
    public AvailableLanguageResource(@Assisted Language language,
                                     @Assisted Location location) {
        mLanguage = language;
        mLocation = location;
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE_AVAILABLE_LANGUAGE + " JOIN " + CacheHelper.TABLE_LANGUAGE + " ON "
                + CacheHelper.LANGUAGE_SHORT + "=" + CacheHelper.PAGE_AVAIL_OTHER_LANGUAGE);

        builder.appendWhere(CacheHelper.PAGE_AVAIL_PAGE_LOCATION + "=" + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE + "=" + String.valueOf(mLanguage.getId()));
        builder.appendWhere(" AND " + CacheHelper.LANGUAGE_LOCATION + "=" + String.valueOf(mLocation.getId()));
        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase, int pageId) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE_AVAILABLE_LANGUAGE + " join " + CacheHelper.TABLE_LANGUAGE + " on "
                + CacheHelper.LANGUAGE_SHORT + "=" + CacheHelper.PAGE_AVAIL_OTHER_LANGUAGE);

        builder.appendWhere(CacheHelper.PAGE_AVAIL_PAGE_LOCATION + "=" + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE + "=" + String.valueOf(mLanguage.getId()));
        builder.appendWhere(" AND " + CacheHelper.LANGUAGE_LOCATION + "=" + String.valueOf(mLocation.getId()));
        builder.appendWhere(" AND " + CacheHelper.PAGE_AVAIL_PAGE_ID + "=" + String.valueOf(pageId));

        return builder.query(readableDatabase, null, null, null, null, null, null);
    }

    @Override
    public AvailableLanguage loadFrom(Cursor cursor, SQLiteDatabase db) {
        AvailableLanguage availableLanguage = AvailableLanguage.loadFrom(cursor);
        Language language = Language.fromCursor(cursor);
        availableLanguage.setLanguage(language);
        return availableLanguage;
    }

    @Override
    public void store(SQLiteDatabase writableDatabase, List<? extends AvailableLanguage> languages) {
        throw new IllegalStateException("Should not be called");
    }

    public void store(SQLiteDatabase db, Page page) {
        ContentValues languageValues = new ContentValues(5);
        for (AvailableLanguage language : page.getAvailableLanguages()) {
            languageValues.clear();
            languageValues.put(CacheHelper.PAGE_AVAIL_PAGE_ID, page.getId());
            languageValues.put(CacheHelper.PAGE_AVAIL_PAGE_LOCATION, mLocation.getId());
            languageValues.put(CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE, mLanguage.getId());
            languageValues.put(CacheHelper.PAGE_AVAIL_OTHER_PAGE, language.getPageId());
            languageValues.put(CacheHelper.PAGE_AVAIL_OTHER_LANGUAGE, language.getLanguage());
            db.replace(CacheHelper.TABLE_PAGE_AVAILABLE_LANGUAGE, null, languageValues);
        }
    }
}
