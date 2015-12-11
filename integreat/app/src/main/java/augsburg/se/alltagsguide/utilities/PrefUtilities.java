/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

import java.util.Calendar;
import java.util.Date;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import roboguice.RoboGuice;
import roboguice.util.Ln;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

/**
 * Created by Daniel-L on 16.08.2015.
 */
@SuppressLint("CommitPrefEdits")
public class PrefUtilities {
    @Inject
    private Gson mGson;
    public static final String MULTIPLE_COLUMNS_PORTRAIT = "multiple_columns_portrait";
    public static final String MULTIPLE_COLUMNS_LANDSCAPE = "multiple_columns_landscape";
    private static final String LOCATION = "location";
    private static final String LANGUAGE = "language";
    private static final String CURRENT_COLOR = "current_color";
    private static final String FONT_STYLE = "font_style";
    private static final String CURRENT_PAGE = "current_page";

    private static final String NAV_DRAWER_LEARNED = "nav_drawer_learned";

    /**
     * The Constant PROPERTY_REGISTERED_TS.
     */
    private static final String PROPERTY_REGISTERED_TS = "registered_ts";

    /**
     * The Constant PROPERTY_REG_ID.
     */
    private static final String PROPERTY_REG_ID = "reg_id";
    private static final String LAST_LOCATION_UPDATE = "last_location_update";

    private final SharedPreferences preferences;

    public void clear() {
        preferences.edit().clear().commit();
    }

    private Context mContext;

    public PrefUtilities(@NonNull Context context) {
        mContext = context;
        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        RoboGuice.injectMembers(context, this);
    }

    public Language getLanguage() {
        return getObject(Language.class, LANGUAGE);
    }

    public <T> T getObject(@NonNull Class<T> clazz, @NonNull String key) {
        try {
            return mGson.fromJson(preferences.getString(key, null), clazz);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public void setNavDrawerLearned() {
        save(preferences.edit().putBoolean(NAV_DRAWER_LEARNED, true));
    }

    public boolean hasNavDrawerLearned() {
        return preferences.getBoolean(NAV_DRAWER_LEARNED, false);
    }


    public void setLocation(Location location) {
        save(preferences.edit().putString(LOCATION, mGson.toJson(location)));
    }

    public Location getLocation() {
        return getObject(Location.class, LOCATION);
    }


    public void setLanguage(Language language) {
        save(preferences.edit().putString(LANGUAGE, mGson.toJson(language)));
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

    public void saveCurrentColor(@ColorInt int color) {
        save(preferences.edit().putInt(CURRENT_COLOR, color));
    }

    @ColorInt
    public int getCurrentColor() {
        return preferences.getInt(CURRENT_COLOR, ContextCompat.getColor(mContext, R.color.primary));
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

    public long lastLocationUpdateTime() {
        return preferences.getLong(LAST_LOCATION_UPDATE, 0);
    }

    public void setLastLocationUpdateTime() {
        save(preferences.edit().putLong(LAST_LOCATION_UPDATE, new Date().getTime()));
    }

    public long lastEventPageUpdateTime(Language language, Location location) {
        return preferences.getLong(makeEventPageKey(language, location), 0);
    }

    private String makeEventPageKey(Language language, Location location) {
        return String.format("event_page_key(%d)(%d)", language.getId(), location.getId());
    }

    private String makePageKey(Language language, Location location) {
        return String.format("page_key(%d)(%d)", language.getId(), location.getId());
    }

    private String makePageDisclaimerKey(Language language, Location location) {
        return String.format("disclaimer_key(%d)(%d)", language.getId(), location.getId());
    }

    public void setLastEventPageUpdateTime(Language language, Location location) {
        save(preferences.edit().putLong(makeEventPageKey(language, location), new Date().getTime()));
    }

    public long lastLanguageUpdateTime(Location location) {
        return preferences.getLong(makeLocationKey(location), 0);
    }

    private String makeLocationKey(Location location) {
        return String.format("location_key(%d)", location.getId());
    }

    public void setLastLanguageUpdateTime(Location location) {
        save(preferences.edit().putLong(makeLocationKey(location), new Date().getTime()));
    }

    public long lastPageUpdateTime(Language language, Location location) {
        return preferences.getLong(makePageKey(language, location), 0);
    }

    public void setLastPageUpdateTime(Language language, Location location) {
        save(preferences.edit().putLong(makePageKey(language, location), new Date().getTime()));
    }

    public long lastPageDisclaimerUpdateTime(Language language, Location location) {
        return preferences.getLong(makePageDisclaimerKey(language, location), 0);
    }

    public void setLastPageDisclaimerUpdateTime(Language language, Location location) {
        save(preferences.edit().putLong(makePageDisclaimerKey(language, location), new Date().getTime()));
    }
}
