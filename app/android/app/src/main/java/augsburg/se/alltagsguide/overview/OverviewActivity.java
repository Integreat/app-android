package augsburg.se.alltagsguide.overview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.event.EventOverviewFragment;
import augsburg.se.alltagsguide.navigation.NavigationAdapter;
import augsburg.se.alltagsguide.page.PageActivity;
import augsburg.se.alltagsguide.settings.SettingsActivity;
import augsburg.se.alltagsguide.start.WelcomeActivity;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.EmptyRecyclerView;
import augsburg.se.alltagsguide.utilities.Objects;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_overview)
public class OverviewActivity extends BaseActivity
        implements PageOverviewFragment.OnPageFragmentInteractionListener, EventOverviewFragment.OnEventPageFragmentInteractionListener, BaseFragment.OnBaseFragmentInteractionListener, NavigationAdapter.OnNavigationSelected {

    private NavigationAdapter mNavigationAdapter;

    @InjectView(R.id.content)
    private View mContentView;

    @InjectView(R.id.events_container)
    private View mEventContainerView;

    @InjectView(R.id.emptyNavView)
    private View mEmptyView;

    @InjectView(R.id.recycler_view_nav)
    private EmptyRecyclerView mRecyclerView;

    @InjectView(R.id.navigation)
    private NavigationView navigationView;

    @InjectView(R.id.header)
    private View navigationHeaderView;

    @InjectView(R.id.location)
    private TextView locationNameTextView;

    @InjectView(R.id.description)
    private TextView locationDescriptionTextView;

    @InjectView(R.id.settings)
    private View settingsView;

    @InjectView(R.id.change_login)
    private View changeLogin;

    @InjectView(R.id.language)
    private ImageView languageFlagImageView;

    @InjectView(R.id.drawer)
    private DrawerLayout drawerLayout;

    private EventPagesHandler eventPagesHandler = new EventPagesHandler(this);
    private PageOverviewFragment mPageOverviewFragment;
    private EventOverviewFragment mEventOverviewFragment;

    private Location mLocation;
    private Language mLanguage;

    private MenuItem columnsMenu;
    private List<Page> mPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = mPrefUtilities.getLocation();
        mLanguage = mPrefUtilities.getLanguage();
        initNavigationDrawer();
        mEventOverviewFragment = EventOverviewFragment.newInstance();
        mPageOverviewFragment = PageOverviewFragment.newInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.events_container, mEventOverviewFragment)
                    .commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.pages_container, mPageOverviewFragment)
                    .commit();
        }
        changeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWelcome();
            }
        });
        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OverviewActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    protected boolean setDisplayHomeAsUp() {
        return false;
    }

    private void initNavigationDrawer() {
        locationNameTextView.setText(mLocation.getName());
        locationDescriptionTextView.setText(mLocation.getDescription());

        if (mLanguage.getIconPath() != null) {
            Picasso.with(this)
                    .load(mLanguage.getIconPath())
                    .placeholder(R.drawable.empty_locations_white)
                    .error(R.drawable.ic_location_not_found_black)
                    .into(languageFlagImageView);
        }
        Picasso.with(this)
                .load(mLocation.getCityImage())
                .placeholder(R.drawable.ic_no_language_found_black)
                .error(R.drawable.ic_no_language_found_black)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            navigationHeaderView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                        } else {
                            navigationHeaderView.setBackground(new BitmapDrawable(getResources(), bitmap));
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        navigationHeaderView.setBackgroundResource(R.drawable.ic_location_not_found_black);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        navigationHeaderView.setBackgroundResource(R.drawable.ic_location_not_found_black);
                    }
                });

        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNavigationAdapter = new NavigationAdapter(this);
        mRecyclerView.setAdapter(mNavigationAdapter);


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
                mPageOverviewFragment.onRefresh();
                mEventOverviewFragment.onRefresh();
                drawerLayout.closeDrawers();
            }
        });
    }

    SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener = null;

    @Override
    protected void onResume() {
        super.onResume();
        mPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String string) {
                if (Objects.equals(string, "font_style")) {
                    restartActivity();
                }
            }
        };
        mPrefUtilities.addListener(mPreferenceListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mPreferenceListener != null) {
            mPrefUtilities.removeListener(mPreferenceListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview, menu);
        columnsMenu = menu.findItem(R.id.menu_columns);
        updateMenu();
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterByText(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filterByText(String filterText) {
        List<Page> pages = new ArrayList<>();
        for (Page page : mPages) {
            String relevantContent = page.getTitle();
            if (page.getContent() != null) {
                relevantContent += page.getContent();
            }

            if (relevantContent.toLowerCase().contains(filterText.toLowerCase())) {
                pages.add(page);
            }
        }
        mPageOverviewFragment.setPages(pages);
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
                mPages = pages;
                mNavigationAdapter.setPages(pages);
                drawerLayout.closeDrawers();
            }
        });
    }


    @Override
    public void onNavigationClicked(Page item) {
        mPageOverviewFragment.changePage(item);
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
        }
        return super.onOptionsItemSelected(item);
    }


    private void startWelcome() {
        mPrefUtilities.setLocation(null);
        mPrefUtilities.setLanguage(null);
        Intent intent = new Intent(OverviewActivity.this, WelcomeActivity.class);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onOpenEventPage(EventPage page) {

    }


    @Override
    public void onEventPagesLoaded(final List<EventPage> pages) {
        int messageCode = pages == null || pages.isEmpty() ? 0 : 1;
        eventPagesHandler.sendEmptyMessage(messageCode);
    }

    static class EventPagesHandler extends Handler {
        private final WeakReference<OverviewActivity> mActivity;

        EventPagesHandler(OverviewActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            OverviewActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity.getSupportFragmentManager().beginTransaction()
                            .show(activity.mEventOverviewFragment)
                            .commit();
                    activity.mEventContainerView.setVisibility(View.VISIBLE);
                } else {
                    activity.getSupportFragmentManager().beginTransaction()
                            .hide(activity.mEventOverviewFragment)
                            .commit();
                    activity.mEventContainerView.setVisibility(View.GONE);
                }
                activity.mContentView.invalidate();
            }

        }
    }

}
