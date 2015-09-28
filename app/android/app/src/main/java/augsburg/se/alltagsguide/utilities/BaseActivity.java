package augsburg.se.alltagsguide.utilities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.google.inject.Inject;
import com.nineoldandroids.animation.ValueAnimator;

import java.io.Serializable;

import augsburg.se.alltagsguide.R;
import roboguice.activity.RoboActionBarActivity;

public class BaseActivity extends RoboActionBarActivity {
    private static final int DURATION = 400;
    private Drawable oldBackgroundActivity = null;
    private Drawable oldBackgroundTabs = null;
    private Integer oldStatusBarColor = null;
    protected Toolbar mToolbar;

    @Inject
    protected PrefUtilities mPrefUtilities;

    @Override
    public void setContentView(int layoutResID) {
        getTheme().applyStyle(mPrefUtilities.getFontStyle().getResId(), true);
        super.setContentView(layoutResID);
        mToolbar = (Toolbar) super.findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(setDisplayHomeAsUp());
        }
        setLastColor();
    }


    protected boolean setDisplayHomeAsUp() {
        return false;
    }


    private void setLastColor() {
        int primaryColor = mPrefUtilities.getCurrentColor();
        changeColor(primaryColor);
    }

    void changeTabColor(Drawable drawable) {
        /* overridable */
    }

    protected void changeColor(int primaryColor) {
        int secondaryColor = ColorManager.shiftColor(primaryColor);
        ColorDrawable colorDrawableActivity = new ColorDrawable(primaryColor);
        ColorDrawable colorDrawableTabs = new ColorDrawable(primaryColor);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            if (oldBackgroundActivity == null) {
                ab.setBackgroundDrawable(colorDrawableActivity);
                changeTabColor(colorDrawableTabs);
            } else {
                TransitionDrawable tdActivity = new TransitionDrawable(new Drawable[]{oldBackgroundActivity, colorDrawableActivity});
                TransitionDrawable tdTabs = new TransitionDrawable(new Drawable[]{oldBackgroundTabs, colorDrawableTabs});
                ab.setBackgroundDrawable(tdActivity);
                changeTabColor(tdTabs);
                tdActivity.startTransition(DURATION);
                tdTabs.startTransition(DURATION);
            }
            animateStatusBar(secondaryColor);
        }
        oldBackgroundActivity = colorDrawableActivity;
        oldBackgroundTabs = colorDrawableTabs;
        mPrefUtilities.saveCurrentColor(primaryColor);
    }

    private void animateStatusBar(final int secondaryColor) {
        final Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (oldStatusBarColor == null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(secondaryColor);
            } else {
                // animation here
                ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        // Use animation position to blend colors.
                        float position = animation.getAnimatedFraction();

                        // Apply blended color to the status bar.
                        int blended = blendColors(oldStatusBarColor, secondaryColor, position);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.setStatusBarColor(blended);
                        }
                    }
                });
                anim.setDuration(DURATION).start();
            }
        }
        oldStatusBarColor = secondaryColor;
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }


    /**
     * Get intent extra
     *
     * @param name
     * @return serializable
     */
    @SuppressWarnings("unchecked")
    protected <V extends Serializable> V getSerializableExtra(final String name) {
        return (V) getIntent().getSerializableExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return int
     */
    protected int getIntExtra(final String name) {
        return getIntent().getIntExtra(name, -1);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return string
     */
    protected String getStringExtra(final String name) {
        return getIntent().getStringExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return string array
     */
    protected String[] getStringArrayExtra(final String name) {
        return getIntent().getStringArrayExtra(name);
    }


    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }
}
