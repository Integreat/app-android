package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.List;

import augsburg.se.alltagsguide.common.Category;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class CategoryResource implements PersistableResource<Category> {

    /**
     * Creation factory
     */
    public interface Factory {
        CategoryResource under(Language lang, Location loc);
    }

    private final Language mLanguage;
    private final Location mLocation;

    private NetworkService mNetwork;

    @Inject
    public CategoryResource(@Assisted Language language,
                            @Assisted Location location,
                            NetworkService network) {
        mLanguage = language;
        mLocation = location;
        mNetwork = network;
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_CATEGORY);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.CATEGORY_LANGUAGE + "=? AND " +
                        CacheHelper.CATEGORY_LOCATION + "=?",
                new String[]{mLanguage.getShortName(), mLocation.getName()}, null, null,
                null);
    }

    @Override
    public Category loadFrom(Cursor cursor) {
        Category category = new Category();
        category.setId(cursor.getInt(0));
        category.setTitle(cursor.getString(1));
        category.setDescription(cursor.getString(2));
        category.setLanguage(mLanguage);
        category.setLocation(mLocation);

        return category;
    }

    @Override
    public void store(SQLiteDatabase db, List<Category> categories) {
        if (categories.isEmpty()) {
            return;
        }
        //db.delete(CacheHelper.TABLE_CATEGORY, null, null); //TODO dont remove everything

        ContentValues values = new ContentValues(5);
        for (Category category : categories) {
            values.clear();
            values.put(CacheHelper.CATEGORY_ID, category.getId());
            values.put(CacheHelper.CATEGORY_TITLE, category.getTitle());
            values.put(CacheHelper.CATEGORY_DESCRIPTION, category.getDescription());
            values.put(CacheHelper.CATEGORY_LANGUAGE, mLanguage.getShortName());
            values.put(CacheHelper.CATEGORY_LOCATION, mLocation.getName());

            db.replace(CacheHelper.TABLE_CATEGORY, null, values);
        }
    }

    @Override
    public List<Category> request() {
        return mNetwork.getContents(mLanguage, mLocation);
    }
}
