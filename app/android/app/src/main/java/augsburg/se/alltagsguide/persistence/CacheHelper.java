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
    private static final int VERSION = 49;

    /**
     * Name of database file
     */
    private static final String NAME = "cache.db";

    public static final String TABLE_ARTICLE = "articles";
    public static final String ARTICLE_ID = "_id";
    public static final String ARTICLE_TITLE = "title";
    public static final String ARTICLE_SUMMARY = "summary";
    public static final String ARTICLE_DESCRIPTION = "description";
    public static final String ARTICLE_URL = "url";
    public static final String ARTICLE_IMAGE = "image";
    public static final String ARTICLE_CATEGORY = "category";
    public static final String ARTICLE_LOCATION = "location";
    public static final String ARTICLE_LANGUAGE = "language";

    public static final String TABLE_CATEGORY = "categories";
    public static final String CATEGORY_ID = "_id";
    public static final String CATEGORY_LANGUAGE = "language";
    public static final String CATEGORY_LOCATION = "location";
    public static final String CATEGORY_TITLE = "title";
    public static final String CATEGORY_DESCRIPTION = "description";
    public static final String CATEGORY_PARENT = "parent";

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
        db.execSQL("CREATE TABLE " + TABLE_ARTICLE + "(" +
                ARTICLE_ID + " INTEGER PRIMARY KEY," +
                ARTICLE_TITLE + " TEXT," +
                ARTICLE_DESCRIPTION + " TEXT," +
                ARTICLE_SUMMARY + " TEXT," +
                ARTICLE_URL + " TEXT," +
                ARTICLE_IMAGE + " TEXT," +
                ARTICLE_CATEGORY + " INTEGER," +
                ARTICLE_LOCATION + " TEXT," +
                ARTICLE_LANGUAGE + " TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORY + "(" +
                CATEGORY_ID + " INTEGER PRIMARY KEY," +
                CATEGORY_TITLE + " TEXT," +
                CATEGORY_DESCRIPTION + " TEXT," +
                CATEGORY_LOCATION + " TEXT," +
                CATEGORY_LANGUAGE + " TEXT," +
                CATEGORY_PARENT + " INTEGER" +
                ")");

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
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                          final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);
        onCreate(db);
    }
}
