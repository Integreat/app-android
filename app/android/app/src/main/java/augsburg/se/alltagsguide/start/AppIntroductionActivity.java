package augsburg.se.alltagsguide.start;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.*;
import android.content.Context;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.GPSCoordinate;
import augsburg.se.alltagsguide.network.LocationLoader;
import augsburg.se.alltagsguide.overview.OverviewActivity;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import augsburg.se.alltagsguide.utilities.ui.BaseActivity;
import augsburg.se.alltagsguide.views.CurrentPageIndicatorView;
import roboguice.activity.RoboActionBarActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amadeus on 06. Nov. 2015.
 */
public class AppIntroductionActivity extends RoboActionBarActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, LoaderManager.LoaderCallbacks<List<augsburg.se.alltagsguide.common.Location>>,LocationSelectionFragment.OnLocationSelectedListener {
    private static final String PREFERENCE_STARTUP_COUNT = "startup_count";
    private static final String LOADING_TYPE_KEY = "FORCED";
    private static final String INSTANCE_STATE_LOCATIONS = "locations";
    private static final String INSTANCE_STATE_USER_COORDINATE = "user_coordinate";
    private static final String INSTANCE_STATE_VIEW_PAGER_POSITION = "page";
    private static final String INSTANCE_STATE_SEARCH = "search_string";

    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 0x43;
    private static final int[] COLORS = {0xffffc107, 0xffFF5722, 0xffCDDC39, 0xff8BC34A, 0xff4CAF50};

    private LocationManager locationManager;
    @Nullable private GPSCoordinate userLoction;

    private List<augsburg.se.alltagsguide.common.Location> locations;

    private LinearLayout container;
    private ViewPager viewPager;
    private ImageButton btnNext;
    private Button btnSkip;
    private FragmentPagerAdapter pagerAdapter;
    private ArrayList<ViewPagerAnimationFragment> fragments;
    private LocationSelectionFragment locationSelectionFragment;

    private CurrentPageIndicatorView currentPageIndicator;

