package augsburg.se.alltagsguide;

import android.app.Application;

import augsburg.se.alltagsguide.utilities.ColorManager;
import augsburg.se.alltagsguide.utilities.PrefUtilities;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PrefUtilities.init(getApplicationContext());
        ColorManager.init(getApplicationContext());
    }
}
