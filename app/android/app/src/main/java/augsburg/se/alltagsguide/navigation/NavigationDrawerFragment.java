package augsburg.se.alltagsguide.navigation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Content;
import augsburg.se.alltagsguide.utilities.PrefUtilities;


public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private NavigationDrawerCallbacks mCallbacks;
    private RecyclerView mDrawerList;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private int mCurrentSelectedPosition;
    private List<Content> mContents;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);

        final List<NavigationItem> navigationItems = getMenu();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(navigationItems);
        adapter.setNavigationDrawerCallbacks(this);
        mDrawerList.setAdapter(adapter);
        selectItem(mCurrentSelectedPosition);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = PrefUtilities.getInstance().getDrawerLearned();
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
        mActionBarDrawerToggle = actionBarDrawerToggle;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar, List<Content> contents) {
        mContents = contents;
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        if (mFragmentContainerView.getParent() instanceof ScrimInsetsFrameLayout) {
            mFragmentContainerView = (View) mFragmentContainerView.getParent();
        }
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.myPrimaryDarkColor));

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    PrefUtilities.getInstance().setDrawerLearned(true);
                }

                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState)
            mDrawerLayout.openDrawer(mFragmentContainerView);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void fillNavigationItemsFromContent(List<NavigationItem> items, Content content, int depth) {
        items.add(new NavigationItem(content.getTitle(), content.getDescription(), depth));
        if (content.getSubContent() != null) {
            for (Content subContent : content.getSubContent()) {
                fillNavigationItemsFromContent(items, subContent, depth + 1);
            }
        }
    }

    public List<NavigationItem> getMenu() {
        List<NavigationItem> items = new ArrayList<>();
        for (Content content : mContents) {
            fillNavigationItemsFromContent(items, content, 0);
        }
        /*items.add(new NavigationItem("Begrüßung", NavigationItem.ItemType.CATEGORY));

        items.add(new NavigationItem("Ankunft", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Schritte", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Übersicht", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Ausblick", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Deutsch lernen", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Offizielle Angebote", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Nachhilfeangebote", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Weiterbildung", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Dolmetscher", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Schulanmeldung", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Ablauf", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Bildung- und Teilhabeförderung", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Kontakt", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Lebensmittel und Kleidung", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Anlaufstellen", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Hilfreiche Tipps", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Gesundheit", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Einleitung", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Ärzte", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Beratung", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Arbeit in Augsburg", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Informationen", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Anlaufstellen", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Fortbewegung", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Tram & Bus", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Fahrrad", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Fahrt nach München", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Banken und Versicherungen", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Tägliches Leben", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Ansprechpartner", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Stadt und Religion", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Bürgerbüro", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Sozialberatung", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Religionshäuser", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Sonst. Einrichtungen", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Leben in Augsburg", NavigationItem.ItemType.CATEGORY));
        items.add(new NavigationItem("Über Augsburg", NavigationItem.ItemType.ITEM));
        items.add(new NavigationItem("Freizeitbeschäftigungen", NavigationItem.ItemType.ITEM));

        items.add(new NavigationItem("Glossar", NavigationItem.ItemType.CATEGORY));*/
        return items;
    }

    /**
     * Changes the icon of the drawer to back
     */
    public void showBackButton() {
        if (getActivity() instanceof ActionBarActivity) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Changes the icon of the drawer to menu
     */
    public void showDrawerButton() {
        if (getActivity() instanceof ActionBarActivity) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        mActionBarDrawerToggle.syncState();
    }

    void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
        ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }
}
