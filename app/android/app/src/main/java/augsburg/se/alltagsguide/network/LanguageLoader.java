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

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.resources.LanguageResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;
import augsburg.se.alltagsguide.utilities.LoadingType;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class LanguageLoader extends BasicLoader<List<Language>> {

    @Inject
    private LanguageResource.Factory languageFactory;
    @Inject
    private DatabaseCache db;

    @NonNull
    private Location mLocation;

    /**
     * Create loader for context
     *
     * @param context
     */
    public LanguageLoader(Context context, @NonNull Location location, LoadingType loadingType) {
        super(context, loadingType);
        mLocation = location;
    }


    @Override
    public List<Language> load() {
        try {
            return get(languageFactory.under(mLocation));
        } catch (IOException e) {
            Ln.e(e);
            return Collections.emptyList();
        }
    }
}
