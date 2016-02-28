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

import android.app.Application;
import android.app.Instrumentation;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.inject.Injector;
import com.liulishuo.filedownloader.FileDownloader;

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
        FileDownloader.init(this);
    }

    /**
     * Injects the object
     *
     * @param object object which should be injected manually
     */
    public static void inject(@NonNull Object object) {
        injector.injectMembers(object);
    }
}
