package augsburg.se.alltagsguide.event;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.EventCategory;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.EventTag;
import augsburg.se.alltagsguide.network.EventPageLoader;
import augsburg.se.alltagsguide.utilities.ui.BasePageWebViewLanguageActivity;
import augsburg.se.alltagsguide.utilities.Objects;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContentView(R.layout.activity_event)
public class EventActivity extends BasePageWebViewLanguageActivity<EventPage> {

    @InjectView(R.id.tags_layout)
    private LinearLayout tagsLayout;

    @InjectView(R.id.categories_base_layout)
    private LinearLayout categoriesBaseLayout;

    @InjectView(R.id.categories_layout)
    private LinearLayout categoriesLayout;

    @InjectView(R.id.author)
    private View authorLayout;

    @InjectView(R.id.time_from_layout)
    private View timeFromLayout;

    @InjectView(R.id.time_to_layout)
    private View timeToLayout;

    @InjectView(R.id.to_date)
    private TextView toDateTextView;

    @InjectView(R.id.from_date)
    private TextView fromDateTextView;

    @InjectView(R.id.author)
    private TextView authorTextView;

    @InjectView(R.id.location)
    private TextView locationTextView;

    @NonNull private SimpleDateFormat dateFormatFrom = new SimpleDateFormat("HH:mm dd.MM.yy", Locale.GERMANY);
    @NonNull private SimpleDateFormat allDayDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);

    @Override
    protected void setMorePageDetails(EventPage page) {
        int color = mPrefUtilities.getCurrentColor();
        if (mPage.getEvent().isAllDay()) {
            fromDateTextView.setText(allDayDateFormat.format(mPage.getEvent().getStartTime()));
            timeToLayout.setVisibility(View.GONE);
        } else {
            fromDateTextView.setText(dateFormatFrom.format(mPage.getEvent().getStartTime()));
            toDateTextView.setText(dateFormatFrom.format(mPage.getEvent().getEndTime()));
        }

        if (mPage.getAuthor() != null) {
            String authorText = mPage.getAuthor().toText();
            if (!Objects.isNullOrEmpty(authorText)) {
                authorTextView.setText(authorText);
            } else {
                authorLayout.setVisibility(View.GONE);
            }
        } else {
            authorLayout.setVisibility(View.GONE);
        }

        if (!mPage.getCategories().isEmpty()) {
            for (EventCategory category : mPage.getCategories()) {
                @SuppressLint("InflateParams") TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.category_item, categoriesLayout, false);
                view.setText(category.getName());
                view.setTextColor(ContextCompat.getColor(this, color));
                view.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
                categoriesLayout.addView(view);
            }
        } else {
            categoriesBaseLayout.setVisibility(View.GONE);
        }

        if (!mPage.getTags().isEmpty()) {
            for (EventTag tag : mPage.getTags()) {
                @SuppressLint("InflateParams") TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.tag_item, tagsLayout, false);
                view.setText(tag.getName());
                view.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                view.setBackgroundColor(ContextCompat.getColor(this, color));
                tagsLayout.addView(view);
            }
        } else {
            tagsLayout.setVisibility(View.GONE);
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
