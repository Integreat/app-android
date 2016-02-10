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

package augsburg.se.alltagsguide.page;

import android.os.Bundle;
import android.support.v4.content.Loader;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.PageLoader;
import augsburg.se.alltagsguide.utilities.ui.BasePageWebViewLanguageActivity;
import roboguice.inject.ContentView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_page)
public class PageActivity extends BasePageWebViewLanguageActivity<Page> {

    protected void setMorePageDetails(Page page) {
    }

    @Override
    public Loader<Page> onCreateLoader(int id, Bundle args) {
        AvailableLanguage language = (AvailableLanguage) args.getSerializable(ARG_LANGUAGE);
        if (language == null || language.getLoadedLanguage() == null) {
            Ln.d("AvailableLanguage is null or has no language.");
            return null;
        }
        return new PageLoader(this, mPrefUtilities.getLocation(), language.getLoadedLanguage(), language.getOtherPageId());
    }

    @Override
    protected String getScreenName() {
        return super.getScreenName() + "PageActivity";
    }
}
