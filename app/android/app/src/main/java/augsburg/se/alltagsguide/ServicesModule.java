package augsburg.se.alltagsguide;

import android.content.Context;
import android.util.Log;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.common.UpdateTime;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.network.NetworkServiceMock;
import augsburg.se.alltagsguide.utilities.ColorManager;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.Path;
import retrofit.http.Query;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class ServicesModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    OkHttpClient okHttpClient(Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);
        OkHttpClient client = new OkHttpClient();
        client.setCache(cache);
        return client;
    }

    @Provides
    OkHttpDownloader okHttpDownloader(OkHttpClient client) {
        return new OkHttpDownloader(client);
    }

    @Provides
    Picasso picasso(Context context, OkHttpDownloader downloader) {
        return new Picasso.Builder(context).downloader(downloader).build();
    }

    @Provides
    NetworkService networkService(Context context, GsonConverter gsonConverter, OkHttpClient client) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setLogLevel(BuildConfig.DEBUG ?
                        RestAdapter.LogLevel.FULL :
                        RestAdapter.LogLevel.NONE)
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
            public List<Page> getPages(@Path("language") Language language, @Path("location") Location location, @Query("since") UpdateTime updateTime) {
                try {
                    return service.getPages(language, location, updateTime);
                } catch (RetrofitError e) {
                    Ln.e(e);
                    return new ArrayList<>();
                }
            }

            @Override
            public List<EventPage> getEventPages(@Path("language") Language language, @Path(value = "location", encode = false) Location location, @Query("since") UpdateTime updateTime) {
                try {
                    return service.getEventPages(language, location, updateTime);
                } catch (RetrofitError e) {
                    Ln.e(e);
                    return new ArrayList<>();
                }
            }

            @Override
            public List<Location> getAvailableLocations() {
                try {
                    return service.getAvailableLocations();
                } catch (RetrofitError e) {
                    Ln.e(e);
                    return null;
                }
            }

            @Override
            public List<Language> getAvailableLanguages(Location location) {
                try {
                    return service.getAvailableLanguages(location);
                } catch (RetrofitError e) {
                    Ln.e(e);
                    return null;
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
