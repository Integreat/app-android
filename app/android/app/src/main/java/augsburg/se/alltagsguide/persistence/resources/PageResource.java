package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import augsburg.se.alltagsguide.common.Author;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;
import augsburg.se.alltagsguide.utilities.PrefUtilities;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class PageResource implements PersistableResource<Page> {
    private static final String PAGE_STATUS_TRASH = "trash";

    /**
     * Creation factory
     */
    public interface Factory {
        PageResource under(Language lang, Location loc);
    }

    private final Language mLanguage;
    private final Location mLocation;
    private NetworkService mNetwork;
    private PrefUtilities mPrefUtilities;

    @Inject
    public PageResource(@Assisted Language language,
                        @Assisted Location location,
                        NetworkService network,
                        PrefUtilities prefUtilities) {
        mLanguage = language;
        mLocation = location;
        mNetwork = network;
        mPrefUtilities = prefUtilities;
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(getTables());

        return builder.query(readableDatabase, new String[]{},
                CacheHelper.PAGE_LANGUAGE + "=? AND " + CacheHelper.PAGE_LOCATION + "=? AND " +
                        CacheHelper.PAGE_STATUS + " != ?",
                new String[]{String.valueOf(mLanguage.getId()), String.valueOf(mLocation.getId()), PAGE_STATUS_TRASH}, null, null,
                null);
    }

    private String getTables() {
        return CacheHelper.TABLE_PAGE
                + " join " + CacheHelper.TABLE_AUTHOR + " ON " + CacheHelper.PAGE_AUTHOR + "=" + CacheHelper.AUTHOR_USERNAME;
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(getTables());
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.PAGE_LANGUAGE + "=? AND " + CacheHelper.PAGE_LOCATION + "=? AND " +
                        CacheHelper.PAGE_ID + "=?",
                new String[]{String.valueOf(mLanguage.getId()), String.valueOf(mLocation.getId()), String.valueOf(id)}, null, null,
                null);
    }

    @Override
    public Page loadFrom(Cursor cursor, SQLiteDatabase db) {
        int index = 0;
        int id = cursor.getInt(index++); // 1
        String title = cursor.getString(index++); // 2
        String type = cursor.getString(index++); // 3
        String status = cursor.getString(index++); // 4
        String modified = cursor.getString(index++); // 5
        String description = cursor.getString(index++); // 6
        String content = cursor.getString(index++); // 7
        int parentId = cursor.getInt(index++); // 8
        int order = cursor.getInt(index++); // 9
        String thumbnail = cursor.getString(index++); // 10

        Author author = Author.fromCursor(cursor, index + 2); // TODO 2 = Language, Location - SELECT ONLY TO THE OTHER FIELDS!
        List<AvailableLanguage> languages = getAvailableLanguages(db, id);
        return new Page(id, title, type, status, modified, description, content, parentId, order, thumbnail, author, languages);
    }

    private Cursor getAvailableLanguagesCursor(SQLiteDatabase readableDatabase, int pageId) {
        //TODO testen!!
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE_AVAILABLE_LANGUAGE);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE + "=? AND " + CacheHelper.PAGE_AVAIL_PAGE_LOCATION + "=? AND " +
                        CacheHelper.PAGE_AVAIL_PAGE_ID + "=?",
                new String[]{String.valueOf(mLanguage.getId()), String.valueOf(mLocation.getId()), String.valueOf(pageId)}, null, null,
                null);
    }

    public List<AvailableLanguage> getAvailableLanguages(SQLiteDatabase db, int pageId) {
        Cursor cursor = getAvailableLanguagesCursor(db, pageId);
        List<AvailableLanguage> cached = new ArrayList<>();
        do {
            cached.add(AvailableLanguage.loadFrom(cursor));
        }
        while (cursor.moveToNext());
        return cached;
    }

    @Override
    public void store(SQLiteDatabase db, List<? extends Page> mPages) {
        if (mPages.isEmpty()) {
            return;
        }

        ContentValues pageValues = new ContentValues(14);
        ContentValues authorValues = new ContentValues(3);
        ContentValues languageValues = new ContentValues(5);
        for (Page mPage : mPages) {
            List<Page> pages = new ArrayList<>();
            pages.add(mPage);
            if (mPage.getSubPages() != null) { //TODO check recursive stuff
                pages.addAll(mPage.getSubPages());
            }
            for (Page page : pages) {
                pageValues.clear();
                pageValues.put(CacheHelper.PAGE_ID, page.getId()); //1
                pageValues.put(CacheHelper.PAGE_TITLE, page.getTitle()); //2
                pageValues.put(CacheHelper.PAGE_TYPE, page.getType()); //3
                pageValues.put(CacheHelper.PAGE_STATUS, page.getStatus()); //4
                pageValues.put(CacheHelper.PAGE_MODIFIED, page.getModified()); //5
                pageValues.put(CacheHelper.PAGE_DESCRIPTION, page.getDescription()); //6
                pageValues.put(CacheHelper.PAGE_CONTENT, page.getContent()); //7
                pageValues.put(CacheHelper.PAGE_PARENT_ID, page.getParentId()); //8
                pageValues.put(CacheHelper.PAGE_ORDER, page.getOrder()); //9
                pageValues.put(CacheHelper.PAGE_THUMBNAIL, page.getThumbnail()); //11
                pageValues.put(CacheHelper.PAGE_LOCATION, mLocation.getId()); //12
                pageValues.put(CacheHelper.PAGE_LANGUAGE, mLanguage.getId()); //13
                pageValues.put(CacheHelper.PAGE_AUTHOR, page.getAuthor().getLogin()); //14
                db.replace(CacheHelper.TABLE_PAGE, null, pageValues);

                authorValues.clear();
                authorValues.put(CacheHelper.AUTHOR_USERNAME, page.getAuthor().getLogin()); // 1
                authorValues.put(CacheHelper.AUTHOR_FIRSTNAME, page.getAuthor().getFirstName()); // 2
                authorValues.put(CacheHelper.AUTHOR_LASTNAME, page.getAuthor().getLastName()); // 3
                db.replace(CacheHelper.TABLE_AUTHOR, null, authorValues);

                for (AvailableLanguage language : page.getAvailableLanguages()) {
                    languageValues.clear();
                    languageValues.put(CacheHelper.PAGE_AVAIL_PAGE_ID, page.getId()); // 1
                    languageValues.put(CacheHelper.PAGE_AVAIL_PAGE_LOCATION, mLocation.getId()); // 2
                    languageValues.put(CacheHelper.PAGE_AVAIL_PAGE_LANGUAGE, mLanguage.getId()); // 3
                    languageValues.put(CacheHelper.PAGE_AVAIL_OTHER_PAGE, language.getPageId()); // 4
                    languageValues.put(CacheHelper.PAGE_AVAIL_OTHER_LANGUAGE, language.getLanguage()); // 5
                    db.replace(CacheHelper.TABLE_PAGE_AVAILABLE_LANGUAGE, null, languageValues);
                }
            }
        }
        mPrefUtilities.setUpdateTime(mLocation, mLanguage, new Date().getTime());
    }

    @Override
    public List<Page> request() {
        return mNetwork.getPages(mLanguage, mLocation, mPrefUtilities.getUpdateTime(mLocation, mLanguage));
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
