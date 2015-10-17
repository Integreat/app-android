package augsburg.se.alltagsguide.navigation;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.Theme;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.RoboGuice;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {
    private OnNavigationSelected mListener;
    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private List<Page> mPages;
    private int mColor;
    private Context mContext;
    private int mCurrentPageId;
    @Inject
    private Picasso mPicasso;


    public void setPages(List<Page> pages) {
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
        mListener = listener;
        mColor = color;
        mContext = context;
        mCurrentPageId = currentPageId;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NavigationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (mPages == null) {
            return 0;
        }
        Page item = mPages.get(position);
        return item.getSubPages() != null ? HEADER : ITEM;
    }

    @Override
    public int getItemCount() {
        if (mPages == null) {
            return 0;
        }
        return mPages.size();
    }


    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        if (mPages == null) {
            return;
        }
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
        holder.itemView.setBackgroundColor(selected ? mColor : mContext.getResources().getColor(android.R.color.white));
        holder.counter.setTextColor(selected ? mContext.getResources().getColor(android.R.color.white) : mContext.getResources().getColor(android.R.color.primary_text_light));
        holder.title.setTextColor(selected ? mContext.getResources().getColor(android.R.color.white) : mContext.getResources().getColor(android.R.color.primary_text_light));
        if (!Objects.isNullOrEmpty(item.getThumbnail())) {
            mPicasso.load(item.getThumbnail()).into(holder.image);
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
