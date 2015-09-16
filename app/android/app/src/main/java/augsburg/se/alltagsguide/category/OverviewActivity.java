package augsburg.se.alltagsguide.category;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.article.ArticleActivity;
import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.common.Category;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.navigation.NavigationAdapter;
import augsburg.se.alltagsguide.network.CategoryLoader;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.EmptyRecyclerView;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_overview)
public class OverviewActivity extends BaseActivity
        implements CategoryFragment.OnCategoryFragmentInteractionListener, LoaderManager.LoaderCallbacks<List<Category>>, NavigationAdapter.OnNavigationSelected {

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

    private CategoryFragment mCategoryFragment;
    private NavigationAdapter mNavigationAdapter;
    private Location mLocation;
    private Language mLanguage;


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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onArticleClicked(Article article) {
        Intent intent = new Intent(OverviewActivity.this, ArticleActivity.class);
        intent.putExtra(ArticleActivity.ARG_INFO, article);

        startActivity(intent);
    }

    @Override
    public void onCategoryClicked(Category category) {
        onNavigationClicked(category);
    }

    @Override
    public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
        return new CategoryLoader(this, mLocation, mLanguage);
    }

    @Override
    public void onLoadFinished(Loader<List<Category>> loader, final List<Category> data) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mNavigationAdapter.setCategory(data.get(0));
                mCategoryFragment = CategoryFragment.newInstance(data.get(0)); //TODO select 0
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mCategoryFragment)
                        .commit();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<Category>> loader) {
    }

    @Override
    public void onNavigationClicked(Category item) {
        mCategoryFragment.changeCategory(item);
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
}
