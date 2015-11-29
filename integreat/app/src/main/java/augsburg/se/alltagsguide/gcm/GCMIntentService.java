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
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import augsburg.se.alltagsguide.gcm.command.AnnouncementCommand;
import augsburg.se.alltagsguide.gcm.command.TestCommand;
import augsburg.se.alltagsguide.utilities.CommonUtilities;
import roboguice.util.Ln;

/**
 * {@link android.app.IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final Map<String, GCMCommand> MESSAGE_RECEIVERS;

    static {
        // Known messages and their GCM message receivers
        Map<String, GCMCommand> receivers = new HashMap<>();
        receivers.put("test", new TestCommand());
        receivers.put("announcement", new AnnouncementCommand());
        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }

    public GCMIntentService() {
        super(CommonUtilities.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        Ln.i("Device registered: regId=" + regId);
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        Ln.i("Device unregistered");
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        String extraData = intent.getStringExtra("extraData");
        Ln.d("Got GCM message, action=" + action + ", extraData=" + extraData);

        if (action == null) {
            Ln.e("Message received without command action");
            return;
        }

        action = action.toLowerCase();
        GCMCommand command = MESSAGE_RECEIVERS.get(action);
        if (command == null) {
            Ln.e("Unknown command received: " + action);
        } else {
            command.execute(this, action, extraData);
        }

    }

    @Override
    public void onError(Context context, String errorId) {
        Ln.e("Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Ln.w("Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }
}