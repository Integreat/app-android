package augsburg.se.alltagsguide.network;

import android.app.Activity;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.resources.LocationResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class LocationLoader extends BasicLoader<List<Location>> {

    @Inject
    private DatabaseCache dbCache;
    @Inject
    private LocationResource locationResource;

    /**
     * Create loader for context
     *
     * @param activity
     */
    @Inject
    public LocationLoader(Activity activity) {
        super(activity);
    }

    @Override
    public List<Location> load() {
        try {
            return dbCache.loadOrRequest(locationResource);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
