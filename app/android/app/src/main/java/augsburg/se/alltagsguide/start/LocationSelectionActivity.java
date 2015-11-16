package augsburg.se.alltagsguide.start;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.GPSCoordinate;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.LocationLoader;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import augsburg.se.alltagsguide.utilities.ui.BaseActivity;
import com.google.inject.Inject;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

import java.util.List;

/**
 * Created by Amadeus on 11. Nov. 2015.
 */
public class LocationSelectionActivity extends BaseActivity implements LocationSelectionFragment.OnLocationSelectedListener, LoaderManager.LoaderCallbacks<List<Location>>, SearchView.OnQueryTextListener {
    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 0x43;
    private static final String LOADING_TYPE_KEY = "FORCED";

    @Inject private PrefUtilities prefs;
    private LocationSelectionFragment fragment;

    private LocationManager locationManager;
    @Nullable
    private GPSCoordinate userLocation;
    private List<Location> locations;

    private SearchView searchView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        setTitle(getString(R.string.location_fragment_title));
        setSubTitle(getString(R.string.location_fragment_subtitle));



        if (savedInstanceState == null) {
            // Add Fragment
            fragment = new LocationSelectionFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        } else {
            // Get fragment
            fragment = (LocationSelectionFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }
        fragment.setOnLocationSelectedListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!requestUserLocation()) {
            // Permission not granted => request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_COARSE_LOCATION);
        }

        if (this.locations == null) refreshLocations(LoadingType.NETWORK_OR_DATABASE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location_selection, menu);
        searchView = (SearchView)MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void refreshLocations(LoadingType type) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(LOADING_TYPE_KEY, type);
        getSupportLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestUserLocation();
                    this.fragment.setUsersLocation(userLocation);
                }
            }
        }
    }

    private boolean requestUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                userLocation = GPSCoordinate.fromLocation(location);
            }
            return true;
        }
    }

    @Override
    public Loader<List<Location>> onCreateLoader(int id, Bundle args) {
        LoadingType loadingType = (LoadingType) args.getSerializable(LOADING_TYPE_KEY);
        return new LocationLoader(this, loadingType);
    }

    @Override
    public void onLoadFinished(Loader<List<augsburg.se.alltagsguide.common.Location>> loader, List<augsburg.se.alltagsguide.common.Location> data) {
        this.locations = data;
        if (this.fragment != null) this.fragment.setLocations(this.locations);
    }

    @Override
    public void onLoaderReset(Loader<List<augsburg.se.alltagsguide.common.Location>> loader) {

    }

    @Override
    public void onLocationSelected(augsburg.se.alltagsguide.common.Location location) {
        prefs.setLocation(location);

        Intent intent = new Intent(this, LanguageSelectionActivity.class);
        intent.putExtra(LanguageSelectionActivity.INTENT_DATA_CHECK_EXISTENCE, true);
        startActivity(intent);
    }

    @Override
    public void onForceReloadLocations() {
        refreshLocations(LoadingType.FORCE_NETWORK);
    }

    @Override
    public boolean onQueryTextSubmit(String filter) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String filter) {
        if(this.fragment != null) this.fragment.setSearchString(filter);
        return true;
    }
}
