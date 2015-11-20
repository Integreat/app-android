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

package augsburg.se.alltagsguide.gcm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gcm.GCMRegistrar;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.NetworkService;
import augsburg.se.alltagsguide.utilities.CommonUtilities;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.util.Ln;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    /**
     * The m context.
     */
    @NonNull private final Context mContext;

    /**
     * The m network communication.
     */
    @NonNull private final NetworkService mNetworkCommunication;

    /**
     * The m pref utilities.
     */
    @NonNull private final PrefUtilities mPrefUtilities;

    /**
     * Instantiates a new push utilities.
     *
     * @param context              the context
     * @param networkCommunication the network communication
     * @param prefUtilities        the pref utilities
     */
    @Inject
    public ServerUtilities(@NonNull Context context, @NonNull NetworkService networkCommunication, @NonNull PrefUtilities prefUtilities) {
        mContext = context;
        mNetworkCommunication = networkCommunication;
        mPrefUtilities = prefUtilities;
    }

    /**
     * Register this account/device pair within the server.
     *
     * @param regId the reg id
     */
    void register(final Location location, final String regId, final Callback<String> callback) {
        Ln.i("registering device (regId = " + regId + ")");
        mNetworkCommunication.subscribePush(location, regId, new Callback<String>() {
                    @Override
                    public void success(String result, Response response) {
                        mPrefUtilities.setRegisteredOnServer(location, true, regId);
                        if (callback != null) {
                            callback.success(result, response);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // Here we are simplifying and retrying on
                        // any error; in a
                        // real
                        // application, it should retry only on
                        // unrecoverable errors
                        // (like HTTP error code 503).
                        Ln.e("Failed to register after several retries",
                                error);
                        unregisterDevice();
                        if (callback != null) {
                            callback.failure(null);
                        }
                    }
                }
        );
    }

    /**
     * Unregister this account/device pair within the server.
     *
     * @param regId the reg id
     */
    public void unregister(final Location location, final String regId) {
        Ln.i("unregistering device (regId = " + regId + ")");
        mNetworkCommunication.unsubscribePush(location, regId, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Ln.i(s);
                        unregisterDevice();
                        // Regardless of server success, clear local preferences
                        mPrefUtilities.setRegisteredOnServer(location, false, null);
                    }

                    @Override
                    public void failure(RetrofitError error) {
// At this point the device is unregistered from GCM,
                        // but still
                        // registered in the server.
                        // We could try to unregister again, but it is not
                        // necessary:
                        // if the server tries to send a message to the device,
                        // it will get
                        // a "NotRegistered" error message and should unregister
                        // the device.
                        Ln.e(error);
                        // Regardless of server success, clear local preferences
                        mPrefUtilities.setRegisteredOnServer(location, false, null);
                    }
                }
        );
    }

    /**
     * Relog.
     */
    public void relog(Location location) {
        mPrefUtilities.setRegisteredOnServer(location, false, "");
        registerGCMClient(location, null);
    }

    /**
     * Register gcm client.
     */
    public void registerGCMClient(Location location, Callback<String> callback) {
        GCMRegistrar.checkDevice(mContext);
        GCMRegistrar.checkManifest(mContext);

        final String regId = GCMRegistrar.getRegistrationId(mContext);

        if (TextUtils.isEmpty(regId)) {
            GCMRegistrar.register(mContext, CommonUtilities.SENDER_ID);
            //TODO do something here?
        } else {
            checkLoginOnServer(location, callback, regId);
        }
    }

    /**
     * Check login on server.
     *
     * @param regId the reg id
     */
    private void checkLoginOnServer(Location location, Callback<String> callback, String regId) {
        // Device is already registered on GCM, needs to check if it is
        // registered on our server as well.
        if (mPrefUtilities.isRegisteredOnServer(location)) {
            // Skips registration.
            Ln.i("Already registered on the server");
            if (callback != null) {
                callback.success("success", null);
            }
        } else {
            register(location, regId, callback);
        }
    }

    /**
     * Unregister device.
     */
    public void unregisterDevice() {
        try {
            GCMRegistrar.unregister(mContext);
        } catch (Exception e) {
            Ln.w("C2DM unregistration error", e);
        }
    }


}