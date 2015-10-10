package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Daniel-L on 23.09.2015.
 */
public class UpdateTime implements Serializable {
    private String mDate;

    public UpdateTime(long time) {
        mDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).format(new Date(time));
    }

    @Override
    public String toString() {
        return mDate;
    }
}
