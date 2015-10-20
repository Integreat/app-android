package augsburg.se.alltagsguide.utilities;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import roboguice.util.Ln;

public class Helper {

    public static SimpleDateFormat FROM_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);

    public static String quote(String s) {
        return "'" + s + "'";
    }

    public static float getFloatOrDefault(JsonElement elem, float defaultValue) {
        try {
            return elem.getAsFloat();
        } catch (Exception e) {
            Ln.d(e);
            return defaultValue;
        }
    }

    public static boolean sameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String getStringOrDefault(JsonElement elem, String defaultValue) {
        try {
            return elem.getAsString();
        } catch (Exception e) {
            Ln.d(e);
            return defaultValue;
        }
    }

    public static int getIntOrDefault(JsonElement elem, int defaultValue) {
        try {
            return elem.getAsInt();
        } catch (Exception e) {
            Ln.d(e);
            return defaultValue;
        }
    }

    public static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<>();
        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

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
    private static Drawable getPressedColorRippleDrawable(int normalColor, int pressedColor) {
        Drawable drawable = new RippleDrawable(getPressedColorSelector(normalColor, pressedColor), getColorDrawableFromColor(normalColor), null);
        drawable.setAlpha(70);
        return drawable;
    }

    public static void setImageDrawable(View view, Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    public static void setPressedColorRippleDrawable(int normalColor, int pressedColor, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(getPressedColorRippleDrawable(normalColor, pressedColor));
        } else {
            view.setBackgroundColor(normalColor);
        }
    }

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

    private static ColorDrawable getColorDrawableFromColor(int color) {
        return new ColorDrawable(color);
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
    public static <T> String join(CharSequence delimiter, Iterable<T> tokens) {
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
