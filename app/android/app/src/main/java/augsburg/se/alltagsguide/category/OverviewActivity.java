package augsburg.se.alltagsguide.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.common.Category;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.article.ArticleActivity;
import augsburg.se.alltagsguide.navigation.NavigationAdapter;
import augsburg.se.alltagsguide.navigation.NavigationItem;
import augsburg.se.alltagsguide.network.NetworkHandler;
import augsburg.se.alltagsguide.network.NetworkHandlerMock;
import augsburg.se.alltagsguide.network.SimpleCallback;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import retrofit.client.Response;

public class OverviewActivity extends BaseActivity
        implements CategoryFragment.OnCategoryFragmentInteractionListener {

    private Toolbar mToolbar;
    private DrawerLayout drawerLayout;

    private List<Category> mCategories;
    private CategoryFragment mCategoryFragment;
    private Location mLocation;
    private Language mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = PrefUtilities.getInstance().getLocation();
        mLanguage = PrefUtilities.getInstance().getLanguage();

        setContentView(R.layout.activity_overview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        NetworkHandler networkHandler = new NetworkHandlerMock();
        networkHandler.getContent(PrefUtilities.getInstance().getLanguage(), PrefUtilities.getInstance().getLocation(), "1", new SimpleCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories, Response response) {
                mCategories = categories;
                mCategoryFragment = CategoryFragment.newInstance(mCategories.get(0));
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mCategoryFragment)
                        .commit();
            }
        });

        initNavigationDrawer();
    }

    @Override
    protected boolean setDisplayHomeAsUp() {
        return false;
    }

    private void initNavigationDrawer() {
        TextView locationName = (TextView) findViewById(R.id.location);
        locationName.setText(mLocation.getName());

        TextView url = (TextView) findViewById(R.id.url);
        url.setText(mLocation.getUrl());

        ImageView flagImageView = (ImageView) findViewById(R.id.language);
        if (mLanguage.getIconPath() != null) {
            Picasso.with(this)
                    .load(mLanguage.getIconPath())
                    .placeholder(R.drawable.placeholder_language)
                    .error(R.drawable.placeholder_language)
                    .into(flagImageView);
        }

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        RecyclerView recyclerView = (RecyclerView) navigationView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NavigationAdapter(getNavigationItems(), new NavigationAdapter.OnNavigationSelected() {
            @Override
            public void onNavigationClicked(NavigationItem item) {
                mCategoryFragment.changeCategory(item.getCategory());
                drawerLayout.closeDrawers();
            }
        }));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
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

    public void fillNavigationItemsFromContent(List<NavigationItem> items, Category category, int depth) {
        boolean hasItems = category.getSubContent() != null && !category.getSubContent().isEmpty();
        items.add(new NavigationItem(category, depth));
        if (hasItems) {
            for (Category subCategory : category.getSubContent()) {
                fillNavigationItemsFromContent(items, subCategory, depth + 1);
            }
        }
    }

    public List<NavigationItem> getNavigationItems() {
        List<NavigationItem> items = new ArrayList<>();
        for (Category category : mCategories) {
            fillNavigationItemsFromContent(items, category, 0);
        }
        return items;
    }
}
