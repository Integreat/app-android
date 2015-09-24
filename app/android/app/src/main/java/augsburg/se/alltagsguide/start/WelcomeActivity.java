package augsburg.se.alltagsguide.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.page.OverviewActivity;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity implements LanguageFragment.OnLanguageFragmentInteractionListener, LocationFragment.OnLocationFragmentInteractionListener {

    @Inject
    private PrefUtilities prefUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, LocationFragment.newInstance())
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // save values
    @Override
    public void onLanguageSelected(Location location, Language language) {
        mPrefUtilities.setLanguage(language);
        mPrefUtilities.setLocation(location);
        Intent intent = new Intent(WelcomeActivity.this, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLocationSelected(Location location) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, LanguageFragment.newInstance(location))
                .addToBackStack(null)
                .commit();
        int color = location.getColor();
        prefUtilities.saveCurrentColor(color);
        changeColor(color);
    }

    @Override
    public void onBackPressed() {
        if (!getFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }
}