    private PrefUtilities prefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            Serializable loc = savedInstanceState.getSerializable(INSTANCE_STATE_LOCATIONS);
            if(loc != null) this.locations = (List<augsburg.se.alltagsguide.common.Location>) loc;
            Serializable gps = savedInstanceState.getSerializable(INSTANCE_STATE_USER_COORDINATE);
            if(gps != null) this.userLoction = (GPSCoordinate) gps;
        }

        prefs = new PrefUtilities(this);

        if (prefs.getLocation() != null) {
            if(prefs.getLanguage() == null) {
                // location has already been selected, but language is NULL --> directly start LanguageSelectionActivity
                Intent languageSelectionIntent = new Intent(this, LanguageSelectionActivity.class);
                startActivity(languageSelectionIntent);
                finish();
            } else {
                // Language and Location have already been selected --> directly start main app
                Intent overviewIntent = new Intent(this, OverviewActivity.class);
                startActivity(overviewIntent);
                finish();
            }
        }

        setContentView(R.layout.activity_app_instruction);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!requestUserLocation()) {
            // Permission not granted => request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_COARSE_LOCATION);
        }

        currentPageIndicator = (CurrentPageIndicatorView) findViewById(R.id.appInstructionActivityPageIndicator);
        container = (LinearLayout)findViewById(R.id.appInstructionActivityContianer);
        btnNext = (ImageButton)findViewById(R.id.appInstructionActivityPageNext);
        btnSkip = (Button)findViewById(R.id.appInstructionActivityPageSkip);
        viewPager = (ViewPager) findViewById(R.id.appInstructionActivityViewPager);
        viewPager.addOnPageChangeListener(this);
        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        pagerAdapter = new FragmentPagerAdapter(this.getSupportFragmentManager());
        initInstructionPages(savedInstanceState);
        if(this.locations == null) refreshLocations(LoadingType.NETWORK_OR_DATABASE);
        viewPager.setAdapter(pagerAdapter);
        if(savedInstanceState != null) viewPager.setCurrentItem(savedInstanceState.getInt(INSTANCE_STATE_VIEW_PAGER_POSITION), false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Don't call super so that the we still have a reference to the fragments after an orientation change
        if(locations != null) outState.putSerializable(INSTANCE_STATE_LOCATIONS, new ArrayList<augsburg.se.alltagsguide.common.Location>(locations));
        outState.putSerializable(INSTANCE_STATE_USER_COORDINATE, this.userLoction);
        outState.putInt(INSTANCE_STATE_VIEW_PAGER_POSITION, this.viewPager.getCurrentItem());
        outState.putString(INSTANCE_STATE_SEARCH, this.locationSelectionFragment.getSearchString());
    }

    private void refreshLocations(LoadingType type) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(LOADING_TYPE_KEY, type);
        getSupportLoaderManager().restartLoader(0, bundle, this);
    }

    private void initInstructionPages(Bundle savedInstanceState) {
        // ToDo: Create Welcome-Pages (The Text) and use resources!
        AppInstructionPageFragment.AppInstructionPage page1 = new AppInstructionPageFragment.AppInstructionPage();
        page1.setTitle("Willkommen bei Integreat");
        page1.setDescription("Finde dich sofort in deiner Stadt zurecht - auch ohne Internetverbindung.");
        page1.setPictureResource(R.mipmap.ic_launcher);

        AppInstructionPageFragment.AppInstructionPage page2 = new AppInstructionPageFragment.AppInstructionPage();
        page2.setTitle("Deutschkurse in deiner Nähe");
        page2.setDescription("Lorem ipsum ksalsa sdklf äasfk äasfkäsadf äasfkaäsfkaäf ksafjkfsaödlfj dsaöf lk.");
        page2.setPictureResource(R.drawable.brandenburger_tor);

        AppInstructionPageFragment.AppInstructionPage page3 = new AppInstructionPageFragment.AppInstructionPage();
        page3.setTitle("Vereinfache deine Amtsbesuche");
        page3.setDescription("Erstelle deinen Lebenslauf und lasse ihn automatisch übersetzen - ganz ohne Dolmetscher.");
        page3.setPictureResource(R.drawable.brandenburger_tor);

        AppInstructionPageFragment fragment1 = new AppInstructionPageFragment();
        fragment1.setPage(page1);
        AppInstructionPageFragment fragment2 = new AppInstructionPageFragment();
        fragment2.setPage(page2);
        AppInstructionPageFragment fragment3 = new AppInstructionPageFragment();
        fragment3.setPage(page3);

        this.locationSelectionFragment = new LocationSelectionFragment();
        this.locationSelectionFragment.setUsersLocation(userLoction);
        this.locationSelectionFragment.setAddPadding(true);
        this.locationSelectionFragment.setOnLocationSelectedListener(this);
        this.locationSelectionFragment.setLocations(this.locations);
        this.locationSelectionFragment.setShowTitles(true);
        this.locationSelectionFragment.setShowSearch(true);
        if(savedInstanceState != null) this.locationSelectionFragment.setSearchString(savedInstanceState.getString(INSTANCE_STATE_SEARCH));

        if(this.locations != null) this.locationSelectionFragment.setLocations(this.locations);


        fragments = new ArrayList<>();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        fragments.add(this.locationSelectionFragment);
        pagerAdapter.setFragments(fragments);
        currentPageIndicator.setPagesCount(fragments.size());
    }

    private boolean requestUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            userLoction = GPSCoordinate.fromLocation(location);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestUserLocation();
                    this.locationSelectionFragment.setUsersLocation(userLoction);
                } else {
                    // The user didn't granted the location permission.
                }
                return;
            }
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPageIndicator.setCurrentScrollLeft(positionOffset);
        currentPageIndicator.setCurrentSelectedPage(position);


        // Change background color depending on scroll position
        int bg = interpolateColor(positionOffset, COLORS[position], COLORS[position+1]);
        container.setBackgroundColor(bg);

        // For the animation of views of the fragments, give them their scroll position
        fragments.get(position).setScrollOffset(-positionOffset);
        if(position+1 < fragments.size()) fragments.get(position+1).setScrollOffset(1-positionOffset);

        // If the users scrolls to the last item -> hide skip & next-buttons
        if(position == fragments.size()-2 && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            btnSkip.setAlpha(1-positionOffset);
            btnNext.setAlpha(1-positionOffset);
        }
        if(position == fragments.size()-1) {
            btnSkip.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.INVISIBLE);
        } else {
            btnSkip.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageSelected(int position) {
        //currentPageIndicator.setCurrentSelectedPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public int interpolateColor(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;
        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;
        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }

    @Override
    public void onClick(View v) {
        if(v == btnNext) {
            if(viewPager.getCurrentItem() < pagerAdapter.getCount()-1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);
            }
        } else if(v == btnSkip) {
            viewPager.setCurrentItem(fragments.size()-1, true);
        }
    }

    @Override
    public Loader<List<augsburg.se.alltagsguide.common.Location>> onCreateLoader(int id, Bundle args) {
        LoadingType loadingType = (LoadingType) args.getSerializable(LOADING_TYPE_KEY);
        return new LocationLoader(this, loadingType);
    }

    @Override
    public void onLoadFinished(Loader<List<augsburg.se.alltagsguide.common.Location>> loader, List<augsburg.se.alltagsguide.common.Location> data) {
        this.locations = data;
        if(this.locationSelectionFragment != null) this.locationSelectionFragment.setLocations(this.locations);
    }

    @Override
    public void onLoaderReset(Loader<List<augsburg.se.alltagsguide.common.Location>> loader) {

    }

    @Override
    public void onLocationSelected(augsburg.se.alltagsguide.common.Location location) {
        prefs.setLocation(location);

        Intent intent = new Intent(this, LanguageSelectionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onForceReloadLocations() {
        refreshLocations(LoadingType.FORCE_NETWORK);
    }
}



class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private ArrayList<ViewPagerAnimationFragment> fragments;

    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ArrayList<ViewPagerAnimationFragment> getFragments() {
        return fragments;
    }

    public void setFragments(ArrayList<ViewPagerAnimationFragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return getFragments().get(position);
    }

    @Override
    public int getCount() {
        if(getFragments() == null) return 0;
        return getFragments().size();
    }
}
