package augsburg.se.alltagsguide.overview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.BaseAdapter;
import augsburg.se.alltagsguide.utilities.Objects;

public class PageAdapter extends BaseAdapter<PageAdapter.BaseContentViewHolder, Page> {

    private OverviewFragment.OnPageFragmentInteractionListener mListener;
    private int mColor;

    public PageAdapter(List<Page> pages, OverviewFragment.OnPageFragmentInteractionListener listener, int primaryColor) {
        super(pages);
        mListener = listener;
        mColor = primaryColor;
    }

    @Override
    public void setItems(@NonNull List<Page> pages) {
        List<Page> pagesWithContent = new ArrayList<>();
        for (Page page : pages) {
            if (!Objects.isNullOrEmpty(page.getContent())) {
                pagesWithContent.add(page);
            }
        }
        super.setItems(pagesWithContent);
        notifyDataSetChanged();
    }

    @Override
    public BaseContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseContentViewHolder holder, int position) {
        final Page page = get(position);
        ContentViewHolder contentHolder = (ContentViewHolder) holder;
        contentHolder.title.setText(page.getTitle());
        contentHolder.title.setBackgroundColor(mColor);
        String desc = page.getDescription();
        if (Objects.isNullOrEmpty(desc) && !Objects.isNullOrEmpty(page.getContent())) {
            desc = page.getContent().substring(0, Math.min(300, page.getContent().length() - 1));
        }
        contentHolder.description.setText(desc);
        contentHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOpenPage(page);
            }
        });
        contentHolder.more.setTextColor(mColor);
    }


    public class BaseContentViewHolder extends RecyclerView.ViewHolder {
        public BaseContentViewHolder(View itemView) {
            super(itemView);
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
