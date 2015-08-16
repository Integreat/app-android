package augsburg.se.alltagsguide.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

/**
 * Created by Daniel-L on 16.08.2015.
 */
@SuppressLint("CommitPrefEdits")
public class PrefUtilities {
    private static final String LOCATION = "location";
    private static final String LANGUAGE = "language";
    private static final String USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private static PrefUtilities _instance;
    private final SharedPreferences preferences;

    private PrefUtilities(Context context) {
        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    public static void init(Context context) {
        _instance = new PrefUtilities(context);
    }

    public static PrefUtilities getInstance() {
        return _instance;
    }

    public String getLocation() {
        return preferences.getString(LOCATION, "Augsburg");
    }

    public void setLocation(String location) {
        save(preferences.edit().putString(LOCATION, location));
    }

    public String getLanguage() {
        return preferences.getString(LANGUAGE, "de");
    }

    public void setLanguage(String language) {
        save(preferences.edit().putString(LANGUAGE, language));
    }

    public void setDrawerLearned(boolean learned) {
        save(preferences.edit().putBoolean(USER_LEARNED_DRAWER, learned));
    }

    public boolean getDrawerLearned() {
        return preferences.getBoolean(USER_LEARNED_DRAWER, false);
    }

    private static boolean isEditorApplyAvailable() {
        return SDK_INT >= GINGERBREAD;
    }

    /**
     * Save preferences in given editor
     *
     * @param editor
     */
    private static void save(final SharedPreferences.Editor editor) {
        if (isEditorApplyAvailable()) {
            editor.apply();
        } else {
            editor.commit();
        }
    }


    public void addListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
