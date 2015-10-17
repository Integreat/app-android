package augsburg.se.alltagsguide.event;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.network.EventPageLoader;
import augsburg.se.alltagsguide.utilities.BasePageWebViewLanguageActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_event)
public class EventActivity extends BasePageWebViewLanguageActivity<EventPage> {

    @InjectView(R.id.to_date)
    private TextView toDateTextView;

    @InjectView(R.id.from_date)
    private TextView fromDateTextView;

    @InjectView(R.id.author)
    private TextView authorTextView;

    @InjectView(R.id.location)
    private TextView locationTextView;

    private SimpleDateFormat dateFormatFrom = new SimpleDateFormat("HH:mm dd.MM.yy", Locale.GERMANY);
    private SimpleDateFormat allDayDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);

    @Override
    protected void setMorePageDetails(EventPage page) {
        if (mPage.getEvent().isAllDay()) {
            fromDateTextView.setText(allDayDateFormat.format(mPage.getEvent().getStartTime()));
            toDateTextView.setVisibility(View.GONE);
        } else {
            fromDateTextView.setText(dateFormatFrom.format(mPage.getEvent().getStartTime()));
            toDateTextView.setText(dateFormatFrom.format(mPage.getEvent().getEndTime()));
        }

        if (mPage.getAuthor() != null) {
            authorTextView.setText(mPage.getAuthor().toText());
        }

        if (mPage.getLocation() != null) {
            String location = "";
            String name = mPage.getLocation().getName();
            String address = mPage.getLocation().getAddress();
            if (name != null) {
                location += name + " - ";
            }
            if (address != null) {
                location += address;
            }
            locationTextView.setText(location);
        }
    }

    @Override
    public Loader<EventPage> onCreateLoader(int id, Bundle args) {
        AvailableLanguage language = (AvailableLanguage) args.getSerializable(ARG_LANGUAGE);
        if (language == null || language.getLoadedLanguage() == null) {
            Ln.d("AvailableLanguage is null or has no language.");
            return null;
        }
        return new EventPageLoader(this, mPrefUtilities.getLocation(), language.getLoadedLanguage(), language.getOtherPageId());
    }
}
