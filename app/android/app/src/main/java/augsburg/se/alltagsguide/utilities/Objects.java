package augsburg.se.alltagsguide.utilities;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Objects {
    public static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    public static boolean isNullOrEmpty(CharSequence s) {
        return Objects.equals(s, null) || Objects.equals("", s) ||
                Objects.equals(s.toString().replaceAll("<.*?>", ""), "");
    }

    public static int compareTo(int a, int b) {
        return a > b ? +1 : a < b ? -1 : 0;
    }

    @NonNull
    public static String emptyIfNull(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }

    @NonNull
    public static <T> String join(@NonNull List<T> objects) {
        StringBuilder builder = new StringBuilder();
        for (T o : objects) {
            builder.append(o.toString()).append(" ");
        }
        return builder.toString();
    }
}
