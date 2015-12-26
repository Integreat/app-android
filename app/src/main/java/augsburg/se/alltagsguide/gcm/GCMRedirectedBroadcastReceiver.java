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

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMRedirectedBroadcastReceiver extends GCMBroadcastReceiver {

    /**
     * Gets the class name of the intent service that will handle GCM messages.
     *
     * Used to override class name, so that GCMIntentService can live in a
     * subpackage.
     */
    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        return GCMIntentService.class.getCanonicalName();
    }

}