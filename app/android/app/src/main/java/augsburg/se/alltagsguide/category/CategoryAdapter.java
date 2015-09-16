package augsburg.se.alltagsguide.category;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.common.Category;
import augsburg.se.alltagsguide.utilities.BaseAdapter;

public class CategoryAdapter extends BaseAdapter<CategoryAdapter.BaseContentViewHolder, Article> {

    private CategoryFragment.OnCategoryFragmentInteractionListener mListener;
    private Category mCategory;
    private int mColor;

    public CategoryAdapter(Category category, CategoryFragment.OnCategoryFragmentInteractionListener listener, int primaryColor) {
        super(new ArrayList<>(category.getArticlesRecursive()));
        mCategory = category;
        mListener = listener;
        mColor = primaryColor;
    }

    @Override
    public BaseContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseContentViewHolder holder, int position) {
        final Article article = get(position);
        ContentViewHolder contentHolder = (ContentViewHolder) holder;
        contentHolder.title.setText(article.getTitle());
        contentHolder.title.setBackgroundColor(mColor);
        contentHolder.description.setText(article.getDescription());
        contentHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onArticleClicked(article);
            }
        });
        contentHolder.more.setTextColor(mColor);
        //TODO inject holder.image.setText(information.getImage());
    }

    public void replace(Category category) {
        removeAll();
        mCategory = category;
        add(mCategory.getArticlesRecursive());
    }

    public class BaseContentViewHolder extends RecyclerView.ViewHolder {

        public BaseContentViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ContentHeaderViewHolder extends BaseContentViewHolder {
        public ContentHeaderViewHolder(View itemView) {
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


   /* @Override
    public long getHeaderId(int position) {
        Article article = get(position);
        Category category = mCategory.getCategoryByArticle(article);
        return category.getTitle() == null ? -1 : category.getTitle().hashCode();
    }

    @Override
    public BaseContentViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_list_header, parent, false);
        return new ContentHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(BaseContentViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        Article article = get(position);
        Category category = mCategory.getCategoryByArticle(article);
        textView.setText(category.getTitle());
    }
*/
}
