package augsburg.se.alltagsguide.gcm;

/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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