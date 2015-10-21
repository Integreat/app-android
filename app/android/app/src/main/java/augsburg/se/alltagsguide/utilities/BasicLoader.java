package augsburg.se.alltagsguide.utilities;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.inject.Inject;

import augsburg.se.alltagsguide.network.NetworkService;
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

    /**
     * Create loader for context
     *
     * @param context
     */
    public BasicLoader(@NonNull final Context context) {
        super(context);
        RoboGuice.injectMembers(context, this);
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
