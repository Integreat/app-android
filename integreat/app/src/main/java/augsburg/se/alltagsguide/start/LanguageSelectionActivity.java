package augsburg.se.alltagsguide.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.overview.OverviewActivity;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import augsburg.se.alltagsguide.utilities.ui.BaseActivity;
import augsburg.se.alltagsguide.utilities.ui.BaseFragment;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

import javax.inject.Inject;

/**
 * Created by Amadeus on 11. Nov. 2015.
 */
public class LanguageSelectionActivity extends BaseActivity implements LanguageFragment.OnLanguageFragmentInteractionListener, BaseFragment.OnBaseFragmentInteractionListener {
    public static final String INTENT_DATA_CHECK_EXISTENCE = "check_existence";
    private LanguageFragment languageFragment;
    private Location location;
    private Language oldLanguage;
    private boolean checkExistence;
    @Inject private PrefUtilities prefs;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        setTitle(getString(R.string.language_fragment_title));
        setSubTitle(getString(R.string.language_fragment_subtitle));

        this.checkExistence = getIntent().getBooleanExtra(INTENT_DATA_CHECK_EXISTENCE, false);

        this.oldLanguage = prefs.getLanguage();

        this.location = prefs.getLocation();
        if(this.location == null) {
            // No location selected --> start LocationSelectionActivity
            prefs.setLanguage(null);
            Intent locSelectActivityIntent = new Intent(this, LocationSelectionActivity.class);
            startActivity(locSelectActivityIntent);
            finish();
        }

        if(savedInstanceState == null) {
            this.languageFragment = LanguageFragment.newInstance(this.location);
            getSupportFragmentManager().beginTransaction().add(R.id.container, this.languageFragment).commit();
            if(checkExistence) this.languageFragment.setOldLanguage(this.oldLanguage);
        } else {
            this.languageFragment = (LanguageFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }
    }

    @Override
    public void onLanguageSelected(Location location, Language language) {
        prefs.setLanguage(language);

        Intent intent = new Intent(this, OverviewActivity.class);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onOldLanguageAvailable() {
        if(checkExistence) {
            // Old language is available in the new location --> start OverviewActivity directly
            Intent intent = new Intent(this, OverviewActivity.class);
            intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
