package augsburg.se.alltagsguide.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import augsburg.se.alltagsguide.R;

public class ColorManager {
    private final int[] colors;
    private final int[] darkColors;

    public ColorManager(Context context) {
        final TypedArray typedArrayDefault = context.getResources().obtainTypedArray(R.array.colors);
        colors = new int[typedArrayDefault.length()];
        darkColors = new int[typedArrayDefault.length()];
        for (int i = 0; i < typedArrayDefault.length(); i++) {
            colors[i] = typedArrayDefault.getColor(i, 0);
            darkColors[i] = shiftColor(colors[i]);
        }
        typedArrayDefault.recycle();
    }

    public static int shiftColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f; // value component
        return Color.HSVToColor(hsv);
    }

    private int getColorById(int id) {
        if (id < colors.length && id >= 0) {
            return colors[id];
        }
        return id;
    }

    private int getDarkColorById(int id) {
        if (id < darkColors.length && id >= 0) {
            return darkColors[id];
        }
        return id;
    }

    public int getIdByColor(int color) {
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == color) {
                return i;
            }
        }
        return color;
    }

    public int getDarkColor(int id) {
        return darkColors[id % darkColors.length];
    }

    public int getColor(int id) {
        return colors[id % colors.length];
    }

    public static int moreAlpha(int currentColor, int alpha) {
        int red = Color.red(currentColor);
        int green = Color.green(currentColor);
        int blue = Color.blue(currentColor);
        return Color.argb(alpha, red, green, blue);
    }
}
