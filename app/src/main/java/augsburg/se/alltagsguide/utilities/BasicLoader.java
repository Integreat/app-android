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

package augsburg.se.alltagsguide.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    @Inject
    protected NetworkService network;

    @Inject
    protected DatabaseCache dbCache;

    private LoadingType mLoadingType;

    private Handler handler;


    /**
     * Create loader for context
     *
     * @param context
     * @param loadingType
     */
    public BasicLoader(@NonNull final Context context, @NonNull LoadingType loadingType) {
        super(context);
        RoboGuice.injectMembers(context, this);
        mLoadingType = loadingType;
    }

    protected <E> List<E> get(PersistableNetworkResource<E> resource) throws IOException {
        switch (mLoadingType) {
            case FORCE_DATABASE:
                return dbCache.load(resource);
            case FORCE_NETWORK:
                dbCache.requestAndStore(resource);
                return dbCache.load(resource);
            case NETWORK_OR_DATABASE:
                return dbCache.loadOrRequest(resource);
            default:
                return dbCache.loadOrRequest(resource);
        }
    }

    /**
     * Helper to publish string value
     *
     * @param value
     */
    protected void publishMessage(int value) {
        if (handler != null) {
            Bundle data = new Bundle();
            data.putSerializable("message", value);

            /* Creating a message */
            Message msg = new Message();
            msg.setData(data);
            msg.what = value;

            /* Sending the message */
            handler.sendMessage(msg);
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

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * Load data
     *
     * @return data
     */
    @Nullable
    public abstract D load();
}
