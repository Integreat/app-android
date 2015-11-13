package augsburg.se.alltagsguide.overview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import augsburg.se.alltagsguide.start.LocationSelectionActivity;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.event.EventActivity;
import augsburg.se.alltagsguide.event.EventOverviewFragment;
import augsburg.se.alltagsguide.navigation.NavigationAdapter;
import augsburg.se.alltagsguide.network.LanguageLoader;
import augsburg.se.alltagsguide.page.PageActivity;
import augsburg.se.alltagsguide.page.PageOverviewFragment;
import augsburg.se.alltagsguide.settings.SettingsActivity;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.ui.BaseActivity;
import augsburg.se.alltagsguide.utilities.ui.BaseFragment;
import augsburg.se.alltagsguide.utilities.ui.EmptyRecyclerView;
import augsburg.se.alltagsguide.utilities.ui.LanguageItemAdapter;
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

    private static final String LOADING_TYPE_KEY = "FORCED";

    // delay to launch nav drawer item, to allow close animation to play
    private static final long NAVDRAWER_LAUNCH_DELAY = 300;
    private static final String OTHER_LANGUAGES_KEY = "OTHER_LANGUAGE_KEY";

    private NavigationAdapter mNavigationAdapter;

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

    @InjectView(R.id.locationName)
    private TextView locationNameTextView;

    @InjectView(R.id.locationDescription)
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

    @InjectView(R.id.pager_tab_strip)
    private PagerTabStrip pagerTabStrip;
    @Inject
    protected Picasso mPicasso;

    @InjectView(R.id.pager)
    private ViewPager mViewPager;

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
        changeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationSelection();
            }
        });
        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OverviewActivity.this, SettingsActivity.class));
            }
        });
        initPager();
        initNavigationDrawer();
    }

    @Override
    protected void changeTabColor(Drawable drawable, int color) {
        pagerTabStrip.setBackgroundDrawable(drawable);
    }

    private void initPager() {
        mViewPager.setAdapter(new InformationFragmentPagerAdapter());
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
                loadOtherLanguages(LoadingType.FORCE_DATABASE);
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
                if (!mOtherLanguages.isEmpty()) {
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
                } else {
                    Snackbar.make(mViewPager, R.string.no_other_languages_available_location, Snackbar.LENGTH_SHORT).show();
                }
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
        Page selectedPage = mNavigationAdapter.getSelectedPage();
        if (selectedPage != null) {
            for (AvailableLanguage lang : selectedPage.getAvailableLanguages()) {
                if (lang.getLoadedLanguage() != null) {
                    if (Objects.equals(lang.getLoadedLanguage().getId(), language.getId())) {
                        mPrefUtilities.setSelectedPage(lang.getOtherPageId());
                        break;
                    }
                }
            }
        }
        mLanguage = language;
        mPrefUtilities.setLanguage(language);

        startLoading();
        if (mPageOverviewFragment != null) {
            mPageOverviewFragment.refresh(LoadingType.NETWORK_OR_DATABASE);
        }
        if (mEventOverviewFragment != null) {
            mEventOverviewFragment.refresh(LoadingType.NETWORK_OR_DATABASE);
        }
        loadOtherLanguages(LoadingType.NETWORK_OR_DATABASE);
    }

    private void loadOtherLanguages(LoadingType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(LOADING_TYPE_KEY, type);
        getSupportLoaderManager().restartLoader(0, bundle, this);
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
                if (mPageOverviewFragment != null) {
                    mPageOverviewFragment.filterByText(newText);
                }
                if (mEventOverviewFragment != null) {
                    mEventOverviewFragment.filterByText(newText);
                }
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
        if (item.getSubPages().isEmpty()) {
            mPrefUtilities.setSelectedPage(-1);
            onOpenPage(item);
            return;
        }
        mPrefUtilities.setSelectedPage(item.getId());
        if (mNavigationAdapter != null) {
            mNavigationAdapter.setSelectedIndex(item.getId());
            mNavigationAdapter.notifyDataSetChanged();
        }
        if (mPageOverviewFragment != null) {
            mPageOverviewFragment.indexUpdated();
        }
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


    private void startLocationSelection() {
        Intent intent = new Intent(this, LocationSelectionActivity.class);
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
    }

    @Override
    public Loader<List<Language>> onCreateLoader(int id, Bundle args) {
        LoadingType loadingType = (LoadingType) args.getSerializable(LOADING_TYPE_KEY);
        return new LanguageLoader(this, mLocation, loadingType);
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

    public class InformationFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;

        public InformationFragmentPagerAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return EventOverviewFragment.newInstance();
            }
            return PageOverviewFragment.newInstance();
        }

        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getResources().getString(R.string.event_list_title);
            }
            return getResources().getString(R.string.information);
        }

        // Here we can finally safely save a reference to the created
        // Fragment, no matter where it came from (either getItem() or
        // FragmentManger). Simply save the returned Fragment from
        // super.instantiateItem() into an appropriate reference depending
        // on the ViewPager position.
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    mPageOverviewFragment = (PageOverviewFragment) createdFragment;
                    break;
                case 1:
                    mEventOverviewFragment = (EventOverviewFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }
    }
}
