package augsburg.se.alltagsguide.navigation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {
    private OnNavigationSelected mListener;
    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private List<Page> mPages;

    public void setPages(List<Page> pages) {
        mPages = filterParents(pages);
        Collections.sort(mPages);
        notifyDataSetChanged();
    }

    private List<Page> filterParents(@NonNull List<Page> pages) {
        List<Page> parentPages = new ArrayList<>();
        for (Page page : pages) {
            if (page.getParent() == null) {
                parentPages.add(page);
            }
        }
        return parentPages;
    }

    public interface OnNavigationSelected {
        void onNavigationClicked(Page item);
    }

    public NavigationAdapter(OnNavigationSelected listener) {
        mListener = listener;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = ITEM;
        switch (viewType) {
            case HEADER:
                layout = R.layout.navigation_header;
                break;
            case ITEM:
                layout = R.layout.navigation_item;
                break;
        }
        return new NavigationViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));

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
        holder.counter.setText(String.valueOf(item.getContentCount()));
        holder.title.setText(generatePadding(item.getDepth(), item.getTitle()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNavigationClicked(item);
            }
        });
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
