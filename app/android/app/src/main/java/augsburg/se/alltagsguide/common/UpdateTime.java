package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

import augsburg.se.alltagsguide.utilities.Helper;

/**
 * Created by Daniel-L on 23.09.2015.
 */
public class UpdateTime implements Serializable {
    @NonNull private String mDate;
    private static final int OFFSET = 1000;

    public UpdateTime(long time) {
        mDate = Helper.TO_DATE_FORMAT.format(new Date(time + OFFSET));
    }

    @NonNull
    @Override
    public String toString() {
        return mDate;
    }
}
