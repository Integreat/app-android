package augsburg.se.alltagsguide;

import android.app.Application;
import android.app.Instrumentation;

import com.crashlytics.android.Crashlytics;
import com.google.inject.Injector;

import io.fabric.sdk.android.Fabric;
import roboguice.RoboGuice;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class BaseApplication extends Application {
    /**
     * The injector which can inject objects later on
     */
    private static Injector injector;

    public BaseApplication() {
        super();
    }

    public BaseApplication(Instrumentation instrumentation) {
        super();
        attachBaseContext(instrumentation.getTargetContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
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
