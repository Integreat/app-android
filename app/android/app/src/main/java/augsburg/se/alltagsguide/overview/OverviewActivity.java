package augsburg.se.alltagsguide.overview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.Loader;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.event.EventActivity;
import augsburg.se.alltagsguide.event.EventOverviewFragment;
import augsburg.se.alltagsguide.navigation.NavigationAdapter;
import augsburg.se.alltagsguide.network.LanguageLoader;
import augsburg.se.alltagsguide.page.PageActivity;
import augsburg.se.alltagsguide.settings.SettingsActivity;
import augsburg.se.alltagsguide.start.WelcomeActivity;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.EmptyRecyclerView;
import augsburg.se.alltagsguide.utilities.LanguageItemAdapter;
import augsburg.se.alltagsguide.utilities.Objects;
import de.hdodenhof.circleimageview.CircleImageView;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_overview)
public class OverviewActivity extends BaseActivity
        implements
        LoaderManager.LoaderCallbacks<List<Language>>,
        PageOverviewFragment.OnPageFragmentInteractionListener,
        EventOverviewFragment.OnEventPageFragmentInteractionListener,
        BaseFragment.OnBaseFragmentInteractionListener,
        NavigationAdapter.OnNavigationSelected {

    // delay to launch nav drawer item, to allow close animation to play
    private static final long NAVDRAWER_LAUNCH_DELAY = 300;
    private static final String OTHER_LANGUAGES_KEY = "OTHER_LANGUAGE_KEY";

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

    @InjectView(R.id.header_image_view)
    private ImageView navigationHeaderImageView;

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

    @InjectView(R.id.drawer)
    private DrawerLayout drawerLayout;

    @InjectView(R.id.other_language_count)
    protected TextView otherLanguageCountTextView;

    @InjectView(R.id.current_language)
    protected CircleImageView circleImageView;

    @Inject
    protected Picasso mPicasso;


    private PageOverviewFragment mPageOverviewFragment;
    private EventOverviewFragment mEventOverviewFragment;

    private Location mLocation;
    private Language mLanguage;

    private List<Language> mOtherLanguages = new ArrayList<>();
    private MenuItem columnsMenu;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = mPrefUtilities.getLocation();
        mLanguage = mPrefUtilities.getLanguage();
        mHandler = new Handler();
        initNavigationDrawer();
        if (savedInstanceState == null) {
            mPageOverviewFragment = PageOverviewFragment.newInstance();
            mEventOverviewFragment = EventOverviewFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.events_container, mEventOverviewFragment, "events")
                    .commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.pages_container, mPageOverviewFragment, "pages")
                    .commit();
        } else {
            mEventOverviewFragment = (EventOverviewFragment) getSupportFragmentManager().findFragmentByTag("events");
            mPageOverviewFragment = (PageOverviewFragment) getSupportFragmentManager().findFragmentByTag("pages");
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable(OTHER_LANGUAGES_KEY);
            if (serializable != null) {
                // set languages button again from the data we already have
                ArrayList<Language> others = (ArrayList<Language>) serializable;
                setLanguageButton(others);
            } else {
                // we need to only hookup the other-languages call as the fragments state should be restored
                loadOtherLanguages();
            }
        } else {
            // init everything
            loadLanguage(mLanguage);
        }
    }

    @Override
    protected boolean setDisplayHomeAsUp() {
        return false;
    }

    private void setLanguageButton(@NonNull List<Language> others) {
        mOtherLanguages = others;
        mPicasso.load(mLanguage.getIconPath())
                .placeholder(R.drawable.icon_language_loading)
                .error(R.drawable.icon_language_loading_error)
                .fit()
                .into(circleImageView);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(OverviewActivity.this)
                        .title(R.string.dialog_choose_language_title)
                        .adapter(new LanguageItemAdapter(OverviewActivity.this, mOtherLanguages),
                                new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        loadLanguage(mOtherLanguages.get(which));
                                        dialog.cancel();
                                    }
                                })
                        .show();
            }
        });
        updateLanguageCount();
    }

    @SuppressLint("SetTextI18n")
    protected void setupLanguagesButton(@NonNull List<Language> data) {
        data.remove(mLanguage);
        setLanguageButton(data);
    }

    @SuppressLint("SetTextI18n")
    private void updateLanguageCount() {
        otherLanguageCountTextView.setText("+" + mOtherLanguages.size());
    }

    private void loadLanguage(@NonNull Language language) {
        mLanguage = language;
        mPrefUtilities.setLanguage(language);

        startLoading();
        mPageOverviewFragment.refresh(false);
        mEventOverviewFragment.refresh(false);
        loadOtherLanguages();
    }

    private void loadOtherLanguages() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    private void initNavigationDrawer() {
        locationNameTextView.setText(mLocation.getName());
        locationDescriptionTextView.setText(mLocation.getDescription());

        String iconPath = mLocation.getCityImage();
        Ln.d("Icon path for city image is: " + iconPath);
        if (!Objects.isNullOrEmpty(mLocation.getCityImage())) {
            mPicasso.load(mLocation.getCityImage())
                    .error(R.drawable.brandenburger_tor)
                    .placeholder(R.drawable.brandenburger_tor)
                    .into(navigationHeaderImageView);
        } else {
            Ln.e("ImagePath should never be null!");
        }

        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNavigationAdapter = new NavigationAdapter(this, mPrefUtilities.getCurrentColor(), this, mPrefUtilities.getSelectedPageId());
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
                mPrefUtilities.setSelectedPage(-1);
                if (mNavigationAdapter != null) {
                    mNavigationAdapter.setSelectedIndex(-1);
                    mNavigationAdapter.notifyDataSetChanged();
                }
                mPageOverviewFragment.indexUpdated();
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
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mPageOverviewFragment.filterByText(newText);
                mEventOverviewFragment.filterByText(newText);
                return false;
            }
        });
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
    public void onOpenPage(@NonNull Page page) {
        Intent intent = new Intent(OverviewActivity.this, PageActivity.class);
        intent.putExtra(PageActivity.ARG_INFO, page);
        startActivity(intent);
    }


    @Override
    public void onPagesLoaded(@NonNull final List<Page> pages) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mNavigationAdapter.setPages(pages);
                drawerLayout.closeDrawers();
                stopLoading();
            }
        });
    }


    @Override
    public void onNavigationClicked(@NonNull final Page item) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNavDrawerItem(item);
            }
        }, NAVDRAWER_LAUNCH_DELAY);
        drawerLayout.closeDrawers();
    }

    private void goToNavDrawerItem(@NonNull Page item) {
        mPrefUtilities.setSelectedPage(item.getId());
        if (mNavigationAdapter != null) {
            mNavigationAdapter.setSelectedIndex(item.getId());
            mNavigationAdapter.notifyDataSetChanged();
        }
        mPageOverviewFragment.indexUpdated();
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
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        mPrefUtilities.setSelectedPage(-1);
                        materialDialog.hide();
                        OverviewActivity.super.onBackPressed();
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
    public void onOpenEventPage(@NonNull EventPage page) {
        Intent intent = new Intent(OverviewActivity.this, EventActivity.class);
        intent.putExtra(EventActivity.ARG_INFO, page);
        startActivity(intent);
    }


    @Override
    public void onEventPagesLoaded(@NonNull final List<EventPage> pages) {
        new Handler().post(new Runnable() {
            final int messageCode = pages.isEmpty() ? 0 : 1;

            public void run() {
                try {
                    if (messageCode == 1) {
                        getSupportFragmentManager().beginTransaction()
                                .show(mEventOverviewFragment)
                                .commitAllowingStateLoss();
                        mEventContainerView.setVisibility(View.VISIBLE);
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .hide(mEventOverviewFragment)
                                .commitAllowingStateLoss();
                        mEventContainerView.setVisibility(View.GONE);
                    }
                    mContentView.invalidate();
                } catch (IllegalStateException e) {
                    // Ignore if this happens
                    Ln.e(e);
                }
            }
        });
    }

    @Override
    public Loader<List<Language>> onCreateLoader(int id, Bundle args) {
        boolean forced = false;
        if (args != null && args.containsKey("FORCED")) {
            forced = args.getBoolean("FORCED");
        }
        return new LanguageLoader(this, mLocation, forced);
    }

    @Override
    public void onLoadFinished(Loader<List<Language>> loader, @NonNull List<Language> data) {
        setupLanguagesButton(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Language>> loader) {
    }

    private MaterialDialog mLoadingDialog;

    private void startLoading() {
        stopLoading();
        mLoadingDialog = new MaterialDialog.Builder(OverviewActivity.this)
                .title(R.string.dialog_title_loading_language)
                .content(R.string.dialog_content_loading_language)
                .progress(true, 0)
                .show();
    }

    private void stopLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
        mLoadingDialog = null;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(OTHER_LANGUAGES_KEY, new ArrayList<>(mOtherLanguages));
        super.onSaveInstanceState(outState);
    }

}
