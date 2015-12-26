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

package augsburg.se.alltagsguide.event;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.ocpsoft.pretty.time.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;
import augsburg.se.alltagsguide.utilities.Objects;

public class EventPageAdapter extends BaseAdapter<EventPageAdapter.ContentViewHolder, Page> {

    @NonNull private EventOverviewFragment.OnEventPageFragmentInteractionListener mListener;
    @ColorInt private int mColor;
    @NonNull SimpleDateFormat dateFormatTo;

    public EventPageAdapter(@NonNull List<EventPage> pages, @NonNull EventOverviewFragment.OnEventPageFragmentInteractionListener listener, @ColorInt int primaryColor, @NonNull Context context) {
        super(new ArrayList<Page>(pages), context);
        mListener = listener;
        mColor = primaryColor;
        mContext = context;
        dateFormatTo = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
    }

    @Override
    public void setItems(@NonNull List<Page> pages) {
        super.setItems(pages);
        Collections.sort(pages);
        notifyDataSetChanged();
    }

    @Override
    public ContentViewHolder getViewHolder(View view) {
        return new ContentViewHolder(view);
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ContentViewHolder contentHolder, int position) {
        final EventPage page = (EventPage) get(position);
        contentHolder.title.setText(page.getTitle());
        String desc = page.getDescription();
        contentHolder.date.setText(dateFormatTo.format(page.getModified()));
        contentHolder.description.setText(Html.fromHtml(desc));
        contentHolder.description.setVisibility(Objects.isNullOrEmpty(desc) ? View.GONE : View.VISIBLE);
        contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOpenEventPage(page);
            }
        });

        Date from = new Date(page.getEvent().getStartTime());
        PrettyTime prettyTime = new PrettyTime();
        String dateText = prettyTime.format(from);

        contentHolder.date.setText(dateText);
        contentHolder.date.setTextColor(mColor);

        if (page.getLocation() != null) {
            String location = "";
            String name = page.getLocation().getName();
            String address = page.getLocation().getAddress();
            if (name != null) {
                location += name + " - ";
            }
            if (address != null) {
                location += address;
            }
            contentHolder.location.setText(location);
            contentHolder.location.setTextColor(mColor);
        }
        if (Objects.isNullOrEmpty(contentHolder.location.getText())) {
            contentHolder.location.setText("/");
        }
    }

    public class ContentViewHolder extends UltimateRecyclerviewViewHolder {
        TextView title;
        TextView description;
        TextView date;
        TextView location;

        public ContentViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
            location = (TextView) itemView.findViewById(R.id.location);
        }
    }
}
