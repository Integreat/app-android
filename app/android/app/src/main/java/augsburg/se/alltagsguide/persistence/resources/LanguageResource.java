package augsburg.se.alltagsguide.persistence.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import java.util.List;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.CacheHelper;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class LanguageResource implements PersistableResource<Language> {

    /**
     * Creation factory
     */
    public interface Factory {
        LanguageResource under(Location location);
    }

    private final Location mLocation;
    private NetworkService mNetwork;

    @AssistedInject
    public LanguageResource(@Assisted Location location, NetworkService network) {
        mLocation = location;
        mNetwork = network;
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_LANGUAGE);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.LANGUAGE_LOCATION + "=?",
                new String[]{String.valueOf(mLocation.getId())}, null, null,
                null);
    }

    @Override
    public Cursor getCursor(SQLiteDatabase readableDatabase, int id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CacheHelper.TABLE_PAGE);
        return builder.query(readableDatabase, new String[]{},
                CacheHelper.LANGUAGE_LOCATION + "=? AND " + CacheHelper.LANGUAGE_ID + "=?",
                new String[]{String.valueOf(mLocation.getId()), String.valueOf(id)}, null, null,
                null);
    }

    @Override
    public Language loadFrom(Cursor cursor) {
        int index = 0;
        int id = cursor.getInt(index++);
        String shortName = cursor.getString(index++);
        String name = cursor.getString(index++);
        String path = cursor.getString(index++);
        int location = cursor.getInt(index++); //only required for sql query
        Language language = new Language(id, shortName, name, path);
        language.setLocation(mLocation);
        return language;
    }

    @Override
    public void store(SQLiteDatabase db, List<Language> languages) {
        if (languages == null || languages.isEmpty()) {
            return;
        }

        ContentValues values = new ContentValues(5);
        for (Language language : languages) {
            values.clear();
            values.put(CacheHelper.LANGUAGE_ID, language.getId()); //1
            values.put(CacheHelper.LANGUAGE_SHORT, language.getShortName()); //2
            values.put(CacheHelper.LANGUAGE_NAME, language.getName()); //3
            values.put(CacheHelper.LANGUAGE_PATH, language.getIconPath()); //4
            values.put(CacheHelper.LANGUAGE_LOCATION, mLocation.getId()); //5

            db.replace(CacheHelper.TABLE_LANGUAGE, null, values);
        }
    }

    @Override
    public List<Language> request() {
        return mNetwork.getAvailableLanguages(mLocation);
    }


    @Override
    public boolean shouldUpdate() {
        return false;
    }
}
