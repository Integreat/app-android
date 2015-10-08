package augsburg.se.alltagsguide.network;

import java.util.List;

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.common.UpdateTime;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Daniel-L on 28.07.2015.
 */
public interface NetworkService {
    @GET("/alive")
    Boolean isServerAlive();

    @GET("/{location}/{language}/wp-json/extensions/v0/modified_content/pages")
    List<Page> getPages(@Path("language") Language language, @Path(value = "location", encode = false) Location location, @Query("since") UpdateTime time);

    @GET("/wordpress/wp-json/extensions/v0/multisites/")
    List<Location> getAvailableLocations();

    @GET("/{location}/de/wp-json/extensions/v0/languages/wpml")
    List<Language> getAvailableLanguages(@Path(value = "location", encode = false) Location location);

}
