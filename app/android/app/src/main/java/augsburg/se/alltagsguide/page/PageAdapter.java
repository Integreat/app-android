package augsburg.se.alltagsguide.page;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.BaseAdapter;
import augsburg.se.alltagsguide.utilities.Objects;

public class PageAdapter extends BaseAdapter<PageAdapter.BaseContentViewHolder, Page> {

    private PagesFragment.OnPageFragmentInteractionListener mListener;
    private int mColor;

    public PageAdapter(List<Page> pages, PagesFragment.OnPageFragmentInteractionListener listener, int primaryColor) {
        super(pages);
        mListener = listener;
        mColor = primaryColor;
    }

    @Override
    public BaseContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseContentViewHolder holder, int position) {
        final Page page = get(position);
        ContentViewHolder contentHolder = (ContentViewHolder) holder;
        contentHolder.title.setText(page.getTitle());
        contentHolder.title.setBackgroundColor(mColor);
        String desc = page.getDescription();
        if (Objects.isNullOrEmpty(desc) && !Objects.isNullOrEmpty(page.getContent())) {
            desc = page.getContent().substring(0, Math.min(200, page.getContent().length() - 1));
        }
        contentHolder.description.setHtmlFromString(desc, new HtmlTextView.RemoteImageGetter());
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
        HtmlTextView description;
        ImageView image;
        TextView more;

        public ContentViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (HtmlTextView) itemView.findViewById(R.id.description);
            image = (ImageView) itemView.findViewById(R.id.image);
            more = (TextView) itemView.findViewById(R.id.more);
        }
    }
}
