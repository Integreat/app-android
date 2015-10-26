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

import com.google.inject.Inject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;
import augsburg.se.alltagsguide.utilities.ui.BitmapColorTransformation;

public class PageAdapter extends BaseAdapter<PageAdapter.ContentViewHolder, Page> {

    private PageOverviewFragment.OnPageFragmentInteractionListener mListener;
    private int mColor;
    private static final int ENTRY = 7;
    private static final int TITLE = 42;

    @NonNull private SimpleDateFormat dateFormatTo;
    @Inject private Picasso mPicasso;
    private Transformation mTransformation;

    public PageAdapter(@NonNull List<Page> pages, PageOverviewFragment.OnPageFragmentInteractionListener listener, int primaryColor, @NonNull Context context) {
        super(pages, context);
        mListener = listener;
        mColor = primaryColor;
        mContext = context;
        dateFormatTo = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
        mTransformation = new BitmapColorTransformation(mColor);
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        final Page page = get(position);
        onBindContentViewHolder(holder, page);
    }

    private void onBindContentViewHolder(ContentViewHolder contentHolder, final Page page) {
        contentHolder.title.setText(page.getTitle());
        contentHolder.title.setTextColor(mColor);
        String desc = page.getDescription();
        contentHolder.date.setText(dateFormatTo.format(page.getModified()));
        contentHolder.description.setText(Html.fromHtml(desc));
        contentHolder.description.setVisibility(Objects.isNullOrEmpty(desc) ? View.GONE : View.VISIBLE);
        contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOpenPage(page);
            }
        });
        contentHolder.more.setTextColor(mColor);
        contentHolder.date.setTextColor(mColor);

        if (!Objects.isNullOrEmpty(page.getThumbnail())) {
            RequestCreator creator = mPicasso.load(page.getThumbnail());
            creator.transform(mTransformation)
                    .fit()
                    .centerInside()
                    .into(contentHolder.image);
            contentHolder.image.setVisibility(View.VISIBLE);
        } else {
            contentHolder.image.setVisibility(View.GONE);
        }
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        ImageView image;
        TextView more;
        TextView date;

        public ContentViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            image = (ImageView) itemView.findViewById(R.id.image);
            more = (TextView) itemView.findViewById(R.id.more);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
