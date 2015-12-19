package augsburg.se.alltagsguide.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import augsburg.se.alltagsguide.R;

/**
 * Created by Amadeus on 06. Nov. 2015.
 */
public class CurrentPageIndicatorView extends View {
    private static final int DEFAULT_RADIUS_DP = 5;
    private static final int DEFAULT_CIRCLE_MARGIN = 8;

    private Paint pointsPaint;
    private int pagesCount;
    private int currentSelectedPage;
    private float currentScrollLeft;

    private float radius;
    private float item_spacing;
    private int selectedColor;
    private int notSelectedColor;

    public CurrentPageIndicatorView(Context context) {
        super(context);
        init(null);
    }

    public CurrentPageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CurrentPageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CurrentPageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CurrentPageIndicatorView, 0, 0);
            try {
                radius = a.getDimension(R.styleable.CurrentPageIndicatorView_radius, px2dp(DEFAULT_RADIUS_DP));
                item_spacing = a.getDimension(R.styleable.CurrentPageIndicatorView_item_spacing, px2dp(DEFAULT_CIRCLE_MARGIN));
                pagesCount = a.getInt(R.styleable.CurrentPageIndicatorView_page_count, 0);
                currentSelectedPage = a.getInt(R.styleable.CurrentPageIndicatorView_selected_item, 0);
                selectedColor = a.getColor(R.styleable.CurrentPageIndicatorView_selected_color, 0xaa000000);
                notSelectedColor = a.getColor(R.styleable.CurrentPageIndicatorView_not_selected_color, 0x55000000);
            } finally {
                a.recycle();
            }
        }

        this.pointsPaint = new Paint();
        this.pointsPaint.setColor(notSelectedColor);
    }

    public int getPagesCount() {
        return pagesCount;
    }

    public void setPagesCount(int pagesCount) {
        if(this.pagesCount != pagesCount) requestLayout();
        this.pagesCount = pagesCount;
        invalidate();
    }

    public int getCurrentSelectedPage() {
        return currentSelectedPage;
    }

    public void setCurrentSelectedPage(int currentSelectedPage) {
        this.currentSelectedPage = currentSelectedPage;
        invalidate();
    }

    public float getCurrentScrollLeft() {
        return currentScrollLeft;
    }

    public void setCurrentScrollLeft(float currentScrollLeft) {
        this.currentScrollLeft = currentScrollLeft;
    }

    private float px2dp(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = getMeasuredWidth();
        int heightSize = getMeasuredHeight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        int desiredHeight = (int)Math.ceil(2*radius+getPaddingBottom()+getPaddingTop());
        int desiredWidth = (int)Math.ceil(getPagesCount()*2*radius+(getPagesCount()-1)* item_spacing +getPaddingLeft()+getPaddingRight());

        int width;
        int height;
        if(widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if(widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(widthSize, desiredWidth);
        } else {
            width = desiredWidth;
        }

        if(heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if(heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(heightSize, desiredHeight);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    private float getLeft(int pos) {
        return getWidth()/2-(((float)getPagesCount())/2-pos)*(2*radius+ item_spacing)+radius+ item_spacing /2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        pointsPaint.setAlpha(50);
        float top = getHeight()/2;
        for(int i=0;i<getPagesCount();i++) {
            Paint paint;
            /*if(i == currentSelectedPage) {
                pointsPaint.setColor(selectedColor);
            } else {
            }*/
            canvas.drawCircle(getLeft(i), top, radius, pointsPaint);
        }
        pointsPaint.setAlpha((int) ((1-currentScrollLeft) * 150));
        canvas.drawCircle(getLeft(currentSelectedPage), top, radius, pointsPaint);

        if(currentSelectedPage <= getPagesCount()) {
            pointsPaint.setAlpha((int) (currentScrollLeft * 150));
            canvas.drawCircle(getLeft(currentSelectedPage+1), top, radius, pointsPaint);
        }

    }
}
