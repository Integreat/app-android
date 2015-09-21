package augsburg.se.alltagsguide.page;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.navigation.NavigationAdapter;
import augsburg.se.alltagsguide.network.PagesLoader;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.EmptyRecyclerView;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_overview)
public class OverviewActivity extends BaseActivity
        implements PagesFragment.OnPageFragmentInteractionListener, LoaderManager.LoaderCallbacks<List<Page>>, NavigationAdapter.OnNavigationSelected {

    @InjectView(R.id.header)
    private View navigationHeaderView;

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

    private PagesFragment mPagesFragment;
    private NavigationAdapter mNavigationAdapter;
    private Location mLocation;
    private Language mLanguage;

    private MenuItem columnsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = mPrefUtilities.getLocation();
        mLanguage = mPrefUtilities.getLanguage();
        initNavigationDrawer();
        getSupportLoaderManager().initLoader(0, null, this);
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
    public Loader<List<Page>> onCreateLoader(int id, Bundle args) {
        return new PagesLoader(this, mLocation, mLanguage);
    }

    @Override
    public void onLoadFinished(Loader<List<Page>> loader, final List<Page> pages) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mNavigationAdapter.setPages(pages);
                mPagesFragment = PagesFragment.newInstance(pages);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mPagesFragment)
                        .commit();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<Page>> loader) {
    }

    @Override
    public void onNavigationClicked(Page item) {
        mPagesFragment.changePage(item);
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title("Exit?")
                .content("Are you sure you want to exit?")
                .negativeText("No")
                .positiveText("Yes")
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
}
