package augsburg.se.alltagsguide.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import roboguice.RoboGuice;

/**
 * Loader which extends AsyncTaskLoaders and handles caveats as pointed out in
 * http://code.google.com/p/android/issues/detail?id=14944.
 * <p/>
 * Based on CursorLoader.java in the Fragment compatibility package
 *
 * @param <D> data type
 * @author Alexander Blom (me@alexanderblom.se)
 */
public abstract class AsyncLoader<D> extends AsyncTaskLoader<D> {

    private D data;

    /**
     * Create async loader
     *
     * @param context
     */
    public AsyncLoader(@NonNull final Context context) {
        super(context);
        RoboGuice.injectMembers(context, this);
    }

    @Override
    public void deliverResult(final D data) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            return;
        }
        this.data = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (data != null) {
            deliverResult(data);
        }
        if (takeContentChanged() || data == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        data = null;
    }
}