package augsburg.se.alltagsguide.network;

import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.resources.LocationResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;
import augsburg.se.alltagsguide.utilities.LoadingType;
import roboguice.util.Ln;

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
     * @param context
     */
    public LocationLoader(Context context, LoadingType loadingType) {
        super(context, loadingType);
    }

    @Override
    public List<Location> load() {
        try {
            return get(locationResource);
        } catch (IOException e) {
            Ln.e(e);
            return Collections.emptyList();
        }
    }
}
