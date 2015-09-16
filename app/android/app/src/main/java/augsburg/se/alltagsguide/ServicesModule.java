package augsburg.se.alltagsguide;

import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.network.NetworkServiceMock;
import augsburg.se.alltagsguide.utilities.ColorManager;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class ServicesModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    NetworkService networkService(Context context) {
        return new NetworkServiceMock(context);
    }

    /*@Provides
    NetworkService networkService(Context context, GsonConverter gsonConverter) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://vmkrcmar21.informatik.tu-muenchen.de/wordpress/")
                .setConverter(gsonConverter)
                .build();

        return restAdapter.create(NetworkService.class);
    }*/


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
