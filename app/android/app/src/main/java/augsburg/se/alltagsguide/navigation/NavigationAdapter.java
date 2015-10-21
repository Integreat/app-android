package augsburg.se.alltagsguide.navigation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.BitmapInvertTransformation;
import augsburg.se.alltagsguide.utilities.Objects;
import roboguice.RoboGuice;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {
    private OnNavigationSelected mListener;
    private static final int HEADER = 0;
    @NonNull private List<Page> mPages;
    private int mColor;
    private int mCurrentPageId;
    @Inject
    private Picasso mPicasso;
    private BitmapInvertTransformation mInvertTransformation;
    private int textColor;
    private int whiteColor;

    public void setPages(@NonNull List<Page> pages) {
        mPages = Page.filterParents(pages);
        Collections.sort(mPages);
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int index) {
        mCurrentPageId = index;
    }


    public interface OnNavigationSelected {
        void onNavigationClicked(Page item);
    }

    public NavigationAdapter(OnNavigationSelected listener, int color, Context context, int currentPageId) {
        RoboGuice.injectMembers(context, this);
        mPages = new ArrayList<>();
        mListener = listener;
        mColor = color;
        mCurrentPageId = currentPageId;
        mInvertTransformation = new BitmapInvertTransformation();
        textColor = context.getResources().getColor(android.R.color.primary_text_light);
        whiteColor = context.getResources().getColor(android.R.color.white);
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NavigationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return HEADER;
    }

    @Override
    public int getItemCount() {
        return mPages.size();
    }


    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        final Page item = mPages.get(position);
        boolean selected = Objects.equals(item.getId(), mCurrentPageId);
        holder.counter.setText(String.valueOf(item.getContentCount()));
        holder.title.setText(generatePadding(item.getDepth(), item.getTitle()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNavigationClicked(item);
            }
        });
        holder.itemView.setBackgroundColor(selected ? mColor : whiteColor);
        holder.counter.setTextColor(selected ? whiteColor : textColor);
        holder.title.setTextColor(selected ? whiteColor : textColor);
        if (!Objects.isNullOrEmpty(item.getThumbnail())) {
            RequestCreator creator = mPicasso.load(item.getThumbnail());
            if (selected) {
                creator = creator.transform(mInvertTransformation);
            }
            creator.into(holder.image);
        } else {
            holder.image.setImageDrawable(null);
        }
    }

    public class NavigationViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView counter;

        public NavigationViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            counter = (TextView) itemView.findViewById(R.id.counter);
        }
    }

    private String generatePadding(int depth, String title) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            builder.append("   ");
        }
        builder.append(title);
        return builder.toString();
    }
}
