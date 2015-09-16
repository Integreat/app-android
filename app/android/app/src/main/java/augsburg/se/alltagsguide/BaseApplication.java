package augsburg.se.alltagsguide;

import android.app.Application;

import com.google.inject.Injector;

import roboguice.RoboGuice;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class BaseApplication extends Application {
    /**
     * The injector which can inject objects later on
     */
    private static Injector injector;

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice.setUseAnnotationDatabases(false);
        injector = RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new MainModule());
    }

    /**
     * Injects the object
     *
     * @param object object which should be injected manually
     */
    public static void inject(Object object) {
        injector.injectMembers(object);
    }
}
