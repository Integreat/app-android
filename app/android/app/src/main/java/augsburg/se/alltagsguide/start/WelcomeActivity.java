/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import augsburg.se.alltagsguide.utilities.ColorManager;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.ui.BaseActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity implements LanguageFragment.OnLanguageFragmentInteractionListener, LocationFragment.OnLocationFragmentInteractionListener {

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
        if (mPrefUtilities.getLocation() != null && mPrefUtilities.getLanguage() != null) {
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
    protected void setLastColor() {
        //override and do nothing as we only show it when a location is selected.
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
        mPrefUtilities.saveCurrentColor(color);
    }

}
