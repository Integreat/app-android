package augsburg.se.alltagsguide.network;

import java.util.List;

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Content;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Daniel-L on 28.07.2015.
 */
public interface NetworkHandler {
    @GET("/alive")
    void isServerAlive(Callback<Boolean> callback);

    @GET("/locations/{location}/languages/{language}/contents")
    void getContents(@Path("language") Language language, @Path("location") Location location, Callback<List<Content>> cb); //todo toString ueberschreiben

    @GET("/locations/{location}/languages/{language}/contents/{contentid}/information")
    void getContent(@Path("language") Language language, @Path("location") Location location, @Path("contentid") String contentId, Callback<List<Content>> cb); //todo toString ueberschreiben

    @GET("/locations")
    void getAvailableLocations(Callback<List<Location>> cb);

    @GET("/locations/{location}/languages")
    void getAvailableLanguages(Location location, Callback<List<Language>> cb);

}
