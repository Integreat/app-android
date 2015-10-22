package augsburg.se.alltagsguide.network;

import android.support.annotation.NonNull;

import java.util.List;

import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.common.UpdateTime;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Daniel-L on 28.07.2015.
 */
public interface NetworkService {
    @GET("/alive")
    @NonNull
    Boolean isServerAlive();

    @GET("/{location}/{language}/wp-json/extensions/v0/modified_content/pages")
    @NonNull
    List<Page> getPages(@NonNull @Path("language") Language language, @NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("since") UpdateTime time);

    @GET("/{location}/{language}/wp-json/extensions/v0/modified_content/events")
    @NonNull
    List<EventPage> getEventPages(@NonNull @Path("language") Language language, @NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("since") UpdateTime time);

    @GET("/wordpress/wp-json/extensions/v0/multisites/")
    @NonNull
    List<Location> getAvailableLocations();

    @GET("/{location}/de/wp-json/extensions/v0/languages/wpml")
    @NonNull
    List<Language> getAvailableLanguages(@NonNull @Path(value = "location", encode = false) Location location);

    @GET("/{location}")
    void subscribePush(@NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("gcm_register_id") String regId, @NonNull Callback<String> callback);

    @GET("/{location}")
    void unsubscribePush(@NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("gcm_unregister_id") String regId, @NonNull Callback<String> callback);
}
