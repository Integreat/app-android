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

    //TODO
    @GET("/{location}/{language}/?rest_route=%2Fextensions%2Fv0%2Fmodified_content%2Fposts_and_pages%2F2014-08-30+12%3A00%3A00")
    List<Page> getPages(@Path("language") Language language, @Path(value = "location", encode = false) Location location); //  @Query("time") UpdateTime time

    @GET("/wordpress/?rest_route=/extensions/v0/multisites/")
    List<Location> getAvailableLocations();

    @GET("/{location}/de/?rest_route=/extensions/v0/languages/wpml")
    List<Language> getAvailableLanguages(@Path(value = "location", encode = false) Location location);

}
