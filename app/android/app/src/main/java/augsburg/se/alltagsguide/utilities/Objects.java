package augsburg.se.alltagsguide.utilities;

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
        if (a > b) {
            return 1;
        } else if (a < b) {
            return -1;
        }
        return 0;
    }

    public static String emptyIfNull(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }
}
