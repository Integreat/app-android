package augsburg.se.alltagsguide;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.common.UpdateTime;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.network.NetworkServiceMock;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.DatabaseInfo;
import augsburg.se.alltagsguide.persistence.resources.PageResource;
import retrofit.Callback;
import retrofit.http.Path;
import retrofit.http.Query;
import roboguice.RoboGuice;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class DatabaseTest {
    @Inject
    Application context;

    @Inject
    DatabaseCache helper;

    @Inject
    private PageResource.Factory pagesFactory;

    private Language german = new Language(1, "de", "Deutsch", null);
    private Location augsburg = new Location(1, "Augsburg", null, "", null, false, 0, null, 0.0f, 0.0f);
    private PageResource pageResource;

    @Before
    public void setup() {
        // Override the default RoboGuice module
        RoboGuice.overrideApplicationInjector(context, new MyTestModule());
        pageResource = pagesFactory.under(german, augsburg);
    }

    @After
    public void teardown() {
        // Don't forget to tear down our custom injector to avoid polluting other test classes
        RoboGuice.Util.reset();
    }

    public void performanceTest() {

    }

    @Test
    public void addPagesToDatabase() throws IOException {
        List<Page> pages = helper.requestAndStore(pageResource);
        List<Page> loadedPages = helper.load(pageResource);
        Assert.assertEquals(pages, loadedPages);
    }

    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
        }

        @Provides
        DatabaseInfo getDatabaseInfo() {
            return new DatabaseInfo("test.db", 1);
        }

        @Provides
        NetworkService networkService(Context context) {
            final NetworkService mock = new NetworkServiceMock(context);
            return new NetworkService() {
                @NonNull
                @Override
                public Boolean isServerAlive() {
                    return true;
                }

                @NonNull
                @Override
                public List<Page> getPages(@NonNull @Path("language") Language language, @NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("since") UpdateTime time) {
                    return mock.getPages(language, location, time);
                }

                @NonNull
                @Override
                public List<EventPage> getEventPages(@NonNull @Path("language") Language language, @NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("since") UpdateTime time) {
                    return mock.getEventPages(language, location, time);
                }

                @NonNull
                @Override
                public List<Location> getAvailableLocations() {
                    return mock.getAvailableLocations();
                }

                @NonNull
                @Override
                public List<Language> getAvailableLanguages(@NonNull @Path(value = "location", encode = false) Location location) {
                    return mock.getAvailableLanguages(location);
                }

                @Override
                public void subscribePush(@NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("gcm_register_id") String regId, @NonNull Callback<String> callback) {

                }

                @Override
                public void unsubscribePush(@NonNull @Path(value = "location", encode = false) Location location, @NonNull @Query("gcm_unregister_id") String regId, @NonNull Callback<String> callback) {

                }
            };
        }

    }
}
