package augsburg.se.alltagsguide.utilities;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.List;

import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.resources.PersistableNetworkResource;
import roboguice.RoboGuice;
import roboguice.inject.ContextScope;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public abstract class BasicLoader<D> extends AsyncLoader<D> {

    @Inject
    private ContextScope contextScope;

    /**
     * Activity using this loader
     */
    @Inject
    protected Activity activity;

    @Inject
    protected NetworkService network;

    @Inject
    protected DatabaseCache dbCache;

    private boolean mForce;

    /**
     * Create loader for context
     *
     * @param context
     * @param force
     */
    public BasicLoader(@NonNull final Context context, boolean force) {
        super(context);
        RoboGuice.injectMembers(context, this);
        mForce = force;
    }

    protected <E> List<E> requestIfForced(PersistableNetworkResource<E> resource) throws IOException {
        if (mForce) {
            dbCache.requestAndStore(resource);
            return dbCache.load(resource);
        } else {
            return dbCache.loadOrRequest(resource);
        }
    }

    @Override
    @Nullable
    public final D loadInBackground() {
        contextScope.enter(getContext());
        try {
            return load();
        } finally {
            contextScope.exit(getContext());
        }
    }

    /**
     * Load data
     *
     * @return data
     */
    @Nullable
    public abstract D load();
}
