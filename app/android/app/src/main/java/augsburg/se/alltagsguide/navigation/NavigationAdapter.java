package augsburg.se.alltagsguide.navigation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Category;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {
    private OnNavigationSelected mListener;
    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private Category rootCategory;
    private List<Category> categories;

    public void setCategory(Category category) {
        rootCategory = category;
        categories = rootCategory.getSubCategoriesRecursive();
        notifyDataSetChanged();
    }

    public interface OnNavigationSelected {
        void onNavigationClicked(Category item);
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
        if (categories == null) {
            return 0;
        }
        Category item = categories.get(position);
        return item.getSubCategories() != null ? HEADER : ITEM;
    }

    @Override
    public int getItemCount() {
        if (rootCategory == null) {
            return 0;
        }
        return rootCategory.getArticlesRecursive().size();
    }


    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        if (categories == null) {
            return;
        }
        final Category item = categories.get(position);
        holder.counter.setText(String.valueOf(item.countItems()));
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
