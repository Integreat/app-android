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
    private static final int VERSION = 3;

    /**
     * Name of database file
     */
    private static final String NAME = "cache.db";

    public static final String TABLE_PAGE = "pages";
    public static final String PAGE_ID = "_id";
    public static final String PAGE_TITLE = "title";
    public static final String PAGE_CONTENT = "content";
    public static final String PAGE_DESCRIPTION = "description";
    public static final String PAGE_LOCATION = "location";
    public static final String PAGE_LANGUAGE = "language";
    public static final String PAGE_PARENT_ID = "parent";

    public static final String TABLE_LANGUAGE = "languages";
    public static final String LANGUAGE_PATH = "path";
    public static final String LANGUAGE_NAME = "name";
    public static final String LANGUAGE_SHORT = "short";
    public static final String LANGUAGE_LOCATION = "location";

    public static final String TABLE_LOCATION = "locations";
    public static final String LOCATION_COLOR = "color";
    public static final String LOCATION_PATH = "path";
    public static final String LOCATION_NAME = "name";
    public static final String LOCATION_URL = "url";


    /**
     * @param context
     */
    @Inject
    public CacheHelper(final Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PAGE + "(" +
                PAGE_ID + " INTEGER PRIMARY KEY," +
                PAGE_TITLE + " TEXT," +
                PAGE_DESCRIPTION + " TEXT," +
                PAGE_CONTENT + " TEXT," +
                PAGE_PARENT_ID + " INTEGER," +
                PAGE_LOCATION + " TEXT," +
                PAGE_LANGUAGE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_LOCATION + "(" +
                LOCATION_NAME + " TEXT PRIMARY KEY," +
                LOCATION_PATH + " TEXT," +
                LOCATION_URL + " TEXT," +
                LOCATION_COLOR + " INTEGER" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_LANGUAGE + "(" +
                LANGUAGE_SHORT + " TEXT," +
                LANGUAGE_NAME + " TEXT," +
                LANGUAGE_PATH + " TEXT," +
                LANGUAGE_LOCATION + " TEXT," +
                "PRIMARY KEY (" + LANGUAGE_SHORT + "," + LANGUAGE_LOCATION + ")" +
                ")");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);
        onCreate(db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                          final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);
        onCreate(db);
    }
}
