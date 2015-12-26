package augsburg.se.alltagsguide.utilities.broadcast;

/**
 * Created by Daniel-L
 * on 29.11.2015
 */
public class NetworkChangeEvent {
    private boolean mIsOnline;

    public NetworkChangeEvent(boolean isOnline) {
        mIsOnline = isOnline;
    }

    public boolean isOnline() {
        return mIsOnline;
    }
}
