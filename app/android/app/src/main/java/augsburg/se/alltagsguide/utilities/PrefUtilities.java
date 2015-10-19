package augsburg.se.alltagsguide.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Calendar;
import java.util.Date;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import roboguice.util.Ln;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

/**
 * Created by Daniel-L on 16.08.2015.
 */
@SuppressLint("CommitPrefEdits")
public class PrefUtilities {
    public static final String MULTIPLE_COLUMNS_PORTRAIT = "multiple_columns_portrait";
    public static final String MULTIPLE_COLUMNS_LANDSCAPE = "multiple_columns_landscape";
    private static final String LOCATION = "location";
    private static final String LANGUAGE = "language";
    private static final String CURRENT_COLOR = "current_color";
    private static final String FONT_STYLE = "font_style";
    private static final String CURRENT_PAGE = "current_page";

    /**
     * The Constant PROPERTY_REGISTERED_TS.
     */
    private static final String PROPERTY_REGISTERED_TS = "registered_ts";

    /**
     * The Constant PROPERTY_REG_ID.
     */
    private static final String PROPERTY_REG_ID = "reg_id";

    private final SharedPreferences preferences;

    public void clear() {
        preferences.edit().clear().commit();
    }

    public PrefUtilities(@NonNull Context context) {
        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    public Language getLanguage() {
        return getObject(Language.class, LANGUAGE);
    }

    public <T> T getObject(@NonNull Class<T> clazz, @NonNull String key) {
        try {
            return new Gson().fromJson(preferences.getString(key, null), clazz);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }


    public void setLocation(Location location) {
        save(preferences.edit().putString(LOCATION, new Gson().toJson(location)));
    }

    public Location getLocation() {
        return getObject(Location.class, LOCATION);
    }


    public void setLanguage(Language language) {
        save(preferences.edit().putString(LANGUAGE, new Gson().toJson(language)));
    }

    private static boolean isEditorApplyAvailable() {
        return SDK_INT >= GINGERBREAD;
    }

    /**
     * Save preferences in given editor
     *
     * @param editor
     */
    private static void save(@NonNull final SharedPreferences.Editor editor) {
        if (isEditorApplyAvailable()) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public void saveCurrentColor(int color) {
        save(preferences.edit().putInt(CURRENT_COLOR, color));
    }

    public int getCurrentColor() {
        return preferences.getInt(CURRENT_COLOR, R.color.myPrimaryColor);
    }

    public int getSelectedPageId() {
        return preferences.getInt(CURRENT_PAGE, -1);
    }

    public void setSelectedPage(int pageId) {
        save(preferences.edit().putInt(CURRENT_PAGE, pageId));
    }

    public void addListener(@NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeListener(@NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public boolean useMultipleColumnsLandscape() {
        return preferences.getBoolean(MULTIPLE_COLUMNS_LANDSCAPE, true);
    }

    public void setMultipleColumnsLandscape(boolean multipleColumns) {
        save(preferences.edit().putBoolean(MULTIPLE_COLUMNS_LANDSCAPE, multipleColumns));
    }

    public boolean useMultipleColumnsPortrait() {
        return preferences.getBoolean(MULTIPLE_COLUMNS_PORTRAIT, false);
    }

    public void setMultipleColumnsPortrait(boolean multipleColumns) {
        save(preferences.edit().putBoolean(MULTIPLE_COLUMNS_PORTRAIT, multipleColumns));
    }

    public FontStyle getFontStyle() {
        try {
            return FontStyle.valueOf(preferences.getString(FONT_STYLE,
                    FontStyle.Medium.name()));
        } catch (Exception e) {
            return FontStyle.Medium;
        }
    }

    private static String makePushKey(Location location) {
        return "_push" + location.getId() + PROPERTY_REGISTERED_TS;
    }

    /**
     * Checks if is registered on server.
     *
     * @return true, if is registered on server
     */
    public boolean isRegisteredOnServer(Location location) {
        // Find registration threshold
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        long yesterdayTS = cal.getTimeInMillis();
        long regTS = preferences.getLong(makePushKey(location), 0);
        if (regTS > yesterdayTS) {
            Ln.v("GCM registration current. regTS=" + regTS + " yesterdayTS=" + yesterdayTS);
            return true;
        } else {
            Ln.v("GCM registration expired. regTS=" + regTS + " yesterdayTS=" + yesterdayTS);
            return false;
        }
    }

    /**
     * Sets whether the device was successfully registered in the server side.
     *
     * @param flag  True if registration was successful, false otherwise
     * @param gcmId True if registration was successful, false otherwise
     */
    public void setRegisteredOnServer(Location location, boolean flag, String gcmId) {
        Ln.d("Setting registered on server status as: " + flag);
        SharedPreferences.Editor editor = preferences.edit();
        if (flag) {
            editor.putLong(makePushKey(location), new Date().getTime());
            editor.putString(PROPERTY_REG_ID, gcmId);
        } else {
            editor.remove(PROPERTY_REG_ID);
            editor.remove(makePushKey(location));
        }
        save(editor);
    }

}
