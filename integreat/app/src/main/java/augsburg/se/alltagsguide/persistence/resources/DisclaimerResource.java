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

package augsburg.se.alltagsguide.persistence.resources;

import android.support.annotation.NonNull;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;


import java.util.Date;
import java.util.List;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.common.UpdateTime;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.utilities.PrefUtilities;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class DisclaimerResource extends PageResource {

    @Override
    public String getType() {
        return "disclaimer";
    }
    /**
     * Creation factory
     */
    public interface Factory {
        DisclaimerResource under(Language lang, Location loc);
    }

    @Inject
    public DisclaimerResource(@NonNull @Assisted Language language, @NonNull @Assisted Location location, @NonNull NetworkService network, @NonNull DatabaseCache cache, @NonNull PrefUtilities preferences) {
        super(language, location, network, cache, preferences);
    }

    @NonNull
    @Override
    public List<Page> request() {
        UpdateTime time = new UpdateTime(getLastModificationDate());
        return mNetwork.getDisclaimers(mLanguage, mLocation, time);
    }

    @Override
    public boolean shouldUpdate() {
        long lastUpdate = mPreferences.lastPageDisclaimerUpdateTime(mLanguage, mLocation);
        long now = new Date().getTime();
        long updateCachingTime = 1000 * 60 * 60 * 4; // 4 hours
        return now - lastUpdate > updateCachingTime;
    }

    @Override
    public void loadedFromNetwork() {
        mPreferences.setLastPageDisclaimerUpdateTime(mLanguage, mLocation);
    }
}
