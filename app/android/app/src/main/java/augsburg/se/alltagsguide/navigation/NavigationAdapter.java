package augsburg.se.alltagsguide.navigation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import augsburg.se.alltagsguide.R;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {
    private List<NavigationItem> mItems;
    private OnNavigationSelected mListener;

    public interface OnNavigationSelected {
        void onNavigationClicked(NavigationItem item);
    }

    public NavigationAdapter(List<NavigationItem> items, OnNavigationSelected listener) {
        mItems = items;
        mListener = listener;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        switch (viewType) {
            case 0:
                layout = R.layout.navigation_header;
                break;
            default:
                layout = R.layout.navigation_item;
                break;
        }
        return new NavigationViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));

    }

    @Override
    public int getItemViewType(int position) {
        NavigationItem item = mItems.get(position);
        return item.hasChilds() ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        final NavigationItem item = mItems.get(position);
        holder.counter.setText("" + item.getContent().countItems());
        holder.title.setText(generatePadding(item.getDepth(), item.getContent().getTitle()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNavigationClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
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

    public class NavigationHeaderViewHolder extends NavigationViewHolder {

        public NavigationHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class NavigationItemViewHolder extends NavigationViewHolder {

        public NavigationItemViewHolder(View itemView) {
            super(itemView);
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
