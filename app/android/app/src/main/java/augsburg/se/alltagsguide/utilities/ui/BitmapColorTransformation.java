package augsburg.se.alltagsguide.utilities.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import com.squareup.picasso.Transformation;

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

    @Override
    public String key() {
        return "ColorTransformation";
    }
}
