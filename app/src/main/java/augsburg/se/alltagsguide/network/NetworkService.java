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

import android.support.annotation.NonNull;

import java.util.List;

import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.common.UpdateTime;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Daniel-L on 28.07.2015.
 */
public interface NetworkService {
    @GET("/alive")
    @NonNull
    Boolean isServerAlive();

    @GET("/{location}/{language}/wp-json/extensions/v0/modified_content/pages")
    @NonNull
    List<Page> getPages(@NonNull @Path("language") Language language, @NonNull @Path(value = "location") Location location, @NonNull @Query("since") UpdateTime time);

    @GET("/{location}/{language}/wp-json/extensions/v0/modified_content/events")
    @NonNull
    List<EventPage> getEventPages(@NonNull @Path("language") Language language, @NonNull @Path(value = "location") Location location, @NonNull @Query("since") UpdateTime time);

    @GET("/wordpress/wp-json/extensions/v1/multisites/")
    @NonNull
    List<Location> getAvailableLocations();

    @GET("/{location}/de/wp-json/extensions/v0/languages/wpml")
    @NonNull
    List<Language> getAvailableLanguages(@NonNull @Path(value = "location") Location location);

    @GET("/{location}")
    void subscribePush(@NonNull @Path(value = "location") Location location, @NonNull @Query("gcm_register_id") String regId, @NonNull Callback<String> callback);

    @GET("/{location}")
    void unsubscribePush(@NonNull @Path(value = "location") Location location, @NonNull @Query("gcm_unregister_id") String regId, @NonNull Callback<String> callback);


    @GET("/{location}/{language}/wp-json/extensions/v0/modified_content/disclaimer")
    @NonNull
    List<Page> getDisclaimers(@NonNull @Path("language") Language language, @NonNull @Path(value = "location") Location location, @NonNull @Query("since") UpdateTime time);

}
