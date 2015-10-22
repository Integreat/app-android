package augsburg.se.alltagsguide.network;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collections;
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
public class EventPagesLoader extends BasicLoader<List<EventPage>> {

    @Inject
    private DatabaseCache dbCache;
    @NonNull private Location mLocation;
    @NonNull private Language mLanguage;

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
    public EventPagesLoader(Activity activity, @NonNull Location location, @NonNull Language language, boolean force) {
        super(activity, force);
        mLocation = location;
        mLanguage = language;
    }

    @Override
    public List<EventPage> load() {
        try {
            List<EventPage> pages = requestIfForced(pagesFactory.under(mLanguage, mLocation));
            List<EventCategory> categories = dbCache.load(categoriesFactory.under(mLanguage, mLocation));
            List<EventTag> tags = dbCache.load(tagsFactory.under(mLanguage, mLocation));
            List<AvailableLanguage> languages = dbCache.load(languageFactory.under(mLanguage, mLocation));
            EventPage.recreateRelations(pages, categories, tags, languages, mLanguage);
            return pages;
        } catch (IOException e) {
            Ln.e(e);
            return Collections.emptyList();
        }
    }
}
