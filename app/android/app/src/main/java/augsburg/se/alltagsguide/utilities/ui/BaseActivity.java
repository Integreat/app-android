package augsburg.se.alltagsguide.utilities.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.inject.Inject;
import com.nineoldandroids.animation.ValueAnimator;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.utilities.ColorManager;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class BaseActivity extends RoboActionBarActivity implements BaseFragment.OnBaseFragmentInteractionListener {
    private static final int DURATION = 400;
    private Drawable oldBackgroundActivity = null;
    private Drawable oldBackgroundTabs = null;
    private Integer oldStatusBarColor = null;
    protected Toolbar mToolbar;

    @InjectView(R.id.toolbar_title)
    private TextView toolbarTitleTextView;

    @InjectView(R.id.toolbar_subtitle)
    private TextView toolbarSubTitleTextView;

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
        updateTextViews();
    }

    private void updateTextViews() {
        if (toolbarSubTitleTextView != null) {
            toolbarSubTitleTextView.setVisibility(
                    Objects.isNullOrEmpty(
                            toolbarSubTitleTextView.getText()) ? View.GONE : View.VISIBLE);
        }
        if (toolbarTitleTextView != null) {
            toolbarTitleTextView.setVisibility(
                    Objects.isNullOrEmpty(
                            toolbarTitleTextView.getText()) ? View.GONE : View.VISIBLE);
        }
    }


    protected boolean setDisplayHomeAsUp() {
        return false;
    }

    protected void restartActivity() {
        Intent intent = getIntent();
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    protected void setLastColor() {
        int primaryColor = mPrefUtilities.getCurrentColor();
        changeColor(primaryColor);
    }

    protected void changeTabColor(Drawable drawable, @ColorInt int color) {
        /* overridable */
    }

    protected void changeColor(@ColorInt int primaryColor) {
        int secondaryColor = ColorManager.shiftColor(primaryColor);
        ColorDrawable colorDrawableActivity = new ColorDrawable(primaryColor);
        ColorDrawable colorDrawableTabs = new ColorDrawable(primaryColor);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            if (oldBackgroundActivity == null) {
                ab.setBackgroundDrawable(colorDrawableActivity);
                changeTabColor(colorDrawableTabs, primaryColor);
            } else {
                TransitionDrawable tdActivity = new TransitionDrawable(new Drawable[]{oldBackgroundActivity, colorDrawableActivity});
                TransitionDrawable tdTabs = new TransitionDrawable(new Drawable[]{oldBackgroundTabs, colorDrawableTabs});
                ab.setBackgroundDrawable(tdActivity);
                changeTabColor(tdTabs, primaryColor);
                tdActivity.startTransition(DURATION);
                tdTabs.startTransition(DURATION);
            }
            animateStatusBar(secondaryColor);
        }
        oldBackgroundActivity = colorDrawableActivity;
        oldBackgroundTabs = colorDrawableTabs;
        mPrefUtilities.saveCurrentColor(primaryColor);
    }

    private void animateStatusBar(@ColorInt final int secondaryColor) {
        final Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (oldStatusBarColor == null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(alpha(secondaryColor));
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
                            window.setStatusBarColor(alpha(blended));
                        }
                    }
                });
                anim.setDuration(DURATION).start();
            }
        }
        oldStatusBarColor = secondaryColor;
    }

    private
    @ColorInt
    int alpha(@ColorInt int color) {
        int alpha = Math.round(Color.alpha(color) * 0.85f);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @ColorInt
    private int blendColors(@ColorInt int from, @ColorInt int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }


    @Override
    public void setTitle(String title) {
        if (toolbarTitleTextView != null) {
            toolbarTitleTextView.setText(title);
        }
        updateTextViews();
    }

    @Override
    public void setSubTitle(String subTitle) {
        if (toolbarSubTitleTextView != null) {
            toolbarSubTitleTextView.setText(subTitle);
        }
        updateTextViews();
    }
}
