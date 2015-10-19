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
