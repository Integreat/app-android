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

import android.content.Context;

import augsburg.se.alltagsguide.gcm.GCMCommand;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 18.10.2015.
 */
public class TestCommand extends GCMCommand {

    @Override
    public void execute(Context context, String type, String extraData) {
        Ln.i("Received GCM message: type=" + type + ", extraData=" + extraData);
    }
}
