package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.common.UpdateTime;
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
        builder.setTables(CacheHelper.TABLE_PAGE);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.PAGE_LANGUAGE + "=? AND " + CacheHelper.PAGE_LOCATION + "=? AND " +
                        CacheHelper.PAGE_STATUS + " != ?",
                new String[]{String.valueOf(mLanguage.getId()), String.valueOf(mLocation.getId()), PAGE_STATUS_TRASH}, null, null,
                null);
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.PAGE_LANGUAGE + "=? AND " + CacheHelper.PAGE_LOCATION + "=? AND " +
                        CacheHelper.PAGE_ID + "=?",
                new String[]{String.valueOf(mLanguage.getId()), String.valueOf(mLocation.getId()), String.valueOf(id)}, null, null,
                null);
    }

    @Override
    public Page loadFrom(Cursor cursor) {
        int index = 0;
        int id = cursor.getInt(index++);
        String title = cursor.getString(index++);
        String type = cursor.getString(index++);
        String status = cursor.getString(index++);
        String modified = cursor.getString(index++);
        String description = cursor.getString(index++);
        String content = cursor.getString(index++);
        int parentId = cursor.getInt(index++);
        int order = cursor.getInt(index++);
        String availableLanguages = cursor.getString(index++);

        return new Page(id, title, type, status, modified, description, content, parentId, order, availableLanguages);
    }

    @Override
    public void store(SQLiteDatabase db, List<Page> mPages) {
        if (mPages.isEmpty()) {
            return;
        }

        ContentValues values = new ContentValues(12);
        for (Page mPage : mPages) {
            List<Page> pages = new ArrayList<>();
            pages.add(mPage);
            if (mPage.getSubPages() != null) {
                pages.addAll(mPage.getSubPages());
            }
            for (Page page : pages) {
                values.clear();
                values.put(CacheHelper.PAGE_ID, page.getId()); //1
                values.put(CacheHelper.PAGE_TITLE, page.getTitle()); //2
                values.put(CacheHelper.PAGE_TYPE, page.getType()); //3
                values.put(CacheHelper.PAGE_STATUS, page.getStatus()); //4
                values.put(CacheHelper.PAGE_MODIFIED, page.getModified()); //5
                values.put(CacheHelper.PAGE_DESCRIPTION, page.getDescription()); //6
                values.put(CacheHelper.PAGE_CONTENT, page.getContent()); //7
                values.put(CacheHelper.PAGE_PARENT_ID, page.getParentId()); //8
                values.put(CacheHelper.PAGE_ORDER, page.getOrder()); //9
                values.put(CacheHelper.PAGE_AVAILABLE_LANGUAGES, page.getAvailableLanguages()); //10
                values.put(CacheHelper.PAGE_LOCATION, mLocation.getId()); //11
                values.put(CacheHelper.PAGE_LANGUAGE, mLanguage.getId()); //12

                db.replace(CacheHelper.TABLE_PAGE, null, values);
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
