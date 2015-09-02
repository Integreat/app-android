package augsburg.se.alltagsguide.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.inject.Inject;


/**
 * Based on https://github.com/pockethub/PocketHub/blob/master/app/src/main/java/com/github/pockethub/persistence/CacheHelper.java
 */
public class CacheHelper extends SQLiteOpenHelper {
    /**
     * Version constant to increment when the database should be rebuilt
     */
    private static final int VERSION = 8;

    /**
     * Name of database file
     */
    private static final String NAME = "cache.db";

    private static final String TABLE_ARTICLE = "articles";
    private static final String TABLE_CATEGORY = "categories";

    /**
     * @param context
     */
    @Inject
    public CacheHelper(final Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ARTICLE + "(id INTEGER PRIMARY KEY, category INTEGER, title TEXT, summary TEXT, description TEXT);");
        db.execSQL("CREATE TABLE " + TABLE_CATEGORY + "(id INTEGER PRIMARY KEY, title TEXT, description TEXT");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                          final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }
}
