package augsburg.se.alltagsguide.category;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.common.Category;
import augsburg.se.alltagsguide.utilities.BaseAdapter;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class CategoryAdapter extends BaseAdapter<CategoryAdapter.ContentViewHolder, Article> {

    private CategoryFragment.OnCategoryFragmentInteractionListener mListener;
    private Category mCategory;

    public CategoryAdapter(Category category, CategoryFragment.OnCategoryFragmentInteractionListener listener) {
        super(category.getArticles());
        mCategory = category;
        mListener = listener;
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO use viewType for which nested level it is
        return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        final Article article = get(position);
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onArticleClicked(article);
            }
        });
        //TODO inject holder.image.setText(information.getImage());
    }

    public void replace(Category category) {
        removeAll();
        mCategory = category;
        add(mCategory.getArticles());
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
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
