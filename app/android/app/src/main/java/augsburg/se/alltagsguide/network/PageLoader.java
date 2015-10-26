package augsburg.se.alltagsguide.network;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.resources.AvailableLanguageResource;
import augsburg.se.alltagsguide.persistence.resources.PageResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;
import augsburg.se.alltagsguide.utilities.LoadingType;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class PageLoader extends BasicLoader<Page> {

    @Inject
    private DatabaseCache dbCache;
    @NonNull private Location mLocation;
    @NonNull private Language mLanguage;
    private int mId;

    @Inject
    private PageResource.Factory pagesFactory;

    @Inject
    private AvailableLanguageResource.Factory availableLanguageFactory;


    /**
     * Create loader for context
     *
     * @param context
     */
    public PageLoader(Context context, @NonNull Location location, @NonNull Language language, int id) {
        super(context, LoadingType.NETWORK_OR_DATABASE);
        mLocation = location;
        mLanguage = language;
        mId = id;
    }

    @Nullable
    @Override
    public Page load() {
        PageResource resource = pagesFactory.under(mLanguage, mLocation);
        try {
            Page translatedPage = dbCache.load(resource, mId);
            if (translatedPage == null) {
                dbCache.requestAndStore(resource);
                translatedPage = dbCache.load(resource, mId);
                if (translatedPage == null) {
                    return null;
                }
            }
            List<Page> pages = new ArrayList<>();
            pages.add(translatedPage);
            List<AvailableLanguage> languages = dbCache.load(availableLanguageFactory.under(mLanguage, mLocation));
            Page.recreateRelations(pages, languages, mLanguage);
            for (Page page : pages) {
                if (page.getId() == mId) {
                    return page;
                }
            }
        } catch (IOException e) {
            Ln.e(e);
        }
        return null;
    }
}
