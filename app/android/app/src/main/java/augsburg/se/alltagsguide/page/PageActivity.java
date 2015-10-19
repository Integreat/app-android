package augsburg.se.alltagsguide.page;

import android.os.Bundle;
import android.support.v4.content.Loader;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.PageLoader;
import augsburg.se.alltagsguide.utilities.BasePageWebViewLanguageActivity;
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

}
