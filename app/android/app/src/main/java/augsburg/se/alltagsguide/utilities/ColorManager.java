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
