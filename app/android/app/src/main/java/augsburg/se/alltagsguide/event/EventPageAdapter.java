package augsburg.se.alltagsguide.event;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class EventPageAdapter extends BaseAdapter<EventPageAdapter.BaseContentViewHolder, Page> {

    @NonNull private EventOverviewFragment.OnEventPageFragmentInteractionListener mListener;
    private int mColor;
    private static final int WITHOUT_IMAGE = 7;
    @NonNull SimpleDateFormat dateFormatTo;

    public EventPageAdapter(@NonNull List<EventPage> pages, @NonNull EventOverviewFragment.OnEventPageFragmentInteractionListener listener, int primaryColor, @NonNull Context context) {
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
    public int getItemViewType(int position) {
        return WITHOUT_IMAGE;
    }

    @Override
    public BaseContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case WITHOUT_IMAGE:
                return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_event_item, parent, false));
            default:
                return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_event_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseContentViewHolder holder, int position) {
        final EventPage page = (EventPage) get(position);
        if (holder instanceof ContentViewHolder) {
            onBindContentViewHolder((ContentViewHolder) holder, page);
        } else if (holder instanceof ContentWithImageViewHolder) {
            onBindTitleViewHolder((ContentWithImageViewHolder) holder, page);
        }
    }

    private void onBindTitleViewHolder(ContentWithImageViewHolder titleHolder, EventPage page) {
        titleHolder.title.setText(page.getTitle());
    }


    private void onBindContentViewHolder(ContentViewHolder contentHolder, final EventPage page) {
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


    public class BaseContentViewHolder extends RecyclerView.ViewHolder {
        public BaseContentViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ContentWithImageViewHolder extends BaseContentViewHolder {
        TextView title;

        public ContentWithImageViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public class ContentViewHolder extends BaseContentViewHolder {
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
