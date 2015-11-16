package augsburg.se.alltagsguide.utilities.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import com.squareup.picasso.Transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Daniel-L on 19.10.2015.
 */
public class BitmapColorTransformation implements Transformation {
    @ColorInt int mColor;

    public BitmapColorTransformation(@ColorInt int color) {
        mColor = color;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (!hasTransparentColor(source)) {
            //bitmap without transparent color -> most likely an image and not an icon
            return source;
        }
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(0, mColor);
        paint.setColorFilter(filter);
        canvas.drawBitmap(source, 0, 0, paint);
        source.recycle();
        return bitmap;
    }

    private boolean hasTransparentColor(Bitmap source) {
        Set<Integer> colorSet = new HashSet<>();
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                colorSet.add(source.getPixel(x, y));
            }
        }
        return colorSet.contains(Color.TRANSPARENT);
    }

    @Override
    public String key() {
        return "ColorTransformation";
    }
}
