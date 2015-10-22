package augsburg.se.alltagsguide.network;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.persistence.resources.AvailableLanguageResource;
import augsburg.se.alltagsguide.persistence.resources.PageResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class PagesLoader extends BasicLoader<List<Page>> {

    @NonNull private Location mLocation;
    @NonNull private Language mLanguage;

    @Inject
    private PageResource.Factory pagesFactory;

    @Inject
    private AvailableLanguageResource.Factory availableLanguageFactory;


    /**
     * Create loader for context
     *
     * @param activity
     */
    public PagesLoader(Activity activity, @NonNull Location location, @NonNull Language language, boolean forced) {
        super(activity, forced);
        mLocation = location;
        mLanguage = language;
    }

    @NonNull
    @Override
    public List<Page> load() {
        try {
            List<Page> pages = requestIfForced(pagesFactory.under(mLanguage, mLocation));
            List<AvailableLanguage> languages = dbCache.load(availableLanguageFactory.under(mLanguage, mLocation));
            Page.recreateRelations(pages, languages, mLanguage);
            return pages;
        } catch (IOException e) {
            Ln.e(e);
            return Collections.emptyList();
        }
    }
}
