package augsburg.se.alltagsguide.utilities.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.inject.Inject;

import de.greenrobot.event.EventBus;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Created by Daniel-L
 * on 29.11.2015
 */
public class NetworkChangeReceiver extends RoboBroadcastReceiver {
    @Inject EventBus eventBus;

    protected void handleReceive(Context context, Intent intent) {
        eventBus.post(new NetworkChangeEvent(isOnline(context)));
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }
}