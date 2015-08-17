package augsburg.se.alltagsguide.information;

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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Content;
import augsburg.se.alltagsguide.common.Information;
import augsburg.se.alltagsguide.navigation.NavigationAdapter;
import augsburg.se.alltagsguide.navigation.NavigationItem;
import augsburg.se.alltagsguide.network.NetworkHandler;
import augsburg.se.alltagsguide.network.NetworkHandlerMock;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.PrefUtilities;

public class OverviewActivity extends BaseActivity
        implements ContentFragment.OnContentFragmentInteractionListener {

    private Toolbar mToolbar;
    private DrawerLayout drawerLayout;

    private List<Content> mContents;
    private ContentFragment mContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NetworkHandler networkHandler = new NetworkHandlerMock();
        mContents = networkHandler.getContent(PrefUtilities.getInstance().getLanguage(), PrefUtilities.getInstance().getLocation());

        mContentFragment = ContentFragment.newInstance(mContents.get(0));
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mContentFragment)
                .commit();
        initNavigationDrawer();
    }


    private void initNavigationDrawer() {
        ImageView flagImageView = (ImageView) findViewById(R.id.language);
        Picasso.with(this)
                .load("https://upload.wikimedia.org/wikipedia/commons/thumb/8/86/Flag_of_Germany_(3-2_aspect_ratio).svg/2000px-Flag_of_Germany_(3-2_aspect_ratio).svg.png")
                .placeholder(R.drawable.placeholder_language)
                .error(R.drawable.placeholder_language)
                .into(flagImageView);

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        RecyclerView recyclerView = (RecyclerView) navigationView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NavigationAdapter(getNavigationItems(), new NavigationAdapter.OnNavigationSelected() {
            @Override
            public void onNavigationClicked(NavigationItem item) {
                mContentFragment.changeContent(item.getContent());
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
    public void onInformationClicked(Information information) {

    }

    public void fillNavigationItemsFromContent(List<NavigationItem> items, Content content, int depth) {
        boolean hasItems = content.getSubContent() != null && !content.getSubContent().isEmpty();
        items.add(new NavigationItem(content, depth));
        if (hasItems) {
            for (Content subContent : content.getSubContent()) {
                fillNavigationItemsFromContent(items, subContent, depth + 1);
            }
        }
    }

    public List<NavigationItem> getNavigationItems() {
        List<NavigationItem> items = new ArrayList<>();
        for (Content content : mContents) {
            fillNavigationItemsFromContent(items, content, 0);
        }
        return items;
    }
}
