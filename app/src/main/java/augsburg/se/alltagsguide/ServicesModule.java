/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.common.UpdateTime;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.network.NetworkServiceMock;
import augsburg.se.alltagsguide.utilities.ColorManager;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import retrofit.Callback;
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

    @Singleton
    @Provides
    OkHttpClient okHttpClient(Context context, @Named("cacheDir") File cachedir) {
        Ln.d("okHttpClient is intialized.");
        int cacheSize = 50 * 1024 * 1024; // 50 MiB
        Cache cache = new Cache(cachedir, cacheSize);
        OkHttpClient client = new OkHttpClient();
        client.setCache(cache);
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        return client;
    }

    @Provides
    @Singleton
    OkHttpDownloader okHttpDownloader(OkHttpClient client) {
        Ln.d("OkHttpDownloader is intialized.");
        return new OkHttpDownloader(client);
    }

    @Provides
    @Singleton
    Picasso picasso(Context context, OkHttpDownloader downloader) {
        Ln.d("Picasso is intialized.");
        return new Picasso.Builder(context).downloader(downloader).build();
    }

    private NetworkService buildRestAdapter(OkHttpClient client, GsonConverter gsonConverter, String endpoint) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setLogLevel(BuildConfig.DEBUG ?
                        RestAdapter.LogLevel.NONE :
                        RestAdapter.LogLevel.NONE)
                .setEndpoint(endpoint)
                .setConverter(gsonConverter)
                .build();
        return restAdapter.create(NetworkService.class);
    }
    @Provides
    @Singleton
    NetworkService networkService(Context context, GsonConverter gsonConverter, OkHttpClient client) {
        Ln.d("NetworkService is intialized.");

        final NetworkService service = buildRestAdapter(client, gsonConverter, "https://cms.integreat-app.de");
        final NetworkService fallbackService = buildRestAdapter(client, gsonConverter, "http://vmkrcmar21.informatik.tu-muenchen.de/");
        final NetworkService mock = new NetworkServiceMock(context);
        return new NetworkService() {
            @NonNull
            @Override
            public Boolean isServerAlive() {
                return mock.isServerAlive();
            }

            @NonNull
            @Override
            public List<Page> getPages(@NonNull @Path("language") Language language, @NonNull @Path("location") Location location, @NonNull @Query("since") UpdateTime updateTime) {
                try {
                    return service.getPages(language, location, updateTime);
                } catch (Exception e) {
                    Ln.e(e);
                    try {
                        return fallbackService.getPages(language, location, updateTime);
                    } catch (Exception e2) {
                        Ln.e(e2);
                        return new ArrayList<>();
                    }
                }
            }

            @NonNull
            @Override
            public List<EventPage> getEventPages(@NonNull @Path("language") Language language, @NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("since") UpdateTime updateTime) {
                try {
                    return service.getEventPages(language, location, updateTime);
                } catch (Exception e) {
                    Ln.e(e);
                    try {
                        return fallbackService.getEventPages(language, location, updateTime);
                    } catch (Exception e2) {
                        Ln.e(e2);
                        return new ArrayList<>();
                    }
                }
            }

            @NonNull
            @Override
            public List<Location> getAvailableLocations() {
                try {
                    return service.getAvailableLocations();
                } catch (Exception e) {
                    Ln.e(e);
                    try {
                        return fallbackService.getAvailableLocations();
                    } catch (Exception e2) {
                        Ln.e(e2);
                        return new ArrayList<>();
                    }
                }
            }

            @NonNull
            @Override
            public List<Language> getAvailableLanguages(@NonNull Location location) {
                try {
                    return service.getAvailableLanguages(location);
                } catch (Exception e) {
                    Ln.e(e);
                    try {
                        return fallbackService.getAvailableLanguages(location);
                    } catch (Exception e2) {
                        Ln.e(e2);
                        return new ArrayList<>();
                    }
                }
            }

            @Override
            public void subscribePush(@NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("gcm_register_id") String regId, @NonNull Callback<String> callback) {
                try {
                    service.subscribePush(location, regId, callback);
                } catch (Exception e) {
                    Ln.e(e);
                    fallbackService.subscribePush(location, regId, callback);
                }
            }

            @Override
            public void unsubscribePush(@NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("gcm_unregister_id") String regId, @NonNull Callback<String> callback) {
                try {
                    service.unsubscribePush(location, regId, callback);
                } catch (Exception e) {
                    Ln.e(e);
                    fallbackService.unsubscribePush(location, regId, callback);
                }
            }

            @NonNull
            @Override
            public List<Page> getDisclaimers(@NonNull @Path("language") Language language, @NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("since") UpdateTime time) {
                try {
                    return service.getDisclaimers(language, location, time);
                } catch (Exception e) {
                    Ln.e(e);
                    try {
                        return fallbackService.getDisclaimers(language, location, time);
                    } catch (Exception e2) {
                        Ln.e(e2);
                        return new ArrayList<>();
                    }
                }
            }
        };
    }


    @Provides
    @Singleton
    ColorManager colorManager(Context context) {
        Ln.d("ColorManager is intialized.");
        return new ColorManager(context);
    }

    @Provides
    @Singleton
    PrefUtilities prefUtilities(Context context) {
        Ln.d("PrefUtilities is intialized.");
        return new PrefUtilities(context);
    }
}
