package augsburg.se.alltagsguide.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Singleton;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.UpdateTime;

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
}
