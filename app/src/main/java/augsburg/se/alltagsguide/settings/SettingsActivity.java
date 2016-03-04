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

package augsburg.se.alltagsguide.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.overview.OverviewActivity;
import augsburg.se.alltagsguide.page.PageActivity;
import augsburg.se.alltagsguide.start.WelcomeActivity;
import augsburg.se.alltagsguide.utilities.ui.BaseActivity;

public class SettingsActivity extends BaseActivity implements PrefFragment.OnPreferenceListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new PrefFragment())
                    .commit();
        }
        setTitle(getString(R.string.action_settings));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, OverviewActivity.class);
                intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected boolean shouldSetDisplayHomeAsUp() {
        return true;
    }

    @Override
    public void onOpenPage(Page page) {
        Intent intent = new Intent(SettingsActivity.this, PageActivity.class);
        intent.putExtra(PageActivity.ARG_INFO, page);
        startActivity(intent);
    }

    @Override
    public void cacheCleared() {
        Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }
}