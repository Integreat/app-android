package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.List;

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
                        NetworkService network, PrefUtilities prefUtilities) {
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
                CacheHelper.PAGE_LANGUAGE + "=? AND " + CacheHelper.PAGE_LOCATION + "=?",
                new String[]{mLanguage.getShortName(), mLocation.getName()}, null, null,
                null);
    }

    @Override
    public Page loadFrom(Cursor cursor) {
        int id = cursor.getInt(0);
        String title = cursor.getString(1);
        String desc = cursor.getString(2);
        String content = cursor.getString(3);
        int parentId = cursor.getInt(4); //TODO

        return new Page(id, title, desc, content);
    }

    @Override
    public void store(SQLiteDatabase db, List<Page> pages) {
        if (pages.isEmpty()) {
            return;
        }
        //db.delete(CacheHelper.TABLE_PAGE, null, null); //TODO dont remove everything

        ContentValues values = new ContentValues(8);
        for (Page page : pages) {
            values.clear();
            values.put(CacheHelper.PAGE_ID, page.getId());
            values.put(CacheHelper.PAGE_TITLE, page.getTitle());
            values.put(CacheHelper.PAGE_CONTENT, page.getContent());
            values.put(CacheHelper.PAGE_DESCRIPTION, page.getDescription());
            values.put(CacheHelper.PAGE_LOCATION, mLocation.getName());
            values.put(CacheHelper.PAGE_LANGUAGE, mLanguage.getShortName());
            values.put(CacheHelper.PAGE_PARENT_ID, -1); //TODO

            db.replace(CacheHelper.TABLE_PAGE, null, values);
        }
    }

    @Override
    public List<Page> request() {
        return mNetwork.getPages(mLanguage, mLocation);
    }
}
