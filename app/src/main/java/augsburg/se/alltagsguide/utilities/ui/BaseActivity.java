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
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;

import augsburg.se.alltagsguide.BaseApplication;
import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class BaseActivity extends RoboActionBarActivity implements BaseFragment.OnBaseFragmentInteractionListener, BaseFragment.Analytics {
    private static final int DURATION = 400;
    private Drawable oldBackgroundActivity = null;
    private Drawable oldBackgroundTabs = null;
    protected Toolbar mToolbar;

    @InjectView(R.id.toolbar_title)
    private TextView toolbarTitleTextView;

    @InjectView(R.id.toolbar_subtitle)
    private TextView toolbarSubTitleTextView;

    @Inject
    protected PrefUtilities mPrefUtilities;

    protected Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication application = (BaseApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendScreen(getScreenName());
    }

    protected String getScreenName() {
        return "Activity~";
    }

    public void sendScreen(String name){
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendEvent(String category, String action) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    @Override
    public void setContentView(int layoutResID) {
        getTheme().applyStyle(mPrefUtilities.getFontStyle().getResId(), true);
        super.setContentView(layoutResID);
        mToolbar = (Toolbar) super.findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        updateDisplayHome();
        setLastColor();
        updateTextViews();
    }

    public int getStatusBarHeight() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
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
