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

package augsburg.se.alltagsguide.gcm.command;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.gcm.GCMCommand;
import augsburg.se.alltagsguide.overview.OverviewActivity;
import roboguice.util.Ln;

public class AnnouncementCommand extends GCMCommand {

    @Override
    public void execute(Context context, String type, String extraData) {
        Ln.i("Received GCM message: " + type);
        displayNotification(context, extraData);
    }

    private void displayNotification(Context context, String message) {
        Ln.i("Displaying notification: " + message);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, new NotificationCompat.Builder(context)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker(message)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message)
                        .setColor(ContextCompat.getColor(context, R.color.primary))
                        .setContentIntent(
                                PendingIntent.getActivity(context, 0,
                                        new Intent(context, OverviewActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                        Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                        0))
                        .setAutoCancel(true)
                        .build());
    }

}
