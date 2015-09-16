package augsburg.se.alltagsguide.network;

import java.util.List;

import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Category;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Daniel-L on 28.07.2015.
 */
public interface NetworkService {
    @GET("/alive")
    Boolean isServerAlive();

    @GET("/{location}/{language}/wp-json/wp/v2/terms/category")
    List<Category> getContents(@Path("language") Language language, @Path("location") Location location); //todo toString ueberschreiben

    @GET("/{location}/{language}/wp-json/wp/v2/terms/category/{categoryId}")
    List<Article> getContent(@Path("language") Language language, @Path("location") Location location, @Path("categoryId") String categoryId); //todo toString ueberschreiben

    @GET("/locations")
    List<Location> getAvailableLocations();

    @GET("/locations/{location}/languages")
    List<Language> getAvailableLanguages(Location location);

}
