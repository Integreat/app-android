package augsburg.se.alltagsguide.common;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import augsburg.se.alltagsguide.utilities.Objects;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 08.10.2015.
 */
public class Event implements Serializable {
    private int mId;
    private long mStartTime;
    private long mEndTime;
    private boolean mAllDay;
    private int mPageId;

    public Event(int id, long startTime, long endTime, boolean allDay, int pageId) {
        mId = id;
        mStartTime = startTime;
        mEndTime = endTime;
        mAllDay = allDay;
        mPageId = pageId;
    }

    public boolean isAllDay() {
        return mAllDay;
    }

    public int getId() {
        return mId;
    }

    public int getPageId() {
        return mPageId;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public @NonNull static Event fromJson(@NonNull final JsonObject jsonEvent, int pageId) {
        int id = jsonEvent.get("id").getAsInt();
        String startDate = jsonEvent.get("start_date").getAsString();
        String endDate = jsonEvent.get("end_date").getAsString();
        String startTime = jsonEvent.get("start_time").getAsString();
        String endTime = jsonEvent.get("end_time").getAsString();
        boolean allDay = jsonEvent.get("all_day").getAsInt() == 1;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.GERMANY);
        long start = -1;
        long end = -1;
        try {
            start = dateFormat.parse(startDate + " " + Objects.emptyIfNull(startTime)).getTime();
            end = dateFormat.parse(endDate + " " + Objects.emptyIfNull(endTime)).getTime();
        } catch (ParseException e) {
            Ln.e(e);

        }
        return new Event(id, start, end, allDay, pageId);
    }


}
