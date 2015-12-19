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
import augsburg.se.alltagsguide.utilities.LoadingType;
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
     * @param context
     */
    public EventPageLoader(Context context, @NonNull Location location, @NonNull Language language, int id) {
        super(context, LoadingType.NETWORK_OR_DATABASE);
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
