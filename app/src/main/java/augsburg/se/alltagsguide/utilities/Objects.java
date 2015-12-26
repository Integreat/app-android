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

    public static boolean containsIgnoreCase(String stringA, String stringB) {
        return stringA != null && (stringB == null || stringA.toLowerCase().contains(stringB.toLowerCase()));
    }
}
