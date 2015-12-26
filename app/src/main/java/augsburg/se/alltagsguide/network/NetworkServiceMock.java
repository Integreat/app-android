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
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.common.UpdateTime;
import augsburg.se.alltagsguide.utilities.ColorManager;
import retrofit.Callback;
import retrofit.http.Path;
import retrofit.http.Query;
import roboguice.RoboGuice;


public class NetworkServiceMock implements NetworkService {

    /**
     * Time the response should wait until it's executed (to simulate network).
     */
    private static final int TIME_TO_SLEEP = 1500;

    @Inject
    private ColorManager cm;

    @Inject
    public NetworkServiceMock(Context context) {
        RoboGuice.injectMembers(context, this);
    }


    @NonNull
    @Override
    public List<Location> getAvailableLocations() {

        List<Location> locations = new ArrayList<>();
        String[] locationNames = {"Augsburg", "Berlin", "Düsseldorf", "Freiburg", "München"};
        String[] locationUrl = {"www.augsburg.de", "www.berlin.de", "www.düsseldorf.de", "www.freiburg.de", "www.münchen.de"};
        int[] locationColors = {cm.getColor(2), cm.getColor(3), cm.getColor(6), cm.getColor(9), cm.getColor(12)};
        String[] locationPictures = {
                "http://www.ina-sic.de/bilder/default/augsburg2.png",
                "http://www.briefmarkenverein-berliner-baer.de/foto-berliner-baer/berliner-baer-wappen.gif",
                "http://www.designtagebuch.de/wp-content/uploads/mediathek//duesseldorf-marke-logo.png",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/81/Wappen_Freiburg_im_Breisgau.svg/140px-Wappen_Freiburg_im_Breisgau.svg.png",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Muenchen_Kleines_Stadtwappen.svg/818px-Muenchen_Kleines_Stadtwappen.svg.png"};

        for (int i = 0; i < locationNames.length; i++) {
            locations.add(new Location(i, locationNames[i], locationPictures[i], locationNames[i], locationUrl[i], false, locationColors[i], null, 0f, 0f, false));
        }
        return sendDelayed(locations);
    }

    @NonNull
    @Override
    public List<Language> getAvailableLanguages(@NonNull Location location) {
        Language english = new Language(0, "https://upload.wikimedia.org/wikipedia/en/thumb/a/ae/Flag_of_the_United_Kingdom.svg/100px-Flag_of_the_United_Kingdom.svg.png", "English", "en");
        Language german = new Language(1, "https://upload.wikimedia.org/wikipedia/en/thumb/b/ba/Flag_of_Germany.svg/100px-Flag_of_Germany.svg.png", "Deutsch", "de");
        Language spanish = new Language(2, "https://upload.wikimedia.org/wikipedia/en/thumb/9/9a/Flag_of_Spain.svg/100px-Flag_of_Spain.svg.png", "Espanol", "es");
        Language frensh = new Language(3, "https://upload.wikimedia.org/wikipedia/en/thumb/c/c3/Flag_of_France.svg/100px-Flag_of_France.svg.png", "Francais", "fr");

        List<Language> languages = new ArrayList<>();
        languages.add(german);
        languages.add(frensh);
        languages.add(english);
        languages.add(spanish);
        return sendDelayed(languages);
    }

    @Override
    public void subscribePush(@NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("gcm_register_id") String regId, @NonNull Callback<String> callback) {
        sendDelayed(callback, "Yes", TIME_TO_SLEEP);
    }

    @Override
    public void unsubscribePush(@NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("gcm_unregister_id") String regId, @NonNull Callback<String> callback) {
        sendDelayed(callback, "Yes", TIME_TO_SLEEP);
    }

    @NonNull
    @Override
    public List<Page> getDisclaimers(@NonNull @Path("language") Language language, @NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("since") UpdateTime time) {
        return getPages(language, location, time);
    }

    /**
     * Sends the response to the success-listener.
     *
     * @param <T>             Type of object, which is send to the successlistener
     * @param successListener listener which should receive the response
     * @param response        response which should be send to the listener
     */
    private <T> void sendDelayed(final Callback<T> successListener, final T response, int timeToSleep) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (successListener != null) {
                    successListener.success(response, null);
                }
            }
        }, timeToSleep);
    }

    private <T> T sendDelayed(final T response) {
        try {
            Thread.sleep(TIME_TO_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }


    @NonNull
    @Override
    public Boolean isServerAlive() {
        return sendDelayed(true);
    }

    @NonNull
    @Override
    public List<Page> getPages(@NonNull @Path("language") Language language, @NonNull @Path("location") Location location, @NonNull @Query("since") UpdateTime updateTime) {
        return new ArrayList<>();
    }

    @NonNull
    @Override
    public List<EventPage> getEventPages(@NonNull @Path("language") Language language, @NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("since") UpdateTime time) {
        return new ArrayList<>();
    }

}
