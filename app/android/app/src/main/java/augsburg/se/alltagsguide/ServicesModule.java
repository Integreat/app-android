package augsburg.se.alltagsguide;

import android.content.Context;
import android.util.Log;

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
import retrofit.RetrofitError;
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
                        //.setEndpoint("http://vmkrcmar21.informatik.tu-muenchen.de/wordpress_test/")
                .setEndpoint("http://vmkrcmar21.informatik.tu-muenchen.de/")
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
                try {
                    return service.getPages(language, location);
                } catch (RetrofitError e) {
                    Log.e("ServicesModule", e.getMessage() != null ? e.getMessage() : "Error");
                    return new ArrayList<>();
                }
            }

            @Override
            public List<Location> getAvailableLocations() {
                try {
                    return service.getAvailableLocations();
                } catch (RetrofitError e) {
                    Log.e("ServicesModule", e.getMessage() != null ? e.getMessage() : "Error");
                    return new ArrayList<>();
                }
            }

            @Override
            public List<Language> getAvailableLanguages(Location location) {
                try {
                    return service.getAvailableLanguages(location);
                } catch (RetrofitError e) {
                    Log.e("ServicesModule", e.getMessage() != null ? e.getMessage() : "Error");
                    return new ArrayList<>();
                }
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
