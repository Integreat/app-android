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

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import roboguice.util.Ln;

public class Helper {

    public static SimpleDateFormat FROM_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
    public static SimpleDateFormat TO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.GERMANY);

    static {
        FROM_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        TO_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @NonNull
    public static String quote(String s) {
        return "'" + s + "'";
    }

    public static float getFloatOrDefault(@NonNull JsonElement elem, float defaultValue) {
        try {
            if (!elem.isJsonNull()){
                String string = elem.getAsString();
                if (string != null){
                    return Float.parseFloat(string.trim());
                }
            }
            return defaultValue;
        } catch (Exception e) {
            Ln.d(e);
            return defaultValue;
        }
    }

    public static boolean sameDate(@NonNull Date date1, @NonNull Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @NonNull
    public static String getStringOrDefault(JsonElement elem, String defaultValue) {
        try {
            if (!elem.isJsonNull()){
                return elem.getAsString();
            }
            return defaultValue;
        } catch (Exception e) {
            Ln.d(e);
            return defaultValue;
        }
    }

    public static int getIntOrDefault(JsonElement elem, int defaultValue) {
        try {
            if (!elem.isJsonNull()){
                String string = elem.getAsString();
                if (string != null){
                    return Integer.parseInt(string.trim());
                }
            }
            return defaultValue;
        } catch (Exception e) {
            Ln.d(e);
            return defaultValue;
        }
    }

    public static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    @NonNull
    public static <T> List<T> intersection(@NonNull List<T> list1, @NonNull List<T> list2) {
        List<T> list = new ArrayList<>();
        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    @NonNull
    public static <T> List<T> nonIntersection(List<T> list1, List<T> list2) {
        if (list1 == null) {
            list1 = new ArrayList<>();
        }
        if (list2 == null) {
            list2 = new ArrayList<>();
        }
        List<T> list = new ArrayList<>();
        for (T t : list1) {
            if (!list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    private static Drawable getPressedColorRippleDrawable(int normalColor, int pressedColor) {
        Drawable drawable = new RippleDrawable(getPressedColorSelector(normalColor, pressedColor), getColorDrawableFromColor(normalColor), null);
        drawable.setAlpha(70);
        return drawable;
    }

    public static void setImageDrawable(@NonNull View view, Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    public static void setPressedColorRippleDrawable(int normalColor, int pressedColor, @NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(getPressedColorRippleDrawable(normalColor, pressedColor));
        } else {
            view.setBackgroundColor(normalColor);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @NonNull
    private static ColorStateList getPressedColorSelector(int normalColor, int pressedColor) {
        return new ColorStateList(
                new int[][]
                        {
                                new int[]{android.R.attr.state_pressed},
                                new int[]{android.R.attr.state_focused},
                                new int[]{android.R.attr.state_activated},
                                new int[]{}
                        },
                new int[]
                        {
                                pressedColor,
                                pressedColor,
                                pressedColor,
                                normalColor
                        }
        );
    }

    @NonNull
    private static ColorDrawable getColorDrawableFromColor(int color) {
        return new ColorDrawable(color);
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
    @NonNull
    public static <T> String join(@NonNull CharSequence delimiter, @NonNull Iterable<T> tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (T token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token.toString());
        }
        return sb.toString();
    }

}
