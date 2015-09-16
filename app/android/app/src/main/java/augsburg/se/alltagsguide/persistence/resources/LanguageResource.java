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
                new String[]{mLocation.getName()}, null, null,
                null);
    }

    @Override
    public Language loadFrom(Cursor cursor) {
        Language language = new Language();
        language.setShortName(cursor.getString(0));
        language.setName(cursor.getString(1));
        language.setIconPath(cursor.getString(2));
        language.setLocation(mLocation);
        return language;
    }

    @Override
    public void store(SQLiteDatabase db, List<Language> languages) {
        if (languages.isEmpty()) {
            return;
        }
        //db.delete(CacheHelper.TABLE_LANGUAGE, null, null); //TODO dont drop everything

        ContentValues values = new ContentValues(3);
        for (Language language : languages) {
            values.clear();
            values.put(CacheHelper.LANGUAGE_SHORT, language.getShortName());
            values.put(CacheHelper.LANGUAGE_NAME, language.getName());
            values.put(CacheHelper.LANGUAGE_PATH, language.getIconPath());
            values.put(CacheHelper.LANGUAGE_LOCATION, mLocation.getName());

            db.replace(CacheHelper.TABLE_LANGUAGE, null, values);
        }
    }

    @Override
    public List<Language> request() {
        return mNetwork.getAvailableLanguages(mLocation);
    }
}
