package augsburg.se.alltagsguide.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.inject.Inject;

import roboguice.util.Ln;


/**
 * Based on https://github.com/pockethub/PocketHub/blob/master/app/src/main/java/com/github/pockethub/persistence/CacheHelper.java
 */
public class CacheHelper extends SQLiteOpenHelper {

    public static final String TABLE_PAGE = "pages";
    public static final String PAGE_ID = "_id"; //1
    public static final String PAGE_TITLE = "_title"; //2
    public static final String PAGE_TYPE = "_type"; //3
    public static final String PAGE_STATUS = "_status"; //4
    public static final String PAGE_MODIFIED = "_modified"; //5
    public static final String PAGE_DESCRIPTION = "_description"; //6
    public static final String PAGE_CONTENT = "_content"; //7
    public static final String PAGE_PARENT_ID = "_parent"; //8
    public static final String PAGE_ORDER = "_order"; //9
    public static final String PAGE_AVAILABLE_LANGUAGES = "_languages"; //10
    public static final String PAGE_LOCATION = "_location"; //11
    public static final String PAGE_LANGUAGE = "_language"; //12

    public static final String TABLE_LANGUAGE = "languages";
    public static final String LANGUAGE_ID = "_id"; //1
    public static final String LANGUAGE_SHORT = "_short"; //2
    public static final String LANGUAGE_NAME = "_name"; //3
    public static final String LANGUAGE_PATH = "_path"; //4
    public static final String LANGUAGE_LOCATION = "_location"; //5

    public static final String TABLE_LOCATION = "locations";
    public static final String LOCATION_ID = "_id"; //1
    public static final String LOCATION_NAME = "_name"; //2
    public static final String LOCATION_ICON = "_icon"; //3
    public static final String LOCATION_PATH = "_path"; //4
    public static final String LOCATION_DESCRIPTION = "_description"; //5
    public static final String LOCATION_GLOBAL = "_global"; //6
    public static final String LOCATION_COLOR = "_color"; //7
    public static final String LOCATION_CITY_IMAGE = "_city_image"; //8
    public static final String LOCATION_LATITUDE = "_latitude"; //9
    public static final String LOCATION_LONGITUDE = "_longitude"; //10

    @Inject
    public CacheHelper(final Context context, DatabaseInfo databaseInfo) {
        super(context, databaseInfo.getName(), null, databaseInfo.getVersion());
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        String pageQuery = "CREATE TABLE " + TABLE_PAGE + "(" +
                PAGE_ID + " INTEGER," +
                PAGE_TITLE + " TEXT," +
                PAGE_TYPE + " TEXT," +
                PAGE_STATUS + " TEXT," +
                PAGE_MODIFIED + " TEXT," +
                PAGE_DESCRIPTION + " TEXT," +
                PAGE_CONTENT + " TEXT," +
                PAGE_PARENT_ID + " INTEGER," +
                PAGE_ORDER + " INTEGER," +
                PAGE_AVAILABLE_LANGUAGES + " TEXT," +
                PAGE_LOCATION + " INTEGER," +
                PAGE_LANGUAGE + " INTEGER," +
                "PRIMARY KEY(" + PAGE_ID + "," + PAGE_LOCATION + "," + PAGE_LANGUAGE + ")" +
                ");";
        Ln.d(pageQuery);
        db.execSQL(pageQuery);

        String locationQuery = "CREATE TABLE " + TABLE_LOCATION + "(" +
                LOCATION_ID + " INTEGER PRIMARY KEY," +
                LOCATION_NAME + " TEXT," +
                LOCATION_ICON + " TEXT," +
                LOCATION_PATH + " TEXT," +
                LOCATION_DESCRIPTION + " TEXT," +
                LOCATION_GLOBAL + " INTEGER," +
                LOCATION_COLOR + " INTEGER," +
                LOCATION_CITY_IMAGE + " TEXT," +
                LOCATION_LATITUDE + " FLOAT," +
                LOCATION_LONGITUDE + " FLOAT" +
                ");";
        Ln.d(locationQuery);
        db.execSQL(locationQuery);

        String languageQuery = "CREATE TABLE " + TABLE_LANGUAGE + "(" +
                LANGUAGE_ID + " INTEGER," +
                LANGUAGE_SHORT + " TEXT," +
                LANGUAGE_NAME + " TEXT," +
                LANGUAGE_PATH + " TEXT," +
                LANGUAGE_LOCATION + " INTEGER," +
                "PRIMARY KEY(" + LANGUAGE_ID + "," + LANGUAGE_LOCATION + ")" +
        ");";
        Ln.d(languageQuery);
        db.execSQL(languageQuery);
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
