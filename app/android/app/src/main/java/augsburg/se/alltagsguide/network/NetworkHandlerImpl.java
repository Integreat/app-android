package augsburg.se.alltagsguide.network;

import java.util.List;

import augsburg.se.alltagsguide.common.Content;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import retrofit.Callback;
import retrofit.http.Path;

/**
 * Created by Daniel-L on 28.07.2015.
 */
public class NetworkHandlerImpl implements NetworkHandler {
    @Override
    public void isServerAlive(Callback<Boolean> callback) {

    }

    @Override
    public void getContents(@Path("language") Language language, @Path("location") Location location, Callback<List<Content>> cb) {

    }

    @Override
    public void getContent(@Path("language") Language language, @Path("location") Location location, @Path("contentid") String contentId, Callback<List<Content>> cb) {

    }

    @Override
    public void getAvailableLocations(Callback<List<Location>> cb) {

    }

    @Override
    public void getAvailableLanguages(Location location, Callback<List<Language>> cb) {

    }
}
