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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import augsburg.se.alltagsguide.R;

public class ColorManager {
    @NonNull private final int[] colors;
    @NonNull private final int[] darkColors;

    @Inject
    public ColorManager(@NonNull Context context) {
        final TypedArray typedArrayDefault = context.getResources().obtainTypedArray(R.array.colors);
        colors = new int[typedArrayDefault.length()];
        darkColors = new int[typedArrayDefault.length()];
        for (int i = 0; i < typedArrayDefault.length(); i++) {
            colors[i] = typedArrayDefault.getColor(i, 0);
            darkColors[i] = shiftColor(colors[i]);
        }
        typedArrayDefault.recycle();
    }

    @ColorInt
    public static int shiftColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f; // value component
        return Color.HSVToColor(hsv);
    }


    @ColorInt
    public int getDarkColor(int id) {
        return darkColors[id % darkColors.length];
    }

    @ColorInt
    public int getColor(int id) {
        return colors[id % colors.length];
    }

    @ColorInt
    public static int moreAlpha(@ColorInt int currentColor, int alpha) {
        int red = Color.red(currentColor);
        int green = Color.green(currentColor);
        int blue = Color.blue(currentColor);
        return Color.argb(alpha, red, green, blue);
    }
}
