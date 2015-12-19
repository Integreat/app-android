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

import augsburg.se.alltagsguide.R;

public enum FontStyle {
    XSmall(R.style.FontStyle_XSmall, "XSmall"),
    Small(R.style.FontStyle_Small, "Small"),
    Medium(R.style.FontStyle_Medium, "Medium"),
    Large(R.style.FontStyle_Large, "Large"),
    XLarge(R.style.FontStyle_XLarge, "XLarge");

    private int resId;
    @NonNull private String title;

    public int getResId() {
        return resId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    FontStyle(int resId, @NonNull String title) {
        this.resId = resId;
        this.title = title;
    }
}