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

import augsburg.se.alltagsguide.R;
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
        updateDisplayHome();
        setLastColor();
        updateTextViews();
        setStatusBarColor();
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

    protected void updateDisplayHome() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(shouldSetDisplayHomeAsUp());
        }
    }

    protected boolean shouldSetDisplayHomeAsUp() {
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
        }
        oldBackgroundActivity = colorDrawableActivity;
        oldBackgroundTabs = colorDrawableTabs;
        mPrefUtilities.saveCurrentColor(primaryColor);
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#20000000"));
        }
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
