package augsburg.se.alltagsguide.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.overview.OverviewActivity;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.ColorManager;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity implements LanguageFragment.OnLanguageFragmentInteractionListener, LocationFragment.OnLocationFragmentInteractionListener {

    @Inject
    private PrefUtilities prefUtilities;

    @Inject
    private ColorManager mColorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String preBuildLocation = getString(R.string.pre_build_location);
        if (!Objects.equals("", preBuildLocation)) {
            // location was set during gradle build, so dont show location here
            throw new IllegalStateException("Not implemented yet");
        }
        if (prefUtilities.getLocation() != null && prefUtilities.getLanguage() != null) {
            startOverview();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, LocationFragment.newInstance())
                    .commit();
        }
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
        startOverview();
    }

    private void startOverview() {
        Intent intent = new Intent(WelcomeActivity.this, OverviewActivity.class);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        int color = mColorManager.getColor(location.getColor());
        prefUtilities.saveCurrentColor(color);
        changeColor(color);
    }

}
