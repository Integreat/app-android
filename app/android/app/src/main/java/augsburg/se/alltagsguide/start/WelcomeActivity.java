package augsburg.se.alltagsguide.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.category.OverviewActivity;
import augsburg.se.alltagsguide.utilities.BaseActivity;
import augsburg.se.alltagsguide.utilities.PrefUtilities;

public class WelcomeActivity extends BaseActivity implements LanguageFragment.OnLanguageFragmentInteractionListener, LocationFragment.OnLocationFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getFragmentManager()
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
        startActivity(new Intent(WelcomeActivity.this, OverviewActivity.class));
    }

    @Override
    public void onLocationSelected(Location location) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, LanguageFragment.newInstance(location))
                .addToBackStack(null)
                .commit();
        int color = location.getColor();
        PrefUtilities.getInstance().saveCurrentColor(color);
        changeColor(color);
    }

    @Override
    public void onBackPressed() {
        if (!getFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }
}
