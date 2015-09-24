package augsburg.se.alltagsguide.utilities;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class Objects {
    public static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    public static boolean isNullOrEmpty(String s) {
        return Objects.equals(s, null) || Objects.equals("", s);
    }
}
