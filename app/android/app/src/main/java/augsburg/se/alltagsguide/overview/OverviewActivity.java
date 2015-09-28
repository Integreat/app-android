package augsburg.se.alltagsguide.overview;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.navigation.NavigationAdapter;
import augsburg.se.alltagsguide.page.PageActivity;
import augsburg.se.alltagsguide.start.WelcomeActivity;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.EmptyRecyclerView;
import augsburg.se.alltagsguide.utilities.PrefFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_overview)
public class OverviewActivity extends BaseActivity
        implements OverviewFragment.OnPageFragmentInteractionListener, NavigationAdapter.OnNavigationSelected, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.header)
    private View navigationHeaderView;

    @InjectView(R.id.navigation)
    private View navigationView;

    @InjectView(R.id.location)
    private TextView locationNameTextView;

    @InjectView(R.id.url)
    private TextView locationUrlTextView;

    @InjectView(R.id.language)
    private ImageView languageFlagImageView;

    @InjectView(R.id.recycler_view)
    private EmptyRecyclerView mRecyclerView;

    @InjectView(R.id.emptyView)
    private View mEmptyView;

    @InjectView(R.id.drawer)
    private DrawerLayout drawerLayout;

    @InjectView(R.id.swipe_refresh)
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OverviewFragment mOverviewFragment;
    private NavigationAdapter mNavigationAdapter;
    private Location mLocation;
    private Language mLanguage;

    private MenuItem columnsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLocation = mPrefUtilities.getLocation();
        mLanguage = mPrefUtilities.getLanguage();
        initNavigationDrawer();
        mOverviewFragment = OverviewFragment.newInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, mOverviewFragment)
                    .commit();
        }
    }

    @Override
    protected boolean setDisplayHomeAsUp() {
        return false;
    }

    private void initNavigationDrawer() {
        locationNameTextView.setText(mLocation.getName());
        locationUrlTextView.setText(mLocation.getUrl());

        if (mLanguage.getIconPath() != null) {
            Picasso.with(this)
                    .load(mLanguage.getIconPath())
                    .placeholder(R.drawable.placeholder_language)
                    .error(R.drawable.placeholder_language)
                    .into(languageFlagImageView);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNavigationAdapter = new NavigationAdapter(this);
        mRecyclerView.setAdapter(mNavigationAdapter);
        mRecyclerView.setEmptyView(mEmptyView);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOverviewFragment.onRefresh();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview, menu);
        columnsMenu = menu.findItem(R.id.menu_columns);
        updateMenu();
        return super.onCreateOptionsMenu(menu);
    }


    private void updateMenu() {
        boolean useMultiple = shouldUseMultipleColumns();
        if (columnsMenu != null) {
            columnsMenu.setTitle(useMultiple ? getString(R.string.single_columns) : getString(R.string.multiple_columns));
        }
    }

    private boolean shouldUseMultipleColumns() {
        boolean useMultiple = false;
        if (getResources() != null) {
            android.content.res.Configuration config = getResources().getConfiguration();
            if (config != null) {
                switch (config.orientation) {
                    case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
                        useMultiple = mPrefUtilities.useMultipleColumnsLandscape();
                        break;
                    case android.content.res.Configuration.ORIENTATION_PORTRAIT:
                        useMultiple = mPrefUtilities.useMultipleColumnsPortrait();
                        break;
                }
            }
        } else {
            useMultiple = mPrefUtilities.useMultipleColumnsPortrait();
        }

        return useMultiple;
    }

    @Override
    public void onOpenPage(Page page) {
        Intent intent = new Intent(OverviewActivity.this, PageActivity.class);
        intent.putExtra(PageActivity.ARG_INFO, page);
        startActivity(intent);
    }

    @Override
    public void onPagesLoaded(final List<Page> pages) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                mNavigationAdapter.setPages(pages);
                drawerLayout.closeDrawers();
            }
        });
    }


    @Override
    public void onNavigationClicked(Page item) {
        mOverviewFragment.changePage(item);
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
            return;
        }
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return;
        }
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_exit)
                .content(R.string.dialog_sure_exit)
                .negativeText(R.string.dialog_answer_no)
                .positiveText(R.string.dialog_answer_yes)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        OverviewActivity.super.onBackPressed();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_columns:
                if (getResources() != null) {
                    Configuration config = getResources().getConfiguration();
                    if (config != null) {
                        switch (config.orientation) {
                            case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
                                mPrefUtilities.setMultipleColumnsLandscape(!mPrefUtilities.useMultipleColumnsLandscape());
                                break;
                            case android.content.res.Configuration.ORIENTATION_PORTRAIT:
                                mPrefUtilities.setMultipleColumnsPortrait(!mPrefUtilities.useMultipleColumnsPortrait());
                                break;
                        }
                    }
                }

                updateMenu();
                break;
            case R.id.action_settings:
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.container, new PrefFragment())
                        .commit();
                break;
            case R.id.action_start:
                startWelcome();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startWelcome() {
        mPrefUtilities.setLocation(null);
        mPrefUtilities.setLanguage(null);
        Intent intent = new Intent(OverviewActivity.this, WelcomeActivity.class);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("reset", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRefresh() {
        mOverviewFragment.onRefresh();
    }

    @Override
    public void onRefreshOverview() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }
}
