package augsburg.se.alltagsguide.network;

import java.util.List;

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Page;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Daniel-L on 28.07.2015.
 */
public interface NetworkService {
    @GET("/alive")
    Boolean isServerAlive();

    @GET("/{location}/{language}/?rest_route=/modified_content/posts_and_pages/15-08-31%2017:38:14")
        //TODO datetime
    List<Page> getPages(@Path("language") Language language, @Path("location") Location location);

    @GET("/locations")
    List<Location> getAvailableLocations();

    @GET("/locations/{location}/languages")
    List<Language> getAvailableLanguages(Location location);

}
