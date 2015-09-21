package augsburg.se.alltagsguide;

import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.network.NetworkServiceMock;
import augsburg.se.alltagsguide.utilities.ColorManager;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Path;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class ServicesModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    NetworkService networkService(Context context, GsonConverter gsonConverter) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://vmkrcmar21.informatik.tu-muenchen.de/wordpress/")
                .setConverter(gsonConverter)
                .build();
        final NetworkService service = restAdapter.create(NetworkService.class);
        final NetworkService mock = new NetworkServiceMock(context);
        return new NetworkService() {
            @Override
            public Boolean isServerAlive() {
                return mock.isServerAlive();
            }

            @Override
            public List<Page> getPages(@Path("language") Language language, @Path("location") Location location) {
                return service.getPages(language, location);
            }

            @Override
            public List<Location> getAvailableLocations() {
                return mock.getAvailableLocations();
            }

            @Override
            public List<Language> getAvailableLanguages(Location location) {
                return mock.getAvailableLanguages(location);
            }
        };
    }


    @Singleton
    @Provides
    ColorManager colorManager(Context context) {
        return new ColorManager(context);
    }

    @Provides
    @Singleton
    PrefUtilities prefUtilities(Context context) {
        return new PrefUtilities(context);
    }
}
