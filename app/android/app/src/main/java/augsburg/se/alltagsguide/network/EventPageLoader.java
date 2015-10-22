package augsburg.se.alltagsguide.network;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.EventCategory;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.EventTag;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.resources.AvailableLanguageResource;
import augsburg.se.alltagsguide.persistence.resources.EventCategoryResource;
import augsburg.se.alltagsguide.persistence.resources.EventPageResource;
import augsburg.se.alltagsguide.persistence.resources.EventTagResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class EventPageLoader extends BasicLoader<EventPage> {

    @Inject
    private DatabaseCache dbCache;
    @NonNull private Location mLocation;
    @NonNull private Language mLanguage;
    private int mId;

    @Inject
    private EventPageResource.Factory pagesFactory;

    @Inject
    private EventCategoryResource.Factory categoriesFactory;

    @Inject
    private EventTagResource.Factory tagsFactory;

    @Inject
    private AvailableLanguageResource.Factory languageFactory;

    /**
     * Create loader for context
     *
     * @param activity
     */
    public EventPageLoader(Activity activity, @NonNull Location location, @NonNull Language language, int id) {
        super(activity, false);
        mLocation = location;
        mLanguage = language;
        mId = id;
    }

    @Nullable
    @Override
    public EventPage load() {
        EventPageResource resource = pagesFactory.under(mLanguage, mLocation);
        try {
            EventPage translatedPage = dbCache.load(resource, mId);
            if (translatedPage == null) {
                dbCache.requestAndStore(resource);
                translatedPage = dbCache.load(resource, mId);
                if (translatedPage == null) {
                    Ln.e("Translated page is null even though this should not happen");
                    return null;
                }
            }
            List<EventPage> pages = new ArrayList<>();
            pages.add(translatedPage);
            List<EventCategory> categories = dbCache.load(categoriesFactory.under(mLanguage, mLocation));
            List<EventTag> tags = dbCache.load(tagsFactory.under(mLanguage, mLocation));
            List<AvailableLanguage> languages = dbCache.load(languageFactory.under(mLanguage, mLocation));
            EventPage.recreateRelations(pages, categories, tags, languages, mLanguage);

            for (EventPage page : pages) {
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
