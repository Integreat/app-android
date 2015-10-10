package augsburg.se.alltagsguide.network;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.resources.EventPageResource;
import augsburg.se.alltagsguide.persistence.resources.PageResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class EventPagesLoader extends BasicLoader<List<? extends Page>> {

    @Inject
    private DatabaseCache dbCache;
    private Location mLocation;
    private Language mLanguage;

    @Inject
    private EventPageResource.Factory pagesFactory;


    /**
     * Create loader for context
     *
     * @param activity
     */
    @Inject
    public EventPagesLoader(Activity activity, @NonNull Location location, @NonNull Language language) {
        super(activity);
        mLocation = location;
        mLanguage = language;
    }

    @Override
    public List<EventPage> load() {
        try {
            List<EventPage> pages = dbCache.loadOrRequest(pagesFactory.under(mLanguage, mLocation));
            Page.recreateRelations(pages);
            return pages;
        } catch (IOException e) {
            Ln.e(e);
            return Collections.emptyList();
        }
    }
}
