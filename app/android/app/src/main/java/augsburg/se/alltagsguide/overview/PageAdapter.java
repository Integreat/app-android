package augsburg.se.alltagsguide.overview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.BaseAdapter;
import augsburg.se.alltagsguide.utilities.Objects;

public class PageAdapter extends BaseAdapter<PageAdapter.BaseContentViewHolder, Page> {

    private OverviewFragment.OnPageFragmentInteractionListener mListener;
    private int mColor;
    private Context mContext;
    private static final int ENTRY = 7;
    private static final int TITLE = 42;

    public PageAdapter(List<Page> pages, OverviewFragment.OnPageFragmentInteractionListener listener, int primaryColor, Context context) {
        super(pages);
        mListener = listener;
        mColor = primaryColor;
        mContext = context;
    }

    @Override
    public void setItems(@NonNull List<Page> pages) {
        super.setItems(pages);
        Collections.sort(pages);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        final Page page = get(position);
        if (Objects.isNullOrEmpty(page.getContent())) {
            return TITLE;
        }
        return ENTRY;
    }

    @Override
    public BaseContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ENTRY:
                return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_item, parent, false));
            case TITLE:
                return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_title, parent, false));
            default:
                return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseContentViewHolder holder, int position) {
        final Page page = get(position);
        if (holder instanceof ContentViewHolder) {
            onBindContentViewHolder((ContentViewHolder) holder, page);
        } else if (holder instanceof TitleViewHolder) {
            onBindTitleViewHolder((TitleViewHolder) holder, page);
        }
    }

    private void onBindTitleViewHolder(TitleViewHolder titleHolder, Page page) {
        titleHolder.title.setText(page.getTitle());
    }

    private void onBindContentViewHolder(ContentViewHolder contentHolder, final Page page) {
        contentHolder.title.setText(page.getTitle());
        contentHolder.title.setBackgroundColor(mColor);
        String desc = page.getDescription();
        contentHolder.description.setText(Html.fromHtml(desc));
        contentHolder.description.setVisibility(Objects.isNullOrEmpty(desc) ? View.GONE : View.VISIBLE);
        contentHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOpenPage(page);
            }
        });
        contentHolder.more.setTextColor(Objects.isNullOrEmpty(desc) ? mContext.getResources().getColor(android.R.color.white) : mColor);
    }


    public class BaseContentViewHolder extends RecyclerView.ViewHolder {
        public BaseContentViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class TitleViewHolder extends BaseContentViewHolder {
        TextView title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public class ContentViewHolder extends BaseContentViewHolder {
        TextView title;
        TextView description;
        ImageView image;
        TextView more;

        public ContentViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            image = (ImageView) itemView.findViewById(R.id.image);
            more = (TextView) itemView.findViewById(R.id.more);
        }
    }
}
